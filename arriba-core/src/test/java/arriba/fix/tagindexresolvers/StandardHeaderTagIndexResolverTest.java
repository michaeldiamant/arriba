package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public class StandardHeaderTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getResolver() {
        return new StandardHeaderTagIndexResolver();
    }

    @Override
    public int[] getExpectedRequiredTags() {
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
    public int[] getExpectedOptionalTags() {
        return new int[] {
                Tags.GAP_FILL_FLAG
        };
    }
}
