package com.jundu.parse8583.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class SimpleUtils {
	public static byte[] getRandom(int length) {
		Random random = new Random();
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = ((byte) random.nextInt(256));
		}
		return result;
	}

	public static String str2HexStr(String str, String charset) throws UnsupportedEncodingException {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes(charset);
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString().trim();
	}

	public static String hexStr2Str(String hexStr, String charset) throws UnsupportedEncodingException {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes, charset);
	}

	public static byte high(byte chr) {
		byte temp = 0;
		for (int i = 7; i >= 4; i--) {
			if ((1L << i & chr) != 0L) {
				temp = (byte) (temp | (byte) (int) (1L << i - 4));
			}
		}
		return temp;
	}

	public static int bytes2int(byte[] bytes, int offset) {
		int length1 = bytes[(offset + 3)] << 24 & 0xFF000000;
		int length2 = bytes[(offset + 2)] << 16 & 0xFF0000;
		int length3 = bytes[(offset + 1)] << 8 & 0xFF00;
		int length4 = bytes[(offset + 0)] << 0 & 0xFF;

		return length1 + length2 + length3 + length4;
	}

	public static short bytes2short(byte[] bytes, int offset) {
		int length1 = bytes[(offset + 0)] << 8 & 0xFF00;
		int length2 = bytes[(offset + 1)] << 0 & 0xFF;

		return (short) (length1 + length2);
	}

	public static byte[] int2bytes(int value) {
		byte[] bytes = new byte[4];

		bytes[0] = ((byte) (value & 0xFF));
		bytes[1] = ((byte) ((value & 0xFF00) >> 8));
		bytes[2] = ((byte) ((value & 0xFF0000) >> 16));
		bytes[3] = ((byte) ((value & 0xFF000000) >> 24));

		return bytes;
	}

	public static byte[] short2bytes(short value) {
		byte[] bytes = new byte[2];

		bytes[0] = ((byte) (value & 0xFF));
		bytes[1] = ((byte) ((value & 0xFF00) >> 8));

		return bytes;
	}

	public static String bytes2hex(byte[] bytes, int len) {
		if ((len > bytes.length) || (len <= 0)) {
			len = bytes.length;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			String temp = Integer.toHexString(bytes[i]);
			switch (temp.length()) {
			case 0:
			case 1:
				temp = "0" + temp;
				break;
			default:
				temp = temp.substring(temp.length() - 2);
			}
			sb.append(temp);
		}
		return sb.toString().toUpperCase();
	}

	public static String bytes2hex(byte[] bytes) {
		return bytes2hex(bytes, 0);
	}

	public static int toInt(char a) {
		if ((a >= '0') && (a <= '9')) {
			return a - '0';
		}
		if ((a >= 'A') && (a <= 'F')) {
			return a - '7';
		}
		if ((a >= 'a') && (a <= 'f')) {
			return a - 'W';
		}
		return 0;
	}

	public static byte[] hex2bytes(String hex) {
		if (hex.length() % 2 != 0) {
			hex = "0" + hex;
		}
		int len = hex.length() / 2;
		byte[] val = new byte[len];
		for (int i = 0; i < len; i++) {
			val[i] = ((byte) (toInt(hex.charAt(2 * i)) * 16 + toInt(hex.charAt(2 * i + 1))));
		}
		return val;
	}

	public static String bcd2string(byte[] bcddata, int offset, int len) {
		int strlen = len * 2;
		byte[] temp = new byte[strlen];

		int i = 0;
		for (int j = 0; i < len; i++) {
			temp[(j++)] = high(bcddata[(offset + i)]);
			temp[(j++)] = ((byte) (bcddata[(offset + i)] & 0xF));
		}
		for (i = 0; i < strlen; i++) {
			if (temp[i] >= 10) {
				temp[i] = ((byte) (65 + temp[i] - 10));
			} else {
				int tmp102_100 = i;
				byte[] tmp102_98 = temp;
				tmp102_98[tmp102_100] = ((byte) (tmp102_98[tmp102_100] + 48));
			}
		}
		return new String(temp);
	}

	public static String bcd2string(byte[] bcddata) {
		return bcd2string(bcddata, 0, bcddata.length);
	}

	static byte hxv(byte chr) {
		if (chr > 96) {
			return (byte) (chr - 97 + 10);
		}
		if (chr > 64) {
			return (byte) (chr - 65 + 10);
		}
		if (chr == 0) {
			return 0;
		}
		return (byte) (chr - 48);
	}

	/*public static byte[] string2bcd(String str, int len) {
		byte[] temp = new byte[len * 2];
		for (int i = 0; i < len * 2; i++) {
			temp[i] = 48;
		}
		StringBuffer sb = new StringBuffer(new String(temp));

		sb = sb.replace(len * 2 - str.length(), sb.length(), str);

		temp = new byte[len];

		int lpi = 0;
		for (int lpj = 0; lpi < len; lpi++) {
			byte high = (byte) (hxv((byte) sb.charAt(lpj++)) << 4);
			byte low = hxv((byte) sb.charAt(lpj++));
			temp[lpi] = ((byte) (high | low));
		}
		return temp;
	}*/
	/**
	 * @功能: 10进制串转为BCD码
	 * @参数: 10进制串
	 * @结果: BCD码
	 */
	public static byte[] string2bcd(String asc) {
		int len = asc.length();
		int mod = len % 2;
		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}
		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}
		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;
		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}
			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}
			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	public static byte[] int2bcd(int data) {
		String buf = String.valueOf(data);

		return string2bcd(buf);
	}

	public static byte[] long2bcd(long data) {
		String buf = String.valueOf(data);

		return string2bcd(buf);
	}

	public static int ntohl(byte[] bytes) {
		return ntohl(bytes, 0);
	}

	public static int ntohl(byte[] bytes, int offset) {
		int length1 = bytes[(offset + 0)] << 24 & 0xFF000000;
		int length2 = bytes[(offset + 1)] << 16 & 0xFF0000;
		int length3 = bytes[(offset + 2)] << 8 & 0xFF00;
		int length4 = bytes[(offset + 3)] << 0 & 0xFF;

		return length1 + length2 + length3 + length4;
	}

	public static byte[] htonl(int value) {
		byte[] bytes = new byte[4];

		bytes[3] = ((byte) (value & 0xFF));
		bytes[2] = ((byte) ((value & 0xFF00) >> 8));
		bytes[1] = ((byte) ((value & 0xFF0000) >> 16));
		bytes[0] = ((byte) ((value & 0xFF000000) >> 24));

		return bytes;
	}

	public static short ntohs(byte[] bytes, int offset) {
		int length = (bytes[(offset + 0)] << 8 & 0xFF00) + (bytes[(offset + 1)] << 0 & 0xFF);

		return (short) length;
	}

	public static byte[] htons(short value) {
		byte[] bytes = new byte[2];

		bytes[1] = ((byte) (value & 0xFF));
		bytes[0] = ((byte) ((value & 0xFF00) >> 8));

		return bytes;
	}

	public static byte[] xor(byte[] b1, byte[] b2) {
		int len = b1.length <= b2.length ? b1.length : b2.length;
		byte[] temp = new byte[len];
		for (int i = 0; i < len; i++) {
			temp[i] = ((byte) (b1[i] ^ b2[i]));
		}
		return temp;
	}

	public static String trunkString(int length, String orig) {
		if (length <= 0) {
			length = 256;
		}
		int len = orig.length();
		if (len < 2 * length + 3) {
			return orig;
		}
		return orig.substring(0, length) + "..." + orig.substring(orig.length() - length);
	}

	public static String adjustString(String value, char fill, int length, boolean head) {
		if ((value == null) || (length < 0)) {
			return null;
		}
		int srcLen = value.getBytes().length;
		if (srcLen >= length) {
			return new String(value.getBytes(), 0, length);
		}
		StringBuffer sb = new StringBuffer().append(fill);
		int len = length - srcLen;
		while (len != 1) {
			len /= 2;
			sb.append(sb);
		}
		sb.append(sb).setLength(length - srcLen);
		if (head) {
			return value;
		}
		return sb.insert(0, value).toString();
	}

	public static String centerString(String value, int len) {
		int length = value.getBytes().length;
		if (length >= len) {
			return value.substring(0, len);
		}
		int subLen = (len - length) / 2;
		return padString(padString(value, length + subLen, -1), len, 1);
	}

	public static String padString(String str, int len, int direct) {
		if (str == null) {
			return "";
		}
		if (str.length() >= len) {
			return str;
		}
		int fill = len - str.length();
		int precount = 0;
		int postcount = 0;
		if (direct < 0) {
			precount = fill;
		} else if (direct > 0) {
			postcount = fill;
		} else {
			precount = fill % 2 > 0 ? fill / 2 + 1 : fill / 2;
			postcount = fill - precount;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < precount; i++) {
			sb.append(" ");
		}
		sb.append(str);
		for (int i = 0; i < postcount; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	public static byte[] read(InputStream in, int size) throws IOException, InterruptedIOException {
		byte[] buf = new byte[size];

		int remain = size;
		int received = 0;
		while (remain > 0) {
			int rc = in.read(buf, received, remain);
			if (rc == -1) {
				throw new IOException("网络连接可能存在问题");
			}
			remain -= rc;
			received += rc;
		}
		return buf;
	}

	public static String getAscii(String str) {
		String tmp;
		StringBuffer sb = new StringBuffer(1000);
		char c;
		int i, j;
		sb.setLength(0);
		for (i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if (c > 255) {
				sb.append("//u");
				j = (c >>> 8);
				tmp = Integer.toHexString(j);
				if (tmp.length() == 1)
					sb.append("0");
				sb.append(tmp);
				j = (c & 0xFF);
				tmp = Integer.toHexString(j);
				if (tmp.length() == 1)
					sb.append("0");
				sb.append(tmp);
			} else {
				sb.append(c);
			}

		}
		return (new String(sb));
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String str = "732568";
		System.out.println(str2HexStr(str, "GBK"));
	}
}
