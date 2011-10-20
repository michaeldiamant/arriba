package arriba.fix;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import arriba.fix.chunk.FixChunk;

public class RepeatingGroupBuilderTest {

    @Test
    public void testRepeatingGroupBuilding() {
        final RepeatingGroupBuilder builder = new RepeatingGroupBuilder();

        builder.setNumberOfRepeatingGroups(2);
        builder.setNumberOfRepeatingGroupsTag(Tags.NUMBER_MD_ENTRIES);

        builder.addField(Tags.MD_ENTRY_PRICE, "1.3455");
        builder.addField(Tags.MD_ENTRY_SIZE, "2");
        builder.addField(Tags.MD_ENTRY_TYPE, "1");

        builder.addField(Tags.MD_ENTRY_PRICE, "1.3555");
        builder.addField(Tags.MD_ENTRY_SIZE, "3");
        builder.addField(Tags.MD_ENTRY_TYPE, "2");

        builder.setNumberOfRepeatingGroups(3);
        builder.setNumberOfRepeatingGroupsTag(Tags.NUMBER_RELATED_SYMBOLS);

        builder.addField(Tags.SYMBOL, "EURUSD");
        builder.addField(Tags.SYMBOL, "USDJPY");
        builder.addField(Tags.SYMBOL, "EURAUD");

        final Map<Integer, FixChunk[]> repeatingGroupNoTagToRepeatingChunks = builder.build();
        Assert.assertEquals(2, repeatingGroupNoTagToRepeatingChunks.size());

        final FixChunk[] mdEntries = repeatingGroupNoTagToRepeatingChunks.get(Tags.NUMBER_MD_ENTRIES);
        Assert.assertEquals(2, mdEntries.length);

        Assert.assertEquals("1", mdEntries[0].getValue(Tags.MD_ENTRY_TYPE));

        final FixChunk[] symbols = repeatingGroupNoTagToRepeatingChunks.get(Tags.NUMBER_RELATED_SYMBOLS);
        Assert.assertEquals(3, symbols.length);
        Assert.assertEquals("USDJPY", symbols[1].getValue(Tags.SYMBOL));
    }

}
