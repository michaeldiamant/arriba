package arriba.examples.handlers;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.fields.MessageType;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.OutboundFixMessageBuilder;
import arriba.transport.Transport;
import arriba.transport.TransportConnectHandler;
import arriba.transport.TransportRepository;

public class LogonOnConnectApplication<T> implements TransportConnectHandler<T> {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    private final AtomicInteger messageCount;
    private final String senderCompId;
    private final String targetCompId;
    private final String username;
    private final String password;
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final TransportRepository<String, T> transportRepository;

    private final OutboundFixMessageBuilder builder = new OutboundFixMessageBuilder();

    public LogonOnConnectApplication(final AtomicInteger messageCount,
            final String senderCompId, final String targetCompId, final String username, final String password,
            final Sender<OutboundFixMessage> fixMessageSender, final TransportRepository<String, T> transportRepository) {
        this.messageCount = messageCount;
        this.senderCompId = senderCompId;
        this.targetCompId = targetCompId;
        this.username = username;
        this.password = password;
        this.fixMessageSender = fixMessageSender;
        this.transportRepository = transportRepository;
    }

    @Override
    public void onConnect(final Transport<T> transport) {
        final SimpleDateFormat sdf = new SimpleDateFormat(SENDING_TIME_FORMAT);

        this.builder
        .addField(Tags.BEGIN_STRING, new String(BeginString.FIXT11))
        .addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.get()))
        .addField(Tags.MESSAGE_TYPE, MessageType.LOGON.getValue())
        .addField(Tags.SENDER_COMP_ID, this.senderCompId)
        .addField(Tags.TARGET_COMP_ID, this.targetCompId)
        .addField(Tags.SENDING_TIME, sdf.format(new Date()))

        .addField(Tags.USERNAME, this.username)
        .addField(Tags.PASSWORD, this.password);

        try {
            this.transportRepository.add(this.targetCompId, transport);

            this.fixMessageSender.send(this.builder.build());
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
