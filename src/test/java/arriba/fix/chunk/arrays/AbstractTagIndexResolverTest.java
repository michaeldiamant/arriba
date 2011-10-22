package arriba.fix.chunk.arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import arriba.fix.Tags;

public abstract class AbstractTagIndexResolverTest {

    public abstract TagIndexResolver getTagIndexResolver();

    public abstract int[] getAllTags();

    @Test
    public void testGetTagCount() {
        assertThat(this.getTagIndexResolver().getTagCount(), is(this.getAllTags().length));
    }

    @Test
    public void testIsDefinedForWithDefinedTag() {
        assertThat(this.getTagIndexResolver().isDefinedFor(Tags.BEGIN_STRING), is(true));
    }

    @Test
    public void testIsDefinedForWithUndefinedTag() {
        assertThat(this.getTagIndexResolver().isDefinedFor(Tags.SYMBOL), is(false));
    }

    @Test
    public void testGetTagIndex() {
        for (final int tag : this.getAllTags()) {
            assertThat(this.getTagIndexResolver().getTagIndex(tag), is(not(TagIndexResolver.INVALID_TAG_INDEX)));
        }
    }
}
