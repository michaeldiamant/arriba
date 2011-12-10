package arriba.disruptor.outbound;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import arriba.fix.outbound.OutboundFixMessage;

public class OutboundDisruptorAdapterTest {

    @Test
    public void testAdapt() {
        final OutboundDisruptorAdapter adapter = new OutboundDisruptorAdapter();
        final OutboundEvent event = new OutboundEvent();
        final OutboundFixMessage message = new OutboundFixMessage(null, null, 0, "", "", "");

        adapter.adapt(message, event);

        assertThat(event.getFixMessage(), is(message));
    }
}
