package mango.condor.cache.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import mango.condor.cache.BaseRedisCache;
import mango.condor.cache.RedisCacheKeyConst;
import mango.condor.domain.chat.ChatMyVo;
import mango.condor.toolkit.Const;
import redis.clients.jedis.JedisPool;

import com.gzyouai.hummingbird.common.json.JsonManager;
import com.gzyouai.hummingbird.common.utils.CollectionUtil;

public class ChatRedisCache extends BaseRedisCache {

	/**
	 * @param pool
	 * @param db
	 * @param keyProfix
	 */
	public ChatRedisCache(JedisPool pool) {
		super(pool, Const.REDIS_DB_CHAT, RedisCacheKeyConst.CHAT.value);
	}

	/**
	 * 获取一个ChatMyVo
	 * @param chatId
	 * @return
	 */
	public ChatMyVo getChat(String chatId) {		
		String key = createJedisKey(chatId);
		
		String value = get(key);
		if (value == null) {
			return null;
		}
		
		return JsonManager.getGson().fromJson(value, ChatMyVo.class);
	}
	
	/**
	 * 批量获取ChatMyVo
	 * @param chatIdList
	 * @return
	 * 		Map<chatId, ChatMyVo>
	 */
	public Map<String, ChatMyVo> batchChatMyVo (List<String> chatIdList) {
		if (CollectionUtil.isEmptyCollection(chatIdList)) {
			return new HashMap<String, ChatMyVo> ();
		}
		
		chatIdList = new ArrayList<String>( new HashSet<String>(chatIdList) );
		
		String[] keys = new String[chatIdList.size()];
		for (int i = 0; i < chatIdList.size(); i++) {
			keys[i] = createJedisKey(chatIdList.get(i));
		}
		
		Map<String, String> map = mget(keys);
		if (map == null) {
			return null;
		}
		
		ChatMyVo chat = null;
		Map<String, ChatMyVo> chatMap = new HashMap<String, ChatMyVo>();
		for (String value : map.values()) {
			if (value != null) {
				chat = JsonManager.getGson().fromJson(value, ChatMyVo.class);
				chatMap.put(chat.getChatId(), chat);
			}
		}
		
		return chatMap;
	}

	/**
	 * 删除一个ChatMyVo
	 * @param chatId
	 * @return
	 */
	public boolean deleteChat(String chatId) {
		String key = createJedisKey(chatId);
		
		return del(key) >= 0;
	}
	
	/**
	 * 批量删除ChatMyVo
	 * @param chatIdList
	 * @return
	 */
	public boolean batchDeleteChat(List<String> chatIdList) {
		if (CollectionUtil.isEmptyCollection(chatIdList)) {
			return false;
		}
		
		// 排重
		chatIdList = new ArrayList<String>(new HashSet<String>(chatIdList));
		String[] keys = new String[chatIdList.size()];
		for (int i = 0; i < chatIdList.size(); i++) {
			keys[i] = createJedisKey(chatIdList.get(i));
		}
		
		return del(keys) >= 0;
	}

	/**
	 * 设置一个ChatMyVo
	 * @param chat
	 * @return
	 */
	public boolean setChat(ChatMyVo chat) {
		if (chat == null) {
			return false;
		}
		
		String key = createJedisKey(chat.getChatId());
		String value = JsonManager.getGson().toJson(chat);
		return set(key, value, 3600);	// 3600秒
	}
	
	/**
	 * 批量设置ChatMyVo
	 * @param chatList
	 * @return
	 */
	public boolean batchSetChat(List<ChatMyVo> chatList) {
		if (CollectionUtil.isEmptyCollection(chatList)) {
			return false;
		}
		
		Map<String, String> kv = new HashMap<String, String>();
		for (ChatMyVo chat : chatList) {
			String key = createJedisKey(chat.getChatId());
			String value = JsonManager.getGson().toJson(chat);
			kv.put(key, value);
		}
		
		return mset(kv, 3600);	// 3600秒
	}
	
	/**
	 * 设置一堆ChatMyVo
	 * @param chatList
	 * @return
	 */
	public boolean setChatList(List<ChatMyVo> chatList) {
		if (chatList == null) {
			return false;
		}
		if (chatList.isEmpty()) {
			return true;
		}
		
		Map<String, String> kv = new HashMap<String, String>();
		for (ChatMyVo chat : chatList) {
			String key = createJedisKey(chat.getChatId());
			String value = JsonManager.getGson().toJson(chat);
			kv.put(key, value);
		}
		
		return mset(kv, 3600); // 3600秒
	}
}
