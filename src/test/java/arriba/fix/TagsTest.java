package arriba.fix;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TagsTest {

    @Test
    public void testToByteArray() {
        final int tag = 100;

        final byte[] undelimitedTagBytes = Integer.toString(tag).getBytes();
        final byte[] expectedTagBytes = new byte[1 + undelimitedTagBytes.length];
        expectedTagBytes[0] = Fields.DELIMITER;
        System.arraycopy(undelimitedTagBytes, 0, expectedTagBytes, 1, undelimitedTagBytes.length);

        final byte[] actualTagBytes = Tags.toByteArray(tag);

        assertThat(actualTagBytes, is(expectedTagBytes));
    }
}
