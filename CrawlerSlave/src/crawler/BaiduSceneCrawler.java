package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import model.WebPage;
import util.AppUtil;
import util.DbUtil;
import util.PageUtil;
import base.BaseCrawler;

public class BaiduSceneCrawler extends BaseCrawler{

	/**
	 * 每一页景点的数量
	 */
	private int listRows = 16;
	/**
	 * 当前的时间戳
	 */
	private long timestamp ;
	//-------------------------------------------
	public BaiduSceneCrawler(){
		super();
		this.timestamp = System.currentTimeMillis();
		this.setDomain("http://lvyou.baidu.com");
	}
	
	/**
	 * 生成爬取的Url
	 * @param surl city的surl;如番禺 panyu
	 * @param cid city的Id
	 * @param page
	 * @return
	 */
	public String generateUrl(String surl, int cid, int page){
		String url = "http://lvyou.baidu.com/destination/ajax/jingdian?format=ajax&";
		url  += "surl=" + surl+ "&cid=" + cid + "&pn=" + page + "&t=" + this.timestamp;
		return url;
	}
	
	/**
	 * 生成爬取的Url
	 * @param surl
	 * @param page
	 * @return
	 */
	public String generateUrl(String surl, int page){
		return this.generateUrl(surl, 0, page);
	}
	
	@Override
	public void loadWaitList() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./data/baidu-scene.txt"));
			String tmpStr = null;
			while((tmpStr = reader.readLine()) != null){
				if (!tmpStr.startsWith("#")) {
					String url = this.generateUrl(tmpStr, 1);
					String uniqueKey = tmpStr + "-1";
					super.addWaitList(url, uniqueKey);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exactor(WebPage page) {
		if (page == null) {
			System.err.println("exactor爬取信息为空");
			return;
		}
		String result = page.getPageContent();
		try {
			result = AppUtil.jsonFormatter(result);
			JSONObject jsonObj = JSONObject.fromObject(result); 
			JSONObject dataObj = jsonObj.getJSONObject("data");
			
			//需要保存的数据库字段
			String sid = dataObj.getString("sid");
			String surl = dataObj.getString("surl");
			String sname = dataObj.getString("sname");

//			String ambiguitySname = dataObj.getString("ambiguity_sname");
//			String parentSid = dataObj.getString("parent_sid");
//			String viewCount = dataObj.getString("view_count");
//			String star = dataObj.getString("star");
//			String sceneLayer = dataObj.getString("scene_layer");
//			int goingCount = dataObj.getInt("going_count");
//			int goneCount = dataObj.getInt("gone_count");
//			double rating = dataObj.getDouble("rating");
//			int ratingCount = dataObj.getInt("rating_count");
			JSONObject extObj = dataObj.getJSONObject("ext");
			String mapInfo = extObj.getString("map_info");//获得经纬度
			
			//用于判断分页，构造url
			int sceneTotal = dataObj.getInt("scene_total");
			int currentPage = dataObj.getInt("current_page");
			
			//将url标记为已经访问
			super.visitUrl(surl + "-" + currentPage);
			
			//取得页数, 将该城市的所有景点页面保存，eg:guangzhou-1.....n添加到等待队列中
			int pageNums = (int) Math.ceil((double)sceneTotal / listRows);
			for(int i=currentPage+1; i<=pageNums; i++){
				//如果该url没有被访问过，则添加到未访问列表中
				String uniqueKey = surl + "-" + i;
				String tmpUrl = this.generateUrl(surl, i);
				addWaitList(tmpUrl, uniqueKey);
			}
			
			//解析景点列表
			JSONArray sceneList = dataObj.getJSONArray("scene_list");
			this.parseSceneList(sceneList);
			
			//将json文件保存下来
			String filename = surl + "-" + currentPage + ".json";
			filename = "./web/" + filename;
			File targetFile = new File(filename);
			if (!targetFile.getParentFile().exists()) {
				targetFile.getParentFile().mkdirs();
			}
			PageUtil.exportFile(targetFile, AppUtil.jsonFormatter(result));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析景点列表，及json的scene_list部分
	 * @param sceneList
	 */
	private void parseSceneList(JSONArray sceneList){
		for(int i=0; i<sceneList.size(); i++){
			JSONObject sceneObj = sceneList.getJSONObject(i);
			String sid = sceneObj.getString("sid");
			String surl = sceneObj.getString("surl");
			String sname = sceneObj.getString("sname");
			String sceneLayer = sceneObj.getString("scene_layer");
			
			//如果该url没有被访问过，则添加到未访问列表中
			String tmpUrl = this.generateUrl(surl, 1);
			String uniqueKey = surl + "-" + 1;
			//添加到等待队列
			addWaitList(tmpUrl, uniqueKey);
			
			System.out.println(sid);
			System.out.println(surl);
			System.out.println(sname);
			System.out.println("------------------------------");
		}
		
	}
	
	public static void main(String[] args){
		final BaiduSceneCrawler crawler = new BaiduSceneCrawler();
		crawler.initSeeds();
		crawler.start();
		
//		new Thread(){
//			public void run(){
//				try {
//					Thread.sleep(5000);
//					System.out.println("stop----------");
//					crawler.stop();
//					Thread.sleep(5000);
//					System.out.println("restart----------");
////					crawler.startWithoutLoading();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}.start();
	}

	
	
}
