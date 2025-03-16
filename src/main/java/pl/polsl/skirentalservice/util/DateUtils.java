package pl.polsl.skirentalservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.dto.BriefTimeData;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {
    private static final DateTimeFormatter DTF_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDateTime parse(String dateStr) {
        return LocalDateTime.parse(toISO8601Format(dateStr), DTF_FORMATTER);
    }

    public static LocalDate parseToDateOnly(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    public static LocalDateTime truncateToTotalHour(LocalDateTime localDateTime) {
        final int minutes = localDateTime.getMinute();
        if (minutes >= 1) {
            localDateTime = localDateTime.plusHours(1);
        }
        return localDateTime.truncatedTo(ChronoUnit.HOURS);
    }

    public static BriefTimeData getHoursAndDaysFromBriefTime(LocalDateTime start, LocalDateTime end) {
        final LocalDateTime startTruncated = DateUtils.truncateToTotalHour(start);
        final LocalDateTime endTruncated = DateUtils.truncateToTotalHour(end);

        long totalRentHours = Duration.between(startTruncated, endTruncated).toHours();
        if (totalRentHours == 0) {
            totalRentHours += 1;
        }
        final long rentDays = totalRentHours / 24;

        return new BriefTimeData(totalRentHours, rentDays);
    }

    public static String paraphraseDayAndHoursSentence(BriefTimeData timeData) {
        return new StringJoiner(StringUtils.EMPTY)
            .add(String.valueOf(timeData.days()))
            .add(timeData.days() == 1 ? " dzień" : " dni")
            .add(", ")
            .add(String.valueOf(timeData.allHours()))
            .add(timeData.allHours() == 1 ? " godzina" : " godzin(y)")
            .toString();
    }

    public static String toISO8601Format(String dateTimeAsStr) {
        return dateTimeAsStr.replace('T', ' ');
    }

    public static LocalDateTime fromISO8601Format(String dateTimeAsStr) {
        return LocalDateTime.parse(dateTimeAsStr.replace(' ', 'T'));
    }
}
