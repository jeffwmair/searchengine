package com.jwm.ir.crawler;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;


public class RobotsTxt {

	final private static Logger log = LogManager.getLogger(RobotsTxt.class);
	private ArrayList<String> _disallows;
	private String _domain;
	public RobotsTxt(String domain) {
		_domain = domain;
		_disallows = new ArrayList<>();
	}
	
	public RobotsTxt(boolean allow) {
		if (!allow) {
			_disallows = new ArrayList<>();
			_disallows.add("/");
		}
	}
	
	private String _parserCurAgent = "";
	public void processRobotTxtLine(String line) {
		
		if (line.startsWith("#") || line.length() == 0) {
			return;
		}
		
		// strip out any inline comments
		if (line.contains("#")) {
			line = 	line.substring(0, line.indexOf("#"));
		}
			
		if (line.toLowerCase().startsWith("user-agent:")) {
			try
			{
				_parserCurAgent = line.split(":")[1];	
			}
			catch(Exception e) {
				// dont worry bout this
			}
			
		}
		else
		{
			if (!_parserCurAgent.trim().equals("*")) {
				return;
			}
			
			if (!line.toLowerCase().contains("disallow")) return;
			
			String[] pair = line.split(":");
			if (pair.length == 2) {
				if (pair[0].trim().toLowerCase().equals("disallow")) {
					String disallow = pair[1].trim().toLowerCase();
					if (disallow.length() > 0) {
						_disallows.add(disallow);	
					}		
				}	
			}
		}
	}
		
	public boolean canCrawl(String url) {
		
		if (_disallows.size() == 0) return true;
		if (_disallows.contains("/")) return false;
		 
		int httpPrefixLength = 7;
		if (_domain.startsWith("http://")) {
			log.error("unexpected domain starting with http: " + _domain);
		}
		
		String resourceOnly = url.substring(httpPrefixLength + _domain.length()).toLowerCase();
		
		boolean canCrawl = true;
		for(String dis : _disallows) {
			if (resourceOnly.startsWith(dis.toLowerCase())) {
				canCrawl = false;
				break;
			}
		}		
		
		return canCrawl;
	}
}
