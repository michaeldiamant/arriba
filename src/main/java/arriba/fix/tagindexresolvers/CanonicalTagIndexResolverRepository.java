package arriba.fix.tagindexresolvers;

import arriba.bytearrays.ByteArrayKeyedMap;
import arriba.bytearrays.ImmutableByteArrayKeyedMapBuilder;
import arriba.fix.RepeatingGroups;
import arriba.fix.Tags;
import arriba.fix.fields.MessageType;

public final class CanonicalTagIndexResolverRepository implements TagIndexResolverRepository {

    private final TagIndexResolver[] repeatingGroupResolvers = new TagIndexResolver[RepeatingGroups.NUMBER_IN_GROUP_TAGS.length];
    private final ByteArrayKeyedMap<TagIndexResolver> bodyResolvers;

    public CanonicalTagIndexResolverRepository() {
        this.initializeRepeatingGroupResolvers();
        this.bodyResolvers = this.initializeBodyResolvers();
    }

    private void initializeRepeatingGroupResolvers() {
        this.repeatingGroupResolvers[Tags.NUMBER_RELATED_SYMBOLS] = new RelatedSymbolsTagIndexResolver();
        this.repeatingGroupResolvers[Tags.NUMBER_MD_ENTRIES] = new MdEntriesTagIndexResolver();
    }

    private ByteArrayKeyedMap<TagIndexResolver> initializeBodyResolvers() {
        return new ImmutableByteArrayKeyedMapBuilder<TagIndexResolver>()
                .put(MessageType.NEW_ORDER_SINGLE.getSerializedValue(), new NewOrderSingleTagIndexResolver())

                .build();
    }

    @Override
    public TagIndexResolver findBodyResolver(final byte[] messageType) {
        return this.bodyResolvers.get(messageType);
    }

    @Override
    public TagIndexResolver findHeaderResolver() {
        return new StandardHeaderTagIndexResolver();
    }

    @Override
    public TagIndexResolver findTrailerResolver() {
        return new StandardTrailerTagIndexResolver();
    }

    @Override
    public TagIndexResolver findRepeatingGroupResolver(final int numberOfRepeatingGroupsTag) {
        return this.repeatingGroupResolvers[numberOfRepeatingGroupsTag];
    }
}
