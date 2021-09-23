package com.blog.utils;

import cn.hutool.core.date.DateUtil;

import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 时间工具类
 *
 * @author yujunhong
 * @date 2021/7/12 14:23
 */
public class DateUtils extends DateUtil {
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        return parseDate(str.toString());
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 根据时间 获取到月初0点 月末23:23:59:999
     *
     * @param tempDate 时间
     * @return
     */
    public static List<Date> getMonthDate(Date tempDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tempDate);
        List<Date> dates = new ArrayList<>();
        //将日期为1号
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND, 0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        //获得当前月第一天
        Date sdate = calendar.getTime();
        dates.add(sdate);
        //将当前月加1；
        calendar.add(Calendar.MONTH, 1);
        //在当前月的下一月基础上减去1毫秒
        calendar.add(Calendar.MILLISECOND, -1);
        //获得当前月最后一天
        Date edate = calendar.getTime();
        dates.add(edate);
        return dates;
    }


    /**
     * 获取 时间范围类的 天
     *
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return 返回每天的时间集合
     */
    public static List<Date> getRangeDay(Date beginDate, Date endDate) {
        List<Date> returns = new ArrayList<>();
        //开始时间
        Calendar beginDateCalendar = Calendar.getInstance();
        beginDateCalendar.setTime(beginDate);
        //结束时间
        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate);
        while (beginDateCalendar.getTimeInMillis() != endDateCalendar.getTimeInMillis()) {
            //加入时间
            returns.add(beginDateCalendar.getTime());
            //添加一天
            beginDateCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        //加入最后一天
        returns.add(beginDateCalendar.getTime());
        return returns;
    }

    /**
     * 获取num 天的时间(不包括本日)
     *
     * @param maintainDate 时间
     * @param num          天数
     * @param flag         num天前
     * @return 时间集合
     */
    public static List<Date> getRangeDay(Date maintainDate, int num, boolean flag) {
        List<Date> returnDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        //要计算 maintainDate 前的 num 天 从小到大添加
        if (flag) {
            for (int i = num; i > 0; i--) {
                calendar.setTime(maintainDate);
                calendar.add(Calendar.DAY_OF_MONTH, -i);
                returnDates.add(calendar.getTime());
            }
        }
        //要计算 maintainDate 后的 num 天 从小到大添加
        else {
            for (int i = 1; i <= num; i++) {
                calendar.setTime(maintainDate);
                calendar.add(Calendar.DAY_OF_MONTH, i);
                returnDates.add(calendar.getTime());
            }
        }
        return returnDates;
    }

    /**
     * 获取第num天的时间
     *
     * @param maintainDate 时间
     * @param num          天数
     * @return 时间集合
     */
    public static Date getRangeDay(Date maintainDate, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(maintainDate);
        calendar.add(Calendar.DAY_OF_MONTH, num);
        return calendar.getTime();
    }

    /**
     * 判断 两个日期是否在同一月
     *
     * @param specifiedDate 指定日期
     * @param date          判定日期
     * @return 时间集合
     */
    public static boolean getTheSameMonth(Date specifiedDate, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(specifiedDate);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        return calendar.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH);
    }

    /**
     * 获取日期是星期几
     *
     * @param date 时间
     * @return 星期几 字符串
     */
    public static String getWeek(Date date) {
        //计算星期几
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String weekStr = "";
        switch (week) {
            case 1:
                weekStr = "星期日";
                break;
            case 2:
                weekStr = "星期一";
                break;
            case 3:
                weekStr = "星期二";
                break;
            case 4:
                weekStr = "星期三";
                break;
            case 5:
                weekStr = "星期四";
                break;
            case 6:
                weekStr = "星期五";
                break;
            case 7:
                weekStr = "星期六";
                break;
            default:
                break;
        }
        return weekStr;
    }

    /**
     * 获取固定间隔时刻集合
     *
     * @param start    开始时间
     * @param end      结束时间
     * @param interval 时间间隔(单位：分钟)
     * @return 时间list
     * @author xxy
     */
    public static List<String> getIntervalTimeList(String start, String end, int interval) {
        Date startDate = dateTime("yyyy-MM-dd HH:mm:ss", start);
        Date endDate = dateTime("yyyy-MM-dd HH:mm:ss", end);
        List<String> list = new ArrayList<>();
        while (startDate.getTime() <= endDate.getTime()) {
            list.add(parseDateToStr("yyyy-MM-dd HH:mm:ss", startDate));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.MINUTE, interval);
            if (calendar.getTime().getTime() > endDate.getTime()) {
                if (!startDate.equals(endDate)) {
                    list.add(parseDateToStr("yyyy-MM-dd HH:mm:ss", endDate));
                }
                startDate = calendar.getTime();
            } else {
                startDate = calendar.getTime();
            }

        }
        return list;
    }

    /**
     * 日期减一个月
     *
     * @param rateDate 日期格式 yyyy-mm
     * @return 日期 yyyy-mm
     * @author xxy
     */
    public static String DateAddOneMonth(String rateDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = sdf.parse(rateDate);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(Calendar.MONTH, -1);
        return sdf.format(rightNow.getTime());
    }

    /**
     * 根据时间 获取到本日0点 本日23:23:59:999
     *
     * @param tempDate 时间
     * @return
     */
    public static List<Date> getDayDate(Date tempDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tempDate);
        List<Date> dates = new ArrayList<>();
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND, 0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        //获得当前月第一天
        Date sdate = calendar.getTime();
        dates.add(sdate);
        //将当前日加1；
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        //在当前月的下一日基础上减去1毫秒
        calendar.add(Calendar.MILLISECOND, -1);
        //获得当前日最后时间
        Date edate = calendar.getTime();
        dates.add(edate);
        return dates;
    }

    /**
     * 得到当前时间减去一个月
     *
     * @return Date
     * @author xxy
     */
    public static Date getLastMonthDate() {
        Calendar calendar = Calendar.getInstance(); //创建Calendar 的实例
        calendar.add(Calendar.MONTH, -1);//当前时间减去一个月，即一个月前的时间
        return calendar.getTime();//获取一年前的时间，或者一个月前的时间
    }

    /**
     * 根据身份证号计算到当前时间年龄
     *
     * @param cardId 传入身份证号
     * @return 该对象年龄
     */
    public static int calculationAge(String cardId) {
        int age = 0;
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        int year = Integer.valueOf(cardId.substring(6, 10));
        int month = Integer.valueOf(cardId.substring(10, 12));
        int day = Integer.valueOf(cardId.substring(12, 14));

        if ((month < monthNow) || (month == monthNow && day <= dayNow)) {
            age = yearNow - year;
        } else {
            age = yearNow - year - 1;
        }

        return age;
    }

    /**
     * 10位int型的时间戳转换为String(yyyy-MM-dd)
     *
     * @param time
     * @return
     */
    public static String timestampToString(Integer time) {
        //int转long时，先进行转型再进行计算，否则会是计算结束后在转型
        long temp = (long) time * 1000;
        Timestamp ts = new Timestamp(temp);
        String tsStr = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            //方法一
            tsStr = dateFormat.format(ts);
            System.out.println(tsStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tsStr;
    }

    /**
     * 10位int型的时间戳转换为String(yyyy-MM-dd HH:mm:ss)
     *
     * @param time
     * @return
     */
    public static String timestampToStringTen(Integer time) {
        //int转long时，先进行转型再进行计算，否则会是计算结束后在转型
        long temp = (long) time * 1000;
        Timestamp ts = new Timestamp(temp);
        String tsStr = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //方法一
            tsStr = dateFormat.format(ts);
            System.out.println(tsStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tsStr;
    }

    /**
     * String(yyyy-MM-dd HH:mm:ss)转10位时间戳
     *
     * @param time
     * @return
     */
    public static Integer StringToTimestamp(String time) {

        int times = 0;
        try {
            times = (int) ((Timestamp.valueOf(time).getTime()) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;

    }

    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime   传入时间
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 在时间段内返回true 不在时间内返回false
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime,
                                         Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        //在时间范围内返回true  和传入时间同一天返回true 其他返回false
        if (date.after(begin) && date.before(end)) {
            return true;
        } else if (nowTime.compareTo(beginTime) == 0 || nowTime.compareTo(endTime) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据身份证的号码算出当前身份证持有者的性别和年龄 18位身份证 根据身份证以及年份计算性别以及年龄
     *
     * @return
     * @throws Exception
     */
    public static String getCarInfo(String CardCode) {
        Map<String, Object> map = new HashMap<String, Object>();
        String year = CardCode.substring(6).substring(0, 4);// 得到年份
        String yue = CardCode.substring(10).substring(0, 2);// 得到月份
        // String day=CardCode.substring(12).substring(0,2);//得到日
        int years = 0;
        if (Integer.parseInt(CardCode.substring(16, 17)) % 2 == 0) {// 判断性别
            years = Integer.valueOf(year) + 55;
        } else {
            years = Integer.valueOf(year) + 60;
        }
//        Date date = new Date();// 得到当前的系统时间
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String fyear = format.format(date).substring(0, 4);// 当前年份
//        String fyue = format.format(date).substring(5, 7);// 月份
//        // String fday=format.format(date).substring(8,10);
//        int age = 0;
//        if (Integer.parseInt(yue) <= Integer.parseInt(fyue)) { // 当前月份大于用户出身的月份表示已过生
//            age = Integer.parseInt(fyear) - Integer.parseInt(year) + 1;
//        } else {// 当前用户还没过生
//            age = Integer.parseInt(fyear) - Integer.parseInt(year);
//        }
//        int age = 0;
//        age = Integer.parseInt(fyear) - Integer.parseInt(year);
//        map.put("sex", sex);
//        map.put("age", age);
        return String.valueOf(years);
    }

    /**
     * 15位身份证的验证 根据身份证以及年份计算性别以及年龄
     *
     * @param
     * @throws Exception
     */
    public static Map<String, Object> getCarInfo15W(String card, String fyear)
            throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        String uyear = "19" + card.substring(6, 8);// 年份
        String uyue = card.substring(8, 10);// 月份
        // String uday=card.substring(10, 12);//日
        String usex = card.substring(14, 15);// 用户的性别
        String sex;
        if (Integer.parseInt(usex) % 2 == 0) {
            sex = "1";
        } else {
            sex = "0";
        }
//        Date date = new Date();// 得到当前的系统时间
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String fyear = format.format(date).substring(0, 4);// 当前年份
//        String fyue = format.format(date).substring(5, 7);// 月份
//        // String fday=format.format(date).substring(8,10);
//        int age = 0;
//        if (Integer.parseInt(uyue) <= Integer.parseInt(fyue)) { // 当前月份大于用户出身的月份表示已过生
//            age = Integer.parseInt(fyear) - Integer.parseInt(uyear) + 1;
//        } else {// 当前用户还没过生
//            age = Integer.parseInt(fyear) - Integer.parseInt(uyear);
//        }
        int age = 0;
        age = Integer.parseInt(fyear) - Integer.parseInt(uyear);
        map.put("sex", sex);
        map.put("age", age);
        return map;
    }

    /**
     * LocalDateTime转成String类型的时间
     * xxy
     *
     * @param time
     * @return
     */
    public static String LocalDateTimeToString(LocalDateTime time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return df.format(time);
    }

    /**
     * 获取某个日期 在加上 秒数的时间
     *
     * @param beforeDate yyyy-MM-dd HH:mm:ss
     * @param timeSecond 加减的秒数
     */
    public static String getDateStr(Date beforeDate, Long timeSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // 返回毫秒数 + 添加的毫秒数
            Long time = beforeDate.getTime() + timeSecond * 1000;
            return format.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当前的月
     *
     * @author yujunhong
     * @date 2021/7/12 14:24
     */
    public static Integer getMonth() {
        Calendar calendar = new GregorianCalendar(TimeZone
                .getDefault());
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前天
     *
     * @author yujunhong
     * @date 2021/7/12 14:24
     */
    public static Integer getDay() {
        Calendar calendar = new GregorianCalendar(TimeZone
                .getDefault());
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前的年
     *
     * @author yujunhong
     * @date 2021/7/12 14:25
     */
    public static Integer getYears() {
        Calendar calendar = new GregorianCalendar(TimeZone
                .getDefault());
        return calendar.get(Calendar.YEAR);

    }

    /**
     *
     *

     */

    /**
     * 获取几天之后的日期
     *
     * @param date yyyy-MM-dd HH:mm:ss
     * @param day  加减的天数
     * @return 日期
     * @author yujunhong
     * @date 2021/9/22 17:00
     */
    public static Date getDate(String date, int day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        try {
            Date beforeDate = format.parse(date);
            cal.setTime(beforeDate);
            cal.add(Calendar.DAY_OF_MONTH, day);
            return cal.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取过去指定天内的日期数组
     *
     * @param intervals intervals天内
     * @param formatStr 格式化字符串   yyyy-MM-dd
     * @return 日期数组
     * @author yujunhong
     * @date 2021/9/22 17:03
     */
    public static ArrayList<String> getDaysByArray(int intervals, String formatStr) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        for (int i = intervals - 1; i >= 0; i--) {
            pastDaysList.add(getPastDate(i, formatStr));
        }
        return pastDaysList;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past      指定前移天数
     * @param formatStr 日期格式
     * @return 日期字符串
     * @author yujunhong
     * @date 2021/9/22 17:04
     */
    public static String getPastDate(int past, String formatStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(today);
    }

    /**
     * 获取某个时间段内所有日期
     *
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return 获取某个时间段内所有日期
     * @author yujunhong
     * @date 2021/9/23 10:30
     */
    public static List<String> getDayBetweenDates(Date beginDate, Date endDate) {
        List<String> lDate = new ArrayList<>();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        lDate.add(sd.format(beginDate));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(beginDate);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(endDate);
        // 测试此日期是否在指定日期之后
        while (endDate.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(sd.format(calBegin.getTime()));
        }
        return lDate;
    }
}
