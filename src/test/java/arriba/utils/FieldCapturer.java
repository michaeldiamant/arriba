package arriba.utils;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class FieldCapturer {

    protected final List<Field<String>> fields = Lists.newArrayList();

    protected void capture(final int tag, final String value) {
        this.fields.add(new Field<String>(tag, value));
    }

    public void reset() {
        this.fields.clear();
    }
}
