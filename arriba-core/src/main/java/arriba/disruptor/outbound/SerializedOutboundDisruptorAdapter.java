package arriba.disruptor.outbound;

import arriba.disruptor.MessageToDisruptorAdapter;

public class SerializedOutboundDisruptorAdapter implements MessageToDisruptorAdapter<byte[], OutboundEvent> {

    @Override
    public void adapt(final byte[] message, final OutboundEvent event) {
        event.setFixMessage(null);
        event.setSessionId(null);
        event.setSerializedFixMessage(message);
        event.setResend(true);
    }
}
