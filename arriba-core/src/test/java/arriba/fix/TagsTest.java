package arriba.fix;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TagsTest {

    private final int tag = 100;

    @Test
    public void testToByteArray() {
        final byte[] expectedTagBytes = Integer.toString(this.tag).getBytes();
        final byte[] actualTagBytes = Tags.toByteArray(this.tag);

        assertThat(actualTagBytes, is(expectedTagBytes));
    }

    @Test
    public void testToDelimitedByteArray() {
        final byte[] tagBytesSansEqualSign = Integer.toString(this.tag).getBytes();
        final byte[] expectedTagBytes = new byte[tagBytesSansEqualSign.length + 1];
        System.arraycopy(tagBytesSansEqualSign, 0, expectedTagBytes, 0, tagBytesSansEqualSign.length);
        expectedTagBytes[tagBytesSansEqualSign.length] = Fields.EQUAL_SIGN;

        final byte[] actualTagBytes = Tags.toDelimitedByteArray(this.tag);

        assertThat(actualTagBytes, is(expectedTagBytes));
    }
}
