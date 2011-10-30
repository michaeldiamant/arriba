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
import arriba.fix.fields.BeginString;
import arriba.fix.fields.MessageType;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.OutboundFixMessageBuilder;
import arriba.transport.channels.ChannelRepository;

public class LogonOnConnectHandler extends SimpleChannelHandler {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    private final AtomicInteger messageCount;
    private final String senderCompId;
    private final String targetCompId;
    private final String username;
    private final String password;
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final ChannelRepository<String> channelRepository;

    private final OutboundFixMessageBuilder builder = new OutboundFixMessageBuilder();

    public LogonOnConnectHandler(final AtomicInteger messageCount,
            final String senderCompId, final String targetCompId, final String username, final String password,
            final Sender<OutboundFixMessage> fixMessageSender, final ChannelRepository<String> channelRepository) {
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

        this.builder
        .addField(Tags.BEGIN_STRING, new String(BeginString.FIXT11))
        .addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.get()))
        .addField(Tags.MESSAGE_TYPE, new String(MessageType.LOGON))
        .addField(Tags.SENDER_COMP_ID, this.senderCompId)
        .addField(Tags.TARGET_COMP_ID, this.targetCompId)
        .addField(Tags.SENDING_TIME, sdf.format(new Date()))

        .addField(Tags.USERNAME, this.username)
        .addField(Tags.PASSWORD, this.password);

        try {
            this.channelRepository.add(this.targetCompId, e.getChannel());

            this.fixMessageSender.send(this.builder.build());
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
