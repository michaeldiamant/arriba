package arriba.examples.handlers;


import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.fields.EncryptMethod;
import arriba.fix.fields.MessageType;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.fix.session.SessionId;
import arriba.fix.session.SessionMonitor;
import arriba.transport.TransportConnectHandler;
import arriba.transport.TransportIdentity;
import arriba.transport.TransportRepository;

public class LogonOnConnectApplication<T> implements TransportConnectHandler<T> {

    private final String senderCompId;
    private final String targetCompId;
    private final int heartbeatIntervalInMs;
    private final String username;
    private final String password;
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final TransportRepository<String, T> transportRepository;
    private final RichOutboundFixMessageBuilder builder;
    private final SessionMonitor sessionMonitor;

    public LogonOnConnectApplication(final String senderCompId,
            final String targetCompId,
            final int heartbeatIntervalInMs,
            final String username,
            final String password,
            final Sender<OutboundFixMessage> fixMessageSender,
            final TransportRepository<String, T> transportRepository,
            final RichOutboundFixMessageBuilder builder,
            final SessionMonitor sessionMonitor) {
        this.senderCompId = senderCompId;
        this.targetCompId = targetCompId;
        this.heartbeatIntervalInMs = heartbeatIntervalInMs;
        this.username = username;
        this.password = password;
        this.fixMessageSender = fixMessageSender;
        this.transportRepository = transportRepository;
        this.builder = builder;
        this.sessionMonitor = sessionMonitor;
    }

    @Override
    public void onConnect(final TransportIdentity<T> identity) {

        this.builder
        .addStandardHeader(MessageType.LOGON, BeginString.FIXT11.getValue(), this.senderCompId, this.targetCompId)

        .addField(Tags.ENCRYPT_METHOD, EncryptMethod.NONE.getValue())
        .addField(Tags.HEARTBEAT_INTERVAL, Integer.toString(this.heartbeatIntervalInMs))
        .addField(Tags.USERNAME, this.username)
        .addField(Tags.PASSWORD, this.password);

        this.transportRepository.add(this.targetCompId, identity);

        this.fixMessageSender.send(this.builder.build());

        this.sessionMonitor.monitor(
                new SessionId(this.senderCompId, this.targetCompId),
                this.heartbeatIntervalInMs
                );
    }
}
