package arriba.disruptor.outbound;

import arriba.fix.outbound.DateSupplier;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SessionResolver;
import arriba.transport.Transport;
import arriba.transport.TransportRepository;

import com.google.common.base.Function;

public final class TransportWritingFixMessageFunction<T> implements Function<OutboundFixMessage, byte[]> {

    private final TransportRepository<String, T> transportRepository;
    private final SessionResolver sessionResolver;

    public TransportWritingFixMessageFunction(final TransportRepository<String, T> transportRepository,
            final SessionResolver sessionResolver) {
        this.transportRepository = transportRepository;
        this.sessionResolver = sessionResolver;
    }

    @Override
    public byte[] apply(final OutboundFixMessage message) {
        final Transport<T> transport = this.transportRepository.find(message.getTargetCompId());
        if (null == transport) {
            throw new IllegalArgumentException("Cannot find transport for target comp ID " + message.getTargetCompId() + ".");
        }

        // TODO Can SessionId be cached?
        final Session session = this.sessionResolver.resolve(new SessionId(message.getSenderCompId(), message.getTargetCompId()));
        if (null == session) {
            throw new IllegalArgumentException("Cannot find session for sender comp ID " + message.getSenderCompId() +
                    " and target comp ID " + message.getTargetCompId() + ".");
        }

        final byte[] messageBytes = message.toBytes(session.getNextSequenceNumber(), DateSupplier.getUtcTimestamp());
        transport.write(messageBytes);

        session.updateLastSentTimestamp();

        return messageBytes;
    }
}
