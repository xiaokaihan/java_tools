package com.key.mail.comm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * 该类是“日期时间”的工具类，提供了一些常用的格式化日期时间的方法
 * <p>
 * 线程安全：该类线程安全。因为它是一个不可变类。
 *
 * @author Key.Xiao
 * @version 1.0
 */
public enum DateTimeFormatter {

	/** yyyyMMdd */
	date1("yyyyMMdd"),

	/** yyyy-MM-dd */
	date2("yyyy-MM-dd"),

	/** HHmmss */
	time1("HHmmss"),

	/** HH:mm:ss,SSS */
	time2("HH:mm:ss,SSS"),

	/** HH:mm */
	time3("HH:mm"),

	/** HHmm */
	time4("HHmm"),

	/** yyyy-MM-dd HH:mm:ss SSS */
	datetime1("yyyy-MM-dd HH:mm:ss SSS"),

	/** yyyyMMdd_HHmmss_SSS */
	datetime2("yyyyMMdd_HHmmss_SSS"),

	/** yyyy/MM/dd HH:mm:ss.SSS */
	datetime3("yyyy/MM/dd HH:mm:ss.SSS"),

	/** yyyyMMddHHmmssSSS */
	datetime4("yyyyMMddHHmmssSSS"),

	/** yyMMddHHmmssSSS */
	datetime5("yyMMddHHmmssSSS"),

	/** yyyyMMddHHmmss */
	datetime6("yyyyMMddHHmmss"),

	/** yyyy-MM-dd HH:mm:ss */
	datetime10("yyyy-MM-dd HH:mm:ss");

	/** 使用的格式字符串 */
	private String format;
	/** 用于格式化 */
	private SimpleDateFormat formatter;

	/**
	 * 传入一个格式字符串，指定当前枚举对象使用的格式
	 *
	 * @param format
	 *            使用的日期格式
	 */
	private DateTimeFormatter(String format) {
		this.format = format;
		formatter = new SimpleDateFormat(format);
		// 在parse()时，严格匹配
		formatter.setLenient(false);
	}

	/**
	 * 得到当前枚举对象对应的格式字符串
	 *
	 * @return 当前枚举对象对应的格式字符串
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * 使用预定义的格式，格式化一个日期
	 *
	 * @param date
	 *            欲格式化的日期
	 * @return 格式化之后的字符串
	 * @throws IllegalArgumentException
	 *             如果date为null
	 */
	public synchronized String format(Date date) {
		ArgumentValidator.notNull(date, "date");
		return formatter.format(date);
	}

	/**
	 * 使用预定义的格式，格式化一个毫秒值
	 *
	 * @param timeMillis
	 *            欲格式化的毫秒值
	 * @return 格式化之后的字符串
	 * @throws IllegalArgumentException
	 *             如果timeMillis<=0
	 */
	public synchronized String format(long timeMillis) {
		return formatter.format(timeMillis);
	}

	/**
	 * 将一个时间字符串解析为对应的Date对象
	 *
	 * @param datetime
	 *            待解析的字符串
	 * @return 对应的Date对象
	 * @throws IllegalArgumentException
	 *             如果datetime为null或""或trimmed empty
	 * @throws ParseException
	 *             解析时发生错误
	 */
	public synchronized Date parse(String datetime) throws ParseException {
		ArgumentValidator.notNullOrTrimmedEmpty(datetime, "datetime");
		ArgumentValidator.isTrue(datetime.length() == getFormat().length(), "Length of datetime and " + getFormat()
				+ " should be the same: " + datetime);
		return formatter.parse(datetime);
	}

	/**
	 * 如果一个字符串中有"#{time_patten}"这样的占位符，则尝试以SimpleDateFormat中的时间模式、并使用指定的date对它进行解析。如果解析成功，则替换并返回新的字符串；否则返回原字符串。
	 *
	 * 如果参数text为null，则返回null。如果参数date为null，则默认使用当前时间。
	 *
	 * @param text
	 *            待解析的字符串
	 * @param timeMillis
	 *            使用什么时间
	 * @param throwException
	 *            如果text中包含的时间格式有误,是否抛出IllegalArgumentException
	 * @return 解析后的字符串。如果解析失败，则返回原字符串。
	 * @throws IllegalArgumentException
	 *             如果timeMillis<=0, 或当throwException为true时,text中包含的时间格式有误
	 */
	public static String replaceDateTime(String text, long timeMillis, boolean throwException) {
		if (StringUtils.isBlank(text))
			return text;
		String[] formats = StringUtils.substringsBetween(text, "#{", "}");
		if (formats == null || formats.length == 0)
			return text;
		for (String format : formats) {
			if (StringUtils.isBlank(format)) {
				if (throwException)
					throw new IllegalArgumentException("text contains empty datetime pattern: " + text);
				continue;
			}
			try {
				String destStr = new SimpleDateFormat(format).format(timeMillis);
				text = StringUtils.replaceOnce(text, "#{" + format + "}", destStr);
			} catch (IllegalArgumentException e) {
				if (throwException)
					throw e;
				continue;
			}
		}
		return text;
	}

	/**
	 * 使用指定的格式字符串来格式化一个日期
	 *
	 * @param format
	 *            指定的格式字符串
	 * @param date
	 *            欲格式化的日期
	 * @return 格式化之后的字符串
	 * @throws IllegalArgumentException
	 *             如果format为null或trimmed empty，或date为null
	 */
	public static String format(String format, Date date) {
		ArgumentValidator.notNullOrTrimmedEmpty(format, "format");
		ArgumentValidator.notNull(date, "date");
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * 使用指定的格式字符串来格式化一个毫秒值
	 *
	 * @param format
	 *            指定的格式字符串
	 * @param timeMillis
	 *            欲格式化的毫秒值
	 * @return 格式化之后的字符串
	 * @throws IllegalArgumentException
	 *             如果fomrat为null或trimmed empty，或timeMillis<=0
	 */
	public static String format(String format, long timeMillis) {
		ArgumentValidator.notNullOrTrimmedEmpty(format, "format");
		return new SimpleDateFormat(format).format(timeMillis);
	}

	/**
	 * 将一个时间字符串按指定的格式解析为一个Date对象
	 *
	 * @param format
	 *            指定时间格式
	 * @param datetime
	 *            待解析的时间字符串
	 * @return 对应的Date对象
	 * @throws IllegalArgumentException
	 *             如果format或datetime为null或trimmed empty
	 * @throws ParseException
	 *             解析出错
	 */
	public static Date parse(String format, String datetime) throws ParseException {
		ArgumentValidator.notNullOrTrimmedEmpty(format, "format");
		ArgumentValidator.notNullOrTrimmedEmpty(datetime, "datetime");
		ArgumentValidator.isTrue(format.length() == datetime.length(),
				"Length of format and datetime should be the same: " + format + ", " + datetime);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
		return sdf.parse(datetime);
	}

}
