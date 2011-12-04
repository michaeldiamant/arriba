package arriba.fix.fields;

public enum EncryptMethod {

    NONE {
        @Override
        public String getValue() {
            return "0";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    };

    public abstract String getValue();

    public abstract byte[] getSerializedValue();
}
