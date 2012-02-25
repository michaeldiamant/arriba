package arriba.configuration;

import arriba.bytearrays.MutableByteArrayKeyedMap;
import arriba.common.Handler;
import arriba.common.HandlerRepository;
import arriba.common.MapHandlerRepository;
import arriba.common.Sender;
import arriba.disruptor.DisruptorSender;
import arriba.disruptor.DisruptorSessionDisconnector;
import arriba.disruptor.DisruptorTransportSender;
import arriba.disruptor.inbound.*;
import arriba.disruptor.outbound.*;
import arriba.fix.chunk.CachingFixChunkBuilderSupplier;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.chunk.FixChunkBuilderSupplier;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilderSupplier;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.deserializers.InboundFixMessageDeserializer;
import arriba.fix.inbound.messages.InboundFixMessageBuilder;
import arriba.fix.inbound.messages.InboundFixMessageFactory;
import arriba.fix.inbound.messages.RepeatingGroupBuilder;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RawOutboundFixMessageBuilder;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.fix.session.*;
import arriba.fix.session.disconnect.LogoutMarkClearingDisconnectListener;
import arriba.fix.session.disconnect.SessionDisconnectListener;
import arriba.fix.session.disconnect.SessionDisconnector;
import arriba.fix.session.disconnect.SessionUnmonitoringDisconnectListener;
import arriba.fix.session.messagejournal.InMemoryMessageJournal;
import arriba.fix.session.messagejournal.MessageJournal;
import arriba.fix.tagindexresolvers.CanonicalTagIndexResolverRepository;
import arriba.transport.TransportIdentity;
import arriba.transport.TransportRepository;
import arriba.transport.TransportSender;
import cern.colt.map.OpenIntObjectHashMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.Map;
import java.util.Set;

public final class ArribaWizard<T> {

    private final TransportRepository<SessionId, T> transportRepository;
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
    private final DisconnectingSessionIdHandler<T> disconnectingSessionIdHandler;
    private final SessionNotifyingEventHandler sessionNotifyingEventHandler;
    private final Sender<OutboundFixMessage> outboundSender;
    private final Sender<byte[]> outboundBytesSender;
    private final TransportSender<T, ChannelBuffer[]> inboundSender;
    private final SessionDisconnector sessionDisconnector;
    private final SessionMonitor sessionMonitor;
    private final ArribaWizardType type;

    private final Disruptor<InboundEvent> inboundDisruptor;
    private final Disruptor<OutboundEvent> outboundDisruptor;

    public ArribaWizard(final ArribaWizardType type, final DisruptorConfiguration inboundConfiguration, final DisruptorConfiguration outboundConfiguration,
            final TransportRepository<SessionId, T> transportRepository) {
        this.type = type;
        this.transportRepository = transportRepository;

        this.disconnectingSessionIdHandler = new DisconnectingSessionIdHandler<>(
                this.transportRepository,
                Sets.<SessionDisconnectListener>newHashSet(new LogoutMarkClearingDisconnectListener(this.logoutTracker))
                );
        this.sessionNotifyingEventHandler = new SessionNotifyingEventHandler(this.sessionResolver);

        this.inboundDisruptor = this.inboundDisruptor(inboundConfiguration);
        this.inboundSender = new DisruptorTransportSender<>(
                inboundDisruptor.getRingBuffer(),
                new InboundDisruptorAdapter<T>()
                );

        this.outboundDisruptor = this.outboundDisruptor(outboundConfiguration);
        this.outboundSender = new DisruptorSender<>(
                outboundDisruptor.getRingBuffer(),
                new OutboundDisruptorAdapter()
                );
        this.outboundBytesSender = new DisruptorSender<>(
                outboundDisruptor.getRingBuffer(),
                new SerializedOutboundDisruptorAdapter()
                );
        this.sessionDisconnector = new DisruptorSessionDisconnector<>(
                outboundDisruptor.getRingBuffer(),
                new OutboundDisruptorAdapter()
                );

        this.sessionMonitor = new ScheduledSessionMonitor(
                this.outboundSender,
                this.createOutboundBuilder(),
                this.sessionDisconnector,
                this.sessionResolver,
                this.logoutTracker
                );

        this.disconnectingSessionIdHandler.addListener(new SessionUnmonitoringDisconnectListener(this.sessionMonitor));
        this.sessionNotifyingEventHandler.setSender(this.outboundSender);
    }

    // TODO Should not be exposed.
    public LogoutTracker getLogoutTracker() {
        return this.logoutTracker;
    }

    public SessionResolver getSessionResolver() {
        return this.sessionResolver;
    }

    public SessionDisconnector getSessionDisconnector() {
        return this.sessionDisconnector;
    }

    // TODO Find a way to avoid exposing SessionMonitor.
    public SessionMonitor getSessionMonitor() {
        return this.sessionMonitor;
    }

    public TransportSender<T, ChannelBuffer[]> getInboundSender() {
        return this.inboundSender;
    }

    public Sender<OutboundFixMessage> getOutboundSender() {
        return this.outboundSender;
    }

    public Sender<byte[]> getSerializedOutboundSender() {
        return this.outboundBytesSender;
    }

    public InboundFixMessageDeserializer getInboundDeserializer() {
        return new InboundFixMessageDeserializer(this.inboundFixMessageBuilder(), new RepeatingGroupBuilder(this.fixChunkBuilderSupplier));
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

    public void stop() {
        this.inboundDisruptor.shutdown();
        this.outboundDisruptor.shutdown();
    }

    @SuppressWarnings("unchecked")
    private Disruptor<OutboundEvent> outboundDisruptor(final DisruptorConfiguration configuration) {
        final Disruptor<OutboundEvent> outgoingDisruptor = new Disruptor<>(
                new OutboundEventFactory(),
                configuration.getExecutor(),
                configuration.getClaimStrategy(),
                configuration.getWaitStrategy()
                );

        outgoingDisruptor
        .handleEventsWith(this.transportDelegatingEventHandler())
        .then(new MessageJournalingEventHandler(this.sessionResolver));
        outgoingDisruptor.start();

        return outgoingDisruptor;
    }

    private EventHandler<OutboundEvent> transportDelegatingEventHandler() {
        return new TransportDelegatingEventHandler<T>(
                new TransportWritingFixMessageHandler<>(this.transportRepository, this.sessionResolver),
                this.disconnectingSessionIdHandler
                );
    }

    @SuppressWarnings("unchecked")
    private Disruptor<InboundEvent> inboundDisruptor(final DisruptorConfiguration configuration) {
        final Disruptor<InboundEvent> inboundDisruptor = new Disruptor<>(
                new InboundFactory(),
                configuration.getExecutor(),
                configuration.getClaimStrategy(),
                configuration.getWaitStrategy()
                );
        inboundDisruptor
        .handleEventsWith(this.loggingEventHandler())
        .then(this.deserializingEventHandler())
        .then(this.sequenceNumberValidatingEventHandler())
        .then(this.sessionNotifyingEventHandler);
        inboundDisruptor.start();

        return inboundDisruptor;
    }

    private EventHandler<InboundEvent> loggingEventHandler() {
        return new LoggingEventHandler();
    }

    private EventHandler<InboundEvent> deserializingEventHandler() {
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

    private EventHandler<InboundEvent> sequenceNumberValidatingEventHandler() {
        return new SequenceNumberValidatingEventHandler(this.sessionResolver, this.createOutboundBuilder(), this.transportRepository);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initializeSessions() {
        final HandlerRepository handlerRepository = new MapHandlerRepository(this.messageTypeToHandler);
        for (final SessionId sessionId : this.sessionIds) {
            this.sessionIdToSession.put(sessionId,
                    new Session(sessionId, handlerRepository, this.messageJournal()));
        }
    }

    private MessageJournal messageJournal() {
        return new InMemoryMessageJournal();
    }
}
