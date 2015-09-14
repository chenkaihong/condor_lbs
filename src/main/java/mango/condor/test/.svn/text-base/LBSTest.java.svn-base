package mango.condor.test;


/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-1 下午4:43:10
 * @Description 
 */
public class LBSTest {
///*	天朝
// *  98.576713,41.184513
//	116.97401,23.712291*/
///**
// *  北京五环
// * 	116.221733,40.024422
//	116.495968,39.841624
//	
// */
//	
///*	乌兰察布-天津
//	113.124088,41.018598
//	117.171494,39.131947*/
//	
///*	南中国
//	104.031512,32.699481
//	120.809846,22.081302*/
//
///*	static double MIN_LAT = 23.712291;
//	static double MAX_LAT = 41.184513;
//	static double MIN_LNG = 98.576713;
//	static double MAX_LNG = 116.97401;*/
//	
///*	static double MIN_LAT = 39.841624;
//	static double MAX_LAT = 40.024422;
//	static double MIN_LNG = 116.221733;
//	static double MAX_LNG = 116.495968;*/
//	
///*	static double MIN_LAT = 39.131947;
//	static double MAX_LAT = 41.018598;
//	static double MIN_LNG = 113.124088;
//	static double MAX_LNG = 117.171494;*/
//	
//	static double MIN_LAT = 22.081302;
//	static double MAX_LAT = 32.699481;
//	static double MIN_LNG = 104.031512;
//	static double MAX_LNG = 120.809846;
//	
//	public static void main(String[] args) throws IOException {
///*		WGS84Point point = new WGS84Point(39.927773, 116.387021);
//		GeoHash gh = GeoHash.withCharacterPrecision(39.927773, 116.387021, 6);
//		System.out.println(gh.getBoundingBox().getUpperLeft());
//		System.out.println(gh.getBoundingBox().getLowerRight());*/
//		genTestFile();
//	}
//	
//	public static void genTestFile() throws IOException {
//		String url = "http://10.1.1.66:18091/lbs/get_neighbour?player_id=%d&server_id=8&lat=%f&lng=%f&is_more=false&gender=A";
//		String url2 = "http://10.1.1.66:18091/lbs/get_neighbour?player_id=%d&server_id=8&lat=%f&lng=%f&is_more=true&gender=A";
//		Random rand = new Random();
//		final int RANDOM_CNT = 100;
//		
//		double diffLat = MAX_LAT - MIN_LAT;
//		double diffLng = MAX_LNG - MIN_LNG;
//		
//		String firstFilePath = "f:\\first.txt";
//		String secFilePath = "f:\\sec.txt";
//		StringBuilder sb1 = new StringBuilder();
//		StringBuilder sb2 = new StringBuilder();
//		
//		for (int i = 0; i < RANDOM_CNT; ++i) {
//			double randomLat = MIN_LAT + diffLat * rand.nextDouble();
//			double randomLng = MIN_LNG + diffLng * rand.nextDouble();
//			int playerId = rand.nextInt();
//			playerId = Math.abs(playerId);
//			sb1.append(String.format(url, playerId, randomLat, randomLng) + "\n");
//			sb2.append(String.format(url2, playerId, randomLat, randomLng) + "\n");
//		}
//		
//		Files.write(sb1.toString(), new File(firstFilePath), Charset.forName("utf8"));
//		Files.write(sb2.toString(), new File(secFilePath), Charset.forName("utf8"));
//	}
//	
///*	public static double[] genRandomCoor() {
//		Random rand = new Random();
//		
//		double diffLat = MAX_LAT - MIN_LAT;
//		double diffLng = MAX_LNG - MIN_LNG;
//		
//		double randomLat = MIN_LAT + diffLat * rand.nextDouble();
//		double randomLng = MIN_LNG + diffLng * rand.nextDouble();
//		
//		return new double[]{randomLat, randomLng};
//	}*/
//	
//	public static void genRandomPlayerInfo() {
//		final int RANDOM_CNT = 100000;
//		Random rand = new Random();
//		
//		double diffLat = MAX_LAT - MIN_LAT;
//		double diffLng = MAX_LNG - MIN_LNG;
//		
//		for (int i = 0; i < RANDOM_CNT; ++i) {
//			double randomLat = MIN_LAT + diffLat * rand.nextDouble();
//			double randomLng = MIN_LNG + diffLng * rand.nextDouble();
//			
//			// String addr = BMapToolkit.getReverseGeo(randomLat, randomLng);
//			PlayerInfo pi = new PlayerInfo();
//			pi.setServerId(8);
//			pi.setPlayerId(i);
//			pi.setName("xcc");
//			pi.setLevel(1);
//			pi.setImageId("imgid");
//			pi.setGender(rand.nextBoolean());
//			pi.setLat(randomLat);
//			pi.setLng(randomLng);
//			
//			// 设置缓存插入的时间，用来计算该用户是什么时间前登录的
//			pi.setUpdateTS(System.currentTimeMillis());
//			
//			// 不管是否过期，都插入缓存
//			RedisToolkit.putPlayerInfoAndGeoHash(pi, null, true);
//		}
//
//	}
//	
//	
//	public static void testLinJu() {
//		GeoHash geoHash1 = GeoHash.withCharacterPrecision(39.848545, 116.418323, 6);
//		System.out.println("lin ju:");
//		for (GeoHash item : geoHash1.getAdjacent()) {
//			System.out.println(item.toBase32());
//		}
//		
//		System.out.println("next");
//		for (int i = 0; i < 20; ++i) {
//			System.out.println(geoHash1.next().toBase32());
//			geoHash1 = geoHash1.next();
//		}
//		
//	}
//	
//	public static void testGeoHash() {
//		GeoHash geoHash1 = GeoHash.withCharacterPrecision(39.848545, 116.418323, 6);
//		System.out.println(geoHash1.toBase32());
//		GeoHash geoHash2 = GeoHash.withCharacterPrecision(39.848532, 116.419356, 6);
//		System.out.println(geoHash2.toBase32());
//	}
//	
//	public static void testDistance() {
//		// 116.418323,39.848545
//		WGS84Point point1 = new WGS84Point(39.848545, 116.418323);
//		// 116.419356,39.848532
//		WGS84Point point2 = new WGS84Point(39.848532, 116.419356);
//		
//		double dis = VincentyGeodesy.distanceInMeters(point1, point2);
//		System.out.println(dis);
//	}
//	
//	public static void testLoadAllPlayer(){
//		for(PlayerInfo info:DBToolkit.loadPlayerInfo()){
//			RedisToolkit.putPlayerInfoAndGeoHash(info, null, true);
//		}
//	}
}
