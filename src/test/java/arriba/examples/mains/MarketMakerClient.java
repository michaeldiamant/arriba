package arriba.examples.mains;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import arriba.common.PrintingHandler;
import arriba.common.Sender;
import arriba.configuration.ArribaWizard;
import arriba.configuration.DisruptorConfiguration;
import arriba.examples.handlers.AuthenticatingLogonHandler;
import arriba.examples.handlers.NewClientSessionHandler;
import arriba.examples.handlers.SubscriptionManagingMarketDataRequestHandler;
import arriba.examples.quotes.RandomQuoteSupplier;
import arriba.examples.subscriptions.InMemorySubscriptionService;
import arriba.examples.subscriptions.SubscriptionService;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.NewOrderSingle;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.transport.InMemoryTransportRepository;
import arriba.transport.TransportRepository;
import arriba.transport.netty.FixMessageFrameDecoder;
import arriba.transport.netty.NettyTransportRepository;
import arriba.transport.netty.SerializedFixMessageHandler;
import arriba.transport.netty.bootstraps.FixServerBootstrap;

import com.google.common.collect.Sets;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.WaitStrategy;

public class MarketMakerClient {

    private final AtomicInteger messageCount = new AtomicInteger();
    private final String senderCompId = "MM";
    private final String targetCompId = "MT";
    private final String expectedUsername = "tr8der";
    private final String expectedPassword = "liquidity";
    private final ExecutorService quotesExecutorService = Executors.newSingleThreadExecutor();
    private final SubscriptionService subscriptionService = new InMemorySubscriptionService();
    private final List<Channel> channels = new CopyOnWriteArrayList<Channel>();

    public MarketMakerClient() {}

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
        .registerMessageHandler(MessageType.LOGON, new AuthenticatingLogonHandler(this.expectedUsername, this.expectedPassword, outboundSender, this.messageCount, this.channels, repository))
        .registerMessageHandler(MessageType.MARKET_DATA_REQUEST, new SubscriptionManagingMarketDataRequestHandler(this.subscriptionService))
        .registerMessageHandler(MessageType.NEW_ORDER_SINGLE, new PrintingHandler<NewOrderSingle>())

        .registerTargetComponentIds(this.targetCompId);

        this.initializeQuotes(outboundSender);

        final ServerBootstrap server = FixServerBootstrap.create(
                new FixMessageFrameDecoder(),
                new NewClientSessionHandler(this.channels),
                new SerializedFixMessageHandler(inboundSender)
                );

        server.bind(new InetSocketAddress("localhost", 8080));
    }

    private void initializeQuotes(final Sender<OutboundFixMessage> outboundSender) {
        final Runnable quoteSupplier = new RandomQuoteSupplier(this.subscriptionService, Sets.newHashSet("EURUSD"),
                this.messageCount, this.senderCompId, outboundSender);
        this.quotesExecutorService.submit(quoteSupplier);
    }

    public static void main(final String[] args) {
        new MarketMakerClient().start();
    }
}
