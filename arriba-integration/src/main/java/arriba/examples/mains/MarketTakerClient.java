package arriba.examples.mains;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import arriba.common.ComposedHandler;
import arriba.common.Handler;
import arriba.configuration.ArribaWizardType;
import arriba.fix.inbound.handlers.*;
import arriba.fix.inbound.messages.Logon;
import arriba.fix.session.SessionId;
import arriba.transport.TransportSender;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import arriba.common.Sender;
import arriba.configuration.ArribaWizard;
import arriba.configuration.DisruptorConfiguration;
import arriba.examples.handlers.NewOrderGeneratingMarketDataHandler;
import arriba.examples.handlers.SubscriptionRequestingLogonHandler;
import arriba.fix.fields.MessageType;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.transport.InMemoryTransportRepository;
import arriba.transport.TransportRepository;
import arriba.transport.handlers.LogonOnConnectHandler;
import arriba.transport.netty.FixMessageFrameDecoder;
import arriba.transport.netty.NettyConnectHandlerAdapter;
import arriba.transport.netty.NettyTransportFactory;
import arriba.transport.netty.NettyTransportRepository;
import arriba.transport.netty.SerializedFixMessageHandler;
import arriba.transport.netty.bootstraps.FixClientBootstrap;

import com.google.common.collect.Sets;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.MultiThreadedClaimStrategy;

public class MarketTakerClient {

    private final String senderCompId = "MT";
    private final String targetCompId = "MM";
    private final int heartbeatIntervalInMs = 1000 * 30;
    private final String username = "tr8der";
    private final String password = "liquidity";

    public MarketTakerClient() {
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
                ArribaWizardType.INITIATOR,
                inboundConfiguration,
                outboundConfiguration,
                repository
        );

        final TransportSender<Channel, ChannelBuffer[]> inboundSender = wizard.getInboundSender();
        final Sender<OutboundFixMessage> outboundSender = wizard.getOutboundSender();

        final Handler<Logon> logonHandler = new ComposedHandler<>(
                new SessionMonitoringLogonHandler(wizard.getSessionMonitor()),
                new SubscriptionRequestingLogonHandler(Sets.newHashSet("EURUSD"), outboundSender, wizard.createOutboundBuilder())
        );

        wizard
                .registerMessageHandler(MessageType.HEARTBEAT, new NoOpHeartbeatHandler())
                .registerMessageHandler(MessageType.TEST_REQUEST, new HeartbeatGeneratingTestRequestHandler(wizard.createOutboundBuilder(), outboundSender))
                .registerMessageHandler(MessageType.RESEND_REQUEST, new MessageResendingResendRequestHandler(wizard.getSessionResolver(), wizard.getSerializedOutboundSender(), wizard.createOutboundBuilder(), wizard.getInboundDeserializer()))
                .registerMessageHandler(MessageType.LOGON, logonHandler)
                .registerMessageHandler(MessageType.LOGOUT, new DisconnectingLogoutHandler(outboundSender, wizard.createOutboundBuilder(), wizard.getSessionDisconnector(), wizard.getLogoutTracker()))
                .registerMessageHandler(MessageType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, new NewOrderGeneratingMarketDataHandler(outboundSender, wizard.createOutboundBuilder()))

                .register(this.senderCompId).with(this.targetCompId)

                .start();

        final ClientBootstrap client = FixClientBootstrap.create(
                new FixMessageFrameDecoder(),
                new NettyConnectHandlerAdapter(new LogonOnConnectHandler<>(this.senderCompId, this.targetCompId, this.heartbeatIntervalInMs, this.username, this.password, outboundSender, repository, wizard.createOutboundBuilder())),
                new SerializedFixMessageHandler(inboundSender)
        );

        client.connect(new InetSocketAddress("localhost", 8080));
    }

    public static void main(final String[] args) {
        new MarketTakerClient().start();
    }
}
