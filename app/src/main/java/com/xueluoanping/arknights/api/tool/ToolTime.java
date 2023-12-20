package com.xueluoanping.arknights.api.tool;

import android.util.Log;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ToolTime {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
    // 获取当前时区相对东八区的偏移值，注意getRawOffset的返回值正负号
    public static long getTimeOffset() {
        // Log.d("ToolTime", "getTimeOffset: "+TimeZone.getTimeZone("Asia/Shanghai").getRawOffset()+","+TimeZone.getDefault().getRawOffset());
        // TimeZone tz = TimeZone.getDefault();
        // System.out.println("TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezone id :: " +tz.getID());
      return   TimeZone.getTimeZone("Asia/Shanghai").getRawOffset()-TimeZone.getDefault().getRawOffset();
    }

    public static long getTimeShanghai() {
        // TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        // 这个时间不知道是不是可以穿过虚拟机获取系统的，所以手动设置时区
        return  System.currentTimeMillis()+getTimeOffset();
    }

    public static String getFormatDate(long lastFreshTs, boolean isUnix) {
        int baseMultiplier = 1;
        if (isUnix) baseMultiplier = 1000;
        Date date = new Date(new BigDecimal(lastFreshTs).multiply(new BigDecimal(baseMultiplier)).longValue());
        String YY = Integer.valueOf(String.format("%tY", date)).toString();
        String MM = Integer.valueOf(String.format("%tm", date)).toString();
        String dd = String.format(Locale.CHINA, "%td", date);
        String HH = String.format("%tk", date);
        String mm = String.format("%tM", date);
        return String.format("%s年%s月%s日%s时%s分", YY, MM, dd, HH, mm);
        // return String.format("%s时%s分", HH, mm);
    }

    // 存在争议
    public static long get0HourTodayMills() {
        long current =  getTimeShanghai();    //当前时间毫秒数
        // +1是因为这个写法自动向下取整
        long offset=TimeZone.getDefault().getRawOffset();
        // 这个+1是因为计算问题，会导致向下取整【存在争议】，取消
        long zeroT = (current / (1000 * 3600 * 24) )* (1000 * 3600 * 24) - offset;  //今天零点零分零秒的毫秒数
        return zeroT;
    }

    public static long transferString2Date(String s) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE).parse(s);
            return date.getTime();
        } catch (Exception e) {
            Log.e("时间转换错误, string = {}", s, e);
        }
        return 0;
    }

    public static long transferString2Date2(String s) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).parse(s);
            return date.getTime();
        } catch (Exception e) {
            Log.e("时间转换错误, string = {}", s, e);
        }
        return 0;
    }
}
