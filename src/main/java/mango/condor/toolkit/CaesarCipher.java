package mango.condor.toolkit;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by http://blog.csdn.net/zhiqiangzhan/article/details/4658106
 * @Date 2013-6-6 上午9:59:49
 * @Description
 */
public class CaesarCipher {
	
	// 我们只对英文做base64，不需要对+号做特殊处理
	// +号只会在对中文做base64才会出现
	// 详见http://www.cnblogs.com/lifesting/archive/2012/07/12/2587923.html
	
	private static final char[] UC_ENCRYPT_CHARS = { 'M', 'D', 'X', 'U', 'P',
			'I', 'B', 'E', 'J', 'C', 'T', 'N', 'K', 'O', 'G', 'W', 'R', 'S',
			'F', 'Y', 'V', 'L', 'Z', 'Q', 'A', 'H' };

	private static final char[] LC_ENCRYPT_CHARS = { 'm', 'd', 'x', 'u', 'p',
			'i', 'b', 'e', 'j', 'c', 't', 'n', 'k', 'o', 'g', 'w', 'r', 's',
			'f', 'y', 'v', 'l', 'z', 'q', 'a', 'h' };

	private static char[] UC_DECRYPT_CHARS = new char[26];

	private static char[] LC_DECRYPT_CHARS = new char[26];

	static {
		for (int i = 0; i < 26; i++) {
			char b = UC_ENCRYPT_CHARS[i];
			UC_DECRYPT_CHARS[b - 'A'] = (char) ('A' + i);

			b = LC_ENCRYPT_CHARS[i];
			LC_DECRYPT_CHARS[b - 'a'] = (char) ('a' + i);
		}
	}

	public static char encrypt(char b) {
		if (b >= 'A' && b <= 'Z') {
			return UC_ENCRYPT_CHARS[b - 'A'];
		} else if (b >= 'a' && b <= 'z') {
			return LC_ENCRYPT_CHARS[b - 'a'];
		} else {
			return b;
		}
	}

	public static char decrypt(char b) {
		if (b >= 'A' && b <= 'Z') {
			return UC_DECRYPT_CHARS[b - 'A'];
		} else if (b >= 'a' && b <= 'z') {
			return LC_DECRYPT_CHARS[b - 'a'];
		} else {
			return b;
		}
	}

	public static String encrypt(String input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			sb.append(encrypt(input.charAt(i)));
		}
		return sb.toString();
	}

	public static String decrypt(String input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			sb.append(decrypt(input.charAt(i)));
		}
		return sb.toString();
	}

}