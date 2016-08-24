package com.jwm.ir.index.workers;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


class HttpUtils {

	
private static final String CHAR_SET = "UTF-8";

	final private static Logger log = LogManager.getLogger(HttpUtils.class);

	public static ArrayList<String> getRobotTxtFromDomain(String domain) {

		ArrayList<String> output = new ArrayList<>();
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
		    	log.error("Error parsing robots.txt for " + domain + "-> " + rex.getMessage() + "input line:" + inputLine );
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

}
