package co.softwarebox.test;

import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class UrlTest implements Runnable {

	private static final String VISITS_URL = "http://localhost:8081/api/v1/visits/";
	private static final String USER_PASS = "demos:password";

	private String url;
	
	private String domain;

	private ExpectedResult expectedResult;
	
	public UrlTest() {
		
	}
	
	public UrlTest(String scheme, String domain, String route, ExpectedResult expectedResult) {
		this.domain = domain;
		this.url = scheme + "://" + domain + "/" + route;
		this.expectedResult = expectedResult;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void run() {
		try {
			
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(VISITS_URL);
	
			httppost.setHeader("Content-Type", "application/json");

			byte[] encoding = Base64.getEncoder().encode(USER_PASS.getBytes());
			httppost.setHeader("Authorization", "Basic " + new String(encoding));

			String json = "{\"url\": \"" + this.url+ "\"}";
			
	        HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
	        httppost.setEntity(entity);
	        HttpResponse response = httpclient.execute(httppost);
	        
	        // String result = 
        		EntityUtils.toString(response.getEntity());
		    
			expectedResult.addStat(this.domain);
		}
		catch (Exception e) {
			System.out.println("Error executing UrlTest");
			e.printStackTrace();
		}
	}

}
