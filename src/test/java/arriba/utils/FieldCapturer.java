package arriba.utils;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class FieldCapturer {

    protected final List<Field<String>> fields = Lists.newArrayList();

    protected void capture(final int tag, final byte[] value) {
        this.capture(tag, new String(value));
    }

    protected void capture(final int tag, final String value) {
        this.fields.add(new Field<String>(tag, new String(value)));
    }

    public void clear() {
        this.fields.clear();
    }
}
