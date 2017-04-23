package com.jundu.parse8583.model;

import java.io.Serializable;

import com.jundu.parse8583.Make8583;

public class Entity8583 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2343300654972914533L;
	/**
	 * 默认数值均为hex数值
	 */
	//报文头
	private String H1;
	private String H2;
	private String H3;
	private String H4;
	private String H5;
	private String H6;
	private String H7;
	//消息类型
	private String messageType;
	//位图信息
	private String bitmap1;
	private String bitmap2;
	//域信息
	private String F2;
	private String F3;
	private String F4;
	private String F11;
	private String F12;
	private String F13;
	private String F14;
	private String F15;
	private String F22;
	private String F23;
	private String F24;
	private String F25;
	private String F26;
	private String F32;
	private String F35;
	private String F36;
	private String F37;
	private String F38;
	private String F39;
	private String F41;
	private String F42;
	private String F44;
	private String F48;
	private String F49;
	private String F52;
	private String F53;
	private String F54;
	private String F55;
	private String F58;
	private String F60;
	private String F61;
	private String F62;
	private String F63;
	private String F64;
	public String getH1() {
		return H1;
	}
	public void setH1(String h1) {
		H1 = h1;
	}
	public String getH2() {
		return H2;
	}
	public void setH2(String h2) {
		H2 = h2;
	}
	public String getH3() {
		return H3;
	}
	public void setH3(String h3) {
		H3 = h3;
	}
	public String getH4() {
		return H4;
	}
	public void setH4(String h4) {
		H4 = h4;
	}
	public String getH5() {
		return H5;
	}
	public void setH5(String h5) {
		H5 = h5;
	}
	public String getH6() {
		return H6;
	}
	public void setH6(String h6) {
		H6 = h6;
	}
	public String getH7() {
		return H7;
	}
	public void setH7(String h7) {
		H7 = h7;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getBitmap1() {
		return bitmap1;
	}
	public void setBitmap1(String bitmap1) {
		this.bitmap1 = bitmap1;
	}
	public String getBitmap2() {
		return bitmap2;
	}
	public void setBitmap2(String bitmap2) {
		this.bitmap2 = bitmap2;
	}
	public String getF2() {
		return F2;
	}
	public void setF2(String f2) {
		F2 = f2;
	}
	public String getF3() {
		return F3;
	}
	public void setF3(String f3) {
		F3 = f3;
	}
	public String getF4() {
		return F4;
	}
	public void setF4(String f4) {
		F4 = f4;
	}
	public String getF11() {
		return F11;
	}
	public void setF11(String f11) {
		F11 = f11;
	}
	public String getF12() {
		return F12;
	}
	public void setF12(String f12) {
		F12 = f12;
	}
	public String getF13() {
		return F13;
	}
	public void setF13(String f13) {
		F13 = f13;
	}
	public String getF14() {
		return F14;
	}
	public void setF14(String f14) {
		F14 = f14;
	}
	public String getF15() {
		return F15;
	}
	public void setF15(String f15) {
		F15 = f15;
	}
	public String getF22() {
		return F22;
	}
	public void setF22(String f22) {
		F22 = f22;
	}
	public String getF23() {
		return F23;
	}
	public void setF23(String f23) {
		F23 = f23;
	}
	public String getF24() {
		return F24;
	}
	public void setF24(String f24) {
		F24 = f24;
	}
	public String getF25() {
		return F25;
	}
	public void setF25(String f25) {
		F25 = f25;
	}
	public String getF26() {
		return F26;
	}
	public void setF26(String f26) {
		F26 = f26;
	}
	public String getF32() {
		return F32;
	}
	public void setF32(String f32) {
		F32 = f32;
	}
	public String getF35() {
		return F35;
	}
	public void setF35(String f35) {
		F35 = f35;
	}
	public String getF36() {
		return F36;
	}
	public void setF36(String f36) {
		F36 = f36;
	}
	public String getF37() {
		return F37;
	}
	public void setF37(String f37) {
		F37 = f37;
	}
	public String getF38() {
		return F38;
	}
	public void setF38(String f38) {
		F38 = f38;
	}
	public String getF39() {
		return F39;
	}
	public void setF39(String f39) {
		F39 = f39;
	}
	public String getF41() {
		return F41;
	}
	public void setF41(String f41) {
		F41 = f41;
	}
	public String getF42() {
		return F42;
	}
	public void setF42(String f42) {
		F42 = f42;
	}
	public String getF44() {
		return F44;
	}
	public void setF44(String f44) {
		F44 = f44;
	}
	public String getF48() {
		return F48;
	}
	public void setF48(String f48) {
		F48 = f48;
	}
	public String getF49() {
		return F49;
	}
	public void setF49(String f49) {
		F49 = f49;
	}
	public String getF52() {
		return F52;
	}
	public void setF52(String f52) {
		F52 = f52;
	}
	public String getF53() {
		return F53;
	}
	public void setF53(String f53) {
		F53 = f53;
	}
	public String getF54() {
		return F54;
	}
	public void setF54(String f54) {
		F54 = f54;
	}
	public String getF55() {
		return F55;
	}
	public void setF55(String f55) {
		F55 = f55;
	}
	public String getF58() {
		return F58;
	}
	public void setF58(String f58) {
		F58 = f58;
	}
	public String getF60() {
		return F60;
	}
	public void setF60(String f60) {
		F60 = f60;
	}
	public String getF61() {
		return F61;
	}
	public void setF61(String f61) {
		F61 = f61;
	}
	public String getF62() {
		return F62;
	}
	public void setF62(String f62) {
		F62 = f62;
	}
	public String getF63() {
		return F63;
	}
	public void setF63(String f63) {
		F63 = f63;
	}
	public String getF64() {
		return F64;
	}
	public void setF64(String f64) {
		F64 = f64;
	}
	@Override
	public String toString() {
		return Make8583.make8583(this);
	}
}
