package mango.condor.toolkit;

import it.sauronsoftware.base64.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.gzyouai.hummingbird.common.component.Pair;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-11 上午10:09:43
 * @Description 
 */
public class CommonToolkit {
	
	// 当前服务端是否在维护
	private static AtomicBoolean isMaintance = new AtomicBoolean(false);
	
	/**
	 * 判断是否可以向对方版本发送语音聊天
	 * @param version
	 * @return
	 */
	public static boolean isCanSendVoice(String version) {
		// 2.2版本开始支持语音
		if (null == version) {
			return false;
		}
		
		String[] tmp = version.split("\\.");
		int mainVersion = Integer.parseInt(tmp[0]);
		if (mainVersion < 2) {
			return false;
		}
		
		int subVersion = Integer.parseInt(tmp[1]);
		if (subVersion < 2) {
			return false;
		}
		
		return true;
	}
		
	// Gson对象的ThreadLocal
    private static ThreadLocal<Gson> gsonThreadLocal = new ThreadLocal<Gson>() {
        protected synchronized Gson initialValue() {
            return new Gson();
        }
    };
    
    // Date对象的ThreadLocal
    private static ThreadLocal<Date> dateThreadLocal = new ThreadLocal<Date>() {
        protected synchronized Date initialValue() {
            return new Date();
        }
    };
    
    // 格式化串
    private static final String TEMPLATE_CHAT_FORMAT = "MM月dd日 HH:mm";
    
    // SimpleDateFormat对象的ThreadLocal
    private static ThreadLocal<SimpleDateFormat> chatFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        protected synchronized SimpleDateFormat initialValue() {
            return new SimpleDateFormat(TEMPLATE_CHAT_FORMAT);
        }
    };
	
	public static Gson getGson() {
		return gsonThreadLocal.get();
	}
	
	public static String getFormatDateString(long timestamp) {
		Date date = dateThreadLocal.get();
		date.setTime(timestamp);
		
		SimpleDateFormat formater = chatFormatThreadLocal.get();
		return formater.format(date);
	}
	
	public static String getPostResult(String url, Map<String, String> pairs, boolean isSecret) {
		HttpClient hc = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		
		for (String key : pairs.keySet()) {
			if (isSecret && key.equals("data")) {
				list.add(new BasicNameValuePair(key, CaesarCipher.encrypt(Base64.encode(pairs.get(key), Const.CHARSET))));
			} else {
				list.add(new BasicNameValuePair(key, pairs.get(key)));
			}
		}
		
		hc.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Const.CHARSET);
		
		String result = null;
		UrlEncodedFormEntity entity = null;
		boolean isSuc = false;
		
		try {
			entity = new UrlEncodedFormEntity(list, Const.CHARSET);
			post.setEntity(entity);
			
			HttpResponse resp = hc.execute(post);
			HttpEntity respEntity = resp.getEntity();
			
			result = EntityUtils.toString(respEntity, Const.CHARSET);
		    isSuc = true;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_HTTP_CLIENT, e, null, null);
			e.printStackTrace();
			post.abort();
		} finally {
			if (isSuc) {
				try {
					EntityUtils.consume(entity);
				} catch (IOException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_HTTP_CLIENT, e, 	null, null);
					e.printStackTrace();
				}
			}
			
			if (entity != null) {
				try {
					entity.getContent().close();
				} catch (IOException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_HTTP_CLIENT, e, 	null, null);
					e.printStackTrace();
				}
			}
			
			if (hc != null) {
				hc.getConnectionManager().shutdown();
			}
		}

	    return result;
	}
	
	public static String httpGet(String url) {
		HttpClient hc = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		
		hc.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "utf8");
		
		String result = null;
		UrlEncodedFormEntity entity = null;
		boolean isSuc = false;
		
		try {
			HttpResponse resp = hc.execute(get);
			HttpEntity respEntity = resp.getEntity();
		    
		    result = EntityUtils.toString(respEntity);
		    isSuc = true;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_HTTP_CLIENT, e, null, null);
			e.printStackTrace();
			get.abort();
		} finally {
			if (isSuc) {
				try {
					EntityUtils.consume(entity);
				} catch (IOException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_HTTP_CLIENT, e, null, null);
					e.printStackTrace();
				}
			}
			
			if (entity != null) {
				try {
					entity.getContent().close();
				} catch (IOException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_HTTP_CLIENT, e, null, null);
					e.printStackTrace();
				}
			}
			
			if (hc != null) {
				hc.getConnectionManager().shutdown();
			}
		}

	    return result;
	}
	
	/**
	 * 得到sessionId
	 * @param senderServerId
	 * @param sender
	 * @param receiverServerId
	 * @param receiver
	 * @return
	 */
	public static String getSessionId(int senderServerId, int sender, int receiverServerId, int receiver) {
		String s = senderServerId + "-" + sender;
		String r = receiverServerId + "-" + receiver;
		return s.compareTo(r) < 0 ? s + ":" + r : r + ":" + s;
	}
	
	/**
	 * 获取某玩家的唯一ID
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static String toUID(int serverId, int playerId) {
		return serverId + "-" + playerId;
	}
	
	/**
	 * 解析Uid
	 * @param uid
	 * @return
	 * 		Pair<ServerId, PlayerId>
	 */
	public static Pair<Integer, Integer> parseUid (String uid) {
		String[] arr = uid.split("-");
		int serverId = Integer.parseInt(arr[0]);
		int playerId = Integer.parseInt(arr[1]);
		
		return Pair.makePair(serverId, playerId);
	}
	
	/**
	 * 判断一个utf8串是否有超过3个字节的字符
	 *
	 * @param str
	 * @return 返回true则合法
	 */
	public static boolean isUTF8StringValid(String str) {
		if (null == str) {
			return false;
		}

		byte[] b = null;
		try {
			b = str.getBytes("utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}

		int i = 0;
		int len = b.length;

		while (i < len) {
			byte tmp = (byte) (b[i] >> 7);
			tmp &= 0x01;
			// ascii字符，跳过
			if (tmp == 0) {
				++i;
				continue;
			}

			byte tmp1 = (byte) (b[i] >> 6);
			byte tmp2 = (byte) (b[i] >> 5);
			byte tmp3 = (byte) (b[i] >> 4);
			tmp1 &= 0x01;
			tmp2 &= 0x01;
			tmp3 &= 0x01;

			// 该字符超过3个字节
			if (tmp1 == 1 && tmp2 == 1 && tmp3 == 1) {
				return false;
			}

			if (tmp1 == 1) {
				if (tmp2 == 1) {
					i += 3;
				} else {
					i += 2;
				}
			} else {
				// impossible
				return false;
			}
		}

		return true;
	}

	public static AtomicBoolean getIsMaintance() {
		return isMaintance;
	}

	public static void setIsMaintance(AtomicBoolean isMaintance) {
		CommonToolkit.isMaintance = isMaintance;
	}

}
