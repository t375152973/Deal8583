package com.jundu.parse8583.util;

public class StringUtil {
	
	public static boolean isBlank(String string) {
		if(string==null){
			return true;
		}else{
			if("".equals(string)){
				return true;
			}else{
				return false;
			}
		}
	}
	
	public static boolean isNotBlank(String string) {
		if(string==null){
			return false;
		}else{
			if("".equals(string)){
				return false;
			}else{
				return true;
			}
		}
	}
}
