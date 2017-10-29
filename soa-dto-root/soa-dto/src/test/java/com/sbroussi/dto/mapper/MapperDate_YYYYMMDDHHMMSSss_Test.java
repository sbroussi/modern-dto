package com.sbroussi.dto.mapper;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MapperDate_YYYYMMDDHHMMSSss_Test {

    private MapperDate mapper = new MapperDate("yyyyMMddHHmmssSS",
            8 + 6 + 2,
            new String[]{"0000000000000000", "9999123100000000", "9999999900000000"});

    @Test
    public void testGetFieldLength() throws Exception {
        Assert.assertEquals(8 + 6 + 2, mapper.getFieldLength());
    }


    @Test
    public void testFormat() throws Exception {

        Assert.assertEquals("", mapper.format(null));
        Assert.assertEquals("2016123100000000", mapper.format(createDate(2016, 12, 31)));
        Assert.assertEquals("2016123100000000", mapper.format(createDate(2016, 12, 31, 0, 0, 0, 0)));
        Assert.assertEquals("2016123123595999", mapper.format(createDate(2016, 12, 31, 23, 59, 59, 99)));

    }


    @Test
    public void testParse() throws Exception {

        Assert.assertEquals(null, mapper.parse(null));
        Assert.assertEquals(null, mapper.parse(""));

        Assert.assertEquals(createDate(2016, 12, 31, 23, 59, 59, 99), mapper.parse("2016123123595999"));

        // check 'null' values
        Assert.assertEquals(null, mapper.parse("00000000"));
        Assert.assertEquals(null, mapper.parse("99991231"));
        Assert.assertEquals(null, mapper.parse("99999999"));

        // the mapper only parse strings of 14 characters
        Assert.assertEquals(null, mapper.parse("x"));
        Assert.assertEquals(null, mapper.parse("2016"));
        Assert.assertEquals(null, mapper.parse("20161231"));
        Assert.assertEquals(null, mapper.parse("201612312359599"));
        Assert.assertNotNull(mapper.parse("2016123123595999"));
        Assert.assertEquals(null, mapper.parse("2016123123595999x"));

    }

    @Test(expected = ParseException.class)
    public void testParseInvalidInput() throws Exception {

        // throw: java.text.ParseException: Unparseable date:"20161231235959xx"
        Assert.assertEquals(null, mapper.parse("20161231235959xx"));
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
