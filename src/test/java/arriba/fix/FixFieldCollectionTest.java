package arriba.fix;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import arriba.fix.FixFieldCollection.Builder;

import com.google.common.collect.Lists;

public class FixFieldCollectionTest {

    private FixFieldCollection fixFieldCollection;
    private List<Field<String>> fields;

    @Before
    public void before() {
        final Builder fixFieldCollectionBuilder = new FixFieldCollection.Builder();
        this.fields = this.buildFieldList();

        for (final Field<String> field : this.fields) {
            fixFieldCollectionBuilder.addField(field.getTag(), field.getValue());
        }

        this.fixFieldCollection = fixFieldCollectionBuilder.build();
    }

    @Test
    public void testGetValue() {
        for (final Field<String> field : this.fields) {
            assertThat(this.fixFieldCollection.getValue(field.getTag()), is(field.getValue()));
        }
    }

    @Test
    public void testToByteArray() throws IOException {
        final byte[] actualMessageBytes = this.fixFieldCollection.toByteArray();
        final byte[] expectedMessageBytes = this.buildMessageBytes();

        assertThat(actualMessageBytes, is(expectedMessageBytes));
    }

    private byte[] buildMessageBytes() throws IOException {
        Collections.sort(this.fields);
        final ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();

        for (final Field<String> field : this.fields) {
            final byte[] tagBytes = Tags.toByteArray(field.getTag());
            final byte[] valueBytes = field.getValue().getBytes();

            messageBytes.write(tagBytes);
            messageBytes.write(Fields.EQUAL_SIGN);
            messageBytes.write(valueBytes);
        }

        messageBytes.write(Fields.DELIMITER);

        return messageBytes.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private List<Field<String>> buildFieldList() {
        return Lists.newArrayList(
                new Field<String>(52, "test"),
                new Field<String>(10, "4"),
                new Field<String>(44, "abc"),
                new Field<String>(35, "D"),
                new Field<String>(176, "Rejected"),
                new Field<String>(7, "15")
        );
    }
}
