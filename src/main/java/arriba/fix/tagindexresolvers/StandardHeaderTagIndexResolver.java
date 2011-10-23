package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public final class StandardHeaderTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.BEGIN_STRING,
                Tags.BODY_LENGTH,
                Tags.MESSAGE_SEQUENCE_NUMBER,
                Tags.MESSAGE_TYPE,
                Tags.SENDER_COMP_ID,
                Tags.SENDING_TIME,
                Tags.TARGET_COMP_ID
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[0];
    }
}
