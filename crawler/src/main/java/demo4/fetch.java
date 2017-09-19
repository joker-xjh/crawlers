package demo4;

import java.io.IOException;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class fetch {
	
	private static CloseableHttpClient client = HttpClients.createDefault();
		

	private static String cookie = "";
	
	private static String userDetailUrl = "https://www.zhihu.com/api/v4/members/";
	private static String userDetailQueryString = "?include=locations,employments,gender,educations,business,voteup_count,thanked_Count,follower_count,following_count,cover_url,following_topic_count,following_question_count,following_favlists_count,following_columns_count,avatar_hue,answer_count,articles_count,pins_count,question_count,columns_count,commercial_question_count,favorite_count,favorited_count,logs_count,marked_answers_count,marked_answers_text,message_thread_token,account_status,is_active,is_bind_phone,is_force_renamed,is_bind_sina,is_privacy_protected,sina_weibo_url,sina_weibo_name,show_sina_weibo,is_blocking,is_blocked,is_following,is_followed,mutual_followees_count,vote_to_count,vote_from_count,thank_to_count,thank_from_count,thanked_count,description,hosted_live_count,participated_live_count,allow_message,industry_category,org_name,org_homepage,badge[?(type=best_answerer)].topics";
	
	public final static String[] userAgentArray = new String[]{
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2623.110 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2623.110 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2623.110 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2623.110 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2623.110 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2623.110 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2623.110 Safari/537.36",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36"
    };
	
	private static Random random = new Random();
	
	private static String followees = "https://www.zhihu.com/api/v4/members/";
	private static String followeesQueryString = "?include=data%5B*%5D.answer_count%2Carticles_count%2Cgender%2Cfollower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics&offset=0&limit=20";
	
	
	private static String userToken = "su-xin-70-51";

	public static void main(String[] args) {
		
		getUserFollowees(userToken);
		
	}
	
	//请求用户关注人页面,并解析
	public static void getUserFollowees(String userToken) {
		boolean running = true;
		String url = followees +userToken+"/followees"+followeesQueryString;
		while(running) {
			HttpGet request = new HttpGet(url);
			request.setHeader("Cookie", cookie);
			request.setHeader("User-Agent", userAgentArray[random.nextInt(userAgentArray.length)]);
			String content="";
			try {
				CloseableHttpResponse response = client.execute(request);
				 content= EntityUtils.toString(response.getEntity(), "UTF-8");
				response.close();
				request.releaseConnection();
				
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(content.length() == 0)
				break;
			JSONObject jsonObject = JSONObject.fromObject(content);
			JSONObject next = jsonObject.getJSONObject("paging");
			running = !next.getBoolean("is_end");
			if(running)
				url = next.getString("next");
//			System.out.println("111111111111111111111111"+"     running:"+running);
//			System.out.println(url);
			JSONArray list = jsonObject.getJSONArray("data");
			for(int i=0; i<list.size(); i++) {
				JSONObject user = list.getJSONObject(i);
				System.out.println(user.getString("url_token"));
			}
			
		}
		
	}
	
	
	
	
	//请求用户信息页面
	public static String getUserDetail(String userToken) {
		String url = userDetailUrl+userToken+userDetailQueryString;
		HttpGet request = new HttpGet(url);
		request.setHeader("Cookie", cookie);
		request.setHeader("User-Agent", userAgentArray[random.nextInt(userAgentArray.length)]);
		try {
			CloseableHttpResponse response = client.execute(request);
			String content = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println(content);
			response.close();
			return content;
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.releaseConnection();
		return "";
	}
	
	//解析用户信息
	public static void parseJsonUserDetail(String content, User user) {
		JSONObject jsonObject = JSONObject.fromObject(content);
		user.setAnswers(jsonObject.getInt("answer_count"));
		user.setUsername(jsonObject.getString("name"));
		user.setAgrees(jsonObject.getInt("voteup_count"));
		user.setFollowees(jsonObject.getInt("following_count"));
		user.setFollowers(jsonObject.getInt("follower_count"));
		user.setHashId(jsonObject.getString("id"));
		user.setAsks(jsonObject.getInt("question_count"));
		user.setPosts(jsonObject.getInt("articles_count"));
		JSONObject buisness = jsonObject.getJSONObject("business");
		if(buisness != null) {
			user.setBusiness(buisness.getString("name"));
		}
		JSONArray array = jsonObject.getJSONArray("educations");
		if(array != null) {
			JSONObject education = array.getJSONObject(0);
			if(education != null) {
				education = education.getJSONObject("school");
				if(education != null) {
					user.setEducation(education.getString("name"));
				}
			}
		}
		
		int gender = jsonObject.getInt("gender");
		if(gender == 1)
			user.setSex("男");
		else
			user.setSex("女");
		array = jsonObject.getJSONArray("locations");
		if(array != null) {
			JSONObject location = array.getJSONObject(0);
			if(location != null) {
				user.setLocation(location.getString("name"));
			}
		}
		array = jsonObject.getJSONArray("employments");
		if(array != null) {
			JSONObject employments = array.getJSONObject(0); 
			user.setEmployment(employments.getJSONObject("company").getString("name"));
			user.setPosition(employments.getJSONObject("job").getString("name"));
		}
		
		System.out.println(user);
		
		
	}
	

}
