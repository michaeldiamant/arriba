package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public class LogoutTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[0];
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[] {
                Tags.TEXT
        };
    }
}
