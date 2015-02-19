package jwm.ir.utils;

import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;


public class HttpUtils {

	
private static final String CHAR_SET = "UTF-8";


	public static Map httpPost(String host, 
			String clientName, 
			String paramName, 
			String paramData, 
			String phpFunction, 
			boolean returnJson, 
			Log log) {
		
		String urlLoc = getServiceLocation(host, phpFunction);
		
		URL url;
		try {
			String urlParameters = String.format(paramName + "=%s", URLEncoder.encode(paramData, CHAR_SET));
            // see utils.php for corresponding hard coded password
            String pass = "searchenginepass";
			urlParameters += "&pass=" + URLEncoder.encode(pass, CHAR_SET);
			url = new URL(urlLoc);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setInstanceFollowRedirects(false); 
			con.setRequestMethod("POST"); 
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf8");
			con.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			con.setUseCaches (false);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream ());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    String inputLine;
		    StringBuilder httpResponseBody = new StringBuilder();
		    
		    while ((inputLine = in.readLine()) != null)
		    	httpResponseBody.append(inputLine + "\n");
		    in.close();
		    con.disconnect();
		    Map json = null;
		    if (returnJson && !httpResponseBody.toString().startsWith("[]")) {
		    	try {
		    		JsonParserFactory factory = JsonParserFactory.getInstance();
					JSONParser parser = factory.newJsonParser();
					json = parser.parseJson(httpResponseBody.toString());	
		    	}
		    	catch(Exception e) {
		    		log.LogMessage(clientName, "Error parsing HTTP json: " + httpResponseBody.toString(), true);
		    	}
		    	
		    }
		    else {
		    	if (httpResponseBody.length() > 0 && !httpResponseBody.toString().trim().equals("[]")) {
		    		log.LogMessage(clientName, "Message returned from HttpPost: " + httpResponseBody.toString() + "\n" + paramName + "=" + paramData.toString(), false);
		    	}
		    }
		    
		    return json;
			
		} catch (Exception e) {
			log.LogMessage(clientName, "Error during http request: " + e.getMessage(), true);
			return null;
		} 	
	}
	
	public static ArrayList<String> getRobotTxtFromDomain(String domain, String clientName, Log log ) {
		
		ArrayList<String> output = new ArrayList<String>();
		if (!domain.endsWith("/")) domain += "/";
		if (!domain.startsWith("http://")) domain = "http://" + domain;
		try {
			URLConnection con = new URL(domain + "robots.txt").openConnection();
			con.setRequestProperty("Accept-Charset", "ASCII");		
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    String inputLine = "";
		    
		    try {
		    	while ((inputLine = in.readLine()) != null) {
		    		output.add(inputLine);
		    	}
			    in.close();	
		    }
		    catch(Exception rex) {
		    	log.LogMessage(clientName, "Error parsing robots.txt for " + domain + "-> " + rex.getMessage() + "input line:" + inputLine, true);
		    }
		}
		catch(Exception ex) {
			// dont worry about not finding robots.txt
			//log.LogMessage(clientName, "Error getting/parsing robots.txt:" + ex.toString(), true);
		}
		
	    return output;
		
	}
	
	private static String getServiceLocation(String host, String phpFunction) {
		return "http://" + host + "/services/" + phpFunction;
	}
	
	public static String cleanUrl(String url) {
		return url.replace("\"", "");
	}
}
