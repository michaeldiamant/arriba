package arriba.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import arriba.fix.Field;
import arriba.fix.inbound.InboundFixMessage;

import com.google.common.collect.Lists;

public final class FieldCapturer {

    private final List<Field<String>> fields = Lists.newArrayList();

    public void capture(final int tag, final String value) {
        this.fields.add(new Field<String>(tag, value));
    }

    public void reset() {
        this.fields.clear();
    }

    public void assertFieldsAreSet(final InboundFixMessage message) {
        for (final Field<String> field : this.fields) {
            assertThat(message.getValue(field.getTag()), is(field.getValue()));
        }
    }
}
