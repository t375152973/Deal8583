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
 * @description��
 * ׼������8583����ͷ���������ͱ�ʶ��λͼ������������ISO8583metadata.xml�����ļ�
 * ׼��8583ʮ�����Ʊ���
 * ʹ��SAXReader��ȡISO8583metadata.xml�ļ������ļ��е����ݽ�����Map<String,Properties>
 * ʹ���ļ���������ȡ8583ʮ�����Ʊ��ĵ��ֽ�������������ֽ����������ת��Ϊ�ֽ�����
 * ���ֽ�����ת�����ַ������˿��ַ�����������ʮ���������������ȫһ�£������ַ������С���ȥ��
 * ���ַ���ת�����ֽ����飨����ʮ������ת����ʮ���Ƶ��ֽ����飩
 * ��������ͷ������ISO8583metadata.xml�У�isBCDȷ�����롢lengthȷ�����ȡ�encodingȷ�����롢name��Ϊ��ǩ�����ָ��ݳ��Ƚ�ȡ�����ж�isBCD���룬������Ӧ�ı�����롣��
 * �����������ͱ�ʶ�����ݳ��ȣ�byte�����飬������Ӧ��encoding������н��룩
 * ����λͼ���жϵ�һ���ֽڵĶ��������λ�Ƿ�Ϊ1��Ϊ1��ʹ����չλͼ��Ϊ0��ʹ����չλͼ�����ݳ��Ȼ�ȡbyte�����飬ת���ɶ�Ӧ�Ķ����ƣ����ݶ������жϴ�����Щ����ֵ��
 * ���������壨�����ڵ���ѭ�����д����ж��Ƿ�䳤������䳤���Ȼ�ȡ�䳤����ʾ�ĳ���ֵ������n..11(LLVAR)Ϊ��λ�䳤���������ֽڱ�ʾ���ȣ����������ֽڼ��㱾����ռ�����ֽڣ��ٻ�ȡ��Ӧ�ֽ������ٸ���encoding������н��룻����Ǳ䳤��ֱ�Ӹ���length��ȡ���ȣ��ٸ���encoding������н��룩
 * ��������ɵ�8583������Ϣ���ڵ�Map���򣬱��ڴ�ӡ�������˴�����˵���������뼴�ɣ�
 * 
 * @warnע���
 * ����0~9������
 *         ʮ������ת����ʮ���ƣ���Ӧ��BCD��ת����ʮ����
 *         һ��ʮ�������൱��һ��byte���൱������[0��9]
 * 
 * @see
 * 8583���Ĳ�����ؼ��㣺
 *     ����ͷ���򡢱������ͱ�ʶ��λͼ���߱���������ʹ�õı��뷽ʽ������BCD���뻹����ͨ��ʮ������
 *     λͼ��ʹ��
 *     ��������ı䳤����
 * 
 * @see
 * �����8583������Ҫ���ڱ���ͽ��롢����ת�����ַ�����һ����ֺ�ϵͳ���˽�
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
		System.out.println("��������:"+hexStr);
		if(hexStr.indexOf("4C5249001C")>0&&hexStr.indexOf("580009490006000000220030")>0){
			hasMonitor = true;
		}
		// �����Ľ������ֽ�����
		byte[] retByte = SimpleUtils.hex2bytes(hexStr);
		// ����8583������
		Map<String, String> fieldMap = parse8583(retByte);
		// ��ȡ�����keys
		getKeyList(fieldMap);
		return fieldMap;
	}

	// ���Ĵ�����
	private static Map<String, String> parse8583(byte[] byteArr) throws UnsupportedEncodingException {
		Map<String, String> fieldMap = new HashMap<String, String>();
		// ��ȡ����ͷ��Ϣ
		parseHeaders(byteArr, fieldMap);
		// ��ȡMsgType
		parseMsgType(byteArr, fieldMap);
		// ��ȡλͼ
		String bitMap1_value = getBitMap(byteArr);
		// ����λͼ�жϴ�����Щ�򼰻�ȡ���ֵ
		parseFields(byteArr, fieldMap, bitMap1_value);
		// ����
		return fieldMap;
	}

	// ��ȡ����ͷ��Ϣ
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

	// ����������
	private static void parseFields(byte[] byteArr, Map<String, String> fieldMap, String bitMap1_value_2)
			throws UnsupportedEncodingException {
		List<Integer> exitFieldList = getExitField(bitMap1_value_2);
		byte[] fieldsValue = new byte[byteArr.length-currentIndex];
		System.arraycopy(byteArr, currentIndex, fieldsValue, 0, byteArr.length-currentIndex);
		// ��ȡ����ֵ
		for (int i = 0; i < exitFieldList.size(); i++) {
			int field = exitFieldList.get(i);
			Properties fieldProperties = map.get("F" + field);
			// ��������ڣ�����
			if (fieldProperties == null)
				continue;
			String field_variable_flag = fieldProperties.getProperty("variable_flag");
			String zipType = fieldProperties.getProperty("zipType");
			if (field_variable_flag == null || "".equals(field_variable_flag)) {
				//ѹ������ΪBCD��
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
					// �Ȼ�ȡ�䳤��ĳ���ֵ
					int variable_flag_length = Integer.parseInt(field_variable_flag);
					if(variable_flag_length%2==1){
						variable_flag_length = variable_flag_length/2+1;
					}else{
						variable_flag_length = variable_flag_length/2;
					}
					byte[] variable_flag_byte = new byte[variable_flag_length];
					System.arraycopy(byteArr, currentIndex, variable_flag_byte, 0, variable_flag_length);
					currentIndex += variable_flag_length;
					// �ٻ�ȡ�䳤�����ʵ����ֵ
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
					// �Ȼ�ȡ�䳤��ĳ���ֵ
					int variable_flag_length = Integer.parseInt(field_variable_flag);
					if(variable_flag_length%2==1){
						variable_flag_length = variable_flag_length/2+1;
					}else{
						variable_flag_length = variable_flag_length/2;
					}
					byte[] variable_flag_byte = new byte[variable_flag_length];
					System.arraycopy(byteArr, currentIndex, variable_flag_byte, 0, variable_flag_length);
					currentIndex += variable_flag_length;
					// �ٻ�ȡ�䳤�����ʵ����ֵ
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

	// ��ȡ������λͼ�ַ���
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

	// ����MsgType
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

	// ���ݶ�����λͼ�ַ�����ȡ���ڵ���
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

	// ��ȡ�����keys
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
