package mango.condor.toolkit;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import mango.condor.domain.multilang.MultiLangConstant;


/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-3-6 下午2:21:20
 * @Description 
 */
public class MultiLanguageManager {
	
	private static ResourceBundle resbZHCN;
	private static ResourceBundle resbZHHK;
	private static ResourceBundle resbZHTW;
	private static ResourceBundle resbKOKR;
	
	public static void init() {
		Locale locale = new Locale("zh", "CN");
		resbZHCN = ResourceBundle.getBundle("server", locale);
		
		locale = new Locale("zh", "HK");
		resbZHHK = ResourceBundle.getBundle("server", locale);
		
		locale = new Locale("zh", "TW");
		resbZHTW = ResourceBundle.getBundle("server", locale);
		
		locale = new Locale("ko", "KR");
		resbKOKR = ResourceBundle.getBundle("server", locale);
	}
	 
	public static String getString(int lang, String key) {
		switch (lang) {
		case Const.LANG_ZH_CN:
			return resbZHCN.getString(key);
		case Const.LANG_ZH_HK:
			return resbZHHK.getString(key);
		case Const.LANG_ZH_TW:
			return resbZHTW.getString(key);
		case Const.LANG_KO_KR:
			return resbKOKR.getString(key);
		default:
			return resbZHCN.getString(key);
		}
	}
	
	/**
	 * 根据 server_zh_CN.properties 生成 MultiLangConstant 常量定义
	 */
	static void print () {
		Locale locale = new Locale("zh", "CN");
		ResourceBundle resbZHCN = ResourceBundle.getBundle("server", locale);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println( "server_zh_CN.properties create key for MultiLangConstant.class" );
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		Set<String> keySet = new TreeSet<String> ();
		
		Enumeration<String> e = resbZHCN.getKeys();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			keySet.add(key);
		}
		
		String prevPrexfi = null;
		for (String key : keySet) {
			String value = resbZHCN.getString(key);
			String newKey = key.toUpperCase();		
			
			if (prevPrexfi != null && !key.startsWith(prevPrexfi)) {
				System.out.println();
			}
			
			System.out.println( "public static final String " + newKey + " = \"" + key + "\"; //" + value );
			
			int pos = key.indexOf("_");
			prevPrexfi = key.substring(0, pos + 1);
		}
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println( "server_zh_CN.properties create key for MultiLangConstant.class END" );
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.exit(0);
	}
	
	/**
	 * 检查配置文件
	 * @return
	 */
	public static boolean check() {
		Field[] fields = MultiLangConstant.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				String value = resbZHCN.getString((String) field.get(null));
				if (value == null) {
					return false;
				}
				
				value = resbZHHK.getString((String) field.get(null));
				if (value == null) {
					return false;
				}
				
				value = resbZHTW.getString((String) field.get(null));
				if (value == null) {
					return false;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		print();
	}
}
