package mango.condor.toolkit;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-6-3 上午10:42:04
 * @Description 
 */
public class Const {
	
	// 缓存的玩家资料的key的前缀
	public static final String LBS_USER_INFO_PREFIX = "pi:";
	
	// 缓存的玩家的 名字->uid 索引的前缀
	public static final String LBS_USER_INFO_NAME_INDEX_PREFIX = "pii:";
	
	// 缓存geohash->玩家资料的key的集合的前缀
	public static final String LBS_GEO_HASH_PREFIX = "gh:";
	
	// 缓存[附近的人]的前缀
	public static final String LBS_NEIGHBOUR_LIST_PREFIX = "nl:";
	
	// 缓存未读瓶子的前缀
	public static final String BOTTLE_UNREAD_PREFIX = "b:";
	
	// 缓存我扔的瓶子的前缀
	public static final String BOTTLE_MY_THROW_PREFIX = "mybt:";
	
	// 缓存我捞的瓶子的前缀
	public static final String BOTTLE_MY_FETCH_PREFIX = "mybf:";
	
	// 缓存未读的瓶子数量的前缀
	public static final String BOTTLE_UNREAD_BOTTLE_NUM_PREFIX = "urbn:";
	
	// 缓存已被捞取的漂流瓶内容的前缀
	public static final String BOTTLE_CONTENT_PREFIX = "br:";
	
	// 分别表示所有，男，女
	public static final String LBS_GEO_HASH_ALL = "A";
	public static final String LBS_GEO_HASH_MEN = "M";
	public static final String LBS_GEO_HASH_WOMEN = "W";
	
	// 聊天数据缓存超时时间，单位为秒，4小时
	public static final int PRIVATE_CACHE_EXPIRE_SECONDS = 4 * 60 * 60;
	
	// 玩家数据缓存超时时间，单位为秒，48小时
	public static final int PLAYER_INFO_CACHE_EXPIRE_SECONDS = 48 * 60 * 60;
	
	// [附近的人]缓存时长, 单位秒
	public static final int NEIGHBOUR_LIST_EXPIRE_SECONDS = 3 * 60;
	
	// 未读瓶子的超时时间，单位为秒，2天
	public static final int UNREAD_BOTTLE_EXPIRE_SECONDS = 60 * 60 * 24 * 2;
	
	// 我的瓶子列表缓存的瓶子ID的超时时间，，单位为秒，1小时
	public static final int MY_BOTTLE_EXPIRE_SECONDS = 60 * 60;
	
	// 未读瓶子数量的超时时间，，单位为秒，1小时
	public static final int MY_UNREAD_BOTTLE_NUM_EXPIRE_SECONDS = 60 * 60;
	
	// 已被捞到的瓶子的超时时间，，单位为秒，2小时
	public static final int BOTTLE_CONTENT_EXPIRE_SECONDS = 60 * 60 * 2;
	
	// GeoHash前缀字符个数，越小格子范围越大
	// 值为6代表1.7公里左右
	public static final int GEO_HASH_PREFIX_CHAR_NUM = 5;
	
	// 每次获取附近的人最多返回的数量
	public static final int MAX_PERSON = 20;
	
	public static final int MAX_NEIGHBOUR_CNT = 3000;
	
	public static final int MAX_TRY_CNT = 50;
	
	// 百度地图SDK密钥
	public static final String BMAP_KEY = "1960874f987e3fe832b5be4262a0593c";
	
	// 百度逆地理编码URL
	public static final String BMAP_REV_GEO_URL = 
			"http://api.map.baidu.com/geocoder/v2/?ak=" + BMAP_KEY + "&location=%f,%f&output=json";
	
	// 百度根据IP地址获取位置信息URL
	public static final String BMAP_IP_GEO_URL = 
			"http://api.map.baidu.com/location/ip?ak=" + BMAP_KEY + "&ip=%s&coor=bd09ll";
	
	// 聊天库
	public static final int REDIS_DB_CHAT = 0;
	
	// 黑名单
	public static final int REDIS_DB_BLACK_LIST = 1;
	
	// 关注、粉丝列表
	public static final int REDIS_DB_FOLLOW_LIST = 2;
	
	// 锁
	public static final int REDIS_DB_LOCK = 3;
	
	// 新鲜事
	public static final int REDIS_DB_INDEX = 4;
	
	// 排行
	public static final int REDIS_DB_RANK = 5;
	
	// 玩家数据
	public static final int REDIS_DB_PLAYER_INFO = 6;
	
	// 地理位置
	public static final int REDIS_DB_GEO = 7;
	
	// 未读的漂流瓶id的缓存数据
	public static final int REDIS_DB_UNREAD_BOTTLE = 8;
	
	// 其它漂流瓶相关的缓存数据
	public static final int REDIS_DB_BOTTLE = 9;
	
	// 我被关注的信息
	public static final int REDIS_DB_FOLLOW_MSG = 10;
	
	/**  地图地理位置1度分布    */
	public static final int REDIS_DB_GEO_DISTRIBUTE = 11;
	
	/**  玩家经纬度分布    */
	public static final int GEO_DISTRIBUTE_LNG_LAT = 13;


	public static final String CHAT_SET_PREFIX = "cs:";			// 跨服某玩家的会话id的集合
	public static final String CHAT_SESSION_PREFIX = "cl:";		// 跨服私聊会话的前缀
	public static final String CHAT_BLACK_LIST_PREFIX = "bl:";	// 黑名单的前缀
	public static final String CHAT_FOLLOW_LIST_PREFIX = "fl:";	// 关注列表的前缀
	public static final String CHAT_FAN_LIST_PREFIX = "fa:";	// 粉丝列表的前缀
	public static final String CHAT_FOLLOW_MSG_PREFIX = "fm:";	// 我被关注的信息表的前缀
	
	// 分布式锁的前缀
	public static final String LOCK_KEY_PREFIX = "L:";
	
	// 首页新鲜事前缀
	public static final String LBS_INDEX_FRESH_PREFIX = "if:";
	
	// 关注排行的前缀
	public static final String LBS_FOLLOW_RANK = "lr:";
	
	// 关注排行 玩家->名字映射的前缀
	public static final String LBS_FOLLOW_RANK_INDEX = "lri:";
	
	//地理位置纬度
	public static final String GEO_DISTRIBUTE_LAT= "lat";

	//地理位置经度
	public static final String GEO_DISTRIBUTE_LNG = "lng";
	
	// 会话正常
	public static final byte CHAT_SESSION_STATUS_NORMAL = 0;
	
	// 会话已删除
	public static final byte CHAT_SESSION_STATUS_DELETED = 1;
	
	// 会话不可见
	public static final byte CHAT_SESSION_STATUS_INVISIBLE = 2;
	
	// 聊天内容状态
	// 正常
	public static final byte CHAT_CONTENT_NORMAL_STATUS = 0;
	
	// 隐藏的
	public static final byte CHAT_CONTENT_INVISIBLE_STATUS = 1;
	
	// 某个会话的内容最多有多少条缓存数据
	public static final int CHAT_MAX_CONTENT_ITEM_NUM = 50;
	
	// 编码
	public static final String CHARSET = "utf8";
	
	// 黑名单状态码
	// 正常状态
	public static final int BLACK_LIST_STATUS_OK = 0;
	
	// 已经对方在自己的黑名单列表
	public static final int BLACK_LIST_STATUS_ADD = 1;
	
	// 自己在对方的黑名单列表
	public static final int BLACK_LIST_STATUS_ADDED = 2;
	
	// 客户端语言类型
	// 简体
	public static final int LANG_ZH_CN = 0;
	
	// 香港
	public static final int LANG_ZH_HK = 1;
	
	// 台湾
	public static final int LANG_ZH_TW = 2;
	
	// 韩国
	public static final int LANG_KO_KR = 3;
	
	// 自旋锁的超时时间，单位:秒
	public static final int LOCK_TTL = 5;
	
	// 无意义，仅作为标识
	public static final String DUMMY = "D";
	
	// 每一次的自旋时间，单位:毫秒
	public static final int LOCK_SPIN_MILLI = 100;
	
	// 自旋次数限制
	public static final int LOCK_SPIN_MAX_CNT = 10;
	
	// 系统聊天机器人相关，未使用
	// 系统聊天机器人服务器id，未使用
	public static final int ROBOT_SERVER_ID = 0;
	
	// 系统消息，预留，未使用
	public static final int ROBOT_SYSTEM_ID = 2;
	
	// 判断玩家是否在线的时间差，现定位30分钟以内
	public static final int ONLINE_TIME_FLAG = 1000 * 60 * 30;
	
	// 关注排行榜人数的限制
	public static final int FOLLOW_RANK_LIMIT = 1000;
	
	// 初始化新鲜事的人数限制
	public static final int INDEX_INIT_LIMIT = 1000;
	
	// 新鲜事的保留时限，超过这个时限则删掉，暂设为1个小时
	public static final int INDEX_RESERVE_MILLI = 1000 * 60 * 60;
	
	// 一次获取新鲜事，最多返回的条数
	public static final int INDEX_GET_LIMIT = 2;
	
	// 同步游戏服的url模版
	public static final String SYNC_URL = "http://%s/game/GetPlayerInfoServlet";
	
	// 同步玩家数据的时间范围
	public static final int SYNC_MIN_TIME = 0;
	public static final int SYNC_MAX_TIME = 1000 * 60 * 60 * 24 * 60;
	
	// 同步玩家表，一次获取的数据条数
	public static final int SYNC_SCAN_LIMIT = 2000;
	
	// 加载关注表，一次获取的数据条数
	public static final int INIT_FOLLOW_LIST_SCAN_LIMIT = 5000;
	
	// 加载黑名单表，一次获取的数据条数
	public static final int INIT_BLACK_LIST_SCAN_LIMIT = 5000;
	
	// 加载瓶子表，一次获取的数据条数
	public static final int INIT_BOTTLE_SCAN_LIMIT = 5000;
	
	// 玩家数据表名
	public static final String TBL_NAME_PLAYER_INFO = "player_info";
	
	// 关注表名
	public static final String TBL_FOLLOW_LIST = "follow_list";
	
	// 黑名单表名
	public static final String TBL_BLACK_LIST = "black_list";
	
	// 瓶子表名
	public static final String TBL_BOTTLE = "bottle";
	
	// 自定义头像前缀
	public static final String DIY_AVATAR_PREFIX = "diy";
	
	// 加关注发私聊，如果对方的未读条数超过一定条数，则不发私聊提示，暂定为30条
	public static final int INSERT_FOLLOW_SEND_CHAT_LIMIT = 30;
	
	// 以下为一些异常信息，Logstash使用
	public static final String LBS_SERVER_NAME = "LBS服务器";
	
	public static final String LBS_EXP_ENTRY = "LBS入口异常";
	
	public static final String LBS_EXP_GZIP = "GZIP压缩异常";
	
	public static final String LBS_EXP_SYNC = "同步玩家数据异常";
	
	public static final String LBS_EXP_INIT_GEOIP = "初始化GEOIP数据库异常";
	
	public static final String LBS_EXP_BAIDU_GEO_REVERSE = "百度->根据经纬度逆解析地址失败";
	
	public static final String LBS_EXP_BAIDU_GEO_IP = "百度->根据IP地址设置经纬度失败";
	
	public static final String LBS_EXP_GEO_IP = "根据IP地址设置经纬度失败";
	
	public static final String LBS_EXP_HTTP_CLIENT = "HTTP CLIENT异常";
	
	public static final String LBS_EXP_DB = "数据库异常";
	
	public static final String LBS_EXP_REDIS = "Redis异常";
	
	public static final String LBS_EXP_GEO_TOOLKIT = "LBS地理工具异常";
}
