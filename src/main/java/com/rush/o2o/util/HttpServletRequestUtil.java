package com.rush.o2o.util;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestUtil {
	public static int getInt(HttpServletRequest request, String key) {
		try {
			return Integer.decode(request.getParameter(key));
		} catch(Exception e) {
			return -1;
		}
	}
	
	public static Long getLong(HttpServletRequest request, String key) {
		try {
			return Long.valueOf(request.getParameter(key));
		}	catch (Exception e) {
				return  -1L;
		}
	}
	
	
	public static Double getDouble(HttpServletRequest request, String key) {
		try {
			return Double.valueOf(request.getParameter(key));
		}	catch (Exception e) {
				return (double) -1;
		}
	}
	
	public static boolean getBoolean(HttpServletRequest request, String key) {
		try {
			return Boolean.valueOf(request.getParameter(key));
		}	catch (Exception e) {
				return false;
		}
	}
	public static String getString(HttpServletRequest request, String key) {
		try {
			String result = request.getParameter(key);
			System.out.println("result: "+result+"\nkey: "+ key);
			if (result != null) {
				result = result.trim();
			}
			if ("".equals(result)) {
			
				result = null;
			}
			return result;
		} catch (Exception e) {
			System.out.println("UTIL出现了错误");
			return null;
		}
	}
	
}
