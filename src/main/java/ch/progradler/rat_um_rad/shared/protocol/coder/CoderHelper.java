package ch.progradler.rat_um_rad.shared.protocol.coder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Helper class for de- and encoding content.
 */
public class CoderHelper {
    private final static DateFormat DATE_CODING_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * @param separatorLevel : the hierarchy level of the serialization.
     * @return a different separator based on level.
     * Separators are uncommon text, so it doesn't appear in a message by accident.
     */
    public static String getSeparator(int separatorLevel) {
        return switch (separatorLevel) {
            case 0 -> "_/_";
            case 1 -> ":/:";
            case 2 -> ";/;";
            case 3 -> ",/,";
            case 4 -> "-/-";
            case 5 -> "|/|";
            case 6 -> "*/*";
            default -> throw new IllegalArgumentException("To high separatorLevel: " + separatorLevel +
                    ". Add a new separator to allow this level."
            );
        };
    }

    /**
     * @return fields in encoded format
     */
    public static String encodeFields(int separatorLevel, String... fields) {
        String separator = getSeparator(separatorLevel);
        return String.join(separator, fields);
    }

    /**
     * @return list of fields from encoded format.
     */
    public static List<String> decodeFields(int separatorLevel, String encoded) {
        String separator = getSeparator(separatorLevel);
        return Arrays.asList(encoded.split(Pattern.quote(separator)));
    }

    public static String encodeDate(Date date) {
        return DATE_CODING_FORMAT.format(date);
    }

    public static Date decodeDate(String date) {
        try {
            return DATE_CODING_FORMAT.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Date cannot be decoded", e.getCause());
        }
    }

    public static String encodeStringList(int separatorLevel, List<String> list) {
        if (list.isEmpty()) {
            return null;
        }
        return encodeFields(separatorLevel, list.toArray(new String[0]));
    }

    public static List<String> decodeStringList(int separatorLevel, String encoded) {
        if(encoded.equals("null")) {
            return Collections.emptyList();
        }
        return decodeFields(separatorLevel, encoded);
    }

    public static String encodeNullableField(Object object) {
        if(object == null) {
            return "null";
        }
        return "";
    }

    public static Object decodeNullableField(String encoded) {
        if(encoded.equals("null")) {
            return null;
        }
        return new String("");
    }
}
