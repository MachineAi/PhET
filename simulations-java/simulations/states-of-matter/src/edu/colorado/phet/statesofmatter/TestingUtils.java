package edu.colorado.phet.statesofmatter;

import junit.framework.TestCase;

public class TestingUtils {
    public static void testEquality(Object x, Object equalToX, Object notEqualToX) {
        TestCase.assertEquals(x, equalToX);
        TestCase.assertEquals(equalToX, x);
        TestCase.assertFalse(x.equals(notEqualToX));
        TestCase.assertFalse(notEqualToX.equals(x));
        TestCase.assertFalse(equalToX.equals(notEqualToX));
        TestCase.assertFalse(notEqualToX.equals(equalToX));
    }

    public static void testHashCode(Object x, Object equalToX, Object notEqualToX) {
        TestCase.assertEquals(x.hashCode(), equalToX.hashCode());
        TestCase.assertEquals(equalToX.hashCode(), x.hashCode());
        TestCase.assertFalse(x.hashCode() == notEqualToX.hashCode());
        TestCase.assertFalse(notEqualToX.hashCode() == x.hashCode());
        TestCase.assertFalse(equalToX.hashCode() == notEqualToX.hashCode());
        TestCase.assertFalse(notEqualToX.hashCode() == equalToX.hashCode());
    }

    public static void testEqualityAndHashCode(Object x, Object equalToX, Object notEqualToX) {
        testEquality(x, equalToX, notEqualToX);
        testHashCode(x, equalToX, notEqualToX);
    }

    public static void testToString(Object x, Object equalToX, Object notEqualToX) {
        TestCase.assertEquals(x.toString(), equalToX.toString());
        TestCase.assertEquals(equalToX.toString(), x.toString());
        TestCase.assertFalse(x.toString().equals(notEqualToX.toString()));
        TestCase.assertFalse(notEqualToX.toString().equals(x.toString()));
        TestCase.assertFalse(equalToX.toString().equals(notEqualToX.toString()));
        TestCase.assertFalse(notEqualToX.toString().equals(equalToX.toString()));
    }
}
