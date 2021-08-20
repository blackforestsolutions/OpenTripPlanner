package org.opentripplanner.util;

import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    // Create some time constants: T<hour>_<minutes>(_<seconds>)? 
    private static final int T00_00 = 0;
    private static final int T00_00_01 = 1;
    private static final int T00_00_59 = 59;
    private static final int T00_01 = 60;
    private static final int T00_05 = 300;
    private static final int T08_07 = (8 * 60 + 7) * 60;
    private static final int T08_47 = (8 * 60 + 47) * 60;
    private static final int T35_00 = 35 * 3600;
    
    // Create some negative time constants: N<hour>_<minutes>(_<seconds>)? 
    private static final int N00_00_01 = -1;
    private static final int N00_00_59 = -59;
    private static final int N00_05 = -300;
    private static final int N08_00 = -8 * 3600;
    private static final int N08_07 = -(8 * 60 + 7) * 60;
    private static final int N08_47 = -(8 * 60 + 47) * 60;

    @Test
    public final void testToDate() {
        Date date = DateUtils.toDate("1970-01-01", "00:00", TimeZone.getTimeZone("UTC"));
        assertEquals(0, date.getTime());

        date = DateUtils.toDate(null, "00:00", TimeZone.getTimeZone("UTC"));
        assertEquals(0, date.getTime() % DateUtils.ONE_DAY_MILLI);
    }
}
