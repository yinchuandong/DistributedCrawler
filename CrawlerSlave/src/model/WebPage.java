package model;

import java.net.URL;


public class WebPage {
	private String pageContent = null;
	private URL url = null;
	private int layer = 0;
	
	public WebPage(){
		
	}
	
	public WebPage(String pageContent, URL url){
		setPageContent(pageContent);
		setUrl(url);
	}
	
	public WebPage(String pageContent, URL url, int layer){
		setPageContent(pageContent);
		setUrl(url);
		setLayer(layer);
	}
	
	public void setPageContent(String pageContent){
		this.pageContent = pageContent;
	}
	
	public void setUrl(URL url){
		this.url = url;
	}
	
	public void setLayer(int layer){
		this.layer = layer;
	}
	
	public String getPageContent(){
		return this.pageContent;
	}
	
	public URL getUrl(){
		return this.url;
	}
	
	public int getLayer(){
		return this.layer;
	}
}
