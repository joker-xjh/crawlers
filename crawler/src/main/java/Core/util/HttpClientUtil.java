package Core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpClientUtil {
	
	private static Logger logger = SimpleLogger.getSimpleLogger(HttpClientUtil.class);
	
	private static RequestConfig requestConfig;
	
	private static Random random = new Random();
	
	private static HttpHost proxy;
	
	private final static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36";

	private static CloseableHttpClient httpClient;
	
	private static CookieStore cookieStore = new BasicCookieStore();
	
	
	
	
	public static String getWebPage(String url) throws IOException {
		HttpGet request = new HttpGet(url);
		String content = getWebPage(request, "UTF-8");
		return content;
	}
	
	public static String getWebPage(HttpRequestBase request) throws IOException {
		return getWebPage(request,"UTF-8");
	}
	
	
	public static String getWebPage(HttpRequestBase request, String encoding) throws IOException {
		CloseableHttpResponse response = getResponse(request);
		logger.info("status---" + response.getStatusLine().getStatusCode());
		String content = EntityUtils.toString(response.getEntity(), encoding);
		request.releaseConnection();
		return content;
	}
	
	
	
	
	public static CloseableHttpResponse getResponse(String url) throws IOException {
		HttpGet request = new HttpGet(url);
		return getResponse(request);
	}
	
	
	
	public static CloseableHttpResponse getResponse(HttpRequestBase request) throws  IOException {
		if(request.getConfig() == null) {
			request.setConfig(requestConfig);
		}
		request.setHeader("User-Agent", Constants.userAgentArray[random.nextInt(Constants.userAgentArray.length)]);
		HttpClientContext httpClientContext = HttpClientContext.create();
		httpClientContext.setCookieStore(cookieStore);
		CloseableHttpResponse response = httpClient.execute(request);
		return response;
		
	}
	
	
	
	public static void downloadFile(String fileUrl, String path, String fileName, boolean isReplaceFile) {
		try {
			CloseableHttpResponse response = getResponse(fileUrl);
			logger.info("status:" + response.getStatusLine().getStatusCode());
			File file = new File(path);
			if(!file.exists() || !file.isDirectory())
				file.mkdirs();
			file = new File(path+fileName);
			if(!file.exists() || isReplaceFile) {
				try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
						InputStream inputStream = new BufferedInputStream(response.getEntity().getContent())){
					byte[] buff = new byte[(int) response.getEntity().getContentLength()];
					
					int offset = 0;
					while(offset < buff.length) {
						int read = inputStream.read(buff, offset, buff.length - offset);
						if(read == -1)
							break;
						offset += read;
					}
					
					logger.info(fileUrl + "--文件成功下载至" + path + fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				logger.info(path);
				logger.info("该文件存在");
			}
			response.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static CookieStore getCookieStore() {
		return cookieStore;
	}
	
	public static void setCookieStore(CookieStore cookieStore) {
		HttpClientUtil.cookieStore = cookieStore;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void serializeObject(Object object, String path) {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path,false))){
			out.writeObject(object);
			logger.info("序列化对象成功");
		} catch (Exception e) {
			e.printStackTrace();
			
		} 
	}
	
	
	
	
	
	public static Object deserializeObject(String path) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
		Object object = in.readObject();
		in.close();		
		return object;
	}
	
	
	public static Builder getRequestConfigBuilder() {
		return RequestConfig.custom().setSocketTimeout(Constants.TIMEOUT).
				                      setConnectTimeout(Constants.TIMEOUT).
				                      setConnectionRequestTimeout(Constants.TIMEOUT).
				                      setCookieSpec(CookieSpecs.STANDARD);
	}
	
	
	public static void setHttpPostParams(HttpPost request, Map<String, String> params) {
		List<NameValuePair> formParams = new ArrayList<>();
		for(String key : params.keySet())
			formParams.add(new BasicNameValuePair(key, params.get(key)));
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(formParams,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		request.setEntity(entity);
	}

}
