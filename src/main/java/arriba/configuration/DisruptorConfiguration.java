package arriba.configuration;

import java.util.concurrent.Executor;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.ClaimStrategy.Option;
import com.lmax.disruptor.WaitStrategy;

public final class DisruptorConfiguration {

    private final int ringBufferSize;
    private final Executor executor;
    private final ClaimStrategy.Option claimStrategy;
    private final WaitStrategy.Option waitStrategy;

    public DisruptorConfiguration(final int ringBufferSize, final Executor executor, final Option claimStrategy,
            final WaitStrategy.Option waitStrategy) {
        this.ringBufferSize = ringBufferSize;
        this.executor = executor;
        this.claimStrategy = claimStrategy;
        this.waitStrategy = waitStrategy;
    }

    public int getRingBufferSize() {
        return this.ringBufferSize;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public ClaimStrategy.Option getClaimStrategy() {
        return this.claimStrategy;
    }


    public WaitStrategy.Option getWaitStrategy() {
        return this.waitStrategy;
    }
}
