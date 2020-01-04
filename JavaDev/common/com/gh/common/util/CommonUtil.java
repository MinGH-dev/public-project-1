package com.gh.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.json.simple.JSONObject;

import com.gh.common.LogLevel;

public class CommonUtil {
	public static void println (Object obj, LogLevel logLevel) {
		if (logLevel.isMoreThanLevel()) {
			System.out.println("["+ logLevel.toString() +"]["+ getNow() +"]" + obj.toString());
		}
	}
	
	public static void println (Exception e) {
		if (e != null) {
			String errMsg = e.getMessage();
			if (!isNullOrEmpty(errMsg)) {
				System.out.println("[" + getNow() + "] " + errMsg);
			}
		}
	}
	
	public static String getNow(){
		return dateToStr("yyyyMMddHHmmssSSS", new Date()) ;
	}
	
	
	public static String getNow (String format) {
		return dateToStr(format, new Date());
	}
	
	public static String dateToStr(String fmt, Date date) {
		DateFormat sdFormat = new SimpleDateFormat(fmt);
		return sdFormat.format(date);
	}
	
	public static boolean isNullOrEmpty(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}

		String temp = str.trim();
		if ("".equals(temp) || "null".equals(temp) || "NULL".equals(temp)) {
			return true;
		}

		return false;
	}
	
	public static boolean isNullOrEmpty (JSONObject[] json) {
		boolean isNull = true;
		
		if (json == null) {
			return isNull;
		}

		for (JSONObject obj : json) {
			if (!isNullOrEmpty(obj)) {
				return false;
			}
		}

		return isNull;
	}
	
	public static boolean isNullOrEmpty(Object obj) {
		if (obj == null) {
			return true;
		}

		String temp = obj.toString().trim();
		if ("".equals(temp) || "null".equals(temp) || "NULL".equals(temp)) {
			return true;
		}

		return false;
	}
	
	public static boolean isNullOrEmpty(Collection<?> collection) {
		if (collection == null) {
			return true;
		}
		
		if (collection.size() == 0) {
			return true;
		}
		
		return false;
	}
	
	

	public static boolean isNullOrEmpty(JSONObject obj) {

		if (obj == null) {
			return true;
		}
		return false;
	}
}
