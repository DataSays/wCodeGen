package org.dataagg.util.lang;

import jodd.util.StringUtil;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
	public final static String Encoding = "utf-8";
	public final static String NULL = "__null__";
	public final static String EMPTY = "____";
	public final static String DateFormat = "yyyyMMdd";
	public final static String TimeFormat = "yyyyMMddHHmmss";
	public final static String DisplayDateFormat = "yyyy-MM-dd";
	public final static String DisplayTimeFormat = "yyyy-MM-dd HH:mm:ss";

	public static String repeat(String tpl, List<String> items, String rmEndStr) {
		StringBuffer sb = new StringBuffer();
		for (String item : items) {
			sb.append(StringUtil.replace(tpl, "${0}", item));
		}
		String code = sb.toString().trim();
		if (rmEndStr != null && code.endsWith(rmEndStr)) {
			code = code.substring(0, code.length() - rmEndStr.length());
		}
		return code;
	}

	public static String toUrlParam(Object value) {
		if (value == null) { return NULL; }
		if (value instanceof Number) { return value.toString(); }
		if (value instanceof String && ((String) value).length() <= 0) { return EMPTY; }
		try {
			return URLEncoder.encode(value.toString(), Encoding);
		} catch (Exception e) {
			return value.toString();
		}
	}

	public static String findGroup1(String text, String regx) {
		Matcher matcher = Pattern.compile(regx).matcher(text);
		if (matcher.find()) { return matcher.group(1); }
		return null;
	}

	public static double round_half_up(double a, int newScale) {
		return new BigDecimal(Double.toString(a)).setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static String formatDate(Date date, String format) {
		if (date == null) { return ""; }
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
		return simpleDateFormat.format(date);
	}

	public static Date parseDate(Long date, String format) {
		if (date == null) { return null; }
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
		try {
			return simpleDateFormat.parse(date + "");
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date parseDate(String date, String format) {
		if (date == null) { return null; }
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
		try {
			return simpleDateFormat.parse(date + "");
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date parseTimeMillis(Long time, String format) {
		if (time == null) { return null; }
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
		String date = simpleDateFormat.format(time);
		try {
			return simpleDateFormat.parse(date + "");
		} catch (ParseException e) {
			return null;
		}
	}

	public static String formatDate(Date date) {
		return formatDate(date, DisplayDateFormat);
	}

	public static String formatTime(Date date) {
		return formatDate(date, DisplayTimeFormat);
	}

	public static String formatMoney(Object money, int bit) {
		try {
			if (money == null || money.toString() == "") {
				money = 0.0;
			}
			String format = "#,###,##0";
			if (bit > 0) {
				format += "." + repeat("#", bit);
			}
			DecimalFormat df = new DecimalFormat();
			df.applyPattern(format);
			return df.format(money);
		} catch (Exception e) {}
		return "";
	}

	public static String weekText(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		int w = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0) {
			w = 0;
		}
		return weekDays[w];
	}

	public static String shortDay(Date date) {
		String day = TextUtils.formatDate(date, "MM-dd");
		day += "(" + TextUtils.weekText(date) + ")";
		return day;
	}

	/**
	 * 标准XMLSchema日期格式转换
	 *
	 * @param date
	 * @param mat
	 * @return
	 */
	public static String formatDateForXMLSchemaDate(String date, String mat) {
		if (StringUtil.isBlank(date)) { return ""; }
		date = date.replace("Z", " UTC");//注意是空格+UTC
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");//注意格式化的表达式
		try {
			Date d = format.parse(date);
			return TextUtils.formatDate(d, mat);
		} catch (ParseException e) {
			// TODO Auto-generated catch block

			return "";
		}
	}

	public static Long simpleDate(Date date) {
		if (date == null) {
			date = new Date();
		}
		return Long.parseLong(formatDate(date, DateFormat));
	}

	public static Long simpleTime(Date date) {
		if (date == null) {
			date = new Date();
		}
		return Long.parseLong(formatDate(date, TimeFormat));
	}

	public static Long formatTimeMillis(Date date) {
		if (date == null) { return null; }
		try {
			return date.getTime();
		} catch (Exception e) {
			return null;
		}
	}

	public static Long formatTimeMillis(String date, String displayTimeFormat) {
		if (date == null) { return null; }
		try {
			return parseDate(date, displayTimeFormat).getTime();
		} catch (Exception e) {
			return null;
		}
	}

	public static String getAgo(Long oldTime) {
		Date old = parseDate(oldTime, TimeFormat);
		Date now = new Date();

		long time = (now.getTime() - old.getTime()) / 1000;

		long hour = time / 3600;
		long minute = time % 3600 / 60;

		String text = "";
		if (hour <= 24) {
			if (hour > 0) {
				text = hour + "小时";
			}
			if (minute > 0) {
				text += minute + "分钟";
			}
		} else {
			long day = hour / 24;
			text += day + "天";
		}
		if (text.length() <= 0) {
			text = "刚才";
		} else {
			text += "前";
		}
		return text;
	}

	public static String getTimeText(Long oldTime) {
		Date old = parseDate(oldTime, TimeFormat);
		return formatTime(old);
	}

	@SuppressWarnings("deprecation")
	public static String getShortTimeText(Long oldTime) {
		Date time = parseDate(oldTime, TimeFormat);
		Date now = new Date();
		String format = "yyyy-MM-dd HH:mm";
		if (time.getYear() == now.getYear()) {
			format = format.substring("yyyy-".length());
			if (time.getMonth() == now.getMonth() && time.getDay() == now.getDay()) {
				format = format.substring("MM-dd ".length());
			}
		}
		return formatDate(time, format);
	}

	public static long cutLong(long time, int scale) {
		return (time - time % scale) / scale;
	}

	/**
	 * 得到N天后的日期,如果days为负数，前得到N天前的日期
	 *
	 * @param format
	 * @param days
	 * @return
	 */
	public static String getAfterByNDay(String format, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, days);
		Date newDate = c.getTime();

		return formatDate(newDate, format);
	}

	/**
	 * 得到N月后的日期,如果months为负数，前得到N月前的日期
	 *
	 * @param format
	 * @param months
	 * @return
	 */
	public static String getAfterByNMonth(String format, int months) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, months);
		Date newDate = c.getTime();

		return formatDate(newDate, format);
	}

	public static String toString(String[] ss) {
		StringBuffer buf = new StringBuffer();
		if (ss != null) {
			for (String s : ss) {
				buf.append(s).append(' ');
			}
		}
		return buf.toString().trim();
	}

	public static String strLen(int num, int len) {
		return strLen(String.valueOf(num), len);
	}

	public static String strLen(long num, int len) {
		return strLen(String.valueOf(num), len);
	}

	public static String strLen(String s, int len) {
		if (s == null) {
			s = "";
		}
		int length = s.length();
		for (int i = 0; i < len - length; i++) {
			s = "0" + s;
		}
		return s;
	}

	public static String[] split(String str, String separator) {
		return split(str, separator, true);
	}

	/**
	 * Splits a string at the specified character.
	 */
	public static String[] split(String s, char c) {
		int i, b, e;
		int cnt;
		String res[];
		int ln = s.length();

		i = 0;
		cnt = 1;
		while ((i = s.indexOf(c, i)) != -1) {
			cnt++;
			i++;
		}
		res = new String[cnt];

		i = 0;
		b = 0;
		while (b <= ln) {
			e = s.indexOf(c, b);
			if (e == -1) {
				e = ln;
			}
			res[i++] = s.substring(b, e);
			b = e + 1;
		}
		return res;
	}

	/**
	 * Splits a string at the specified string.
	 */
	public static String[] split(String s, String sep, boolean caseInsensitive) {
		String splitString = caseInsensitive ? sep.toLowerCase() : sep;
		String input = caseInsensitive ? s.toLowerCase() : s;
		int i, b, e;
		int cnt;
		String res[];
		int ln = s.length();
		int sln = sep.length();

		if (sln == 0) { throw new IllegalArgumentException("The separator string has 0 length"); }

		i = 0;
		cnt = 1;
		while ((i = input.indexOf(splitString, i)) != -1) {
			cnt++;
			i += sln;
		}
		res = new String[cnt];

		i = 0;
		b = 0;
		while (b <= ln) {
			e = input.indexOf(splitString, b);
			if (e == -1) {
				e = ln;
			}
			res[i++] = s.substring(b, e);
			b = e + sln;
		}
		return res;
	}

	public static String repeat(String s, int count) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static String repeatIds(Long[] ids, String s) {
		StringBuffer sb = new StringBuffer();
		for (Long id : ids) {
			sb.append(id + s);
		}
		if (sb.length() > 1) {
			return sb.substring(0, sb.length() - 1);
		} else {
			return "";
		}
	}

	public static boolean contains(Collection<String> mainCollection, String str) {
		String[] mainArr = new String[mainCollection.size()];
		return contains(mainCollection.toArray(mainArr), str);
	}

	public static boolean contains(Collection<String> mainCollection, Collection<String> subCollection) {
		String[] mainArr = new String[mainCollection.size()];
		String[] subArr = new String[subCollection.size()];
		return contains(mainCollection.toArray(mainArr), subCollection.toArray(subArr));
	}

	public static boolean contains(String[] mainArr, String[] subArr) {
		for (String s : subArr) {
			if (!contains(mainArr, s)) { return false; }
		}
		return true;
	}

	public static boolean contains(String[] strArr, String str) {
		for (String s : strArr) {
			if (s.equals(str)) { return true; }
		}
		return false;
	}

	public static String[] rebuildArray(String[] arr) {
		if (arr != null) {
			List<String> list = new ArrayList<String>(Arrays.asList(arr));
			for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
				String s = iter.next();
				if (StringUtil.isEmpty(s)) {
					iter.remove();
				}
			}
			String[] newArr = new String[list.size()];
			return list.toArray(newArr);
		}
		return new String[0];
	}

	public static Long[] parseLongs(String text) {
		List<Long> randIds = new ArrayList<Long>();
		if (text != null) {
			for (String strBID : StringUtil.split(text, "|")) {
				if (strBID != null && !strBID.trim().equals("")) {
					randIds.add(Long.parseLong(strBID));
				}
			}
		}
		return randIds.toArray(new Long[] {});
	}

	public static String getIdsString(List<Long> ids) {
		if (ids != null && ids.size() > 0) {
			return "";
		} else {
			return getIdsString(new Long[] {});
		}
	}

	public static String getIdsString(Long[] ids) {
		String text = "";
		for (Long id : ids) {
			text += "|" + id.longValue() + "|";
		}
		return text;
	}

	public static String toText(Object[] objs, String ch) {
		StringBuffer text = new StringBuffer();
		for (Object o : objs) {
			text.append(o.toString());
			text.append(ch);
		}
		String t = text.toString().trim();
		if (t.endsWith(ch)) {
			t = t.substring(0, t.length() - 1);
		}
		return t;
	}

	public static Long parseId(Object id) {
		if (id == null) { return null; }
		if (id instanceof String && !StringUtil.isBlank((String) id)) {
			return Long.parseLong((String) id);
		} else if (id instanceof Number) { return ((Number) id).longValue(); }
		return -1l;
	}

	public static boolean match(String text, String regex) {
		regex = StringUtil.replace(regex, "\\", "\\\\");
		regex = StringUtil.replace(regex, ".", "\\.");
		regex = StringUtil.replace(regex, "[", "\\[");
		regex = StringUtil.replace(regex, "]", "\\]");
		regex = StringUtil.replace(regex, "(", "\\(");
		regex = StringUtil.replace(regex, ")", "\\)");

		regex = StringUtil.replace(regex, "?", ".+");
		regex = StringUtil.replace(regex, "*", ".*");
		return text.matches(regex);
	}

	public static String getAbbreviation(String text) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch >= 'A' && ch <= 'Z') {
				sb.append(ch);
			}
		}
		if (sb.length() > 0) { return sb.toString(); }
		return text;
	}

	public static String prefix(String key) {
		return StringUtil.isBlank(key) ? "" : (key + ".");
	}

	public static String postfix(String key) {
		return StringUtil.isBlank(key) ? "" : ("." + key);
	}

	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "").trim();
	}

	public static String poolishToString(Object obj) {
		if (obj == null) { return "null"; }
		if (obj instanceof String) { return "\"" + obj.toString() + "\""; }
		if (obj instanceof Character) { return "'" + obj.toString() + "'"; }
		if (obj instanceof Boolean) { return ((Boolean) obj) ? "true" : "false"; }
		if (obj instanceof Number) { return obj.toString(); }
		if (obj instanceof Date) { return ((Date) obj).getTime() + ""; }
		if (obj instanceof Object[]) {
			Object[] objs = (Object[]) obj;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < objs.length; i++) {
				sb.append(poolishToString(objs[i]));
				if (i < objs.length - 1) {
					sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		}
		if (obj instanceof int[]) {
			int[] objs = (int[]) obj;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < objs.length; i++) {
				sb.append(poolishToString(objs[i]));
				if (i < objs.length - 1) {
					sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		}
		if (obj instanceof long[]) {
			long[] objs = (long[]) obj;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < objs.length; i++) {
				sb.append(poolishToString(objs[i]));
				if (i < objs.length - 1) {
					sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		}
		if (obj instanceof double[]) {
			double[] objs = (double[]) obj;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < objs.length; i++) {
				sb.append(poolishToString(objs[i]));
				if (i < objs.length - 1) {
					sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		}
		if (obj instanceof float[]) {
			float[] objs = (float[]) obj;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < objs.length; i++) {
				sb.append(poolishToString(objs[i]));
				if (i < objs.length - 1) {
					sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		}
		if (obj instanceof boolean[]) {
			boolean[] objs = (boolean[]) obj;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < objs.length; i++) {
				sb.append(poolishToString(objs[i]));
				if (i < objs.length - 1) {
					sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		}
		if (obj instanceof char[]) {
			char[] objs = (char[]) obj;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < objs.length; i++) {
				sb.append(poolishToString(objs[i]));
				if (i < objs.length - 1) {
					sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		}
		if (obj instanceof Collection<?>) {
			Collection<?> objs = (Collection<?>) obj;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			Iterator<?> iterator = objs.iterator();
			while (iterator.hasNext()) {
				sb.append(poolishToString(iterator.next()));
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append("]");
			return sb.toString();
		}
		if (obj instanceof Map<?, ?>) {
			Map<?, ?> maps = (Map<?, ?>) obj;
			if (maps.size() < 1) { return "{}"; }
			int i = 0;
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			for (Object key : maps.keySet()) {
				Object val = maps.get(key);
				sb.append(poolishToString(key));
				sb.append(":");
				sb.append(poolishToString(val));
				if (i < maps.size() - 1) {
					sb.append(",");
				}
				i++;
			}
			sb.append("}");
			return sb.toString();
		}
		return obj.toString();
	}

	public static String stringNO(Number no, int bit) {
		return String.format("%0" + bit + "d", no);
	}
	/**
	 * 移除重复部分,并排序
	 * @param text
	 * @param separator
	 * @return
	 */
	public static String rmDuplicate(String text, String separator) {
		String[] all = StringUtil.split(text, separator);
		Set<String> idsMap = new HashSet<>();
		if (all != null) {
			for (String a : all) {
				if (a.trim().length() > 0 && !"null".equals(a.trim())) {
					idsMap.add(a.trim());
				}
			}
		}
		all = idsMap.toArray(new String[] {});
		Arrays.sort(all);
		return StringUtil.join(all, separator);
	}
}
