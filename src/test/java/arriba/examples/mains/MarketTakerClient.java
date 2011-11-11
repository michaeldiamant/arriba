package arriba.examples.mains;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import arriba.common.Sender;
import arriba.configuration.ArribaWizard;
import arriba.configuration.DisruptorConfiguration;
import arriba.examples.handlers.LogonOnConnectApplication;
import arriba.examples.handlers.NewOrderGeneratingMarketDataHandler;
import arriba.examples.handlers.SubscriptionRequestingLogonHandler;
import arriba.fix.fields.MessageType;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.transport.InMemoryTransportRepository;
import arriba.transport.TransportRepository;
import arriba.transport.netty.FixMessageFrameDecoder;
import arriba.transport.netty.NettyConnectHandlerAdapter;
import arriba.transport.netty.NettyTransportRepository;
import arriba.transport.netty.SerializedFixMessageHandler;
import arriba.transport.netty.bootstraps.FixClientBootstrap;

import com.google.common.collect.Sets;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.WaitStrategy;

public class MarketTakerClient {

    private final AtomicInteger messageCount = new AtomicInteger();
    private final String senderCompId = "MT";
    private final String targetCompId = "MM";
    private final String username = "tr8der";
    private final String password = "liquidity";

    public MarketTakerClient() {}

    public void start() {
        final DisruptorConfiguration configuration = new DisruptorConfiguration(
                512,
                Executors.newCachedThreadPool(),
                ClaimStrategy.Option.SINGLE_THREADED,
                WaitStrategy.Option.YIELDING
                );
        final TransportRepository<String, Channel> repository = new NettyTransportRepository<>(new InMemoryTransportRepository<String, Channel>());

        final ArribaWizard<Channel> wizard = new ArribaWizard<>(
                configuration,
                repository
                );

        final Sender<ChannelBuffer> inboundSender = wizard.getInboundSender();
        final Sender<OutboundFixMessage> outboundSender = wizard.getOutboundSender();

        wizard
        .registerMessageHandler(MessageType.LOGON, new SubscriptionRequestingLogonHandler(Sets.newHashSet("EURUSD"), outboundSender, wizard.createOutboundBuilder()))
        .registerMessageHandler(MessageType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, new NewOrderGeneratingMarketDataHandler(outboundSender, wizard.createOutboundBuilder()))

        .register(this.senderCompId).with(this.targetCompId);

        final ClientBootstrap client = FixClientBootstrap.create(
                new FixMessageFrameDecoder(),
                new NettyConnectHandlerAdapter(new LogonOnConnectApplication<Channel>(this.senderCompId, this.targetCompId, this.username, this.password, outboundSender, repository, wizard.createOutboundBuilder())),
                new SerializedFixMessageHandler(inboundSender)
                );

        client.connect(new InetSocketAddress("localhost", 8080));
    }

    public static void main(final String[] args) {
        new MarketTakerClient().start();
    }
}
