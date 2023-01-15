package tgtools.util;

import tgtools.exceptions.APPErrorException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 日期帮助类
 *
 * @author tianjing
 */
public class DateUtil {
    private static String LONG_TIME = "yyyy-MM-dd HH:mm:ss";
    private static String Full_LONG_TIME = "yyyy-MM-dd HH:mm:ss.SSS";

    private static String SHORT_TIME = "yyyy-MM-dd";

    public static long ONE_DAY_SECONDS = 86400L;

    /**
     * 将时间格式化成 yyyy-MM-dd HH:mm:ss
     *
     * @param date 需要转换的时间对象
     * @return
     */
    public static String formatLongtime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(LONG_TIME);
        return sdf.format(date);
    }

    /**
     * 将时间格式化成 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param date 要转换的时间
     * @return 返回时间字符串
     */
    public static String formatFullLongtime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(Full_LONG_TIME);
        return sdf.format(date);
    }

    /**
     * @param date   要转换的时间
     * @param custem 时间格式 如：yyyy-MM-dd HH:mm:ss.SSS
     * @return
     */
    public static String formatCustemtime(Date date, String custem) {
        SimpleDateFormat sdf = new SimpleDateFormat(custem);
        return sdf.format(date);
    }

    /**
     * 将字符串转换成时间 "yyyy-MM-dd"
     *
     * @param time
     * @return
     * @throws APPErrorException
     */
    public static Date parseShotmtime(String time) throws APPErrorException {
        return parseCustemtime(time, SHORT_TIME);
    }

    /**
     * 将字符串转换成时间 "yyyy-MM-dd HH:mm:ss"
     *
     * @param time 需要转换的时间字符串
     * @return
     * @throws APPErrorException
     */
    public static Date parseLongmtime(String time) throws APPErrorException {
        return parseCustemtime(time, LONG_TIME);
    }

    /**
     * 将字符串转换成时间 "yyyy-MM-dd HH:mm:ss.SSS"
     *
     * @param time 需要转换的时间字符串
     * @return
     * @throws APPErrorException
     */
    public static Date parseFullLongmtime(String time) throws APPErrorException {
        return parseCustemtime(time, Full_LONG_TIME);
    }

    /**
     * 将字符串转换成时间
     *
     * @param time   需要转换的时间字符串
     * @param custem 时间的格式 如：yyyy-MM-dd HH:mm:ss.SSS
     * @return
     * @throws APPErrorException
     */
    public static Date parseCustemtime(String time, String custem) throws APPErrorException {
        SimpleDateFormat sdf = new SimpleDateFormat(custem);
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            throw new APPErrorException("时间转换失败，格式：" + custem + ";值：" + time);
        }
    }

    /**
     * 将时间格式化成 yyyy-MM-dd
     *
     * @param date 需要转换的时间对象
     * @return
     */
    public static String formatShortTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(SHORT_TIME);
        return sdf.format(date);
    }

    /**
     * 两个时间的相差的天数
     *
     * @param one 日期 减数
     * @param two 日期 被减数
     * @return
     */
    public static long getDiffDays(Date one, Date two) {
        return (one.getTime() - two.getTime()) / 86400000L;
    }

    /**
     * 增加天数
     *
     * @param date1 日期
     * @param days  需要加入天数
     * @return
     */
    public static Date addDays(Date date1, long days) {
        return addSeconds(date1, days * ONE_DAY_SECONDS);
    }

    /**
     * 增加秒数
     *
     * @param date1 日期
     * @param secs  需要增加的秒数
     * @return
     */
    public static Date addSeconds(Date date1, long secs) {
        return new Date(date1.getTime() + secs * 1000L);
    }

    /**
     * 将日期转换成短日期 yyyy-MM-dd
     *
     * @param pDate 日期字符串
     * @return
     */
    public static Date parseShortDate(String pDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(SHORT_TIME);
        try {
            return sdf.parse(pDate);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 将日期转换成长日期 yyyy-MM-dd HH:mm:ss
     *
     * @param pDate 日期字符串
     * @return
     */
    public static Date parseLongDate(String pDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(LONG_TIME);
        try {
            return sdf.parse(pDate);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 将时间转换成 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param pDate 需要转换的时间
     * @return
     */
    public static String formatLongestDate(Date pDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            return sdf.format(pDate);
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 获取一个最小的时间对象（可以理解为初始化）
     *
     * @return
     */
    public static Date getMinDate() {
        return new Date(0);
    }

    /**
     * 获取一个最小的时间对象（可以理解Long 最大值）
     *
     * @return
     */
    public static Date getMaxDate() {
        return new Date(Long.MAX_VALUE);
    }

    /**
     * 获取两个时间的差值 (one - two)
     *
     * @param pOne
     * @param pTwo
     * @return
     */
    public static long DateMinus(Date pOne, Date pTwo) {
        return pOne.getTime() - pTwo.getTime();
    }

    /**
     * 判断是否是null或者最小值
     *
     * @param pDate
     * @return
     */
    public static boolean isNullOrMinDate(Date pDate) {
        if (null == pDate) {
            return true;
        }
        if (0 == pDate.getTime()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Date date = new Date(System.currentTimeMillis());
        java.sql.Timestamp dd = new java.sql.Timestamp(System.currentTimeMillis());
        String str = formatFullLongtime(date);
        System.out.print(str);
    }

    public static Date getTodayOfDay() {
        ZonedDateTime today1 = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault());
        return new Date(today1.toEpochSecond() * 1000);
    }
}
