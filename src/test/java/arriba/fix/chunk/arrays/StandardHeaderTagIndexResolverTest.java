package arriba.fix.chunk.arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import arriba.fix.Tags;
import arriba.fix.chunk.arrays.StandardHeaderTagIndexResolver;
import arriba.fix.chunk.arrays.TagIndexResolver;

public class StandardHeaderTagIndexResolverTest {

    final TagIndexResolver resolver = new StandardHeaderTagIndexResolver();

    @Test
    public void testGetTagCount() {
        assertThat(this.resolver.getTagCount(), is(Tags.getHeaders().length));
    }

    @Test
    public void testIsDefinedForWithDefinedTag() {
        assertThat(this.resolver.isDefinedFor(Tags.BEGIN_STRING), is(true));
    }

    @Test
    public void testIsDefinedForWithUndefinedTag() {
        assertThat(this.resolver.isDefinedFor(Tags.SYMBOL), is(false));
    }

    @Test
    public void testGetTagIndex() {
        for (final int tag : Tags.getHeaders()) {
            assertThat(this.resolver.getTagIndex(tag), is(not(TagIndexResolver.INVALID_TAG_INDEX)));
        }
    }
}
