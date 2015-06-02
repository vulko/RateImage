package home24.testtask.rateimage.backend;

import home24.testtask.rateimage.datamodel.RatableImage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class BackendLoaderAdapter {
	
	private static final String TAG = "BackendLoaderAdapter";
	private static final String BACKEND_API_URL = "https://api-mobile.home24.com/api/v1/articles?appDomain=1&limit=10";

	public List<RatableImage> loadRemoteData() {
		try {
			String jstr = loadJSONStringfromURL(BACKEND_API_URL);
			
			if (jstr == null) {
				// TODO: apparently there should be raised an exception, so this won't occur.
				return null;
			}

			JSONObject jobj = new JSONObject(jstr);
			JSONArray jlinks = jobj.getJSONObject("_links").getJSONArray("articles");
			ArrayList<String> linksList = new ArrayList<String>(jlinks.length());
			// parse links to articles
			for (int i = 0; i < jlinks.length(); ++i) {
				jobj = (JSONObject) jlinks.get(i);
				linksList.add(i, (String) jobj.get("href"));
			}
			
			// load and parse img url and sku for each item
			String sku, imgUrl;
			List<RatableImage> retList = new LinkedList<RatableImage>();
			for (String itemLink : linksList) {
				jstr = loadJSONStringfromURL(itemLink);
				jobj = new JSONObject(jstr);
				sku = jobj.getString("sku");
				jlinks = jobj.getJSONArray("media");
				jobj = (JSONObject) jlinks.get(0);
				imgUrl = jobj.getString("uri");
				retList.add( new RatableImage(sku, imgUrl, false) );
			}
			
			return retList;


			/*
			// workaround
			Thread.sleep(1000); // immitate asynchronous behavior :)
			List<RatableImage> retList = new LinkedList<RatableImage>();
			retList.add(new RatableImage("1", "http://cdn.home24.net/images/media/catalog/product/135x135/png/r/e/relaxliege-listone-meshstoff-schwarz-413096.jpg", false));
			retList.add(new RatableImage("2", "http://cdn.home24.net/images/media/catalog/product/135x135/png/r/e/relaxliege-carson-kunstleder-schwarz-strukturstoff-hellgrau-459483.jpg", false));
			retList.add(new RatableImage("3", "http://cdn.home24.net/images/media/catalog/product/135x135/png/r/e/relaxsessel-portia-echtleder-schwarz-462243.jpg", false));
			retList.add(new RatableImage("4", "http://cdn.home24.net/images/media/catalog/product/135x135/png/b/e/beistelltisch-teak-line-liviko-aus-teakholz-in-braun-1370943.jpg", false));
			retList.add(new RatableImage("5", "http://cdn.home24.net/images/media/catalog/product/135x135/png/r/e/relaxliege-listone-meshstoff-weiss-337266.jpg", false));
			retList.add(new RatableImage("6", "http://cdn.home24.net/images/media/catalog/product/135x135/png/w/m/wmf-2013-08-20-254960.jpg", false));
			retList.add(new RatableImage("7", "http://cdn.home24.net/images/media/catalog/product/135x135/jpg/j/a/jan-kurtz-lucca-tisch--teak-leisten-g-jk-gti-0221-1.jpg", false));
			retList.add(new RatableImage("8", "http://cdn.home24.net/images/media/catalog/product/135x135/png/b/a/balkontisch-vancouver-garden-teakholz-massiv-1544770.jpg", false));
			retList.add(new RatableImage("9", "http://cdn.home24.net/images/media/catalog/product/135x135/png/a/u/ausziehtisch-comodoro-eukalyptus-massiv-geoelt-1743370.jpg", false));
			retList.add(new RatableImage("10", "http://cdn.home24.net/images/media/catalog/product/135x135/png/k/l/klappstuhl-schlossgarten-2er-set-flachstahl-eukalyptus-massiv-1808802.jpg", false));
			return retList;
			// end of workaround
			*/
			
			
			//Log.e("json", jobj.toString());

/*			URL obj = new URL(BACKEND_API_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 
			// optional default is GET
			con.setRequestMethod("GET");
	 
			//add request header
			con.setRequestProperty("Accept-Language", "de-DE");
	 
			int responseCode = con.getResponseCode();
			Log.e(TAG, "Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				Log.e(TAG, "parse: " + inputLine);
			}
			in.close();*/
			
			//return null;
		} catch (Exception e) {
			// TODO: need to handle different types of exception, like connection error, parse json error etc
			Log.e(TAG, "Error: " + e.toString());
			return null;
		}		
	}
	
	private String loadJSONStringfromURL(String url) throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Accept-Language", "de_DE");
//		httpGet.addHeader("User-Agent", "Mozilla/5.0 ( compatible ) ");
//		httpGet.addHeader("Accept", "*/*");

		// restore cookies
		
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		InputStream is = httpEntity.getContent();
		Log.e(TAG, httpResponse.toString());

		BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			sb.append(line + "n");
			//Log.e("parsing", line);
		}
		is.close();
		
		return sb.toString();		
	}
	
}
