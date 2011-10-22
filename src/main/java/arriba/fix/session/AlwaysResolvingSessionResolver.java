package arriba.fix.session;

import java.util.HashMap;
import java.util.Map;

import arriba.common.Handler;
import arriba.common.MapHandlerRepository;
import arriba.common.PrintingHandler;
import arriba.fix.inbound.FixMessage;

public final class AlwaysResolvingSessionResolver implements SessionResolver {

    private final Session session;

    public AlwaysResolvingSessionResolver() {
        final Map<String, Handler<FixMessage>> messageTypeToHandler = new HashMap<String, Handler<FixMessage>>();
        messageTypeToHandler.put("D", new PrintingHandler<FixMessage>());

        this.session = new Session(new SimpleSessionId("targetCompId"),
                new MapHandlerRepository<String, FixMessage>(messageTypeToHandler));
    }

    public Session resolve(final SessionId sessionId) throws UnknownSessionIdException {
        return this.session;
    }

}
