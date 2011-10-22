package arriba.fix.chunk.arrays;

public interface TagIndexResolver {

    final int INVALID_TAG_INDEX = -1;

    boolean isDefinedFor(int tag);

    int getTagIndex(int tag);

    int getTagCount();
}
