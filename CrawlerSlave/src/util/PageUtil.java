package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageUtil {
	public static String inputStreamToString(InputStream inputStream) throws IOException{
		return inputStreamToString(inputStream,null);
	}
	
	public static String inputStreamToString(InputStream inputStream, String charset) throws IOException{
		StringBuffer buffer = new StringBuffer();
		String result = "";
		String msg = null;
		byte[] bytes = new byte[4096];
		int len = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,charset));
		while((msg = reader.readLine()) != null ){
			buffer.append(msg + "\r\n");
		}
//		while((len = inputStream.read(bytes)) > 0 ){
//			buffer.append(new String(bytes, 0, len, charset)+"\r\n");
//		}
		result = buffer.toString();
		return result;
	}
	
	public static String getCharset(InputStream inputStream) throws IOException{
		InputStream stream = inputStream;
		StringBuffer buffer = new StringBuffer();
		String result = "";
		String msg = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		while((msg = reader.readLine()) != null ){
			buffer.append(msg + "\r\n");
		}
		result = buffer.toString();
		Pattern pattern = Pattern.compile("\\<meta(.*)charset=(.*?)/>", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(result);
		if(matcher.find()){
			String metaCharset = matcher.group(2);
			metaCharset = metaCharset.replaceAll("[\"| ]", "");
			return metaCharset;
		}else{
			return null;
		}
	}
	
	public static String getCharsetByHeader(URLConnection urlConnection){
		Map<String, List<String>> map = urlConnection.getHeaderFields();
		Set<String> keys = map.keySet();
		Iterator<String> iterator = keys.iterator();

		String key = null;
		String tmp = null;
		while (iterator.hasNext()) {
		    key = iterator.next();
		    tmp = map.get(key).toString().toLowerCase();
		    if (key != null && key.equals("Content-Type")) {
		        int m = tmp.indexOf("charset=");
		        if (m != -1) {
		            String strencoding = tmp.substring(m + 8).replace("]", "");
		            return strencoding;
		        }
		    }
		}
		return null;
	}
	
	public static String parseDomain(String url){
		String domain = null;
		Pattern pattern = Pattern.compile("((https?|ftp|news):\\/\\/)?([\\w]+\\.)?([\\w-]+\\.)(com|net|org|gov|cc|biz|info|cn|edu)(\\.(cn|hk))*");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			domain = matcher.group();
		}
		return domain; 
	}
	public static String parseHost(String url){
		String domain = null;
		Pattern pattern = Pattern.compile("([\\w]+\\.)?([\\w-]+\\.)(com|net|org|gov|cc|biz|info|cn|edu)(\\.(cn|hk))*");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			domain = matcher.group();
		}
		return domain; 
	}
	
	public static void exportFile(String dirPath, String pageContent){
		try {
			FileOutputStream outputStream = new FileOutputStream(new File(dirPath));
			PrintWriter writer = new PrintWriter(outputStream);
			writer.write(pageContent);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	public static String getFileNameByUrl(String urlstr){
		String filename = urlstr.replaceAll("http\\://", "");
		filename = filename.replaceAll("/", "-").substring(0, filename.length()-1);
		return filename;
	}
	
	public static String readFile(File file){
		String result = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tmp = "";
			while((tmp = reader.readLine()) != null){
				result += tmp;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
}
