package com.sbroussi.dto.mapper;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MapperDate_YYYY_MM_DD_T_HH_MM_SS_Test {

    private MapperDate mapper = new MapperDate("yyyy-MM-dd'T'HH:mm:ss",
            10 + 1 + 8,
            new String[]{"0000-00-00T00:00:00", "9999-12-31T00:00:00", "9999-99-99T00:00:00"});


    @Test
    public void testGetFieldLength() throws Exception {
        Assert.assertEquals(10 + 1 + 8, mapper.getFieldLength());
    }

    @Test
    public void testFormat() throws Exception {

        Assert.assertEquals("", mapper.format(null));
        Assert.assertEquals("2016-12-31T00:00:00", mapper.format(createDate(2016, 12, 31)));
        Assert.assertEquals("2016-12-31T00:00:00", mapper.format(createDate(2016, 12, 31, 0, 0, 0, 0)));
        Assert.assertEquals("2016-12-31T23:59:59", mapper.format(createDate(2016, 12, 31, 23, 59, 59, 99)));

    }


    @Test
    public void testParse() throws Exception {

        Assert.assertEquals(null, mapper.parse(null));
        Assert.assertEquals(null, mapper.parse(""));

        Assert.assertEquals(createDate(2016, 12, 31, 23, 59, 59, 0), mapper.parse("2016-12-31T23:59:59"));

        // check 'null' values
        Assert.assertEquals(null, mapper.parse("0000-00-00T00:00:00"));
        Assert.assertEquals(null, mapper.parse("9999-12-31T00:00:00"));
        Assert.assertEquals(null, mapper.parse("9999-99-99T00:00:00"));

        // the mapper only parse strings of 14 characters
        Assert.assertEquals(null, mapper.parse("x"));
        Assert.assertEquals(null, mapper.parse("2016"));
        Assert.assertEquals(null, mapper.parse("2016-12-31"));
        Assert.assertEquals(null, mapper.parse("2016-12-31T23:59:5"));
        Assert.assertNotNull(mapper.parse("2016-12-31T23:59:59"));
        Assert.assertEquals(null, mapper.parse("2016-12-31T23:59:59x"));

    }

    @Test(expected = ParseException.class)
    public void testParseInvalidInput() throws Exception {

        // throw: java.text.ParseException: Unparseable date
        Assert.assertEquals(null, mapper.parse("2016-12-31T23:59:xx"));
    }


    private Date createDate(final int year, final int month, final int date) {
        return createDate(year, month, date, 0, 0, 0, 0);
    }

    private Date createDate(final int year, final int month, final int date, final int hrs, final int min, final int sec, final int ms) {

        final GregorianCalendar cal = new GregorianCalendar();
        // cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(year, month - 1, date, hrs, min, sec);
        cal.set(Calendar.MILLISECOND, ms);
        return cal.getTime();
    }

}
