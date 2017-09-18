package demo1;

import java.io.IOException;
import java.util.Random;

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

public class douBan {
	
	private static String book_url = "http://www.douban.com/tag/";
	private static String[] book_list = {"心理学", "人物传记", "中国历史", "旅行", "生活", "科普"};
	private static String[] user_agents = {"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:34.0) Gecko/20100101 Firefox/34.0", 
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6", 
			"Mozilla/5.0 (Windows NT 6.2) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.12 Safari/535.11",
			"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)",
			"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:40.0) Gecko/20100101 Firefox/40.0",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/44.0.2403.89 Chrome/44.0.2403.89 Safari/537.36"};
	private static Random random = new Random();
	
	private static CloseableHttpClient client = HttpClients.createDefault();
	
	private static HttpResponse getResponse(String url) {
		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent", user_agents[random.nextInt(user_agents.length)]);
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
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
		}finally {
			
		}
		
		
		return "";
	}
	
	private static void parseContent(String content) {
		Document document = Jsoup.parse(content);
		//System.out.println(document);
		Elements element = document.getElementsByClass("book-list");
		Element list = element.get(0);
		Elements books = list.select("dl");
		for(int i=0; i<books.size(); i++) {
			Element dl= books.get(i);
			Element dd = dl.select("dd").get(0);
			String name = dd.select("a").get(0).text();
			String descript = dd.select("div.desc").get(0).text();
			String rating = dd.select("span.rating_nums").get(0).text();
			System.out.println(name +"  "+rating+"  "+descript);
		}
	}
	
	
	public static void fetch() {
		String url = book_url + book_list[random.nextInt(book_list.length)]+"/book?start=";
		
		for(int i=1;i<20;i++) {
			url = url + i;
			String content = getContent(url);
			//System.out.println(content);
			parseContent(content);
		}
		
	}
	
	
	

	public static void main(String[] args) {
		
		fetch();

	}

}
