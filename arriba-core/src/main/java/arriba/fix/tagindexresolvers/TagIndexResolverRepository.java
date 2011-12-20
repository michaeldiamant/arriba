package arriba.fix.tagindexresolvers;


public interface TagIndexResolverRepository {

    TagIndexResolver findBodyResolver(byte[] messageType);

    TagIndexResolver findHeaderResolver();

    TagIndexResolver findTrailerResolver();

    TagIndexResolver findRepeatingGroupResolver(int numberOfRepeatingGroupsTag);
}
