package jwm.ir.crawlerutils;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jwm.ir.utils.Log;

import org.jsoup.*;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WebPage {
	
	private String _url;
	private Log _log;
	private Document _page;
	private String _client;
	public WebPage(String clientName, String url, Log log) {
		_url = url;
		_log = log;
		_client = clientName;
	}
	
	public boolean crawl() {
		try
		{
			Response response = 
				Jsoup
					.connect(_url)
					.timeout(10000)
					.header("Accept-Charset", "UTF-8")
					.header("user-agent", "JeffMairFriendlyWebCrawler")
					.execute();
			int code = response.statusCode();
			if (code == 200) {
				_page = response.parse();
				return true;
			}
			else if (code < 400 && code > 499) {
				_log.LogMessage(_client, "HTTP " + code + ": " + _url, true);
				return false;
			}
			else {
				// 400 series
				return false;
			}
			
		}
		catch(Exception ex) {
			_log.LogMessage(_client, "Error loading web page: " + _url +  ex.toString(), true);
			return false;
		}
	}
	
	public String getPageTitle() {
		Elements titleEls = _page.select("head title");
		if (titleEls != null && titleEls.size() > 0) {
			String title = titleEls.first().text().replace("\"", "\\\"");
			title = title.replace("\n", "");
			title = title.replace("\r", "");
			return title;
		}
		else {
			return null;
		}
	}
	
	public String getPageDescription() {
		Elements meta = _page.select("head meta");
		String desc = null;
		if (meta != null && meta.size() > 0) {
			for(Element el : meta) {
				if (el.hasAttr("name") && el.attr("name").equals("description")) {
					
					desc = el.attr("content").replace("\"", "\\\"");
					desc = desc.replace("\n", "");
					desc = desc.replace("\r", "");
					break;
				}
			}
		}
			
		return desc;
	}
	
	public void getRobotMetaRules(boolean[] rules) {
		
		rules[0] = false;
		rules[1] = false;
		Elements metaEls = _page.select("meta");
		for(Element metaEl : metaEls) {
			String elNameAtt = metaEl.attr("name");
			if (elNameAtt.toLowerCase().equals("robots")) {
				String[] robotContent = metaEl.attr("content").split(",");
				for(String s : robotContent) {
					if (s.toLowerCase().equals("noindex")) {
						rules[0] = true;
					}
					if (s.toLowerCase().equals("nofollow")) {
						rules[1] = true;
					}
				}
			}
		}
	}
	
	public HashMap<String,String> getHyperlinks(ArrayList<String> validPageExtensions, ArrayList<String> validDomainExtensions) {
		
		HashMap<String, String> links = new HashMap<String, String>();
		Elements linkElements = _page.select("a");
		for(Element el : linkElements)  {
			String anchorText = el.text();
			String url = null;
			try {
				url = UrlUtils.getAbsoluteUrlFromHyperlink(_url, el.attr("href"));
			} catch (Exception e) {
				_log.LogMessage(_client, "Error getHyperlinks(); " + e.toString(), true);
			}
			if (url != null)  {
				
				if (!links.containsKey(url) && anchorText.length() > 2) {
					
					boolean isValidUrl = false;
					try
					{
						isValidUrl = UrlUtils.isValidUrl(url, validPageExtensions, validDomainExtensions, _client, _log);
					}
					catch(Exception ex)
					{
						_log.LogMessage(_client, "Error determining the validity of url: " + url, true);
					}
					
					if (isValidUrl) {
						links.put(url, anchorText);
					}
				}				
			}
		}
		return links;
		
	}
	
	public void writeToFile(String filenamePrefix, String folder) {
		
	    Format formatter = new SimpleDateFormat("yyyy_M_dd HHmmssSSS");
	    String dateFormatted = formatter.format(new Date());
		
        try 
        {
        	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(folder + "/" + filenamePrefix + "_" + dateFormatted + ".txt"), "UTF-8"));
            out.write(_url + "\n");
    		out.write(_page.text().replace("\\\"", "\""));	
            out.close();
		} 
        catch (IOException e) 
        {
        	_log.LogMessage(_client, "Error writing url to file " + _url, true);
			e.printStackTrace();
		}        
	}
	
	

}
