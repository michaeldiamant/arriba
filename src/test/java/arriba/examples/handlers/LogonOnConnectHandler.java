package arriba.examples.handlers;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;
import arriba.fix.fields.BeginString;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.transport.channels.ChannelRepository;

public class LogonOnConnectHandler extends SimpleChannelHandler {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    private final InboundFixMessageBuilder inboundFixMessageBuilder =
            new InboundFixMessageBuilder(
                    new ArrayFixChunkBuilder(null),  // FIXME Replace null TagIndexResolver.
                    new ArrayFixChunkBuilder(null),
                    new ArrayFixChunkBuilder(null)
                    );
    private final AtomicInteger messageCount;
    private final String senderCompId;
    private final String targetCompId;
    private final String username;
    private final String password;
    private final Sender<InboundFixMessage> fixMessageSender;
    private final ChannelRepository<String> channelRepository;

    public LogonOnConnectHandler(final AtomicInteger messageCount,
            final String senderCompId, final String targetCompId, final String username, final String password,
            final Sender<InboundFixMessage> fixMessageSender, final ChannelRepository<String> channelRepository) {
        this.messageCount = messageCount;
        this.senderCompId = senderCompId;
        this.targetCompId = targetCompId;
        this.username = username;
        this.password = password;
        this.fixMessageSender = fixMessageSender;
        this.channelRepository = channelRepository;
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        final SimpleDateFormat sdf = new SimpleDateFormat(SENDING_TIME_FORMAT);

        this.inboundFixMessageBuilder.addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.get()));
        this.inboundFixMessageBuilder.setMessageType("A");
        this.inboundFixMessageBuilder.setBeginStringBytes(BeginString.FIXT11);
        this.inboundFixMessageBuilder.addField(Tags.SENDER_COMP_ID, this.senderCompId);
        this.inboundFixMessageBuilder.addField(Tags.TARGET_COMP_ID, this.targetCompId);
        this.inboundFixMessageBuilder.addField(Tags.SENDING_TIME, sdf.format(new Date()));

        this.inboundFixMessageBuilder.addField(Tags.USERNAME, this.username);
        this.inboundFixMessageBuilder.addField(Tags.PASSWORD, this.password);

        try {
            this.channelRepository.add(this.targetCompId, e.getChannel());

            this.fixMessageSender.send(this.inboundFixMessageBuilder.build());
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }

        this.inboundFixMessageBuilder.clear();
    }

}
