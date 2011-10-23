package arriba.fix.tagindexresolvers;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.List;

import org.junit.Test;

import arriba.fix.tagindexresolvers.TagIndexResolver;

import com.google.common.primitives.Ints;

public abstract class AbstractTagIndexResolverTest {

    private final TagIndexResolver resolver = this.getResolver();

    public abstract TagIndexResolver getResolver();

    public abstract int[] getExpectedRequiredTags();

    public abstract int[] getExpectedOptionalTags();

    @Test
    public void assertRequiredTagsAreProvided() {
        final List<Integer> requiredTags = Ints.asList(this.resolver.getRequiredTags());
        for (final int expectedRequiredTag : this.getExpectedRequiredTags()) {
            assertThat(requiredTags, hasItem(expectedRequiredTag));
        }
    }

    @Test
    public void assertOptionalTagsAreProvided() {
        final List<Integer> optionalTags = Ints.asList(this.resolver.getOptionalTags());
        for (final int expectedOptionalTag : this.getExpectedOptionalTags()) {
            assertThat(optionalTags, hasItem(expectedOptionalTag));
        }
    }
}
