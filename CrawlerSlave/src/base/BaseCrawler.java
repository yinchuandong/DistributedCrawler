package base;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.Jedis;
import model.WebPage;
import util.AppUtil;
import util.DbUtil;
import util.HttpUtil;
import util.PageUtil;
import util.RedisUtil;

/**
 * 爬取通用html页面的基类
 * @author yuanyun
 *
 */
public abstract class BaseCrawler {

	private final static int TASK_NUM = 10;
	/**
	 * 保存爬过的Url和深度，key是url的md5值，value是深度值
	 */
	private ConcurrentHashMap<String, Integer> urlDeeps;
	
	/**
	 * redis对象，记录爬虫信息
	 */
	protected Jedis jedis;
	
	/**
	 * 等待的队列
	 */
	private LinkedList<String> waitList;
	/**
	 * 线程池
	 */
	private ExecutorService taskPool;
	/**
	 * 网页的字符编码
	 */
	private String charset = "utf-8";
	/**
	 * 网页的域名，如：http://lvyou.baidu.com
	 */
	private String domain = "";
	/**
	 * 爬虫最大的深度
	 */
	private int crawlerDeeps = 2;
	/**
	 * 延时时间
	 */
	private int delay = 200;
	
	/**
	 * 判断爬虫是否正在运行
	 */
	private boolean isRunning = false;
	
	private boolean isPaused = false;
	
	public BaseCrawler(){
		urlDeeps = new ConcurrentHashMap<String, Integer>();
		waitList =  new LinkedList<String>();
		taskPool = Executors.newCachedThreadPool();
		jedis = RedisUtil.getInstance();
	}
	
	/**
	 * 初始化种子
	 */
	public void initSeeds(){
		reset();
		loadWaitList();
	}
	
	/**
	 * 自动加载种子数据,开始爬取,由外部调用
	 */
	public void start(){
		this.isRunning = false;
		this.isPaused = false;
		doCrawl();
	}
	
	public void pause(){
		this.isRunning = false;
		this.isPaused = true;
	}
	
	public void stop(){
		this.isRunning = false;
		this.isPaused = true;
		this.waitList.clear();
	}
	
	public void reset(){
		this.isRunning = false;
		this.isPaused = false;
		this.waitList.clear();
	}

	
	/**
	 * 开启爬虫线程
	 */
	private void doCrawl(){
		if (isRunning) {
			return;
		}
		new Thread(){
			@Override
			public void run(){
				isRunning = true;
				System.out.println("----------启动线程----------------------");
				while(!waitList.isEmpty() && !isPaused){
					String url = popWaitList();
					taskPool.execute(new ProcessThread(url));
					try {
						Thread.sleep(delay);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				isRunning = false;
			}
		}.start();
	}
	
	/**
	 * 将waitList的头结点弹出
	 * @return
	 */
	public synchronized String popWaitList(){
		String temp = waitList.poll();
		return temp;
	}
	
	/**
	 * 将url添加到等待队列中，并且添加到redis未访问记录中
	 * @param url
	 * @param uniqueKey
	 */
	public synchronized void addWaitList(String url, String uniqueKey){
		//如果url没有被记录
		if(!jedis.exists(uniqueKey)){
			waitList.offer(url);
			jedis.set(uniqueKey, 0+"");
			return;
		}
		
		//如果url被记录了但是没有被爬取
		if(jedis.get(uniqueKey).equals("0") && !waitList.contains(url)){
			waitList.offer(url);
		}
	}
	
	/**
	 * 将url添加到未访问的列表, 不会判断redis是否存在改uniqueKey
	 * @param uniqueKey 如:guangzhou-1
	 */
	public synchronized void addUnVisitPath(String uniqueKey){
		jedis.set(uniqueKey, 0+"");
	}
	
	/**
	 * 将该url标记为已经访问过
	 * @param uniqueKey 能唯一标示Url的，
	 * 如http://lvyou.baidu.com/destination/ajax/jingdian?format=ajax&cid=1&pn=2
	 * 则uniqueKey为baiyunshan-2的md5值
	 */
	public synchronized void visitUrl(String uniqueKey){
		jedis.set(uniqueKey, 1+"");
	}
	
	
	/**
	 * 获得已经爬取过的url深度列表，key是url的md5值，value是深度值
	 * @return
	 */
	public ConcurrentHashMap<String, Integer> getUrlDeeps(){
		return this.urlDeeps;
	}
	
	/**
	 * 执行完爬虫之后的回调函数
	 * @param webPage
	 */
	public abstract void exactor(WebPage webPage);
	
	/**
	 * 加载等待队列
	 */
	public abstract void loadWaitList();
	
	/**
	 * 设置字符集
	 * @param charset
	 */
	public void setCharset(String charset){
		this.charset = charset;
	}
	
	/**
	 * 设置域名, 解决相对地址问题
	 * @param domain
	 */
	public void setDomain(String domain){
		this.domain = domain;
	}
	
	/**
	 * 设置爬虫的深度，默认为2
	 * @param deeps
	 */
	public void setCrawlerDeeps(int deeps){
		if(deeps >= 0){
			this.crawlerDeeps = deeps;
		}
	}
	
	
	
	/**
	 * 具体爬取的线程
	 * @author yinchuandong
	 *
	 */
	public class ProcessThread implements Runnable{

		private String url;
		public ProcessThread(String url){
			this.url = url;
		}
		
		@Override
		public void run() {
			System.out.println("正在爬取waitList:" + waitList.size() + "个：" + url);
			HttpUtil httpUtil = new HttpUtil();
			httpUtil.setCharset(charset);
			String pageContent = httpUtil.get(url);
			WebPage webPage;
			try {
				webPage = new WebPage(pageContent, new URL(url));
				exactor(webPage);
			} catch (MalformedURLException e) {
				exactor(null);
				e.printStackTrace();
			}
			//再次调用爬虫，避免因解析耗时过多，导致等待队列为空，爬虫停止的情况
			if(!waitList.isEmpty() && !isRunning && !isPaused){
				doCrawl();
			}
		}
	}
	
}
