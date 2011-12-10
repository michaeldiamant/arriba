package arriba.fix.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.fields.MessageType;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;

public final class ScheduledSessionMonitor implements SessionMonitor {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final RichOutboundFixMessageBuilder builder;
    private final Map<Session, ScheduledFuture<?>> sessionToMonitorFuture = new HashMap<>();
    private final Lock sessionToMonitorFutureLock = new ReentrantLock();
    private final SessionDisconnector disconnector;
    private final SessionResolver resolver;
    private final LogoutTracker tracker;

    public ScheduledSessionMonitor(final Sender<OutboundFixMessage> fixMessageSender,
            final RichOutboundFixMessageBuilder builder,
            final SessionDisconnector disconnector,
            final SessionResolver resolver,
            final LogoutTracker tracker) {
        this.fixMessageSender = fixMessageSender;
        this.builder = builder;
        this.disconnector = disconnector;
        this.resolver = resolver;
        this.tracker = tracker;
    }

    @Override
    public void unmonitor(final SessionId sessionId) {
        final Session session = this.resolver.resolve(sessionId);
        this.sessionToMonitorFutureLock.lock();
        try {
            this.sessionToMonitorFuture.remove(session);
        } finally {
            this.sessionToMonitorFutureLock.unlock();
        }
    }

    @Override
    public void monitor(final SessionId sessionId, final long heartbeatIntervalInMs) {
        final Session session = this.resolver.resolve(sessionId);
        if (null == session) {
            return;
        }

        this.sessionToMonitorFutureLock.lock();
        try {
            if (!this.sessionToMonitorFuture.containsKey(session)) {
                final Runnable monitorRunnable = new Runnable() {

                    private boolean isHeartbeatRequired() {
                        return System.currentTimeMillis() - session.getLastSentTimestamp() >= heartbeatIntervalInMs;
                    }

                    private boolean isTestRequestRequired() {
                        return System.currentTimeMillis() - session.getLastReceivedTimestamp() >= heartbeatIntervalInMs;
                    }

                    private boolean isLogoutRequired() {
                        // No messages received in 2+ heartbeat intervals indicates that the session did not respond to test request.
                        return System.currentTimeMillis() - session.getLastReceivedTimestamp() >= 2 * heartbeatIntervalInMs;
                    }

                    private boolean hasTimedoutWaitingForLogoutResponse() {
                        // No messages received in 3+ hearbeat intervals indicates that the session did not respond to logout request.
                        return System.currentTimeMillis() - session.getLastReceivedTimestamp() >= 3 * heartbeatIntervalInMs;
                    }

                    @Override
                    public void run() {
                        if (this.hasTimedoutWaitingForLogoutResponse()) {
                            ScheduledSessionMonitor.this.disconnector.disconnect(new SessionId(session.getSenderCompId(), session.getTargetCompId()));
                        } else if (this.isLogoutRequired()) {
                            this.sendLogout();
                        } else if (this.isTestRequestRequired()) {
                            this.sendTestRequest();
                        } else if (this.isHeartbeatRequired()) {
                            this.sendHeartbeat();
                        }
                    }

                    private void sendLogout() {
                        final OutboundFixMessage logout = ScheduledSessionMonitor.this.builder
                                .addStandardHeader(MessageType.LOGOUT, BeginString.FIXT11.getValue(), session.getSenderCompId(), session.getTargetCompId())
                                .build();
                        ScheduledSessionMonitor.this.fixMessageSender.send(logout);
                        ScheduledSessionMonitor.this.tracker.markLogout(sessionId);
                    }

                    private void sendHeartbeat() {
                        final OutboundFixMessage heartbeat = ScheduledSessionMonitor.this.builder
                                .addStandardHeader(MessageType.HEARTBEAT, BeginString.FIXT11.getValue(), session.getSenderCompId(), session.getTargetCompId())
                                .build();
                        ScheduledSessionMonitor.this.fixMessageSender.send(heartbeat);
                    }

                    private void sendTestRequest() {
                        final OutboundFixMessage testRequest = ScheduledSessionMonitor.this.builder
                                .addStandardHeader(MessageType.TEST_REQUEST, BeginString.FIXT11.getValue(), session.getSenderCompId(), session.getTargetCompId())
                                .addField(Tags.TEST_REQUEST_ID, Long.toString(System.currentTimeMillis()))
                                .build();
                        ScheduledSessionMonitor.this.fixMessageSender.send(testRequest);
                    }
                };

                final ScheduledFuture<?> monitorFuture = this.executor.scheduleAtFixedRate(monitorRunnable, 0, heartbeatIntervalInMs, TimeUnit.MILLISECONDS);
                this.sessionToMonitorFuture.put(session, monitorFuture);
            }
        } finally {
            this.sessionToMonitorFutureLock.unlock();
        }
    }
}
