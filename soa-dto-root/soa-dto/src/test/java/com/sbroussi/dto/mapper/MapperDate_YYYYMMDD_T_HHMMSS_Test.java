package com.sbroussi.dto.mapper;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MapperDate_YYYYMMDD_T_HHMMSS_Test {

    private MapperDate mapper = new MapperDate("yyyyMMdd'T'HHmmss",
            8 + 1 + 6,
            new String[]{"00000000T000000", "99991231T000000", "99999999T000000"});


    @Test
    public void testGetFieldLength() throws Exception {
        Assert.assertEquals(8 + 1 + 6, mapper.getFieldLength());
    }

    @Test
    public void testFormat() throws Exception {

        Assert.assertEquals("", mapper.format(null));
        Assert.assertEquals("20161231T000000", mapper.format(createDate(2016, 12, 31)));
        Assert.assertEquals("20161231T000000", mapper.format(createDate(2016, 12, 31, 0, 0, 0, 0)));
        Assert.assertEquals("20161231T235959", mapper.format(createDate(2016, 12, 31, 23, 59, 59, 99)));

    }


    @Test
    public void testParse() throws Exception {

        Assert.assertEquals(null, mapper.parse(null));
        Assert.assertEquals(null, mapper.parse(""));

        Assert.assertEquals(createDate(2016, 12, 31, 23, 59, 59, 0), mapper.parse("20161231T235959"));

        // check 'null' values
        Assert.assertEquals(null, mapper.parse("00000000T000000"));
        Assert.assertEquals(null, mapper.parse("99991231T000000"));
        Assert.assertEquals(null, mapper.parse("99999999T000000"));

        // the mapper only parse strings of 14 characters
        Assert.assertEquals(null, mapper.parse("x"));
        Assert.assertEquals(null, mapper.parse("2016"));
        Assert.assertEquals(null, mapper.parse("20161231"));
        Assert.assertEquals(null, mapper.parse("20161231T23595"));
        Assert.assertNotNull(mapper.parse("20161231T235959"));
        Assert.assertEquals(null, mapper.parse("20161231T235959x"));

    }

    @Test(expected = ParseException.class)
    public void testParseInvalidInput() throws Exception {

        // throw: java.text.ParseException: Unparseable date
        Assert.assertEquals(null, mapper.parse("20161231T2359xx"));
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
