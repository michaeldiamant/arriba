package arriba.examples.handlers;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.TestRequest;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;

public final class HeartbeatGeneratingTestRequestHandler implements Handler<TestRequest> {

    private final RichOutboundFixMessageBuilder builder;
    private final Sender<OutboundFixMessage> sender;

    public HeartbeatGeneratingTestRequestHandler(final RichOutboundFixMessageBuilder builder, final Sender<OutboundFixMessage> sender) {
        this.builder = builder;
        this.sender = sender;
    }

    @Override
    public void handle(final TestRequest message) {
        final OutboundFixMessage heartbeat = this.builder
                .addStandardHeader(MessageType.HEARTBEAT, message)
                .addField(Tags.TEST_REQUEST_ID, message.getTestRequestId())
                .build();

        this.sender.send(heartbeat);
    }
}
