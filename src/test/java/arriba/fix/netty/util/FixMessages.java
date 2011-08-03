package arriba.fix.netty.util;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import arriba.fix.Field;
import arriba.fix.Fields;

import com.google.common.collect.Lists;

public class FixMessages {

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
        for (final String rawField : rawFixMessage.split(FIELD_DELIMITER)) {
            final String[] tagAndValue = rawField.split(EQUALS_SIGN);
            fields.add(new Field<String>(Integer.parseInt(tagAndValue[0]), tagAndValue[1]));
        }

        return fields;
    }
}
