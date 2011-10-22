package arriba.disruptor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import arriba.disruptor.outbound.OutboundFixMessageEvent;
import arriba.disruptor.outbound.OutboundFixMessageToDisruptorAdapter;
import arriba.fix.OutboundFixMessage;

public class OutboundFixMessageToDisruptorAdapterTest {

    @Test
    public void testAdapt() {
        final OutboundFixMessageToDisruptorAdapter adapter = new OutboundFixMessageToDisruptorAdapter();
        final OutboundFixMessageEvent event = new OutboundFixMessageEvent();
        final OutboundFixMessage message = new OutboundFixMessage(new byte[0], "targetCompId");

        adapter.adapt(message, event);

        assertThat(event.getMessage(), is(message));
    }
}
