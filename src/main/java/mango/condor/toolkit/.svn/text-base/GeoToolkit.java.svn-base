package mango.condor.toolkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import mango.condor.component.DataPager;
import mango.condor.domain.lbs.NeighbourInfo;
import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.lbs.RangePlayerInfo;
import mango.condor.service.StorageService;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.VincentyGeodesy;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-2 上午11:47:52
 * @Description
 */
public class GeoToolkit {

	/**
	 * 根据经纬度取得GeoHash的Base32串
	 * 
	 * @param lat
	 * @param log
	 * @return
	 */
	public static String getGeoHashBase32(double lat, double lng) {
		try {
			GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lng, Const.GEO_HASH_PREFIX_CHAR_NUM);
			return geoHash.toBase32();
		} catch (IllegalArgumentException e) {
			// LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_GEO_TOOLKIT, e, null, null);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取附近的人
	 * 		1) 用户的3个坐标
	 * 			A) 原始坐标： playerInfo的字段存储
	 * 				a) 计算 用户A 和 用户B 的距离可以取得最精确距离
	 * 			B) 缩放geohash存储最小点坐标: redis缓存geohash的 key
	 * 				a) 获取附近的人时候，减少向外 [迭代获取 附近的人 的次数]
	 * 			C) 缩放附近的人的存储坐标: redis缓存附近的人的key
	 * 				a) 用户简单移动也不会发生变化
	 * 				b) 该区域内用户使用同一key
	 * 			
	 * @param serverId
	 * @param playerId
	 * @param lat
	 * @param lng
	 * @param gender
	 * @param isOnline
	 * @param isLocal
	 * @param isAvatar
	 * @param isVoice
	 * @param isMore
	 * @return
	 */
	public static List<PlayerInfo> getNeighbourPlayerInfoList (int serverId, int playerId, final double lat, final double lng, 
			String gender, boolean isOnline, boolean isLocal, boolean isAvatar, boolean isVoice, 
			boolean isMore) {		
		final long now = System.currentTimeMillis();
		
		// geohash - 缓存的key
		double minPointLng = BMapToolkit.scaleLatOrLngForGeohashMinPoint(lng);
		double minPointLat = BMapToolkit.scaleLatOrLngForGeohashMinPoint(lat);
		String geoHash = GeoToolkit.getGeoHashBase32(minPointLat, minPointLng);
		
		// 附近的人
		String uid = CommonToolkit.toUID(serverId, playerId);
		NeighbourInfo neighbourInfo = RedisToolkit.getNeighbourInfo(uid, gender, isOnline);
		
		if (neighbourInfo == null) {
			if (isMore) {
				return null;
			}
			
			final int maxNeighbour = 1000;	// 最多获取多少位[附近的人]
			final int maxScalePoint = 5000;	// 最多搜索多少个[缩放坐标点]
			final long millisecondLimit = 1000L * 10; // 向外迭代获取[附近的人]的时间限制 - 单位毫秒
			
			// 向外 迭代 获取 [附近的人]
			PointManger pointManger = new PointManger(geoHash);
			Set<String> neighbourKeySet = new HashSet<String>();
			
			while (neighbourKeySet.size() <= maxNeighbour && pointManger.deadScaledSet.size() <= maxScalePoint) {
				String nextGeoHash = pointManger.nextPointGeoHash();
				if (nextGeoHash == null) {
					break;
				}
				
				Set<String> set = RedisToolkit.getGeoHashSet(nextGeoHash, gender, isOnline);
				if (set != null) {
					neighbourKeySet.addAll( set );
				}
				
				long diff = System.currentTimeMillis() - now;
				if (diff >= millisecondLimit) {
					// 时间太长了
					System.out.println( "getNeighbourPlayerInfoList search timeout. diff=" + diff + ", searchPointCount=" + pointManger.deadScaledSet.size());
					break; 
				}
			}
			pointManger.clear();
			
			neighbourKeySet.remove(uid);
			
			neighbourInfo = new NeighbourInfo();
			neighbourInfo.setLastPageNum(0);
			neighbourInfo.setUidSet(neighbourKeySet);
		}
		
		// 获取 符合条件的 附近的人
		List<PlayerInfo> playerList = includeNeighbourPlayer(serverId, playerId, lat, lng, 
				isOnline, isLocal, isAvatar, isVoice, 
				now, geoHash, neighbourInfo.getUidSet());

		// 按照 距离 升序排序
		Collections.sort(playerList, new Comparator<PlayerInfo>() {
			@Override
			public int compare(PlayerInfo o1, PlayerInfo o2) {
				return Double.compare(o1.getDistance(), o2.getDistance());
			}
		});
		
		// 提取分页数据
		final int pageSize = 20;
		int pageNum = isMore ? (neighbourInfo.getLastPageNum()) + 1 : 1;
		DataPager<PlayerInfo> pager = new DataPager<PlayerInfo>(playerList, pageSize);
		if (pageNum > pager.getPageCount()) {
			return new ArrayList<PlayerInfo>();
		}
		
		// 更新[附近的人]到缓存
		neighbourInfo.setLastPageNum(pageNum);
		RedisToolkit.putNeighbourInfo(uid, gender, isOnline, neighbourInfo);
		 
		return pager.getPageData(pageNum);
	}

	/**
	 * @param serverId
	 * @param playerId
	 * @param lat
	 * @param lng
	 * @param gender
	 * @param isOnline
	 * @param isLocal
	 * @param isAvatar
	 * @param isVoice
	 * @param minLat 最小纬度
	 * @param minLng 最小经度
	 * @param maxLat 最大纬度
	 * @param maxLng 最大经度
	 * @return
	 */
	public static List<PlayerInfo> getRangePlayerInfoList(int serverId, int playerId, final double lat, final double lng, String gender,
			boolean isOnline, boolean isLocal, boolean isAvatar, boolean isVoice, final double minLat, final double minLng,
			final double maxLat, final double maxLng) {
		final long now = System.currentTimeMillis();
		final int maxRangeSize = 10; // 最多获取多少位[范围的人]

		// geohash - 缓存的key
		double minPointLng = BMapToolkit.scaleLatOrLngForGeohashMinPoint(lng);
		double minPointLat = BMapToolkit.scaleLatOrLngForGeohashMinPoint(lat);

		// 范围内的人
		String uid = CommonToolkit.toUID(serverId, playerId);
		RangePlayerInfo neighbourInfo = new RangePlayerInfo();
		Set<String> neighbourKeySet = new HashSet<String>();
		String selfGeoHash = GeoToolkit.getGeoHashBase32(minPointLat, minPointLng);

		double searchMinLat = BMapToolkit.scaleLatOrLngForGeohashMinPoint(minLat);
		double searchMinLng = BMapToolkit.scaleLatOrLngForGeohashMinPoint(minLng);
		double searchMaxLat = BMapToolkit.scaleLatOrLngForGeohashMinPoint(maxLat);
		double searchMaxLng = BMapToolkit.scaleLatOrLngForGeohashMinPoint(maxLng);

		String minGeoHash = GeoToolkit.getGeoHashBase32(searchMinLat, searchMinLng);
		String maxGeoHash = GeoToolkit.getGeoHashBase32(searchMaxLat, searchMaxLng);
		String areaGeoHash = minGeoHash + maxGeoHash;
		// 最大最小经纬度数值范围内数据先从jedis中读取，然后再存到jedis中去
//		neighbourInfo = RedisToolkit.getGeoHashAreaPlayer(areaGeoHash);
		neighbourInfo = null;
		// 根据传进来的经纬度大小范围，读取玩家数据
		if (neighbourInfo == null || neighbourInfo.getUidSet().size() == 0) {
			neighbourInfo = new RangePlayerInfo();
			Set<String> set = RedisToolkit.getGeoHashByLngAndLat(minLat, maxLat, minLng, maxLng);
			if (set.size() > maxRangeSize) {
				ArrayList<String> list = new ArrayList<String>(set);
				Random random = new Random();
				for (int i = 0; i < maxRangeSize; i++) {
					int size = list.size();
					int index = random.nextInt(size);
					neighbourKeySet.add(list.get(index));
					list.remove(index);
				}
			} else {
				neighbourKeySet.addAll(set);
			}
			neighbourKeySet.remove(uid);
			// 移除自己队列
			neighbourInfo.setUidSet(neighbourKeySet);
			neighbourInfo.setSize(neighbourKeySet.size());
			RedisToolkit.putGeoHashAreaPlayer(areaGeoHash, neighbourInfo);
		}
		// 获取 符合条件的 范围内的人
		List<PlayerInfo> playerList = includeNeighbourPlayer(serverId, playerId, lat, lng, isOnline, isLocal, isAvatar, isVoice, now,
				selfGeoHash, neighbourInfo.getUidSet());
		long diff = System.currentTimeMillis() - now;
//		for(PlayerInfo info :playerList){
//			System.out.println( info.getName() + "  " + info.getLng() + "   " + info.getLat());
//		}
		System.out.println("getRangePlayerInfoList search timeout. diff=" + diff);
		return playerList;
	}

	/**
	 * 过滤附近的人
	 * 
	 * @param serverId
	 * @param playerId
	 * @param lat
	 * @param lng
	 * @param isOnline
	 * @param isLocal
	 * @param isAvatar
	 * @param isVoice
	 * @param now
	 * @param geoHash 缩放geohash存储最小点坐标: redis缓存geohash的 key 用于playerInfo不存在的情况下, 清除 geohash里面对应的数据
	 * @param playerInfoKeySet
	 */
	private static List<PlayerInfo> includeNeighbourPlayer(int serverId, int playerId, final double lat, final double lng,
			boolean isOnline, boolean isLocal, boolean isAvatar, boolean isVoice, final long now, String geoHash,
			Set<String> playerInfoKeySet) {
		List<PlayerInfo> list = new ArrayList<PlayerInfo>();
		
		long timeMilli = isOnline ? Const.ONLINE_TIME_FLAG : -1;
				
		PlayerInfo playerInfo = null;
		for (String playerInfoKey : playerInfoKeySet) {
			
			playerInfo = RedisToolkit.getPlayerInfo(playerInfoKey);
			if (null == playerInfo) {
				// 如果缓存中没有数据，则删除对应的GeoHash集合，跳过
				RedisToolkit.removeGeoHash(geoHash, playerInfoKey);
				continue;
			}
			
			// 搜到的玩家是自己，跳过
			if (playerInfo.getPlayerId() == playerId && playerInfo.getServerId() == serverId) {
				continue;
			}
			
			// 如果玩家搜本服的人且双方服务器id不一致，跳过
			if (isLocal && serverId != playerInfo.getServerId()) {
				continue;
			}
			
			// 如果搜索有自定义头像的玩家且玩家没有自定义头像，跳过
			if (isAvatar && (playerInfo.getImageId() == null || !playerInfo.getImageId().startsWith("diy"))) {
				continue;
			}
			
			// 如果搜索有语音的玩家且该玩家没有语音，跳过
			if (isVoice && 
					(playerInfo.getVoiceId() == null || playerInfo.getVoiceId().isEmpty())) {
				continue;
			}
			
			// 对方的登录时间超过限制，跳过
			if (timeMilli != -1 && now - playerInfo.getUpdateTS() > timeMilli) {
				continue;
			}
			
			//如果对方经纬度为0，则去除玩家列表
			if(playerInfo.getLat() == 0 && playerInfo.getLng() == 0){
				continue;
			}

			// 计算与自己的距离
			double distance = distanceInMeters(lat, lng, playerInfo.getLat(), playerInfo.getLng());
			playerInfo.setDistance(distance);

			// 是否已关注
			playerInfo.setFollow(StorageService.isInFollowList(serverId, playerId, playerInfo.getServerId(), playerInfo.getPlayerId()));
			playerInfo.calDiffTS();

			list.add(playerInfo);
		}

		return list;
	}

	/**
	 * 计算两个坐标点的距离，返回的结果单位为米
	 * 
	 * @param fooLat
	 * @param fooLng
	 * @param barLat
	 * @param barLng
	 * @return
	 */
	public static double distanceInMeters(double fooLat, double fooLng, double barLat, double barLng) {
		double a = 6378137, b = 6356752.3142, f = 1 / 298.257223563; // WGS-84
		// ellipsiod
		double L = (barLng - fooLng) * VincentyGeodesy.degToRad;
		double U1 = Math.atan((1 - f) * Math.tan(fooLat * VincentyGeodesy.degToRad));
		double U2 = Math.atan((1 - f) * Math.tan(barLat * VincentyGeodesy.degToRad));
		double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
		double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

		double cosSqAlpha, sinSigma, cos2SigmaM, cosSigma, sigma;

		double lambda = L, lambdaP, iterLimit = 20;
		do {
			double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
			sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
					* (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
			if (sinSigma == 0) {
				return 0; // co-incident points
			}
			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
			sigma = Math.atan2(sinSigma, cosSigma);
			double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
			cosSqAlpha = 1 - sinAlpha * sinAlpha;
			cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
			if (cos2SigmaM == Double.NaN) {
				cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (�6)
			}
			double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
			lambdaP = lambda;
			lambda = L + (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
		} while (Math.abs(lambda - lambdaP) > VincentyGeodesy.EPSILON && --iterLimit > 0);

		if (iterLimit == 0) {
			return Double.NaN;
		}
		double uSquared = cosSqAlpha * (a * a - b * b) / (b * b);
		double A = 1 + uSquared / 16384 * (4096 + uSquared * (-768 + uSquared * (320 - 175 * uSquared)));
		double B = uSquared / 1024 * (256 + uSquared * (-128 + uSquared * (74 - 47 * uSquared)));
		double deltaSigma = B
				* sinSigma
				* (cos2SigmaM + B
						/ 4
						* (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma)
								* (-3 + 4 * cos2SigmaM * cos2SigmaM)));
		double s = b * A * (sigma - deltaSigma);

		return s;
	}

	/**
	 * 地图坐标点 Copyright (c) 2011-2012 by 广州游爱 Inc.
	 * 
	 * @Author Create by 李兴
	 * @Date 2013年12月4日 上午11:39:03
	 * @Description
	 */
	private static class Point {
		double lat;
		double lng;
		String geohash;

		String getMinPointGeohash() {
			double lat = BMapToolkit.scaleLatOrLngForGeohashMinPoint(this.lat);
			double lng = BMapToolkit.scaleLatOrLngForGeohashMinPoint(this.lng);
			return getGeoHashBase32(lat, lng);
		}
	}

	/**
	 * 地图坐标点 管理器 Copyright (c) 2011-2012 by 广州游爱 Inc.
	 * 
	 * @Author Create by 李兴
	 * @Date 2013年12月4日 上午11:37:11
	 * @Description
	 */
	private static class PointManger {
		private Set<String> deadScaledSet = new HashSet<String>(); // 搜索过的 [缩放过的点]
		private Queue<String> lastDeadScaledQueue = new LinkedList<String>(); // 刚刚搜索过的 [缩放过的点]
		private Queue<String> readyScaledQueue = new LinkedList<String>(); // 可用的 [缩放过的点]

		PointManger(String scaledGeohash) {
			deadScaledSet.add(scaledGeohash);
			readyScaledQueue.offer(scaledGeohash);
		}

		/**
		 * 搜索附近的[原始坐标点]
		 * 
		 * @param geohash
		 * @return
		 */
		private Queue<Point> searchNearbyPoint(String geohash) {
			Queue<Point> queue = new LinkedList<Point>();

			GeoHash tmpGh = GeoHash.fromGeohashString(geohash);
			for (GeoHash nextGeoHash : tmpGh.getAdjacent()) {
				WGS84Point p = nextGeoHash.getPoint();

				Point point = new Point();
				point.lat = p.getLatitude();
				point.lng = p.getLongitude();
				point.geohash = nextGeoHash.toBase32();
				queue.add(point);
			}

			return queue;
		}

		/**
		 * 搜索新的可用[缩放坐标点]
		 * 
		 * @param rootGeohash
		 * @return
		 */
		private Queue<String> searchFreshAvailablePoint(String rootGeohash) {
			Queue<String> queue = new LinkedList<String>();

			Set<String> deadSet = new HashSet<String>(); // 使用过的点
			Queue<String> lastDeadQueue = new LinkedList<String>(); // 刚刚使用过的点，用于搜索下一层的点
			Queue<Point> readyQueue = searchNearbyPoint(rootGeohash); // 准备好的点，可以使用了

			// 基于某个点向外搜索，最多10层
			for (int i = 0; i < 10 && queue.isEmpty(); i++) {
				while (!readyQueue.isEmpty()) {
					Point p = readyQueue.poll();

					lastDeadQueue.offer(p.geohash);

					// 查看是否可用
					String geohash = p.getMinPointGeohash();
					if (!deadScaledSet.contains(geohash)) {
						deadScaledSet.add(geohash);
						queue.offer(geohash);
					}
				}

				// 搜索下一层
				while (!lastDeadQueue.isEmpty()) {
					String g = lastDeadQueue.poll();
					Queue<Point> nearbyQueue = searchNearbyPoint(g);

					Point p = null;
					while ((p = nearbyQueue.poll()) != null) {
						// 排重，其实是排除向内搜索的点
						if (!deadSet.contains(p.geohash)) {
							deadSet.add(p.geohash);
							readyQueue.add(p);
						}
					}
				}
			}

			deadSet.clear();
			lastDeadQueue.clear();
			readyQueue.clear();

			return queue;
		}

		String nextPointGeoHash() {
			if (readyScaledQueue.isEmpty()) {
				String geohash = lastDeadScaledQueue.poll();
				if (geohash == null) {
					return null;
				}

				Queue<String> nearby = searchFreshAvailablePoint(geohash);
				readyScaledQueue.addAll(nearby);
			}

			String geohash = readyScaledQueue.poll();
			if (geohash != null) {
				lastDeadScaledQueue.offer(geohash);
			}

			return geohash;
		}

		void clear() {
			deadScaledSet.clear();
			lastDeadScaledQueue.clear();
			readyScaledQueue.clear();
		}
	}
}
