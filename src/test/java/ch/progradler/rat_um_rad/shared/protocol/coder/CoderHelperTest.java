package ch.progradler.rat_um_rad.shared.protocol.coder;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoderHelperTest {


    @Test
    void encodeFields() {
        String arg1 = "arg1";
        String arg2 = "arg2";
        String arg3 = "arg3";

        int level = 2;
        String separator = CoderHelper.getSeparator(level);
        String expected = arg1 + separator + arg2 + separator + arg3;
        assertEquals(expected, CoderHelper.encodeFields(level, arg1, arg2, arg3));
    }

    @Test
    void decodeFields() {
        String arg1 = "arg1";
        String arg2 = "arg2";
        String arg3 = "arg3";

        int level = 2;
        String separator = CoderHelper.getSeparator(level);

        String encoded = arg1 + separator + arg2 + separator + arg3;

        List<String> expected = Arrays.asList(arg1, arg2, arg3);
        assertEquals(expected, CoderHelper.decodeFields(level, encoded));
    }

    @Test
    void encodeDate() {
        Date date = new Date(2023 - 1900, Calendar.APRIL, 13, 9, 25, 50);
        String encoded = CoderHelper.encodeDate(date);
        assertEquals("2023-04-13 09:25:50", encoded);
    }


    @Test
    void decodeDate() {
        String encoded = "2023-04-13 09:25:50";
        Date expected = new Date(2023 - 1900, Calendar.APRIL, 13, 9, 25, 50);
        Date decoded = CoderHelper.decodeDate(encoded);
        assertEquals(expected, decoded);
    }

    @Test
    void encodeStringList() {
        int level = 4;
        List<String> list = Arrays.asList("a", "b", "c");
        String encoded = CoderHelper.encodeStringList(level, list);
        assertEquals(CoderHelper.encodeFields(level, "a", "b", "c"), encoded);
    }

    @Test
    void decodeStringList() {
        int level = 4;
        String encoded = CoderHelper.encodeFields(level, "a", "b", "c");
        List<String> decoded = CoderHelper.decodeStringList(level, encoded);
        assertEquals(Arrays.asList("a", "b", "c"), decoded);
    }

    @Test
    void encodeStringMap() {
        int level = 4;
        Map<String, String> map = Map.of("a", "b", "c", "d");
        String encoded = CoderHelper.encodeStringMap(level, map);
        assertEquals(CoderHelper.encodeFields(level, "a", "b", "c", "d"), encoded);
    }

    @Test
    void decodeStringMap() {
        int level = 4;
        String encoded = CoderHelper.encodeFields(level, "a", "b", "c", "d");
        Map<String, String> decoded = CoderHelper.decodeStringMap(level, encoded);

        assertEquals(Map.of("a", "b", "c", "d"), decoded);
    }
}