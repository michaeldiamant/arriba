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
        final byte[] undelimitedTagBytes = Integer.toString(this.tag).getBytes();
        final byte[] expectedTagBytes = new byte[1 + undelimitedTagBytes.length];
        expectedTagBytes[0] = Fields.DELIMITER;
        System.arraycopy(undelimitedTagBytes, 0, expectedTagBytes, 1, undelimitedTagBytes.length);

        final byte[] actualTagBytes = Tags.toDelimitedByteArray(this.tag);

        assertThat(actualTagBytes, is(expectedTagBytes));
    }
}
