package arriba.fix.fields;

public enum MessageType {

    HEARTBEAT {
        @Override
        public String getValue() {
            return "0";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    },
    TEST_REQUEST {
        @Override
        public String getValue() {
            return "1";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    },
    RESEND_REQUEST {
        @Override
        public String getValue() {
            return "2";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    },
    LOGON {
        @Override
        public String getValue() {
            return "A";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    },
    SEQUENCE_RESET {
        @Override
        public String getValue() {
            return "4";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    },
    LOGOUT {
        @Override
        public String getValue() {
            return "5";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    },
    NEW_ORDER_SINGLE {
        @Override
        public String getValue() {
            return "D";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    },
    MARKET_DATA_SNAPSHOT_FULL_REFRESH {
        @Override
        public String getValue() {
            return "W";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    },
    MARKET_DATA_REQUEST {
        @Override
        public String getValue() {
            return "V";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    };

    public abstract String getValue();

    public abstract byte[] getSerializedValue();

    public static MessageType toMessageType(final String fixMessageType) {
        for (final MessageType messageType : MessageType.values()) {
            if (messageType.getValue().equals(fixMessageType)) {
                return messageType;
            }
        }

        throw new IllegalArgumentException("Unknown message type:  " + fixMessageType + ".");
    }
}
