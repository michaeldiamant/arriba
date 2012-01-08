package arriba.configuration;

import java.util.concurrent.Executor;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.WaitStrategy;

public final class DisruptorConfiguration {

    private final Executor executor;
    private final ClaimStrategy claimStrategy;
    private final WaitStrategy waitStrategy;

    public DisruptorConfiguration(final Executor executor, final ClaimStrategy claimStrategy,
            final WaitStrategy waitStrategy) {
        this.executor = executor;
        this.claimStrategy = claimStrategy;
        this.waitStrategy = waitStrategy;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public ClaimStrategy getClaimStrategy() {
        return this.claimStrategy;
    }


    public WaitStrategy getWaitStrategy() {
        return this.waitStrategy;
    }
}
