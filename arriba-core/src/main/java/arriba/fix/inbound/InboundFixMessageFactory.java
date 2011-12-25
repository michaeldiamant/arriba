package arriba.fix.inbound;

import arriba.bytearrays.ByteArrayKeyedMap;
import arriba.bytearrays.ImmutableByteArrayKeyedMapBuilder;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.fields.MessageType;

public final class InboundFixMessageFactory {

    private final ByteArrayKeyedMap<MessageTypeFactory> messageTypeToFixMessageCreator;

    public InboundFixMessageFactory() {
        this.messageTypeToFixMessageCreator = this.initializeMessageTypeFactories();
    }

    private ByteArrayKeyedMap<MessageTypeFactory> initializeMessageTypeFactories() {
        final ImmutableByteArrayKeyedMapBuilder<MessageTypeFactory> builder = new ImmutableByteArrayKeyedMapBuilder<>();

        for (final MessageTypeFactory factory : MessageTypeFactory.values()) {
            builder.put(factory.getSerializedValue(), factory);
        }

        return builder.build();
    }

    public InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk,
            final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
        final byte[] messageType = headerChunk.getSerializedValue(Tags.MESSAGE_TYPE);

        final MessageTypeFactory messageTypeFactory = this.messageTypeToFixMessageCreator.get(messageType);
        if (null == messageTypeFactory) {
            throw new IllegalArgumentException("No message type factory for message type " + new String(messageType) + ".");
        }

        return messageTypeFactory.create(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    private enum MessageTypeFactory {
        HEARTBEAT {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new Heartbeat(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.HEARTBEAT.getSerializedValue();
            }
        },
        TEST_REQUEST {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new TestRequest(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.TEST_REQUEST.getSerializedValue();
            }
        },
        RESEND_REQUEST {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new Logon(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.RESEND_REQUEST.getSerializedValue();
            }
        },
        LOGON {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new Logon(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.LOGON.getSerializedValue();
            }
        },
        LOGOUT {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new Logout(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.LOGOUT.getSerializedValue();
            }
        },
        NEW_ORDER_SINGLE {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new NewOrderSingle(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.NEW_ORDER_SINGLE.getSerializedValue();
            }
        },
        MARKET_DATA_REQUEST {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new MarketDataRequest(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.MARKET_DATA_REQUEST.getSerializedValue();
            }
        },
        MARKET_DATA_SNAPSHOT_FULL_REFRESH {
            @Override
            InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
                return new MarketDataSnapshotFullRefresh(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
            }

            @Override
            byte[] getSerializedValue() {
                return MessageType.MARKET_DATA_SNAPSHOT_FULL_REFRESH.getSerializedValue();
            }
        };

        abstract byte[] getSerializedValue();

        abstract InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk,
                final FixChunk trailerChunk, final FixChunk[][] repeatingGroups);
    }
}

