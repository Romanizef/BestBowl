package de.softwareprojekt.bestbowl;

import de.softwareprojekt.bestbowl.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marten Voß
 */
@SpringBootTest
class UtilsTests {
    @Test
    void isStringNotEmpty() {
        Assertions.assertTrue(Utils.isStringNotEmpty("a"));
        Assertions.assertTrue(Utils.isStringNotEmpty("aaaaaaaaaaaa"));
        String nullString = null;
        Assertions.assertFalse(Utils.isStringNotEmpty(nullString));
        Assertions.assertFalse(Utils.isStringNotEmpty(""));
    }

    @Test
    void isStringNotEmpty2() {
        Assertions.assertTrue(Utils.isStringNotEmpty("a", "a"));
        Assertions.assertTrue(Utils.isStringNotEmpty("aaaaaaaaaaaa", "aa", "aaaaaa", "aaaaaaaaaaaa"));
        Assertions.assertFalse(Utils.isStringNotEmpty(null, "aaaa", "a"));
        Assertions.assertFalse(Utils.isStringNotEmpty("aa", ""));
    }

    @Test
    void isStringMinNChars() {
        Assertions.assertTrue(Utils.isStringMinNChars("a", 1));
        Assertions.assertTrue(Utils.isStringMinNChars("12345678", 5));
        Assertions.assertFalse(Utils.isStringMinNChars(null, 5));
        Assertions.assertFalse(Utils.isStringMinNChars("123", 4));
        Assertions.assertFalse(Utils.isStringMinNChars("123456", 20));
    }

    @Test
    void matches() {
        Assertions.assertTrue(Utils.matches("abcdefg", "cde"));
        Assertions.assertTrue(Utils.matches("abcdefg", "ab"));
        Assertions.assertTrue(Utils.matches("abcdefg", "fg"));
        Assertions.assertTrue(Utils.matches("abcdefg", "abcdefg"));
        Assertions.assertTrue(Utils.matches("abcdefg", ""));
        Assertions.assertTrue(Utils.matches("abcdefg", null));
        Assertions.assertFalse(Utils.matches("abcdefg", "245"));
        Assertions.assertFalse(Utils.matches("abcdefg", "abcdefgh"));
    }

    @Test
    void matchAndRemoveIfContains() {
        String[] values = new String[]{"abc", "123", "xyz"};
        List<String> expectedList = new ArrayList<>(Arrays.stream(values).toList());
        List<String> testList = new ArrayList<>(Arrays.stream(values).toList());

        expectedList.remove("abc");
        Utils.matchAndRemoveIfContains("abcde", testList);
        Assertions.assertLinesMatch(expectedList, testList);

        Utils.matchAndRemoveIfContains("159", testList);
        Assertions.assertLinesMatch(expectedList, testList);
    }

    @Test
    void formatDouble() {
        Assertions.assertEquals("1,23", Utils.formatDouble(1.2345));
        Assertions.assertEquals("1,20", Utils.formatDouble(1.2));
        Assertions.assertEquals("1,00", Utils.formatDouble(1.0));
    }

    @Test
    void toHoursString() {
        Assertions.assertEquals("1,50 Std.", Utils.toHoursString(
                Duration.ofHours(1).toMillis() + Duration.ofMinutes(30).toMillis()));
        Assertions.assertEquals("2,75 Std.", Utils.toHoursString(
                Duration.ofHours(2).toMillis() + Duration.ofMinutes(45).toMillis()));
        Assertions.assertEquals("0,00 Std.", Utils.toHoursString(0));
    }
}
