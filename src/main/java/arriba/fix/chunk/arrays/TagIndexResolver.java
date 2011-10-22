package arriba.fix.chunk.arrays;

public abstract class TagIndexResolver {

    final protected static int INVALID_TAG_INDEX = -1;

    public boolean isDefinedFor(final int tag) {
        return tag <= this.getMaxTag() && this.getTagIndex(tag) != TagIndexResolver.INVALID_TAG_INDEX;
    }

    public abstract int getTagIndex(int tag);

    public abstract int getTagCount();

    protected abstract int getMaxTag();
}
