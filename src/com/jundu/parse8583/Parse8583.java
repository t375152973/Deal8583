package com.jundu.parse8583;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.jundu.parse8583.util.ByteUtil;
import com.jundu.parse8583.util.SimpleUtils;

/*
 * @description：
 * 准备包含8583报文头、报文类型标识、位图、报文体各域的ISO8583metadata.xml配置文件
 * 准备8583十六进制报文
 * 使用SAXReader读取ISO8583metadata.xml文件，将文件中的内容解析成Map<String,Properties>
 * 使用文件输入流读取8583十六进制报文到字节数组输出流，字节数组输出流转换为字节数组
 * 将字节数组转换成字符串，此刻字符串的内容与十六进制里的内容完全一致，并将字符串换行、空去掉
 * 将字符串转换成字节数组（即将十六进制转换成十进制的字节数组）
 * 解析报文头（根据ISO8583metadata.xml中：isBCD确定编码、length确定长度、encoding确定编码、name作为标签名。现根据长度截取，再判断isBCD编码，根据相应的编码解码。）
 * 解析报文类型标识（根据长度，byte子数组，根绝对应的encoding编码进行解码）
 * 解析位图（判断第一个字节的二进制最高位是否为1，为1则使用扩展位图，为0则不使用扩展位图；根据长度获取byte字数组，转换成对应的二进制；根据二进制判断存在哪些域有值）
 * 解析报文体（将存在的域循环进行处理：判断是否变长，如果变长，先获取变长所表示的长度值，比如n..11(LLVAR)为两位变长，有两个字节表示长度，先拿两个字节计算本域所占几个字节，再获取相应字节数，再根据encoding编码进行解码；如果非变长，直接根据length获取长度，再根据encoding编码进行解码）
 * 将解析完成的8583报文信息所在的Map排序，便于打印阅览（此处不再说明，看代码即可）
 * 
 * @warn注意点
 * 对于0~9的数字
 *         十六进制转换成十进制，相应于BCD码转换成十进制
 *         一个十六进制相当于一个byte，相当于两个[0，9]
 * 
 * @see
 * 8583报文拆组包关键点：
 *     报文头各域、表问类型标识、位图或者报文体域所使用的编码方式，比如BCD编码还是普通的十六进制
 *     位图的使用
 *     报文体域的变长处理
 * 
 * @see
 * 拆组包8583报文需要对于编码和解码、进制转换、字符集有一个充分和系统的了解
 */
public class Parse8583 {
	static int currentIndex = 0;
	static Map<String, Properties> map = Bean8583Factory.getInstance().getMap();
	static boolean hasMonitor = false;
	
	public static Map<String, String> analyze8583(byte[] message) throws UnsupportedEncodingException {
		String hexStr = SimpleUtils.bytes2hex(message);
		return analyze8583(hexStr);
	}
	
	public static Map<String, String> analyze8583(String hexStr) throws UnsupportedEncodingException {
		System.out.println("报文内容:"+hexStr);
		if(hexStr.indexOf("4C5249001C")>0&&hexStr.indexOf("580009490006000000220030")>0){
			hasMonitor = true;
		}
		// 将报文解析成字节数组
		byte[] retByte = SimpleUtils.hex2bytes(hexStr);
		// 解析8583报文体
		Map<String, String> fieldMap = parse8583(retByte);
		// 获取有序的keys
		getKeyList(fieldMap);
		return fieldMap;
	}

	// 报文处理函数
	private static Map<String, String> parse8583(byte[] byteArr) throws UnsupportedEncodingException {
		Map<String, String> fieldMap = new HashMap<String, String>();
		// 获取报文头信息
		parseHeaders(byteArr, fieldMap);
		// 获取MsgType
		parseMsgType(byteArr, fieldMap);
		// 获取位图
		String bitMap1_value = getBitMap(byteArr);
		// 根据位图判断存在哪些域及获取域的值
		parseFields(byteArr, fieldMap, bitMap1_value);
		// 返回
		return fieldMap;
	}

	// 获取报文头信息
	private static void parseHeaders(byte[] byteArr, Map<String, String> fieldMap)
			throws UnsupportedEncodingException {
		for (int i = 1; i <= 7; i++) {
			if(!hasMonitor){
				if(i==3){
					continue;
				}else if (i==4) {
					continue;
				}else if (i==5) {
					continue;
				}else if (i==6) {
					continue;
				}
			}
			Properties headProperties = map.get("H" + i);
			if (headProperties == null)
				continue;
			int head_length = Integer.parseInt(headProperties.getProperty("length"));
			byte[] head_value_byte = new byte[head_length];
			System.arraycopy(byteArr, currentIndex, head_value_byte, 0, head_length);
			currentIndex += head_length;

			String isBCD = headProperties.getProperty("isBCD");
			if ("true".equals(isBCD)) {
				String head_value = SimpleUtils.bcd2string(head_value_byte);
				fieldMap.put(headProperties.getProperty("name"), head_value);
			} else {
				String head_value = SimpleUtils.bytes2hex(head_value_byte);
				fieldMap.put(headProperties.getProperty("name"), head_value);
			}
		}
	}

	// 解析各个域
	private static void parseFields(byte[] byteArr, Map<String, String> fieldMap, String bitMap1_value_2)
			throws UnsupportedEncodingException {
		List<Integer> exitFieldList = getExitField(bitMap1_value_2);
		byte[] fieldsValue = new byte[byteArr.length-currentIndex];
		System.arraycopy(byteArr, currentIndex, fieldsValue, 0, byteArr.length-currentIndex);
		// 获取各域值
		for (int i = 0; i < exitFieldList.size(); i++) {
			int field = exitFieldList.get(i);
			Properties fieldProperties = map.get("F" + field);
			// 如果不存在，跳过
			if (fieldProperties == null)
				continue;
			String field_variable_flag = fieldProperties.getProperty("variable_flag");
			String zipType = fieldProperties.getProperty("zipType");
			if (field_variable_flag == null || "".equals(field_variable_flag)) {
				//压缩类型为BCD码
				if(zipType!=null&&"leftBCD".equals(zipType)){
					int temp_length = 0;
					int field_length = Integer.parseInt(fieldProperties.getProperty("length"));
					if(field_length%2==1){
						temp_length = field_length/2+1;
					}else{
						temp_length = field_length/2;
					}
					byte[] field_value_byte = new byte[temp_length];
					System.arraycopy(byteArr, currentIndex, field_value_byte, 0, temp_length);
					currentIndex += temp_length;
					String field_value = SimpleUtils.bytes2hex(field_value_byte);
					field_value = field_value.substring(0, field_length);
					fieldMap.put(fieldProperties.getProperty("name"), field_value);
				}else
				if(zipType!=null&&"ASCII".equals(zipType)){
					int field_length = Integer.parseInt(fieldProperties.getProperty("length"));
					byte[] field_value_byte = new byte[field_length];
					System.arraycopy(byteArr, currentIndex, field_value_byte, 0, field_length);
					currentIndex += field_length;
					String field_value = SimpleUtils.bytes2hex(field_value_byte);
					field_value = SimpleUtils.hexStr2Str(field_value, fieldProperties.getProperty("encoding"));
					fieldMap.put(fieldProperties.getProperty("name"), field_value);
				}else{
					int field_length = Integer.parseInt(fieldProperties.getProperty("length"));
					byte[] field_value_byte = new byte[field_length];
					System.arraycopy(byteArr, currentIndex, field_value_byte, 0, field_length);
					currentIndex += field_length;
					String field_value = SimpleUtils.bytes2hex(field_value_byte);
					fieldMap.put(fieldProperties.getProperty("name"), field_value);
				}
				
			} else {
				if(zipType!=null&&"leftBCD".equals(zipType)){
					// 先获取变长域的长度值
					int variable_flag_length = Integer.parseInt(field_variable_flag);
					if(variable_flag_length%2==1){
						variable_flag_length = variable_flag_length/2+1;
					}else{
						variable_flag_length = variable_flag_length/2;
					}
					byte[] variable_flag_byte = new byte[variable_flag_length];
					System.arraycopy(byteArr, currentIndex, variable_flag_byte, 0, variable_flag_length);
					currentIndex += variable_flag_length;
					// 再获取变长域的真实长度值
					int temp_length = 0;
					int field_length = Integer.valueOf(SimpleUtils.bytes2hex(variable_flag_byte));
					if(field_length%2==1){
						temp_length = field_length/2+1;
					}else{
						temp_length = field_length/2;
					}
					byte[] field_value_byte = new byte[temp_length];
					System.arraycopy(byteArr, currentIndex, field_value_byte, 0, temp_length);
					currentIndex += temp_length;
					String field_value = SimpleUtils.bytes2hex(field_value_byte);
					field_value = field_value.substring(0, field_length);
					fieldMap.put(fieldProperties.getProperty("name"), field_value);
				}else
				if(zipType!=null&&"ASCII".equals(zipType)){
					// 先获取变长域的长度值
					int variable_flag_length = Integer.parseInt(field_variable_flag);
					if(variable_flag_length%2==1){
						variable_flag_length = variable_flag_length/2+1;
					}else{
						variable_flag_length = variable_flag_length/2;
					}
					byte[] variable_flag_byte = new byte[variable_flag_length];
					System.arraycopy(byteArr, currentIndex, variable_flag_byte, 0, variable_flag_length);
					currentIndex += variable_flag_length;
					// 再获取变长域的真实长度值
					int field_length = SimpleUtils.bytes2short(variable_flag_byte, 0);
					byte[] field_value_byte = new byte[field_length];
					System.arraycopy(byteArr, currentIndex, field_value_byte, 0, field_length);
					currentIndex += field_length;
					String field_value = SimpleUtils.bytes2hex(field_value_byte);
					field_value = SimpleUtils.hexStr2Str(field_value, fieldProperties.getProperty("encoding"));
					fieldMap.put(fieldProperties.getProperty("name"), field_value);
				}
			}
		}
	}

	// 获取二进制位图字符串
	private static String getBitMap(byte[] byteArr) {
		Properties bitMap1 = map.get("bitmap1");
		int bitMap1_length = Integer.parseInt(bitMap1.getProperty("length"));
		byte[] bitMap1_value_byte = new byte[bitMap1_length];
		System.arraycopy(byteArr, currentIndex, bitMap1_value_byte, 0, bitMap1_length);
		currentIndex += bitMap1_length;
		String bitMap1_value_2 = ByteUtil.binary(bitMap1_value_byte);
		if (bitMap1_value_2.startsWith("1")) {
			Properties bitMap2 = map.get("bitmap2");
			int bitMap2_length = Integer.parseInt(bitMap2.getProperty("length"));
			byte[] bitMap2_value_byte = new byte[bitMap2_length];
			System.arraycopy(byteArr, currentIndex, bitMap2_value_byte, 0, bitMap2_length);
			currentIndex += bitMap2_length;
			String bitMap2_value_2 = ByteUtil.binary(bitMap2_value_byte);
			bitMap1_value_2 += bitMap2_value_2;
		}
		return bitMap1_value_2;
	}

	// 解析MsgType
	private static void parseMsgType(byte[] byteArr, Map<String, String> mapRet)
			throws UnsupportedEncodingException {
		Properties msgType = map.get("MsgType");
		int msgType_length = Integer.parseInt(msgType.getProperty("length"));
		byte[] msgType_value_byte = new byte[msgType_length];
		System.arraycopy(byteArr, currentIndex, msgType_value_byte, 0, msgType_length);
		currentIndex += msgType_length;
		String msgType_value = SimpleUtils.bytes2hex(msgType_value_byte);
		mapRet.put("F0", msgType_value);
	}

	// 根据二进制位图字符串获取存在的域
	private static List<Integer> getExitField(String bitMap2String) {
		List<Integer> exitFieldList = new ArrayList<Integer>();
		for (int i = 2; i <= bitMap2String.length(); i++) {
			int field_index = Integer.parseInt(String.valueOf(bitMap2String.charAt(i - 1)));
			if (field_index == 1) {
				exitFieldList.add(i);
			}
		}
		return exitFieldList;
	}

	// 获取有序的keys
	private static List<String> getKeyList(Map<String, String> fieldMap) {
		Set<String> keySet = fieldMap.keySet();
		List<String> keyList = new ArrayList<String>(keySet);
		Collections.sort(keyList, new Comparator<String>() {
			@Override
			public int compare(String str1, String str2) {
				Integer value1 = Integer.parseInt(str1.substring(1));
				Integer value2 = Integer.parseInt(str2.substring(1));
				return value1.compareTo(value2);
			}
		});
		Collections.sort(keyList, new Comparator<String>() {
			@Override
			public int compare(String str1, String str2) {
				char value1 = str1.charAt(0);
				char value2 = str2.charAt(0);
				return value1 < value2 ? 1 : 0;
			}
		});
		return keyList;
	}
}
