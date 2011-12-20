package arriba.fix.fields;

public enum BeginString {

    FIXT11 {
        @Override
        public String getValue() {
            return "FIXT.1.1";
        }

        @Override
        public byte[] getSerializedValue() {
            return this.getValue().getBytes();
        }
    };

    public abstract String getValue();

    public abstract byte[] getSerializedValue();
}
