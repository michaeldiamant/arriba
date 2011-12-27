package arriba.disruptor.inbound;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;

public final class LoggingEventHandler implements EventHandler<InboundEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEventHandler.class);

    @Override
    public void onEvent(final InboundEvent event, final boolean endOfBatch) throws Exception {
        // Naive implementation that does not batch updates.

        for (final ChannelBuffer message : event.getSerializedMessages()) {
            LOGGER.info("{}", new String(message.array()));
        }
    }
}
