package teammates.common.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link TimeHelper}.
 */
public class TimeHelperTest extends BaseTestCase {

    private static final String DATETIME_DISPLAY_FORMAT = "EEE, dd MMM yyyy, hh:mm a z";

    @Test
    public void testEndOfYearDates() {
        LocalDateTime date = LocalDateTime.of(2015, Month.DECEMBER, 30, 12, 0);
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON UTC", TimeHelper.formatInstant(
                date.atZone(ZoneId.of("UTC")).toInstant(), "UTC", DATETIME_DISPLAY_FORMAT));
    }

    @Test
    public void testFormatDateTimeForDisplay() {
        String zoneId = "UTC";
        Instant instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 12, 0).atZone(ZoneId.of(zoneId)).toInstant();
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC", TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));

        zoneId = "Asia/Singapore";
        instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 16, 0).atZone(ZoneId.of(zoneId)).toInstant();
        assertEquals("Mon, 30 Nov 2015, 04:00 PM SGT", TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));

        instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 4, 0).atZone(ZoneId.of(zoneId)).toInstant();
        assertEquals("Mon, 30 Nov 2015, 04:00 AM SGT", TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));
    }

    @Test
    public void testGetMidnightAdjustedInstantBasedOnZone() {
        String zoneId = "UTC";
        Instant instantAt0000 = LocalDateTime.of(2015, Month.NOVEMBER, 30, 0, 0).atZone(ZoneId.of(zoneId)).toInstant();

        Instant backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt0000, zoneId, false);
        // assertEquals("Sun, 29 Nov 2015, 11:59 PM UTC",
        //         TimeHelper.formatInstant(backwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));
        
        assertEquals(Instant.parse("2015-11-29T23:59:00Z"), backwardAdjusted);
        // ^^ caso de teste CT3 -
        // entrada instantAt0000  = 2015-11-30T00:00:00Z
        // saída backwardAdjusted = 2015-11-29T23:59:00Z

        Instant forwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt0000, zoneId, true);
        // assertEquals("Mon, 30 Nov 2015, 12:00 AM UTC",
        //         TimeHelper.formatInstant(forwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));
        
        assertEquals(Instant.parse("2015-11-30T00:00:00Z"), forwardAdjusted);
        // ^^ caso de teste CT4 -
        // entrada instantAt0000 = 2015-11-30T00:00:00Z
        // saída forwardAdjusted = 2015-11-30T00:00:00Z

        Instant instantAt2359 = LocalDateTime.of(2015, Month.NOVEMBER, 29, 23, 59).atZone(ZoneId.of(zoneId)).toInstant();

        backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt2359, zoneId, false);
        // assertEquals("Sun, 29 Nov 2015, 11:59 PM UTC",
        //         TimeHelper.formatInstant(backwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));

        assertEquals(Instant.parse("2015-11-29T23:59:00Z"), backwardAdjusted);
        // ^^ caso de teste CT2 -
        // entrada instantAt2359  = 2015-11-29T23:59:00Z
        // saída backwardAdjusted = 2015-11-29T23:59:00Z

        forwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt2359, zoneId, true);
        // assertEquals("Mon, 30 Nov 2015, 12:00 AM UTC",
        //         TimeHelper.formatInstant(forwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));

        assertEquals(Instant.parse("2015-11-30T00:00:00Z"), forwardAdjusted);
        // ^^ caso de teste CT1 -
        // entrada instantAt2359 = 2015-11-29T23:59:00Z
        // saída forwardAdjusted = 2015-11-30T00:00:00Z

        Instant anyOtherInstant = LocalDateTime.of(2018, Month.APRIL, 15, 23, 0).atZone(ZoneId.of(zoneId)).toInstant();
        Instant instantIsTheSame = 
            TimeHelper.getMidnightAdjustedInstantBasedOnZone(anyOtherInstant, zoneId, true);
        assertEquals(Instant.parse("2018-04-15T23:00:00Z"), instantIsTheSame);
        // ^^ caso de teste CT5 -
        // entrada anyOtherInstant = 2018-04-15T23:00:00Z
        // saída instantIsTheSame  = 2018-04-15T23:00:00Z

        anyOtherInstant = LocalDateTime.of(2021, Month.MARCH, 19, 20, 10).atZone(ZoneId.of(zoneId)).toInstant();
        instantIsTheSame = 
            TimeHelper.getMidnightAdjustedInstantBasedOnZone(anyOtherInstant, zoneId, false);
        assertEquals(Instant.parse("2021-03-19T20:10:00Z"), instantIsTheSame);
        // ^^ caso de teste CT6 -
        // entrada anyOtherInstant = 2021-03-19T20:10:00Z
        // saída instantIsTheSame  = 2021-03-19T20:10:00Z


        // String wrongTimeZone = "Asia/Singapore";

        // backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt0000, wrongTimeZone, false);
        // assertEquals("Mon, 30 Nov 2015, 12:00 AM UTC",
        //         TimeHelper.formatInstant(backwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));

        // forwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt2359, wrongTimeZone, true);
        // assertEquals("Sun, 29 Nov 2015, 11:59 PM UTC",
        //         TimeHelper.formatInstant(forwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));
    }

    @Test
    public void testGetInstantNearestHourBefore() {
        Instant expected = Instant.parse("2020-12-31T16:00:00Z");
        Instant actual = TimeHelper.getInstantNearestHourBefore(Instant.parse("2020-12-31T16:00:00Z"));

        assertEquals(expected, actual);

        actual = TimeHelper.getInstantNearestHourBefore(Instant.parse("2020-12-31T16:10:00Z"));

        assertEquals(expected, actual);

        actual = TimeHelper.getInstantNearestHourBefore(OffsetDateTime.parse("2021-01-01T00:30:00+08:00").toInstant());

        assertEquals(expected, actual);

        actual = TimeHelper.getInstantNearestHourBefore(OffsetDateTime.parse("2020-12-31T12:59:00-04:00").toInstant());

        assertEquals(expected, actual);
    }

    @Test
    public void testGetInstantDaysOffsetFromNow() {
        // Comparison using second precision is sufficient
        Instant expected = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant actual = TimeHelper.getInstantDaysOffsetFromNow(0).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);

        expected = Instant.now().plus(Duration.ofDays(365)).truncatedTo(ChronoUnit.SECONDS);
        actual = TimeHelper.getInstantDaysOffsetFromNow(365).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetInstantDaysOffsetBeforeNow() {
        // Comparison using second precision is sufficient
        Instant expected = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant actual = TimeHelper.getInstantDaysOffsetBeforeNow(0).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);

        expected = Instant.now().minus(Duration.ofDays(365)).truncatedTo(ChronoUnit.SECONDS);
        actual = TimeHelper.getInstantDaysOffsetBeforeNow(365).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetInstantHoursOffsetFromNow() {
        // Comparison using second precision is sufficient
        Instant expected = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant actual = TimeHelper.getInstantHoursOffsetFromNow(0).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);

        expected = Instant.now().plus(Duration.ofHours(60)).truncatedTo(ChronoUnit.SECONDS);
        actual = TimeHelper.getInstantHoursOffsetFromNow(60).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);
    }

}
