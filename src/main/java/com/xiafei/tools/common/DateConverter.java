package com.xiafei.tools.common;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * <P>Description: 时间转换工具类 </P>
 * <P>CALLED BY:   王国梁 </P>
 * <P>UPDATE BY:   王国梁 </P>
 * <P>CREATE DATE: 2018/11/1</P>
 * <P>UPDATE DATE: 2018/11/1</P>
 *
 * @author wangguoliang
 * @version 1.0
 * @since java 1.8.0
 */
public class DateConverter {

    /**
     * 时间格式
     */
    public static final String YMD_STR = "yyyy-MM-dd";

    /**
     * 时间格式
     */
    public static final String YMD_STR1 = "yyyyMMdd";

    /**
     * 时间格式(时分秒)
     */
    public static final String YMD_HMS_STR = "yyyy-MM-dd HH:mm:ss";

    /**
     * 字符串转时间
     * @param strDate 时间类型字符串
     * @param strFormat 时间格式
     * @return
     */
    public static Date str2Date(String strDate, String strFormat) {
        if (StringUtils.isBlank(strDate)){
            return null;
        }
        if (StringUtils.isBlank(strFormat)) {
            strFormat = YMD_STR;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {

        }
        return date;
    }

    /**
     * 时间转字符串
     * @param date 时间
     * @param formatStr 时间格式
     * @return
     */
    public static String date2Str(Date date, String formatStr) {
        if (date == null){
            return null;
        }
        if (StringUtils.isBlank(formatStr)) {
            formatStr = YMD_STR;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String formatDate = sdf.format(date);
        return formatDate;
    }

    /**
     * 字符串转LocalDate
     * @param strDate
     * @param strFormat
     * @return
     */
    public static LocalDate str2LocalDate(String strDate, String strFormat){
        if (strFormat==null){
            strFormat = "yyyy-MM-dd";
        }
        return LocalDate.parse(strDate, DateTimeFormatter.ofPattern(strFormat));
    }

    /**
     * Date转LocalDate
     * @param date
     * @return
     */
    public static LocalDate date2LocalDate(Date date){
        if (date==null){
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }

    /**
     * LocalDate转Date
     * @param localDate
     * @return
     */
    public static Date localDate2Date(LocalDate localDate){
        if (localDate==null){
            return null;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    /**
     * 日期间隔天数
     *
     * @param start
     * @param end
     * @return
     */
    public static long substractDate(LocalDate start, LocalDate end){
        long days  = end.toEpochDay() - start.toEpochDay();
        return days;
    }

    /**
     * 将（2012年10月15日至2032年10月14日）类型字符串转换为 yyyy-MM-dd 数组
     * @param date
     * @return
     */
    public static String[] dateCN2Date(String date){
        if (StringUtils.isBlank(date)){
            return null;
        }

        if (date.contains("failed")){
            String[] dates = new String[2];
            dates[0] = "2099-12-31";
            dates[1] = "2099-12-31";
            return dates;
        }

        String newDate = date.replace("年","-").replace("月","-").replace("日","");
        String[] newDates = newDate.split("至");
        return newDates;
    }

    public static Date beforeDate(Date date){
        if (date==null){
            return null;
        }
        LocalDate localDate = date2LocalDate(date);
        localDate.minusDays(1);
        Date beforeDate = localDate2Date(localDate);
        return beforeDate;
    }

}
