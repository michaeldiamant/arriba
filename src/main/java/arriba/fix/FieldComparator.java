package arriba.fix;

import java.util.Comparator;

public final class FieldComparator implements Comparator<Field<?>>{

    public int compare(final Field<?> field1, final Field<?> field2) {
        if (field1.getTag() > field2.getTag()) {
            return 1;
        } else if (field1.getTag() < field2.getTag()) {
            return -1;
        }

        return 0;
    }
}
