package arriba.fix.tagindexresolvers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import arriba.fix.Tags;
import arriba.fix.tagindexresolvers.TagIndexResolver;

public class TagIndexResolverTest {

    private static final int INVALID_TAG_INDEX = -1;

    private final int[] requiredTags = {
            Tags.BEGIN_STRING,
            Tags.BODY_LENGTH,
            Tags.CHECKSUM,
            Tags.CLIENT_ORDER_ID
    };

    private final int[] optionalTags = {
            Tags.MARKET_DEPTH,
            Tags.MD_ENTRY_PRICE,
            Tags.MD_ENTRY_TYPE
    };

    private final TagIndexResolver resolver = new TagIndexResolver() {

        @Override
        protected int[] getRequiredTags() {
            return TagIndexResolverTest.this.requiredTags;
        }

        @Override
        protected int[] getOptionalTags() {
            return TagIndexResolverTest.this.optionalTags;
        }
    };

    @Test
    public void testConstructionWithZeroRequiredTags() {
        final TagIndexResolver resolverWithAllOptionalTags = new TagIndexResolver() {

            @Override
            protected int[] getRequiredTags() {
                return new int[0];
            }

            @Override
            protected int[] getOptionalTags() {
                return TagIndexResolverTest.this.optionalTags;
            }
        };

        this.assertRequiredAndOptionalTagsAreSet(resolverWithAllOptionalTags);
    }

    @Test
    public void testConstructionWithZeroOptionalTags() {
        final TagIndexResolver resolverWithAllRequiredTags = new TagIndexResolver() {

            @Override
            protected int[] getRequiredTags() {
                return TagIndexResolverTest.this.requiredTags;
            }

            @Override
            protected int[] getOptionalTags() {
                return new int[0];
            }
        };

        this.assertRequiredAndOptionalTagsAreSet(resolverWithAllRequiredTags);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructionWithZeroTotalTags() {
        new TagIndexResolver() {

            @Override
            protected int[] getRequiredTags() {
                return new int[0];
            }

            @Override
            protected int[] getOptionalTags() {
                return new int[0];
            }
        };
    }

    @Test
    public void testGetTagCount() {
        final int expectedTagCount = this.requiredTags.length + this.optionalTags.length;
        assertThat(this.resolver.getTagCount(), is(expectedTagCount));
    }

    @Test
    public void testIsDefinedForWithRequiredTag() {
        assertThat(this.resolver.isDefinedFor(this.requiredTags[0]), is(true));
    }

    @Test
    public void testIsDefinedForWithOptionalTag() {
        assertThat(this.resolver.isDefinedFor(this.optionalTags[0]), is(true));
    }

    @Test
    public void testIsDefinedForWithUndefinedTagGreaterThanMaxTag() {
        assertThat(this.resolver.isDefinedFor(Integer.MAX_VALUE), is(false));
    }

    @Test
    public void testIsDefinedForWithUndefinedTagLessThanMaxTag() {
        assertThat(this.resolver.isDefinedFor(0), is(false));
    }

    @Test
    public void testGetTagIndex() {
        this.assertRequiredAndOptionalTagsAreSet(this.resolver);
    }

    private void assertRequiredAndOptionalTagsAreSet(final TagIndexResolver resolver) {
        for (final int tag : resolver.getRequiredTags()) {
            assertThat(resolver.getTagIndex(tag), is(not(INVALID_TAG_INDEX)));
        }

        for (final int tag : resolver.getOptionalTags()) {
            assertThat(resolver.getTagIndex(tag), is(not(INVALID_TAG_INDEX)));
        }
    }
}
