package no.timesaver.dao;


import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.TimeZone;

public interface AbstractDao {

    default String getValidNowSql(String identifier) {
        return getValidNowSql(identifier,true);
    }
    default String getValidNowSql(String identifier,boolean and) {
        String timeZone = getShortTimeZone();

        identifier = StringUtils.isEmpty(identifier) ? "": identifier+".";
//        return (and ? " and " : " ") + identifier+"valid_from <= current_timestamp AT TIME ZONE '"+timeZone+"' and "+identifier+"valid_to >= current_timestamp AT TIME ZONE '"+timeZone+"'";
        return (and ? " and " : " ") + identifier+"valid_from <= current_timestamp and "+identifier+"valid_to >= current_timestamp";
    }

    default String getShortTimeZone() {
        Calendar now = Calendar.getInstance();
        TimeZone tz = now.getTimeZone();
        return tz.getDisplayName(false,TimeZone.SHORT);
    }
}
