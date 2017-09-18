package Core.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class HttpClientUtil {
	
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
