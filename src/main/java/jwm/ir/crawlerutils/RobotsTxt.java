package jwm.ir.crawlerutils;

import java.util.ArrayList;

import jwm.ir.utils.Log;


public class RobotsTxt {

	private ArrayList<String> _disallows;
	String _domain;
	public RobotsTxt(String domain) {
		_domain = domain;
		_disallows = new ArrayList<String>();
	}
	
	public RobotsTxt(boolean allow) {
		if (!allow) {
			_disallows = new ArrayList<String>();
			_disallows.add("/");
		}
	}
	
	public ArrayList<String> getDisallows() { return _disallows; }
	private String _parserCurAgent = "";
	public void processRobotTxtLine(String line, Log l, String client) {
		
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
		
	public boolean canCrawl(String url, String clientName, Log log) {
		
		if (_disallows.size() == 0) return true;
		if (_disallows.contains("/")) return false;
		 
		int httpPrefixLength = 7;
		if (_domain.startsWith("http://")) {
			log.LogMessage("Robot", "unexpected domain starting with http: " + _domain, true);
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
