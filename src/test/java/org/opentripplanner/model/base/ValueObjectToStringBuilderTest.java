package org.opentripplanner.model.base;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValueObjectToStringBuilderTest {
    private enum  AEnum { A }
    private static class Foo {
        int a;
        String b;

        public Foo(int a, String b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return ValueObjectToStringBuilder.of()
                    .addStr(b)
                    .toString();
        }
    }

    private ValueObjectToStringBuilder subject() { return ValueObjectToStringBuilder.of(); }

    @Test
    public void addStr() {
        assertEquals("'text'", subject().addStr("text").toString());
    }

    @Test
    public void addObj() {
        assertEquals(
                "'X'",
                subject().addObj(new Foo(5, "X")).toString()
        );
        assertEquals(
                "null",
                subject().addObj(null).toString()
        );
    }

    @Test
    public void addCoordinate() {
        assertEquals(
                "(60.98766, 11.98)",
                subject().addCoordinate(60.9876599999999d, 11.98d).toString()
        );
    }
}