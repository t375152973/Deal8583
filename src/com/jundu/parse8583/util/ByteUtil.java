package com.jundu.parse8583.util;

public class ByteUtil {
	// ���ֽ�����ת���ɶ������ַ���
	public static String binary(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(byte2Bit(b));
		}
		return sb.toString();
	}

	/**
	 * ByteתBit
	 */
	public static String byte2Bit(byte b) {
		return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1) + (byte) ((b >> 5) & 0x1)
				+ (byte) ((b >> 4) & 0x1) + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
				+ (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
	}

	/**
	 * bitתByte
	 * @param bit
	 * @return
	 */
	public static byte[] bit2Byte(String bit) {
		if(StringUtil.isBlank(bit)){
			return null;
		}
		int len = bit.length();
		if (len%4!=0 && len%8!=0) {
			return null;
		}
		if (len%8==0) {// 8 bit����
			int length = len/8;
			byte[] bytes = new byte[length];
			for (int i = 0; i < bytes.length; i++) {
				String tempBit = bit.substring(8*i, 8*(i+1));
				if (tempBit.charAt(0) == '0') {// ����
					bytes[i] = (byte)Integer.parseInt(tempBit, 2);
				} else {// ����
					bytes[i] = (byte)(Integer.parseInt(tempBit, 2) - 256);
				}
			}
			return bytes;
		} else {// 4 bit����
			int length = len/4;
			byte[] bytes = new byte[length];
			for (int i = 0; i < bytes.length; i++) {
				String tempBit = bit.substring(4*i, 4*(i+1));
				bytes[i] = (byte)Integer.parseInt(tempBit, 2);
			}
			return bytes;
		}
	}
}
