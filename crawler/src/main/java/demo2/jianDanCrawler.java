package demo2;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class jianDanCrawler {
	
	private static CloseableHttpClient client = HttpClients.createDefault();
	
	private static ExecutorService pool = Executors.newFixedThreadPool(5);
	
	private static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	
	private static volatile boolean running = true;
	
	private static AtomicInteger counter = new AtomicInteger(0);
	
	private static String home = "http://jandan.net/ooxx/page-";
	private static String downLoadPath = "src/main/java/demo2/";
	
	static class fetcher implements Runnable{
		

		public void run() {
			while(running || queue.size() > 0) {
				try {
					String url = queue.take();
					url = "http:"+url;
					String fileName = ""+counter.incrementAndGet()+".jpg";
					URL resource = new URL(url);
					try(BufferedInputStream in = new BufferedInputStream(resource.openStream());
							BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(downLoadPath+fileName));) {
						byte[] buff = new byte[1024];
						int read = -1;
						while((read = in.read(buff)) !=-1) {
							out.write(buff, 0, read);
						}
						out.flush();
						System.out.println("下载成功:"+url);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} 
			}
		}
		
	}
	
	
	
	private static HttpResponse getResponse(String url) {
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(request);
			return response;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getContent(String url) {
		HttpResponse response = getResponse(url);
		HttpEntity entity = response.getEntity();
		try {
			String content = EntityUtils.toString(entity, "UTF-8");
			return content;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private static void parseContent(String content) {
		Document document = Jsoup.parse(content);
		Element ol = document.getElementsByClass("commentlist").get(0);
		Elements picts = ol.select("li");
		for(int i=0; i<picts.size(); i++) {
			Element picture = picts.get(i);
			Element img = picture.select("img").get(0);
			String url = img.attr("src");
			//System.out.println(url);
			try {
				queue.put(url);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void fetch() {
		for(int i=1;i<=100; i++) {
			String url = home + i;
			String content = getContent(url);
			parseContent(content);
		}
		running = false;
		System.out.println(queue.size());
	}
	
	
	
	

	public static void main(String[] args) {
		fetcher f1 = new fetcher();
		fetcher f2 = new fetcher();
		fetcher f3 = new fetcher();

		pool.submit(f1);
		pool.submit(f2);
		pool.submit(f3);
		fetch();
		
	}

}
