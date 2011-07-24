package arriba.fix.session;

public interface SessionId {

    String getBeginString();

    String getSenderCompId();

    String getSenderSubId();

    String getSenderLocationId();

    String getTargetCompId();

    String getTargetSubId();

    String getTargetLocationId();
}