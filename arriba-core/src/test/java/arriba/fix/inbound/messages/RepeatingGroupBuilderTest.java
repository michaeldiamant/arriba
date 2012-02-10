package arriba.fix.inbound.messages;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilderSupplier;
import arriba.fix.inbound.messages.RepeatingGroupBuilder;
import arriba.fix.tagindexresolvers.CanonicalTagIndexResolverRepository;

public class RepeatingGroupBuilderTest {

    private RepeatingGroupBuilder builder = null;

    @Before
    public void before() {
        this.builder = new RepeatingGroupBuilder(new ArrayFixChunkBuilderSupplier(new CanonicalTagIndexResolverRepository()));
    }

    @Test
    public void testBuildingSingleSetOfRepeatingGroups() {
        this.builder
        .setNumberOfRepeatingGroupsTag(Tags.NUMBER_MD_ENTRIES)

        .addField(Tags.MD_ENTRY_PRICE, "1.3455".getBytes())
        .addField(Tags.MD_ENTRY_SIZE, "2".getBytes())
        .addField(Tags.MD_ENTRY_TYPE, "1".getBytes())

        .addField(Tags.MD_ENTRY_PRICE, "1.3555".getBytes())
        .addField(Tags.MD_ENTRY_SIZE, "3".getBytes())
        .addField(Tags.MD_ENTRY_TYPE, "2".getBytes())

        .setNumberOfRepeatingGroupsTag(Tags.NUMBER_RELATED_SYMBOLS)

        .addField(Tags.SYMBOL, "EURUSD".getBytes())
        .addField(Tags.SYMBOL, "USDJPY".getBytes())
        .addField(Tags.SYMBOL, "EURAUD".getBytes());

        final FixChunk[][] repeatingGroups = this.builder.build();

        assertThat(repeatingGroups.length, is(2));

        final FixChunk[] mdEntries = repeatingGroups[0];
        assertThat(mdEntries.length, is(2));

        assertThat(mdEntries[0].getValue(Tags.MD_ENTRY_TYPE), is("1"));
        assertThat(mdEntries[0].getValue(Tags.MD_ENTRY_SIZE), is("2"));
        assertThat(mdEntries[1].getValue(Tags.MD_ENTRY_PRICE), is("1.3555"));
        assertThat(mdEntries[1].getValue(Tags.MD_ENTRY_TYPE), is("2"));

        final FixChunk[] symbols = repeatingGroups[1];
        assertThat(symbols.length, is(3));

        assertThat(symbols[0].getValue(Tags.SYMBOL), is("EURUSD"));
        assertThat(symbols[1].getValue(Tags.SYMBOL), is("USDJPY"));
        assertThat(symbols[2].getValue(Tags.SYMBOL), is("EURAUD"));
    }

    @Test
    public void testBuildingTwoSetsOfRepeatingGroups() {
        this.builder
        .setNumberOfRepeatingGroupsTag(Tags.NUMBER_MD_ENTRIES)
        .addField(Tags.MD_ENTRY_PRICE, "1.3455".getBytes())
        .addField(Tags.MD_ENTRY_SIZE, "2".getBytes())
        .addField(Tags.MD_ENTRY_TYPE, "1".getBytes())

        .setNumberOfRepeatingGroupsTag(Tags.NUMBER_RELATED_SYMBOLS)
        .addField(Tags.SYMBOL, "EURUSD".getBytes())

        .build();
        this.builder.clear();

        this.builder
        .setNumberOfRepeatingGroupsTag(Tags.NUMBER_RELATED_SYMBOLS)
        .addField(Tags.SYMBOL, "AUDCAD".getBytes())
        .addField(Tags.SYMBOL, "EURUSD".getBytes())
        .addField(Tags.SYMBOL, "CADSWF".getBytes())
        .addField(Tags.SYMBOL, "JPYAUD".getBytes())

        .setNumberOfRepeatingGroupsTag(Tags.NUMBER_MD_ENTRIES)
        .addField(Tags.MD_ENTRY_TYPE, "0".getBytes())
        .addField(Tags.MD_ENTRY_SIZE, "10".getBytes())
        .addField(Tags.MD_ENTRY_PRICE, "3.1337".getBytes());

        final FixChunk[][] repeatingGroups = this.builder.build();

        assertThat(repeatingGroups.length, is(2));

        final FixChunk[] symbols = repeatingGroups[0];
        assertThat(symbols.length, is(4));

        assertThat(symbols[0].getValue(Tags.SYMBOL), is("AUDCAD"));
        assertThat(symbols[1].getValue(Tags.SYMBOL), is("EURUSD"));
        assertThat(symbols[2].getValue(Tags.SYMBOL), is("CADSWF"));
        assertThat(symbols[3].getValue(Tags.SYMBOL), is("JPYAUD"));

        final FixChunk[] mdEntries = repeatingGroups[1];
        assertThat(mdEntries.length, is(1));

        assertThat(mdEntries[0].getValue(Tags.MD_ENTRY_TYPE), is("0"));
        assertThat(mdEntries[0].getValue(Tags.MD_ENTRY_SIZE), is("10"));
        assertThat(mdEntries[0].getValue(Tags.MD_ENTRY_PRICE), is("3.1337"));
    }

    @Test
    public void testGettingNumberOfRepeatingGroupsTags() {
        this.builder
        .setNumberOfRepeatingGroupsTag(Tags.NUMBER_MD_ENTRIES)

        .addField(Tags.MD_ENTRY_PRICE, "1.3455".getBytes())
        .addField(Tags.MD_ENTRY_SIZE, "2".getBytes())
        .addField(Tags.MD_ENTRY_TYPE, "1".getBytes())

        .addField(Tags.MD_ENTRY_PRICE, "1.3555".getBytes())
        .addField(Tags.MD_ENTRY_SIZE, "3".getBytes())
        .addField(Tags.MD_ENTRY_TYPE, "2".getBytes())

        .setNumberOfRepeatingGroupsTag(Tags.NUMBER_RELATED_SYMBOLS)

        .addField(Tags.SYMBOL, "EURUSD".getBytes())
        .addField(Tags.SYMBOL, "USDJPY".getBytes())
        .addField(Tags.SYMBOL, "EURAUD".getBytes())

        .build();

        final int[] numberOfRepeatingGroupTags = this.builder.getNumberOfRepeatingGroupTags();

        assertThat(numberOfRepeatingGroupTags.length, is(2));
        assertThat(numberOfRepeatingGroupTags[0], is(Tags.NUMBER_MD_ENTRIES));
        assertThat(numberOfRepeatingGroupTags[1], is(Tags.NUMBER_RELATED_SYMBOLS));
    }
}
