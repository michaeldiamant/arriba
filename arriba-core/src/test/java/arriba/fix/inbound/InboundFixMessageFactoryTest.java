package arriba.fix.inbound;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.fields.MessageType;

public class InboundFixMessageFactoryTest {

    private final InboundFixMessageFactory factory = new InboundFixMessageFactory();

    @Test
    public void testCreationOfAllMessageTypes() {
        final FixChunk mockBodyChunk = mock(FixChunk.class);
        final FixChunk mockTrailerChunk = mock(FixChunk.class);
        final FixChunk[][] repeatingGroups = new FixChunk[0][];

        for (final MessageType messageType : MessageType.values()) {
            final FixChunk mockHeaderChunk = mock(FixChunk.class);
            when(mockHeaderChunk.getSerializedValue(Tags.MESSAGE_TYPE)).thenReturn(messageType.getSerializedValue());

            this.factory.create(mockHeaderChunk, mockBodyChunk, mockTrailerChunk, repeatingGroups);

            verifyZeroInteractions(mockBodyChunk, mockTrailerChunk);
        }
    }
}
