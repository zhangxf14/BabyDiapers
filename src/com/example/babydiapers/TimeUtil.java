package com.example.babydiapers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;

/**
 * 时间工具类
 * 
 * @author zhangxf
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	/**
	 * 获取当前时间
	 */
	public static String nowString() {		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式		
		return df.format(new Date());
	}
	
	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(time));
	}
		

	public static String getTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
	
	public static String getTime2(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
		return format.format(date);
	}
	
	public static String getTime3(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(date);
	}
	
	public static Date getDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date;
		try {
			date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}	
	
	public static Date getDate2(String str) {
//		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss 格林尼治标准时间+0800 yyyy", Locale.ENGLISH);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", new Locale("ENGLISH"));
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		Date date;
		try {
			date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static String getTimeWithoutYear(long time) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getTimeWithoutYear(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		return format.format(date);
	}
	
	public static String getYearMonthDay(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	public static long getWebTimeToLong(String time) {
		try {
		time = time.replace("T", " ");
		time = time.substring(0, time.lastIndexOf(":"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		
			date = format.parse(time);
			return date.getTime();
		} catch (ParseException e) {
		} catch (NullPointerException e) {
		}catch(Exception e){}
		return 0;
	}

	public static long getLong(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = format.parse(time);
			return date.getTime();
		} catch (ParseException e) {
		} catch (NullPointerException e) {
		}
		return 0;
	}
	
	public static long getLong(Date date) {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		Date date = null;
		try {
//			date = format.parse(time);
			return date.getTime();
//		} catch (ParseException e) {
		} catch (NullPointerException e) {
		}
		return 0;
	}
	
	public static long getLong2(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date = null;
		try {
			date = format.parse(time);
			return date.getTime();
		} catch (ParseException e) {
		} catch (NullPointerException e) {
		}
		return 0;
	}
	/*
	 *  将时间换算成天数
	 *  
	 */
	public static String getDays(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			long days= (date.getTime()-  format.parse(time).getTime())/(1000*60*60*24);			
			if(days%365==0 && days>0){
				return (days/365)+"年";
			}else if(days/365>=1 && days%365!=0){
				return (days/365)+"年"+(days%365)+"天";
			}else{
				return (days+"天");	
			}			
		} catch (ParseException e) {
		} catch (NullPointerException e) {
		}
		return "0天";
	}
	
	/*
	 *  将时间换算成x岁x月x天
	 *  
	 */
	public static String getYMD(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		
		long dd=1000*60*60*24;
		long mm=30*dd;
		long yy=12*mm;
		try {
			long delta=date.getTime()-  format.parse(time).getTime();
			long year=delta /yy;			
			long month=(delta-year*yy)/mm;
			long day= (delta-year*yy-month*mm)/dd;
			return year+"岁"+month+"个月"+day+"天";		
		} catch (ParseException e) {
		} catch (NullPointerException e) {
		}
		return "0岁0个月0天";
	}
	
	/**
	* 获取年龄
	* @return 
	*/
	public static String getAge(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = new Date();//构造当前日期
		Date d2 = new Date();
		try {
			if (time==null) {
				return 0 +"岁"+ 0+"个月"+0+"天";	
			}
			d2 = format.parse(time);
		} catch (ParseException e) {			
			e.printStackTrace();
			return 0 +"岁"+ 0+"个月"+0+"天";
		}
	    Calendar c1 = Calendar.getInstance();
	    Calendar c2 = Calendar.getInstance();
	    c1.setTime(d1);
	    c2.setTime(d2);
	    if(c1.getTimeInMillis() < c2.getTimeInMillis()) return "0岁0个月0天";
	    int year1 = c1.get(Calendar.YEAR);
	    int year2 = c2.get(Calendar.YEAR);
	    int month1 = c1.get(Calendar.MONTH);
	    int month2 = c2.get(Calendar.MONTH);
	    int day1 = c1.get(Calendar.DAY_OF_MONTH);
	    int day2 = c2.get(Calendar.DAY_OF_MONTH);
	    // 获取年的差值 假设 d1 = 2015-8-16 d2 = 2011-9-30
	    int yearInterval = year1 - year2;
	    // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
	    if(month1 < month2 || month1 == month2 && day1 < day2) yearInterval --;
	    // 获取月数差值
	    int monthInterval = (month1 + 12) - month2 ;
	    if(day1 < day2) monthInterval --;
	    monthInterval %= 12;
	    //获取天数差值
	    int dayInterval=day1-day2;
	    dayInterval=dayInterval<0?dayInterval+30:dayInterval;
	    return yearInterval +"岁"+ monthInterval+"个月"+dayInterval+"天";
	}


	/**
	 * 将毫秒数换算成x天x时x分x秒x毫秒
	 * 
	 * @return
	 */
	public static String[] formatDHMS(long ms) {
		String[] times = new String[4];
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;

		String strDay = times[0] = day < 10 ? "0" + day : "" + day;
		String strHour = times[1] = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = times[2] = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = times[3] = second < 10 ? "0" + second : "" + second;
		// String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : ""
		// + milliSecond;
		// strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : ""
		// + strMilliSecond;

		// return strDay + " " + strHour + ":" + strMinute + ":" + strSecond +
		// " "
		// + strMilliSecond;
		return times;
	}
	
	/**
	 * 将毫秒数换算成x天x时x分x秒x毫秒
	 * 
	 * @return
	 */
	public static String formatDHMS2(long ms) {
		String[] times = new String[4];
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;

		String strDay = times[0] = day < 10 ? "0" + day : "" + day;
		String strHour = times[1] = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = times[2] = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = times[3] = second < 10 ? "0" + second : "" + second;
		// String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : ""
		// + milliSecond;
		// strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : ""
		// + strMilliSecond;

		 return strDay + "d" + strHour + "h" + strMinute + "m" + strSecond +
		 "s";
//		 + strMilliSecond;
//		return times;
	}

	public static String getChatTime(long timesamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "今天 " + getHourAndMin(timesamp);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(timesamp);
			break;
		case 2:
			result = "前天 " + getHourAndMin(timesamp);
			break;

		default:
			// result = temp + "天前 ";
			result = getTime(timesamp);
			break;
		}

		return result;
	}
}
