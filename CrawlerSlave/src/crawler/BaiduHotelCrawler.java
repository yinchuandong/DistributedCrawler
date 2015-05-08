package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import model.WebPage;
import util.AppUtil;
import util.PageUtil;
import util.UrlParmUtil;
import base.BaseCrawler;

public class BaiduHotelCrawler extends BaseCrawler{

	public BaiduHotelCrawler(){
		
	}
	

	/**
	 * 生成爬取的Url
	 * @param sid
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public String generateUrl(String sid, double mapX, double mapY){
		double r = 2000;
		double left = mapX - r;
		double right = mapX + r;
		double top = mapY - r;
		double bottom = mapY + r;
		String url = "http://lvyou.baidu.com/business/ajax/hotel/searcharound?sid=" +
				sid + "&wd=%E9%85%92%E5%BA%97&is_detail=0&nb_x=" +
				mapX + "&nb_y=" + mapY + "&r=" + r + "&b=(" + left + "," + top + ";" + right + "," + bottom + ")";
		return url;
	}

	@Override
	public void loadWaitList() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("./data/baidu-hotel.txt")));
			String buff = null;
			while((buff = reader.readLine()) != null){
				String[] arr = buff.split("\\s");
				String sid = arr[0];
				double mapX = Double.parseDouble(arr[1]);
				double mapY = Double.parseDouble(arr[2]);
				String url = this.generateUrl(sid, mapX, mapY);
				String uniqueKey = sid + "-" + mapX + "-" + mapY;
				this.addUnVisitPath(uniqueKey);
				this.addWaitList(url, uniqueKey);
				System.out.println(url);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void exactor(WebPage webPage) {
		HashMap<String, String> params = UrlParmUtil.parseUrl(webPage.getUrl().toString());
		String sid = params.get("sid");
		double mapX = Double.parseDouble(params.get("nb_x"));
		double mapY = Double.parseDouble(params.get("nb_y"));
		String uniqueKey = sid + "-" + mapX + "-" + mapY;
		this.visitUrl(uniqueKey);
		
		//将json文件保存下来
		String filename = "./web-hotel/" + sid + ".txt";
		File targetFile = new File(filename);
		if (!targetFile.getParentFile().exists()) {
			targetFile.getParentFile().mkdirs();
		}
		PageUtil.exportFile(targetFile, webPage.getPageContent());
	}
	
	
	public static void main(String[] args){
		BaiduHotelCrawler crawler = new BaiduHotelCrawler();
		crawler.initSeeds();
		crawler.start();
	}
	
}
