package arriba.fix.netty.util;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import arriba.fix.Field;
import arriba.fix.Fields;

import com.google.common.collect.Lists;

public class FixMessages {
	
	public static final String EXAMPLE_NEW_ORDER_SINGLE = "8=FIX.4.0\u00019=108\u000135=D\u000149=0\u000156=0\u000134=1\u000152=99990909-17:17:17\u000111=90001008\u000121=1\u000155=IBM\u000154=1\u000138=10\u000140=1\u000159=0\u000110=191\u0001";
	
	private static final String FIELD_DELIMITER = new String(new char[]{(char) Fields.DELIMITER});
	private static final String EQUALS_SIGN = new String(new char[]{(char) Fields.EQUAL_SIGN});
	
	public static ChannelBuffer toChannelBuffer(final String rawFixMessage, final int times) {
		final StringBuilder fixMessageBuilder = new StringBuilder();
		for (int time = 0; time < times; time++) {
			fixMessageBuilder.append(rawFixMessage);
		}
		
		return ChannelBuffers.copiedBuffer(fixMessageBuilder.toString().getBytes());
	}
	
	public static ChannelBuffer toChannelBuffer(final String rawFixMessage) {
		return toChannelBuffer(rawFixMessage, 1);
	}
	
	public static List<String> splitOnTag(final String tag, final String fixMessage) {
		final String delimitedTag = tag + "=";
		final int delimitedTagIndex = fixMessage.indexOf(delimitedTag);
		return Lists.newArrayList(
			fixMessage.substring(0, delimitedTagIndex),
			fixMessage.substring(delimitedTagIndex, fixMessage.length())
		);
	}
	
	public static List<Field<String>> toFields(final String rawFixMessage) {
		final List<Field<String>> fields = Lists.newArrayList();
		for (final String rawField : rawFixMessage.split(FixMessages.FIELD_DELIMITER)) {
			final String[] tagAndValue = rawField.split(FixMessages.EQUALS_SIGN);
			fields.add(new Field<String>(Integer.parseInt(tagAndValue[0]), tagAndValue[1]));
		}
		
		return fields;
	}
}
