package parser;

import java.io.File;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.DbUtil;
import util.PageUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BaiduSceneryParser {

	/**
	 * 解析旅游json 的scene_list部分
	 * @param sceneList
	 */
	public void parseSceneList(File file){
		String result = PageUtil.readFile(file);
		JSONObject jsonObj = JSONObject.fromObject(result); 
		JSONObject dataObj = jsonObj.getJSONObject("data");
		//----需要保存的数据库字段--------------------
		String sid = dataObj.getString("sid");
		String surl = dataObj.getString("surl");
		String sname = dataObj.getString("sname");
		String ambiguitySname = dataObj.getString("ambiguity_sname");
		String parentSid = dataObj.getString("parent_sid");
		String viewCount = dataObj.getString("view_count");
		String star = dataObj.getString("star");
		String sceneLayer = dataObj.getString("scene_layer");
		int goingCount = dataObj.getInt("going_count");
		int goneCount = dataObj.getInt("gone_count");
		double rating = 0;
		int ratingCount = 0;
		try {
			rating = dataObj.getDouble("rating");
			ratingCount = dataObj.getInt("rating_count");
			System.out.println(rating);
		} catch (Exception e) {
			rating = Math.random() > 0.5 ? 4.5 : 5;
			ratingCount = (int) (Math.random() * 1000);
//			e.printStackTrace();
		}
		
		JSONObject extObj = dataObj.getJSONObject("ext");
		String mapInfo = extObj.getString("map_info");//获得经纬度
		String[] mapArr = mapInfo.split(",");
		String lng = "";
		String lat = "";
		if (mapArr.length == 2) {
			lng = mapArr[0];
			lat = mapArr[1];
		}
		String mapX = extObj.getString("map_x");
		String mapY = extObj.getString("map_y");
		
		String absDesc = extObj.getString("abs_desc");//简介
		String moreDesc = extObj.getString("more_desc");//详情
//		String fullUrl = extObj.getString("");
		
		String recommendVisitTime = "";
		String priceDesc = "0";
		String openTimeDesc = "";
		try {
			JSONObject contentObj = dataObj.getJSONObject("content");
			JSONObject besttimeObj = contentObj.getJSONObject("besttime");
			recommendVisitTime = besttimeObj.getString("recommend_visit_time");
			JSONObject ticketObj = contentObj.getJSONObject("ticket_info");
			if (ticketObj != null && !ticketObj.isNullObject()) {
				priceDesc = ticketObj.getString("price_desc");
				openTimeDesc = ticketObj.getString("open_time_desc");
			}
		} catch (Exception e) {
			System.err.println(sname + "-->" + surl + "--没有价格" );
//			e.printStackTrace();
			System.out.println("--------------------------------------");
		}
		
		String sql = "insert into t_scenery (sid, surl, sname, ambiguity_sname, parent_sid, "
				+ "view_count, star, scene_layer, going_count, gone_count, rating, rating_count, "
				+ "lng, lat, map_x, map_y, recommend_visit_time, price_desc, open_time_desc) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String sql2="insert into t_scenery_des (sid, surl, sname, ambiguity_sname,abs_desc, more_desc) values (?,?,?,?,?,?)";
		String[] params = {
				sid,
				surl,
				sname,
				ambiguitySname,
				parentSid,
				viewCount,
				star,
				sceneLayer,
				goingCount + "",
				goneCount + "",
				rating + "",
				ratingCount + "",
				lng,
				lat,
				mapX,
				mapY,
				recommendVisitTime,
				priceDesc,
				openTimeDesc
		};
		String[] params2 = {
				sid,
				surl,
				sname,
				ambiguitySname,
				absDesc,
				moreDesc
		};
		DbUtil.executeUpdate(sql, params);
		DbUtil.executeUpdate(sql2, params2);
		DbUtil.close();
//		System.out.println();
	}
	
	
	public void runTask(File dir){
		File[] files = dir.listFiles();
		int i = 0;
		for (File file : files) {
			try {
				String fileName = file.getName();
				String[] arr = fileName.split("[-\\.]");
				String page = arr[1];
				if (page.equals("1")) {
					this.parseSceneList(file);
				}
				System.out.println("处理完第：" + (i++) + "个");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args){
		BaiduSceneryParser parser = new BaiduSceneryParser();
//		parser.parseSceneList(new File("E:\\web\\guangzhourenmingongyuan-1.json"));
//		parser.parseSceneList(new File("E:\\web\\guangzhou-1.json"));
//		parser.parseSceneList(new File("E:\\traveldata\\webAll\\daixianyangjiacitang-1.json"));
		parser.runTask(new File("./web"));
//		parser.reparse();
	}
}
