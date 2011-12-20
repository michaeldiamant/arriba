package arriba.configuration;


public final class SessionWizard {

    private final String senderCompId;
    private final ArribaWizard<?> arribaWizard;

    public SessionWizard(final String senderCompId, final ArribaWizard<?> arribaWizard) {
        this.senderCompId = senderCompId;
        this.arribaWizard = arribaWizard;
    }

    public ArribaWizard<?> with(final String... targetCompIds) {
        return this.arribaWizard.registerSessions(this.senderCompId, targetCompIds);
    }
}
