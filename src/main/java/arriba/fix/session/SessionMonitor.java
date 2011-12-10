package arriba.fix.session;

public interface SessionMonitor {

    void unmonitor(final Session session);

    void monitor(final Session session, final long heartbeatIntervalInMs);
}