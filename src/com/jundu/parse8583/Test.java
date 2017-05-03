package com.jundu.parse8583;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.jundu.parse8583.util.SimpleUtils;

public class Test {
	public static void main(String[] args) throws UnsupportedEncodingException {
		String hexString = "004E600301000060220000000004003020048002C08011000000000000000100008212021000303037303131313832313130333239303037303131313132313135360008220001294138414230344638";
		Parse8583 parse8583 = new Parse8583();
		parse8583.analyze8583(hexString);
	}
	
	public static Map<String, Object> getValue(byte[] params, int index,String charset) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			index += 2;
			byte[] lengthByte = new byte[2];
			for (int i = 0; i < lengthByte.length; i++) {
				lengthByte[i] = params[index-2+i];
			}
			String paramLength = new String(lengthByte,charset);
			byte[] valueByte = new byte[Integer.valueOf(paramLength)];
			for (int i = 0; i < valueByte.length; i++) {
				valueByte[i] = params[index+i];
			}
			String paramValue = new String(valueByte,charset);
			index += Integer.valueOf(paramLength);
			result.put("index", index);
			result.put("value", paramValue);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
}
