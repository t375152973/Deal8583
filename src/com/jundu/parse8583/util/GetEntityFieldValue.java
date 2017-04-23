package com.jundu.parse8583.util;

import java.lang.reflect.Field;

import com.jundu.parse8583.model.Entity8583;

public class GetEntityFieldValue {
	
	public static String getFieldValue(String field,Entity8583 entity8583) {
		String returnValue = null;
		try {
			Field[] fields = entity8583.getClass().getDeclaredFields();
			for (Field field2 : fields) {
				field2.setAccessible(true);
				if(field2.getName().equals(field)){
					returnValue = (String) field2.get(entity8583);
					break;
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	
	public static String setFieldValue(String field,Entity8583 entity8583,String value) {
		String returnValue = null;
		try {
			Field[] fields = entity8583.getClass().getDeclaredFields();
			for (Field field2 : fields) {
				field2.setAccessible(true);
				if(field2.getName().equals(field)){
					field2.set(entity8583, value);
					break;
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
}
