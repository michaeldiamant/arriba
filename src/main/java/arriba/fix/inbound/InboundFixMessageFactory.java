package arriba.fix.inbound;

import java.util.Map;

import arriba.bytearrays.ByteArrayKeyedMap;
import arriba.bytearrays.ImmutableByteArrayKeyedMap;
import arriba.bytearrays.RichByteArray;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.fields.MessageType;

import com.google.common.collect.Maps;

public final class InboundFixMessageFactory {

    private static final ByteArrayKeyedMap<MessageTypeFactory> messageTypeToFixMessageCreator;

    static {
        // TODO Create a ByteArrayKeyedMap builder.
        final Map<RichByteArray, MessageTypeFactory> backingMap = initializeMessageTypeFactory();

        messageTypeToFixMessageCreator = new ImmutableByteArrayKeyedMap<MessageTypeFactory>(backingMap);
    }

    private static Map<RichByteArray, MessageTypeFactory> initializeMessageTypeFactory() {
        final Map<RichByteArray, MessageTypeFactory> initializedFactoryMap = Maps.newHashMap();

        for (final MessageTypeFactory factory : MessageTypeFactory.values()) {
            initializedFactoryMap.put(new RichByteArray(factory.getSerializedValue()), factory);
        }

        return initializedFactoryMap;
    }

    public static InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk,
            final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
        final byte[] messageType = headerChunk.getSerializedValue(Tags.MESSAGE_TYPE);

        final MessageTypeFactory messageTypeFactory = messageTypeToFixMessageCreator.get(messageType);
        if (null == messageTypeFactory) {
            throw new IllegalArgumentException();
        }

        return messageTypeFactory.create(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    private enum MessageTypeFactory {
        LOGON {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new Logon(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.LOGON;
            }
        },
        NEW_ORDER_SINGLE {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new NewOrderSingle(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.NEW_ORDER_SINGLE;
            }
        },
        MARKET_DATA_REQUEST {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new MarketDataRequest(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.MARKET_DATA_REQUEST;
            }
        },
        MARKET_DATA_SNAPSHOT_FULL_REFRESH {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new MarketDataSnapshotFullRefresh(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.MARKET_DATA_SNAPSHOT_FULL_REFRESH;
            }
        };

        abstract byte[] getSerializedValue();

        abstract InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk,
                final FixChunk trailerChunk, final FixChunk[][] repeatingGroups);
    }
}

