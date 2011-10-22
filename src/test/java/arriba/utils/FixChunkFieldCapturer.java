package arriba.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;

public final class FixChunkFieldCapturer extends FieldCapturer {

    private final FixChunkBuilder builder;

    public FixChunkFieldCapturer(final FixChunkBuilder builder) {
        this.builder = builder;
    }

    public FixChunkBuilder addField(final int tag, final String value) {
        this.capture(tag, value);

        return this.builder.addField(tag, value);
    }

    public FixChunk build() {
        return this.builder.build();
    }

    @Override
    public void clear() {
        super.clear();

        this.builder.clear();
    }

    public void assertFieldsAreSet(final FixChunk fixChunk) {
        for (final Field<String> field : this.fields) {
            assertThat(fixChunk.getValue(field.getTag()), is(field.getValue()));
        }
    }
}
