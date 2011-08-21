package arriba.fix;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import arriba.fix.FixFieldCollection.Builder;
import arriba.fix.netty.util.FixMessages;

public class FixFieldCollectionTest {

    private FixFieldCollection fixFieldCollection;

    @Before
    public void before() {
        final Builder fixFieldCollectionBuilder = new FixFieldCollection.Builder();

        for (final Field<String> field : FixMessages.toFields(FixMessages.EXAMPLE_NEW_ORDER_SINGLE)) {
            fixFieldCollectionBuilder.addField(field.getTag(), field.getValue());
        }

        this.fixFieldCollection = fixFieldCollectionBuilder.build();
    }

    @Test
    public void testGetValue() {
        for (final Field<String> field : FixMessages.toFields(FixMessages.EXAMPLE_NEW_ORDER_SINGLE)) {
            assertThat(this.fixFieldCollection.getValue(field.getTag()), is(field.getValue()));
        }
    }

    @Ignore
    @Test
    public void testToByteArray() throws IOException {
        final byte[] actualMessageBytes = this.fixFieldCollection.toByteArray();
        final byte[] expectedMessageBytes = FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE).array();

        assertThat(actualMessageBytes, is(expectedMessageBytes));
    }
}
