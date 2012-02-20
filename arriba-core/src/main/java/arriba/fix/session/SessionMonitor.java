package arriba.fix.session;

public interface SessionMonitor {

    void unmonitor(final SessionId sessionId);

    void monitor(final SessionId sessionId, final long heartbeatIntervalInMs);

    void shutdown();
}