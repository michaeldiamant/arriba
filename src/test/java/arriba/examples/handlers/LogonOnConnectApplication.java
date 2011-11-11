package arriba.examples.handlers;


import java.io.IOException;

import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.fields.MessageType;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.transport.Transport;
import arriba.transport.TransportConnectHandler;
import arriba.transport.TransportRepository;

public class LogonOnConnectApplication<T> implements TransportConnectHandler<T> {

    private final String senderCompId;
    private final String targetCompId;
    private final String username;
    private final String password;
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final TransportRepository<String, T> transportRepository;
    private final RichOutboundFixMessageBuilder builder;

    public LogonOnConnectApplication(final String senderCompId,
            final String targetCompId,
            final String username,
            final String password,
            final Sender<OutboundFixMessage> fixMessageSender,
            final TransportRepository<String, T> transportRepository,
            final RichOutboundFixMessageBuilder builder) {
        this.senderCompId = senderCompId;
        this.targetCompId = targetCompId;
        this.username = username;
        this.password = password;
        this.fixMessageSender = fixMessageSender;
        this.transportRepository = transportRepository;
        this.builder = builder;
    }

    @Override
    public void onConnect(final Transport<T> transport) {

        this.builder
        .addStandardHeader(MessageType.LOGON, BeginString.FIXT11.getValue(), this.senderCompId, this.targetCompId)

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
