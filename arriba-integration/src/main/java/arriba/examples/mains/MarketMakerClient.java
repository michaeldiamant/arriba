package arriba.examples.mains;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import arriba.common.ComposedHandler;
import arriba.common.Handler;
import arriba.configuration.ArribaWizardType;
import arriba.fix.inbound.handlers.*;
import arriba.fix.inbound.messages.Logon;
import arriba.fix.session.SessionId;
import arriba.transport.TransportSender;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import arriba.common.PrintingHandler;
import arriba.common.Sender;
import arriba.configuration.ArribaWizard;
import arriba.configuration.DisruptorConfiguration;
import arriba.examples.handlers.SubscriptionManagingMarketDataRequestHandler;
import arriba.examples.quotes.RandomQuoteSupplier;
import arriba.examples.subscriptions.InMemorySubscriptionService;
import arriba.examples.subscriptions.SubscriptionService;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.messages.NewOrderSingle;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.transport.InMemoryTransportRepository;
import arriba.transport.TransportRepository;
import arriba.transport.netty.FixMessageFrameDecoder;
import arriba.transport.netty.NettyTransportFactory;
import arriba.transport.netty.NettyTransportRepository;
import arriba.transport.netty.GroupAddingChannelHandler;
import arriba.transport.netty.SerializedFixMessageHandler;
import arriba.transport.netty.bootstraps.FixServerBootstrap;

import com.google.common.collect.Sets;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.MultiThreadedClaimStrategy;

public class MarketMakerClient {

    private final String senderCompId = "MM";
    private final String targetCompId = "MT";
    private final String expectedUsername = "tr8der";
    private final String expectedPassword = "liquidity";
    private final ScheduledExecutorService quotesExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final SubscriptionService subscriptionService = new InMemorySubscriptionService();

    public MarketMakerClient() {
    }

    public void start() {
        final DisruptorConfiguration inboundConfiguration = new DisruptorConfiguration(
                Executors.newCachedThreadPool(),
                new MultiThreadedClaimStrategy(256),
                new BlockingWaitStrategy()
        );
        final DisruptorConfiguration outboundConfiguration = new DisruptorConfiguration(
                Executors.newCachedThreadPool(),
                new MultiThreadedClaimStrategy(256),
                new BlockingWaitStrategy()
        );

        final TransportRepository<SessionId, Channel> backingRepository = new InMemoryTransportRepository<>(new NettyTransportFactory());
        final TransportRepository<SessionId, Channel> repository = new NettyTransportRepository<>(backingRepository);

        final ArribaWizard<Channel> wizard = new ArribaWizard<>(
                ArribaWizardType.ACCEPTOR,
                inboundConfiguration,
                outboundConfiguration,
                repository
        );

        final TransportSender<Channel, ChannelBuffer[]> inboundSender = wizard.getInboundSender();
        final Sender<OutboundFixMessage> outboundSender = wizard.getOutboundSender();

        final Handler<Logon> logonHandler = new ComposedHandler<>(
                new AuthenticatingLogonHandler(this.expectedUsername, this.expectedPassword, outboundSender, wizard.createOutboundBuilder()),
                new SessionMonitoringLogonHandler(wizard.getSessionMonitor())
        );

        wizard
                .registerMessageHandler(MessageType.HEARTBEAT, new NoOpHeartbeatHandler())
                .registerMessageHandler(MessageType.TEST_REQUEST, new HeartbeatGeneratingTestRequestHandler(wizard.createOutboundBuilder(), outboundSender))
                .registerMessageHandler(MessageType.RESEND_REQUEST, new MessageResendingResendRequestHandler(wizard.getSessionResolver(), wizard.getSerializedOutboundSender(), wizard.createOutboundBuilder(), wizard.getInboundDeserializer()))
                .registerMessageHandler(MessageType.LOGON, logonHandler)
                .registerMessageHandler(MessageType.LOGOUT, new DisconnectingLogoutHandler(outboundSender, wizard.createOutboundBuilder(), wizard.getSessionDisconnector(), wizard.getLogoutTracker()))
                .registerMessageHandler(MessageType.MARKET_DATA_REQUEST, new SubscriptionManagingMarketDataRequestHandler(this.subscriptionService))
                .registerMessageHandler(MessageType.NEW_ORDER_SINGLE, new PrintingHandler<NewOrderSingle>())

                .register(this.senderCompId).with(this.targetCompId)

                .start();

        this.initializeQuotes(outboundSender, wizard.createOutboundBuilder());

        final ServerBootstrap server = FixServerBootstrap.create(
                new FixMessageFrameDecoder(),
                new GroupAddingChannelHandler(null),
                new SerializedFixMessageHandler(inboundSender)
        );

        server.bind(new InetSocketAddress("localhost", 8080));
    }

    private void initializeQuotes(final Sender<OutboundFixMessage> sender, final RichOutboundFixMessageBuilder builder) {
        final Runnable quoteSupplier = new RandomQuoteSupplier(this.subscriptionService, Sets.newHashSet("EURUSD"),
                this.senderCompId, sender, builder);
        this.quotesExecutorService.scheduleWithFixedDelay(quoteSupplier, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public static void main(final String[] args) {
        new MarketMakerClient().start();
    }
}
