package arriba.configuration;

import java.util.Map;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.bytearrays.MutableByteArrayKeyedMap;
import arriba.common.Handler;
import arriba.common.HandlerRepository;
import arriba.common.MapHandlerRepository;
import arriba.common.Sender;
import arriba.disruptor.DisruptorSender;
import arriba.disruptor.inbound.DeserializingFixMessageEventHandler;
import arriba.disruptor.inbound.InboundFixMessageEvent;
import arriba.disruptor.inbound.InboundFixMessageEventFactory;
import arriba.disruptor.inbound.SerializedFixMessageToDisruptorAdapter;
import arriba.disruptor.inbound.SessionNotifyingInboundFixMessageEventHandler;
import arriba.disruptor.outbound.OutboundFixMessageEvent;
import arriba.disruptor.outbound.OutboundFixMessageEventFactory;
import arriba.disruptor.outbound.OutboundFixMessageToDisruptorAdapter;
import arriba.disruptor.outbound.TransportWritingFixMessageEventHandler;
import arriba.fix.chunk.CachingFixChunkBuilderSupplier;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.chunk.FixChunkBuilderSupplier;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilderSupplier;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.InboundFixMessageFactory;
import arriba.fix.inbound.RepeatingGroupBuilder;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RawOutboundFixMessageBuilder;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.fix.session.InMemorySessionResolver;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SessionResolver;
import arriba.fix.tagindexresolvers.CanonicalTagIndexResolverRepository;
import arriba.transport.TransportRepository;
import cern.colt.map.OpenIntObjectHashMap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.wizard.DisruptorWizard;

public final class ArribaWizard<T> {

    private final TransportRepository<String, T> transportRepository;
    private final Map<SessionId, Session> sessionIdToSession = Maps.newHashMap();
    private final Map<String, Handler<?>> messageTypeToHandler = Maps.newHashMap();
    private final Set<SessionId> sessionIds = Sets.newHashSet();
    private final FixChunkBuilderSupplier fixChunkBuilderSupplier = new CachingFixChunkBuilderSupplier(
            new ArrayFixChunkBuilderSupplier(new CanonicalTagIndexResolverRepository()),
            new MutableByteArrayKeyedMap<FixChunkBuilder>(),
            new OpenIntObjectHashMap()
            );
    private final SessionResolver sessionResolver = new InMemorySessionResolver(this.sessionIdToSession);
    private final Sender<OutboundFixMessage> outboundSender;
    private final Sender<ChannelBuffer> inboundSender;

    public ArribaWizard(final DisruptorConfiguration disruptorConfiguration, final TransportRepository<String, T> transportRepository) {
        this.transportRepository = transportRepository;

        final RingBuffer<InboundFixMessageEvent> inboundDisruptor = this.inboundDisruptor(disruptorConfiguration);
        this.inboundSender = new DisruptorSender<>(
                inboundDisruptor,
                new SerializedFixMessageToDisruptorAdapter()
                );

        final RingBuffer<OutboundFixMessageEvent> outboundDisruptor = this.outboundDisruptor(disruptorConfiguration);
        this.outboundSender = new DisruptorSender<>(
                outboundDisruptor,
                new OutboundFixMessageToDisruptorAdapter()
                );
    }

    public Sender<ChannelBuffer> getInboundSender() {
        return this.inboundSender;
    }

    public Sender<OutboundFixMessage> getOutboundSender() {
        return this.outboundSender;
    }

    public RichOutboundFixMessageBuilder createOutboundBuilder() {
        return new RichOutboundFixMessageBuilder(new RawOutboundFixMessageBuilder());
    }

    public ArribaWizard<T> registerMessageHandler(final MessageType messageType, final Handler<?> handler) {
        final String fixValue = messageType.getValue();

        if (this.messageTypeToHandler.containsKey(fixValue)) {
            throw new IllegalArgumentException("Message type " + fixValue + " already has a message handler.");
        }

        this.messageTypeToHandler.put(fixValue, handler);

        return this;
    }

    public SessionWizard register(final String senderCompId) {
        return new SessionWizard(senderCompId, this);
    }

    ArribaWizard<T> registerSessions(final String senderCompId, final String[] targetCompIds) {
        for (final String targetCompId : targetCompIds) {
            this.sessionIds.add(new SessionId(senderCompId, targetCompId));
        }

        return this;
    }


    public void start() {
        this.initializeSessions();
    }

    @SuppressWarnings("unchecked")
    private RingBuffer<OutboundFixMessageEvent> outboundDisruptor(final DisruptorConfiguration configuration) {
        final DisruptorWizard<OutboundFixMessageEvent> outgoingDisruptor = new DisruptorWizard<>(
                new OutboundFixMessageEventFactory(),
                configuration.getRingBufferSize(),
                configuration.getExecutor(),
                configuration.getClaimStrategy(),
                configuration.getWaitStrategy()
                );
        outgoingDisruptor.handleEventsWith(this.transportWritingConsumer());

        return outgoingDisruptor.start();
    }

    private EventHandler<OutboundFixMessageEvent> transportWritingConsumer() {
        return new TransportWritingFixMessageEventHandler<T>(this.transportRepository, this.sessionResolver);
    }

    @SuppressWarnings("unchecked")
    private RingBuffer<InboundFixMessageEvent> inboundDisruptor(final DisruptorConfiguration configuration) {
        final DisruptorWizard<InboundFixMessageEvent> inboundDisruptor = new DisruptorWizard<>(
                new InboundFixMessageEventFactory(),
                configuration.getRingBufferSize(),
                configuration.getExecutor(),
                configuration.getClaimStrategy(),
                configuration.getWaitStrategy()
                );
        inboundDisruptor
        .handleEventsWith(this.deserializingConsumer())
        .then(this.sessionNotifyingConsumer());

        return inboundDisruptor.start();
    }

    private EventHandler<InboundFixMessageEvent> deserializingConsumer() {
        return new DeserializingFixMessageEventHandler(
                this.inboundFixMessageBuilder(),
                new RepeatingGroupBuilder(this.fixChunkBuilderSupplier)
                );
    }

    private InboundFixMessageBuilder inboundFixMessageBuilder() {
        return new InboundFixMessageBuilder(
                this.fixChunkBuilderSupplier,
                new InboundFixMessageFactory()
                );
    }

    private EventHandler<InboundFixMessageEvent> sessionNotifyingConsumer() {
        return new SessionNotifyingInboundFixMessageEventHandler(this.sessionResolver);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initializeSessions() {
        final HandlerRepository handlerRepository = new MapHandlerRepository(this.messageTypeToHandler);
        for (final SessionId sessionId : this.sessionIds) {
            this.sessionIdToSession.put(sessionId, new Session(sessionId, handlerRepository));
        }
    }
}
