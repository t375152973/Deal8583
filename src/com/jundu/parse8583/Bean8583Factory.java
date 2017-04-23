package com.jundu.parse8583;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Bean8583Factory {

	private static Map<String, Properties> map = new HashMap<String, Properties>();

	private static Bean8583Factory instance = null;

	public static Bean8583Factory getInstance() {
		if (null == instance) {
			map.clear();
			instance = new Bean8583Factory();
		}
		return instance;
	}

	private Bean8583Factory() {
		init();
	}

	public void init() {
		System.out.println("����8583���ÿ�ʼ");

		File f = new File(this.getClass().getResource("").getPath() + "ISO8583medata.xml");
		if ((f.exists()) && (f.isFile())) {
			SAXReader reader = new SAXReader();
			try {
				Iterator<Node> iterator2;
				Document doc = reader.read(f);
				List obj = doc.getRootElement().elements();
				if (obj == null) {
					return;
				}
				Iterator<Node> iterator = obj.iterator();
				while (iterator.hasNext()) {
					Node imetadata = iterator.next();
					Properties pop = new Properties();

					Node isHeader = imetadata.selectSingleNode("@isHeader");
					if (null != isHeader) {
						pop.setProperty("isHeader", isHeader.getText());
					}

					Node isBCD = imetadata.selectSingleNode("@isBCD");
					if (null != isBCD) {
						pop.setProperty("isBCD", isBCD.getText());
					}

					Node type = imetadata.selectSingleNode("@type");
					if (null != type) {
						pop.setProperty("type", type.getText());
					}

					Node length = imetadata.selectSingleNode("@length");
					if (null != length) {
						pop.setProperty("length", length.getText());
					}

					Node variable_flag = imetadata.selectSingleNode("@variable_flag");
					if (null != variable_flag) {
						pop.setProperty("variable_flag", variable_flag.getText());
					}

					Node field_index = imetadata.selectSingleNode("@field_index");
					if (null != field_index) {
						pop.setProperty("field_index", field_index.getText());
					}

					Node encoding = imetadata.selectSingleNode("@encoding");
					if (null != encoding) {
						pop.setProperty("encoding", encoding.getText());
					}
					
					Node zipType = imetadata.selectSingleNode("@zipType");
					if (null != zipType) {
						pop.setProperty("zipType", zipType.getText());
					}

					pop.setProperty("name", imetadata.getName());
					map.put(imetadata.getName(), pop);

				}
			} catch (DocumentException e) {
				e.printStackTrace();
				System.out.println("����8583�����쳣");
			}
		}
		System.out.println("����8583�������");
	}

	public Map<String, Properties> getMap() {
		return map;
	}

	public Properties getFieldPropertie(String fieldName) {
		return map.get(fieldName);
	}

	public String getFieldPropertieVal(String fieldName, String propertieName) {
		return map.get(fieldName).getProperty(propertieName);
	}

}