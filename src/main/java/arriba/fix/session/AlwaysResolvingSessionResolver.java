package arriba.fix.session;

import java.util.HashMap;
import java.util.Map;

import arriba.common.Handler;
import arriba.common.MapHandlerRepository;
import arriba.common.PrintingHandler;
import arriba.fix.inbound.InboundFixMessage;

public final class AlwaysResolvingSessionResolver implements SessionResolver {

    private final Session session;

    public AlwaysResolvingSessionResolver() {
        final Map<String, Handler<InboundFixMessage>> messageTypeToHandler = new HashMap<String, Handler<InboundFixMessage>>();
        messageTypeToHandler.put("D", new PrintingHandler<InboundFixMessage>());

        this.session = new Session(new SimpleSessionId("targetCompId"),
                new MapHandlerRepository<String, InboundFixMessage>(messageTypeToHandler));
    }

    public Session resolve(final SessionId sessionId) throws UnknownSessionIdException {
        return this.session;
    }

}
