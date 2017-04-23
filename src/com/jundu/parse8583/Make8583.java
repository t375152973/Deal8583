package com.jundu.parse8583;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.jundu.parse8583.model.Entity8583;
import com.jundu.parse8583.util.ByteUtil;
import com.jundu.parse8583.util.GetEntityFieldValue;
import com.jundu.parse8583.util.SimpleUtils;
import com.jundu.parse8583.util.StringUtil;

public class Make8583 {
	static int currentIndex = 0;
	static Map<String, Properties> map = Bean8583Factory.getInstance().getMap();
	static boolean hasMonitor = false;
	private static String message8583 = "";
	private static List<Integer> bitmapList = new ArrayList<Integer>();
	public static String make8583(Entity8583 entity8583) {
		try {
			if(StringUtil.isNotBlank(entity8583.getH3())&&StringUtil.isNotBlank(entity8583.getH4())
					&&StringUtil.isNotBlank(entity8583.getH5())&&StringUtil.isNotBlank(entity8583.getH6())){
				hasMonitor = true;
			}
			message8583 = new String();
			//组件头
			makeHeader(entity8583);
			//组件消息类型
			makeMessageType(entity8583);
			//组件位图信息
			makeBitmap(entity8583);
			//组件域
			makeFields(entity8583);
			if (StringUtil.isNotBlank(message8583)) {
				int length = message8583.length()/2;
				String totalLength = SimpleUtils.bytes2hex(getByteMessageLen(length));
				message8583 = totalLength+message8583;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message8583;
	}
	
	public static void makeHeader(Entity8583 entity8583) {
		for (int i = 1; i <= 7; i++) {
			if(!hasMonitor){
				if(i==3||i==4||i==5||i==6){
					continue;
				}
			}
			String content = GetEntityFieldValue.getFieldValue("H"+i, entity8583);
			if(StringUtil.isNotBlank(content)){
				message8583+=content;
			}
		}
	}
	
	public static void makeMessageType(Entity8583 entity8583) {
		message8583 += entity8583.getMessageType();
	}
	
	public static void makeBitmap(Entity8583 entity8583) {
		String bitmap = "00000000"+"00000000"+"00000000"+"00000000"+"00000000"+"00000000"+"00000000"+"00000000";
		for(int i = 2; i <= 64; i++){
			String content = GetEntityFieldValue.getFieldValue("F"+i, entity8583);
			if(StringUtil.isNotBlank(content)){
				bitmap = bitmap.substring(0, i-1)+"1"+bitmap.substring(i, bitmap.length());
				bitmapList.add(i);
			}
		}
		byte[] bytes = ByteUtil.bit2Byte(bitmap);
		message8583 += SimpleUtils.bytes2hex(bytes);
	}
	
	public static void makeFields(Entity8583 entity8583) throws UnsupportedEncodingException {
		if(bitmapList.size()==0){
			return;
		}
		for (Integer integer : bitmapList) {
			Properties properties = map.get("F"+integer);
			String content = GetEntityFieldValue.getFieldValue("F"+integer, entity8583);
			String variableFlag = properties.getProperty("variable_flag");
			String zipType = properties.getProperty("zipType");
			//variableFlag为空，表示当前域为定长域
			if(StringUtil.isBlank(variableFlag)){
				//值内容为左靠BCD压缩
				if("leftBCD".equals(zipType)){
					if(content.length()%2==1){
						content += "0";
					}
					byte[] value = SimpleUtils.string2bcd(content);
					message8583 += SimpleUtils.bytes2hex(value);
				}else if ("ASCII".equals(zipType)) {
					String value = SimpleUtils.str2HexStr(content, "GBK");
					message8583 += value;
				}else{
					int length = content.length();
					if(length%2==1){
						content+="0";
						length++;
					}
					byte[] value = new byte[length/2];
					for (int i = 0; i < length/2; i++) {
						String temp = content.substring(2*i, 2*i+2);
						value[i]=(byte)Integer.parseInt(temp, 16);
					}
					message8583 += SimpleUtils.bytes2hex(value);
				}
			}else{
				int byteLength = Integer.valueOf(variableFlag);
				if(byteLength%2==1){
					byteLength = byteLength/2+1;
				}else{
					byteLength = byteLength/2;
				}
				if("leftBCD".equals(zipType)){
					int valueLength = content.length();
					if(content.length()%2==1){
						content += "0";
					}
					byte[] value = SimpleUtils.string2bcd(content);
					byte[] lengthByte = SimpleUtils.int2bcd(valueLength);
					String temp = SimpleUtils.bytes2hex(lengthByte);
					if(temp.length()/2!=byteLength){
						temp = "00"+temp;
					}
					message8583+= temp+SimpleUtils.bytes2hex(value);
				}else if ("ASCII".equals(zipType)) {
					String value = SimpleUtils.str2HexStr(content, "GBK");
					int valueLength = SimpleUtils.hex2bytes(value).length;
					byte[] lengthByte = SimpleUtils.int2bcd(valueLength);
					String temp = SimpleUtils.bytes2hex(lengthByte);
					if(temp.length()/2!=byteLength){
						temp = "00"+temp;
					}
					message8583+= temp+value;
				}else{
					byte[] value = content.getBytes();
					int valueLength = value.length;
					byte[] lengthByte = SimpleUtils.int2bcd(valueLength);
					String temp = SimpleUtils.bytes2hex(lengthByte);
					if(temp.length()/2!=byteLength){
						temp = "00"+temp;
					}
					message8583+= temp+SimpleUtils.bytes2hex(value);
				}
			}
		}
	}
	
	public static byte[] getByteMessageLen(int len) {
		byte[] buf = new byte[2];
		// 取高8位
		buf[0] = (byte) (len >> 8);
		// 取低8
		buf[1] = (byte) (len & 0xff);
		return buf;
	}
	
}
