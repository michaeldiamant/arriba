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
import arriba.disruptor.DisruptorSessionDisconnector;
import arriba.disruptor.inbound.DeserializingFixMessageEventHandler;
import arriba.disruptor.inbound.InboundDisruptorAdapter;
import arriba.disruptor.inbound.InboundEvent;
import arriba.disruptor.inbound.InboundFactory;
import arriba.disruptor.inbound.SessionNotifyingInboundFixMessageEventHandler;
import arriba.disruptor.outbound.OutboundDisruptorAdapter;
import arriba.disruptor.outbound.OutboundEvent;
import arriba.disruptor.outbound.OutboundEventFactory;
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
import arriba.fix.session.InMemoryLogoutTracker;
import arriba.fix.session.InMemorySessionResolver;
import arriba.fix.session.LogoutTracker;
import arriba.fix.session.ScheduledSessionMonitor;
import arriba.fix.session.Session;
import arriba.fix.session.SessionDisconnector;
import arriba.fix.session.SessionId;
import arriba.fix.session.SessionMonitor;
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
    private final LogoutTracker logoutTracker = new InMemoryLogoutTracker();
    private final SessionResolver sessionResolver = new InMemorySessionResolver(this.sessionIdToSession);
    private final Sender<OutboundFixMessage> outboundSender;
    private final Sender<ChannelBuffer> inboundSender;
    private final SessionDisconnector sessionDisconnector;
    private final SessionMonitor sessionMonitor;

    public ArribaWizard(final DisruptorConfiguration disruptorConfiguration, final TransportRepository<String, T> transportRepository) {
        this.transportRepository = transportRepository;

        final RingBuffer<InboundEvent> inboundDisruptor = this.inboundDisruptor(disruptorConfiguration);
        this.inboundSender = new DisruptorSender<>(
                inboundDisruptor,
                new InboundDisruptorAdapter()
                );

        final RingBuffer<OutboundEvent> outboundDisruptor = this.outboundDisruptor(disruptorConfiguration);
        this.outboundSender = new DisruptorSender<>(
                outboundDisruptor,
                new OutboundDisruptorAdapter()
                );
        this.sessionDisconnector = new DisruptorSessionDisconnector<>(
                outboundDisruptor,
                new OutboundDisruptorAdapter()
                );

        this.sessionMonitor = new ScheduledSessionMonitor(
                this.outboundSender,
                this.createOutboundBuilder(),
                this.sessionDisconnector,
                this.sessionResolver,
                this.logoutTracker
                );
    }

    // TODO Find a way to avoid exposing SessionMonitor.
    public SessionMonitor getSessionMonitor() {
        return this.sessionMonitor;
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
    private RingBuffer<OutboundEvent> outboundDisruptor(final DisruptorConfiguration configuration) {
        final DisruptorWizard<OutboundEvent> outgoingDisruptor = new DisruptorWizard<>(
                new OutboundEventFactory(),
                configuration.getRingBufferSize(),
                configuration.getExecutor(),
                configuration.getClaimStrategy(),
                configuration.getWaitStrategy()
                );
        outgoingDisruptor.handleEventsWith(this.transportWritingConsumer());

        return outgoingDisruptor.start();
    }

    private EventHandler<OutboundEvent> transportWritingConsumer() {
        return new TransportWritingFixMessageEventHandler<T>(
                this.transportRepository,
                this.sessionResolver,
                this.sessionMonitor
                );
    }

    @SuppressWarnings("unchecked")
    private RingBuffer<InboundEvent> inboundDisruptor(final DisruptorConfiguration configuration) {
        final DisruptorWizard<InboundEvent> inboundDisruptor = new DisruptorWizard<>(
                new InboundFactory(),
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

    private EventHandler<InboundEvent> deserializingConsumer() {
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

    private EventHandler<InboundEvent> sessionNotifyingConsumer() {
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
