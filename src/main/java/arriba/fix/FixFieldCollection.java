package arriba.fix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// TODO Implement Map<K, V>.
public class FixFieldCollection {
	
	private final int[] tagArray;
	private final String[] valueArray;
	
	private FixFieldCollection(final List<Field<String>> fields) {
		Collections.sort(fields);
		
		this.tagArray = new int[fields.size()];
		this.valueArray = new String[fields.size()];
		
		int arrayIndex = 0;
		for (final Field<String> field : fields) {
			this.tagArray[arrayIndex] = field.getTag();
			this.valueArray[arrayIndex] = field.getValue();
			++arrayIndex;
		}
	}
	
	public byte[] toByteArray() {
		// TODO Performance test ByteArrayOutputStream vs building LinkedList of all bytes to be written
		// and performing one allocation / write.
		try {
			final ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
			for (int tagIndex = 0; tagIndex < this.tagArray.length; tagIndex++) {
				final int tag = this.tagArray[tagIndex];
				final String value = this.valueArray[tagIndex];
				
				final byte[] tagBytes = Tags.toByteArray(tag);
				final byte[] valueBytes = value.getBytes();
				messageBytes.write(tagBytes);
				messageBytes.write(Fields.EQUAL_SIGN);
				messageBytes.write(valueBytes);
				messageBytes.write(Fields.DELIMITER);
			}
			
			return messageBytes.toByteArray();
		} catch (final IOException e) {
			// FIXME What should happen here?  This is an unrecoverable error.
			return new byte[0];
		}
	}
	
	public String getValue(final int tag) {
		final int valueIndex = Arrays.binarySearch(this.tagArray, tag);
		
		// TODO Handle not finding a tag.
		return valueIndex < 0 ? "" : this.valueArray[valueIndex];
	}
	
	public static class Builder {
		
		private final List<Field<String>> fields = new LinkedList<Field<String>>();
		
		public Builder() {}
		
		public Builder addField(final int tag, final String value) {
			this.fields.add(new Field<String>(tag, value));
			
			return this;
		}
		
		public FixFieldCollection build() {
			return new FixFieldCollection(this.fields);
		}
	}
}
