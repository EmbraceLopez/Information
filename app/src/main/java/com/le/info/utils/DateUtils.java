package com.le.info.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class DateUtils {

    /**
     * 获取当前年-月-日
     *
     * @return
     */

    public static String StringData() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }


    /**
     * 解析出时间年-月-日
     */
    public static String formatData(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 解析出时间年-月-日 时：分：秒
     */
    public static String formatDataDetail(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 根据当前日期获得是星期几
     *
     * @return
     */
    public static String getWeek(String time) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "星期天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "星期一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "星期二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "星期三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "星期四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "星期五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "星期六";
        }
        return Week;
    }


    /**
     * 获取今天往后一周的日期（月/号）更新版本
     */
    public static List<String> getSevendateNew() {
        List<String> dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        c.setTime(new Date());
        for (int i = 0; i < 7; i++) {
            String month = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
            String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
            String date = month + "/" + day;
            dates.add(date);
            //加一天
            c.add(Calendar.DAY_OF_MONTH, +1);
        }
        return dates;
    }


    /**
     * 得到当年当月的最大日期
     **/
    public static int MaxDayFromDay_OF_MONTH(int year, int month) {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
        time.set(Calendar.MONTH, month - 1);//注意,Calendar对象默认一月为0
        return time.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

}