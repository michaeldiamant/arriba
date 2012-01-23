package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public final class LogonTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.ENCRYPT_METHOD,
                Tags.HEARTBEAT_INTERVAL
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[] {
                Tags.USERNAME,
                Tags.PASSWORD,
                Tags.RESET_SEQUENCE_NUMBER_FLAG
        };
    }
}
