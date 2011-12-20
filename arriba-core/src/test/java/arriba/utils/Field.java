package arriba.utils;

public final class Field<T> implements Comparable<Field<?>> {

    private final int tag;
    private T value;

    public Field(final int tag) {
        this(tag, null);
    }

    public Field(final int tag, final T value) {
        this.tag = tag;
        this.value = value;

    }

    public int getTag() {
        return this.tag;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(final T value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.tag;
        result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Field<?> other = (Field<?>) obj;
        if (this.tag != other.tag) {
            return false;
        }
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.tag + "=" + this.value;
    }

    public int compareTo(final Field<?> otherField) {
        if (this.tag > otherField.getTag()) {
            return 1;
        } else if (this.tag < otherField.getTag()) {
            return -1;
        }

        return 0;
    }
}
