package arriba.fix.inbound;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilderSupplier;
import arriba.fix.tagindexresolvers.CanonicalTagIndexResolverRepository;

@Ignore
public class RepeatingGroupBuilderTest {

    private final RepeatingGroupBuilder builder =
            new RepeatingGroupBuilder(new ArrayFixChunkBuilderSupplier(new CanonicalTagIndexResolverRepository()));

    @Test
    public void testRepeatingGroupBuilding() {
        final int numberMdEntries = 2;
        final int numberRelatedSymbols = 3;
        this.builder
        .setNumberOfRepeatingGroupsField(Tags.NUMBER_MD_ENTRIES, numberMdEntries)

        .addField(Tags.MD_ENTRY_PRICE, "1.3455".getBytes())
        .addField(Tags.MD_ENTRY_SIZE, "2".getBytes())
        .addField(Tags.MD_ENTRY_TYPE, "1".getBytes())

        .addField(Tags.MD_ENTRY_PRICE, "1.3555".getBytes())
        .addField(Tags.MD_ENTRY_SIZE, "3".getBytes())
        .addField(Tags.MD_ENTRY_TYPE, "2".getBytes())

        .setNumberOfRepeatingGroupsField(Tags.NUMBER_RELATED_SYMBOLS, numberRelatedSymbols)

        .addField(Tags.SYMBOL, "EURUSD".getBytes())
        .addField(Tags.SYMBOL, "USDJPY".getBytes())
        .addField(Tags.SYMBOL, "EURAUD".getBytes());

        final FixChunk[][] repeatingGroups = this.builder.build();

        assertThat(repeatingGroups.length, is(2));

        final FixChunk[] mdEntries = repeatingGroups[0];
        Assert.assertEquals(numberMdEntries, mdEntries.length);

        Assert.assertEquals("1", mdEntries[0].getValue(Tags.MD_ENTRY_TYPE));

        final FixChunk[] symbols = repeatingGroups[1];
        Assert.assertEquals(numberRelatedSymbols, symbols.length);
        Assert.assertEquals("USDJPY", symbols[1].getValue(Tags.SYMBOL));
    }
}
