package mango.condor.domain.msg;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-16 下午2:20:30
 * @Description 
 */
public class LBSRequestMessage extends LBSMessage {
	
	protected int lang;	// 客户端语言类型,0代表简体，1代表香港，2代表台湾，3代表韩文
	private String version;	// 客户端版本号

	public LBSRequestMessage(int messageId) {
		super(messageId);
	}

	/**
	 * 是否 >= 2.5 版本
	 * @return
	 */
	public boolean isNotLessThanVersion_2_5 () {
		if (version == null) {
			return false;
		}
		
		version = version.trim();
		if (version.isEmpty()) {
			return false;
		}
		
		String[] arr = version.split(",");
		if (arr.length > 0) {
			try {
				return Integer.parseInt(arr[0]) > 29300;
			}
			catch (Exception e) {
				System.out.println( "illegal version: " + version);
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public int getLang() {
		return lang;
	}

	public void setLang(int lang) {
		this.lang = lang;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
