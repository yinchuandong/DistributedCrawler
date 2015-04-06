package util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UrlParmUtil {

	/**
	 * @param url http://www.mafengwo.cn/hotel/ajax.php?sAction=getPoiList2&iMddId=34888&sKeyWord=&sCheckIn=2014-07-29&sCheckOut=2014-07-30&iPage=1&sTags=&iPriceMin=0&iPriceMax=&sSortType=comment&sSortFlag=DESC
	 * @return {"action":"getPoiList2",....}
	 */
	public static HashMap<String, String> parseUrl(String url){
		HashMap<String, String> result = new HashMap<String, String>();
		try {
			URL uri = new URL(url);
			String query = uri.getQuery();
			String[] lineArr = query.split("&");
			for (String line : lineArr) {
				String[] arr = line.split("=");
				switch (arr.length) {
				case 2:
					result.put(arr[0], arr[1]);
					break;
				case 1:
					result.put(arr[0], "");
				default:
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args){
		HashMap<String, String> map = parseUrl("http://www.mafengwo.cn/hotel/ajax.php?sAction=getPoiList2&iMddId=34888&sKeyWord=&sCheckIn=2014-07-29&sCheckOut=2014-07-30&iPage=1&sTags=&iPriceMin=0&iPriceMax=&sSortType=comment&sSortFlag=DESC");
		System.out.println(map.get("iMddId"));
		System.out.println();
	}
}
