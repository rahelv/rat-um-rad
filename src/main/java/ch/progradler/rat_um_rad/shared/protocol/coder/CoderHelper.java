package ch.progradler.rat_um_rad.shared.protocol.coder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
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
            case 7 -> ".-.";
            case 8 -> "_:,";
            case 9 -> "_/,";
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
        if (encoded.equals("null")) {
            return Collections.emptyList();
        }
        return decodeFields(separatorLevel, encoded);
    }

    public static String encodeStringMap(int separatorLevel, Map<String, String> map) {
        List<String> asList = new ArrayList<>();
        for (String key : map.keySet()) {
            asList.add(key);
            asList.add(map.get(key));
        }
        return CoderHelper.encodeStringList(separatorLevel, asList);
    }

    public static Map<String, String> decodeStringMap(int separatorLevel, String encoded) {
        List<String> list = CoderHelper.decodeStringList(separatorLevel, encoded);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < list.size(); i += 2) {
            map.put(list.get(i), list.get(i + 1));
        }
        return map;
    }

    public static <T> String encodeNullableField(T object, Function<T, String> encodeNonNull) {
        if (object == null) return "null";
        return encodeNonNull.apply(object);
    }

    public static <T> T decodeNullableField(String encoded, Function<String, T> decodeNonNull) {
        if (encoded.equals("null")) return null;
        return decodeNonNull.apply(encoded);
    }

    public static <T> String encodeList(Coder<T> coder, List<T> destinationCards, int level) {
        List<String> listOfEncoded = destinationCards.stream()
                .map((destinationCard) -> coder.encode(destinationCard, level + 1))
                .toList();
        return encodeStringList(level, listOfEncoded);
    }

    public static <T> List<T> decodeList(Coder<T> coder, String encoded, int level) {
        List<String> stringList = CoderHelper.decodeStringList(level, encoded);
        return stringList.stream()
                .map((s) -> coder.decode(s, level + 1)).toList();
    }
}
