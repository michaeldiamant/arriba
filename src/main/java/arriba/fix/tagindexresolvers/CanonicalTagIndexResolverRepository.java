package arriba.fix.tagindexresolvers;

import arriba.fix.RepeatingGroups;
import arriba.fix.Tags;

public final class CanonicalTagIndexResolverRepository implements TagIndexResolverRepository {

    private final TagIndexResolver[] repeatingGroupResolvers = new TagIndexResolver[RepeatingGroups.NUMBER_IN_GROUP_TAGS.length];

    public CanonicalTagIndexResolverRepository() {
        this.initializeRepeatingGroupResolvers();
    }

    private void initializeRepeatingGroupResolvers() {
        this.repeatingGroupResolvers[Tags.NUMBER_RELATED_SYMBOLS] = new RelatedSymbolsTagIndexResolver();
        this.repeatingGroupResolvers[Tags.NUMBER_MD_ENTRIES] = new MdEntriesTagIndexResolver();
    }

    @Override
    public TagIndexResolver findBodyResolver(final byte[] messageType) {
        // TODO
        return null;
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
