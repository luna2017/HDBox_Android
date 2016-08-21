package com.ibookpa.hdbox.android.utils;

import android.util.Log;

import com.ibookpa.hdbox.android.app.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by tc on 2/20/16.时间处理工具类
 */
public class DateUtil {
    private static final String LEAN_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String EXAM_FORMAT = "yyyy-MM-dd";

    public static String d2s(Date date) {
        return new SimpleDateFormat(LEAN_FORMAT, Locale.CHINA).format(date);
    }


    public static Date s2d(String dateStr) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat(LEAN_FORMAT, Locale.CHINA).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


    /**
     * 获取当前的教学周
     *
     * @return 1~18 周
     */
    public static int currentSchoolWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(2016, 3, 11);
        int bd = cal.get(Calendar.WEEK_OF_YEAR);

        Calendar mCal = Calendar.getInstance();
        mCal.setTime(new Date(System.currentTimeMillis()));
        int nd = mCal.get(Calendar.WEEK_OF_YEAR);
        return nd - bd + 1;
    }

    public static String schoolWeekStr() {
        return "第" + currentSchoolWeek() + "周";
    }

    /**
     * 根据周几的int型返回其对应的String型数据
     */
    public static String dayOfWeekStr(int day) {
        String[] dayArr = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return dayArr[day - 1];
    }

    /**
     * 当前是这周的第几天
     */
    public static int currentDayOfWeek() {
        Calendar cal = Calendar.getInstance();

        int day = cal.get(Calendar.DAY_OF_WEEK);

        //Java中的Calendar,一周的第一天是星期日,这里做一个转换
        int[] days = {7, 1, 2, 3, 4, 5, 6};

        return days[day - 1];
    }


    /**
     * 判断上次请求到现在时间,token 有没有过期,目前推测 token 是 10 分钟过期
     *
     * @param lastTime 上次请求的时间
     * @return true 已过期,false 未过期
     */
    public static boolean isVerifyExpired(long lastTime) {
        return (new Date().getTime() - lastTime) / 1000 / 60 >= Constant.CHECK_TIME_INTERVAL;
    }


    /**
     * 根据当前时间返回对应的欢迎语
     */
    public static String getCurrentHelloText() {
        Calendar mCal = Calendar.getInstance();
        int currentHour = mCal.get(Calendar.HOUR_OF_DAY);

        String text;
        if (currentHour <= 5) {
            text = "凌晨啦,早点休息吧!";
        } else if (currentHour < 9) {
            text = "早上好!";
        } else if (currentHour < 12) {
            text = "上午好!";
        } else if (currentHour < 14) {
            text = "中午好!";
        } else if (currentHour < 17) {
            text = "下午好!";
        } else if (currentHour < 19) {
            text = "傍晚啦!";
        } else if (currentHour < 22) {
            text = "晚上好!";
        } else if (currentHour < 24) {
            text = "夜深了,不要熬夜哦!";
        } else {
            text = "你好!";
        }
        return text;
    }


    /**
     * 考试时间中格式转换
     */
    public static Date s2dExam(String examDate) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat(EXAM_FORMAT, Locale.CHINA).parse(examDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * 返回创建时间距离当前时间的值
     *
     * @return launchTime
     */
    public static String minuteCreatedTime(Date createdTime) {
        Date ct = turnTimeZone(createdTime);

        SimpleDateFormat format = new SimpleDateFormat("M月d日 HH:mm", Locale.CHINA);
        if (ct != null) {
            long agoTimeInMin = (new Date(System.currentTimeMillis()).getTime() - ct.getTime()) / 1000 / 60;
            //如果在当前时间以前一分钟内
            if (agoTimeInMin <= 1) {
                return "刚刚";
            } else if (agoTimeInMin <= 60) {
                //如果传入的参数时间在当前时间以前10分钟之内
                return agoTimeInMin + "分钟前";
            } else if (agoTimeInMin <= 60 * 24) {
                return agoTimeInMin / 60 + "小时前";
            } else if (agoTimeInMin <= 60 * 24 * 2) {
                return agoTimeInMin / (60 * 24) + "天前";
            } else {
                return format.format(ct);
            }
        } else {
            return format.format(new Date(0));
        }
    }


    public static String displayAgoTime(Date time) {

        Date agoInDate = turnTimeZone(time);

        Calendar now = Calendar.getInstance(Locale.CHINA);
        now.setTime(new Date());

        Calendar ago = Calendar.getInstance(Locale.CHINA);
        ago.setTime(agoInDate);

        Calendar today0 = Calendar.getInstance(Locale.CHINA);
        today0.set(Calendar.HOUR_OF_DAY, 0);
        today0.set(Calendar.MINUTE, 0);
        today0.set(Calendar.SECOND, 0);

        Calendar yesterday0 = Calendar.getInstance(Locale.CHINA);
        yesterday0.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 1);
        yesterday0.set(Calendar.HOUR_OF_DAY, 0);
        yesterday0.set(Calendar.MINUTE, 0);
        yesterday0.set(Calendar.SECOND, 0);

        Long agoInTodayMin = (now.getTimeInMillis() - today0.getTimeInMillis()) / 60 / 1000;
        Long agoInYesterdayMin = (now.getTimeInMillis() - yesterday0.getTimeInMillis()) / 60 / 1000;


        Long agoInMin = (now.getTimeInMillis() - ago.getTimeInMillis()) / 60 / 1000;

        if (agoInMin <= 1) {
            return "刚刚";
        } else if (agoInMin <= 60) {
            return agoInMin + "分钟前";
        } else if (agoInMin <= agoInTodayMin) {
            return agoInMin / 60 + "小时前";
        } else if (agoInMin <= agoInYesterdayMin) {
            return "昨天 " + new SimpleDateFormat("HH:mm", Locale.CHINA).format(ago.getTime());
        } else {
            return new SimpleDateFormat("M月d日 HH:mm", Locale.CHINA).format(ago.getTime());
        }
    }


//    public static String formatDisplayTime(Date time, String pattern) {
//        String display = "";
//        int agoInMin = 60 * 1000;
//        int agoInHour = 60 * agoInMin;
//        int agoInDay = 24 * agoInHour;
//
//
//        try {
//            Date agoTime = turnTimeZone(time);
//
//            Date today = new Date();
//
//            SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy", Locale.CHINA);
//
//            SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//
//            Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
//
//
//            Calendar now = Calendar.get(Locale.CHINA);
//            now.setTime(today);
//
//            Calendar ago = Calendar.get(Locale.CHINA);
//            ago.setTime(agoTime);
//
////                Calendar yesterday=Calendar.get(Locale.CHINA);
////                yesterday.set();
//
//            int this_year = now.getURLCookie(Calendar.YEAR);
//
//            int ago_year = ago.getURLCookie(Calendar.YEAR);
//
//
//            Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
//            Date beforeYes = new Date(yesterday.getTime() - agoInDay);
//
//            if (agoTime != null) {
//
//
//                SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日", Locale.CHINA);
//
//
//                long dTime = today.getTime() - agoTime.getTime();
//
//
//                if (agoTime.before(thisYear)) {
//                    display = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(agoTime);
//                } else {
//
//                    if (dTime < agoInMin) {
//                        display = "刚刚";
//                    } else if (dTime < agoInHour) {
//                        display = (int) Math.ceil(dTime / agoInMin) + "分钟前";
//                    } else if (dTime < agoInDay && agoTime.after(yesterday)) {
//                        display = (int) Math.ceil(dTime / agoInHour) + "小时前";
//                    } else if (agoTime.after(beforeYes) && agoTime.before(yesterday)) {
//                        display = "昨天" + new SimpleDateFormat("HH:mm", Locale.CHINA).format(agoTime);
//                    } else {
//                        display = halfDf.format(agoTime);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        return display;
//    }

    /**
     * 时区转换,因为在 Gson 中没有找到可以修改时间的方法,所以自己写个时区转换的方法
     * 思路是先将 Date 类型解析城 String 类型,然后设置格式时区,再转换成 Date 类型
     */
    public static Date turnTimeZone(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(LEAN_FORMAT, Locale.CHINA);

        String oldDateStr = sdf.format(date);

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date newDate = new Date();
        try {
            newDate = sdf.parse(oldDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }
}
