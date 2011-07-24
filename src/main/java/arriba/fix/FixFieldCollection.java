package arriba.fix;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// TODO Implement Map<K, V>.
public class FixFieldCollection {

    private final int[] tagArray;
    private final String[] valueArray;

    private FixFieldCollection(final List<Field<String>> fields) {
        Collections.sort(fields);

        this.tagArray = new int[fields.size()];
        this.valueArray = new String[fields.size()];

        int arrayIndex = 0;
        for (final Field<String> field : fields) {
            this.tagArray[arrayIndex] = field.getTag();
            this.valueArray[arrayIndex] = field.getValue();
            ++arrayIndex;
        }
    }

    public String getValue(final int tag) {
        final int valueIndex = Arrays.binarySearch(this.tagArray, tag);

        // TODO Handle not finding a tag.
        return valueIndex < 0 ? "" : this.valueArray[valueIndex];
        //        for (int valueIndex = 0; valueIndex < this.tagArray.length; valueIndex++) {
        //            if (this.tagArray[valueIndex] == tag) {
        //                return this.valueArray[valueIndex];
        //            }
        //        }
    }

    public static class Builder {

        private final List<Field<String>> fields = new LinkedList<Field<String>>();

        public Builder() {}

        public Builder addField(final int tag, final String value) {
            this.fields.add(new Field<String>(tag, value));

            return this;
        }

        public FixFieldCollection build() {
            return new FixFieldCollection(this.fields);
        }
    }
}
