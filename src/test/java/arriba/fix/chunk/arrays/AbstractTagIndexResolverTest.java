package arriba.fix.chunk.arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public abstract class AbstractTagIndexResolverTest {

    public abstract TagIndexResolver getTagIndexResolver();

    public abstract int[] getAllTags();

    private final TagIndexResolver resolver = this.getTagIndexResolver();
    private final int[] allTags = this.getAllTags();

    @Test
    public void testGetTagCount() {
        assertThat(this.resolver.getTagCount(), is(this.allTags.length));
    }

    @Test
    public void testIsDefinedForWithDefinedTag() {
        final int lastDefinedTag = this.allTags[this.allTags.length - 1];
        assertThat(this.resolver.isDefinedFor(lastDefinedTag), is(true));
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
        for (final int tag : this.allTags) {
            assertThat(this.resolver.getTagIndex(tag), is(not(TagIndexResolver.INVALID_TAG_INDEX)));
        }
    }

    @Test
    public void testGetMaxTag() {
        int expectedMaxTag = 0;
        for (final int tag : this.allTags) {
            if (tag > expectedMaxTag) {
                expectedMaxTag = tag;
            }
        }

        assertThat(this.resolver.getMaxTag(), is(expectedMaxTag));
    }
}
