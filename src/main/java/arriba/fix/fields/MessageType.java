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
}
