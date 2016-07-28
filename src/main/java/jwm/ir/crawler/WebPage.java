package jwm.ir.crawler;


import jwm.ir.message.WebResource;
import jwm.ir.message.WebResourcePageImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;


public class WebPage {

	final private static Logger log = LogManager.getLogger(WebPage.class);
	private String _url;
	private Document _page;
	public WebPage(String url) {
		_url = url;
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
				log.error("HTTP " + code + ": " + _url);
				return false;
			}
			else {
				// 400 series
				return false;
			}
			
		}
		catch(Exception ex) {
			log.error("Error loading web page: " + _url +  ex.toString());
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
	
	public HashMap<String,String> getHyperlinks(ArrayList<String> validDomainExtensions) {
		
		HashMap<String, String> links = new HashMap<>();
		Elements linkElements = _page.select("a");
		for(Element el : linkElements)  {
			String anchorText = el.text();
			String url = null;
			try {
				url = UrlUtils.getAbsoluteUrlFromHyperlink(_url, el.attr("href"));
			} catch (Exception e) {
				log.error("Error getHyperlinks(); " + e.toString());
			}
			if (url != null)  {
				log.info("url is:"+url);

				if (!links.containsKey(url) && anchorText.length() > 2) {
					log.info("url is accepted (not found yet and length > 2)");

					boolean isValidUrl = false;
						isValidUrl = UrlUtils.isValidUrl(url, validDomainExtensions);
						log.info("url is valid:"+isValidUrl);

					if (isValidUrl) {
						log.info("keeping url");
						links.put(url, anchorText);
					}
				}				
			}
		}
		return links;
		
	}

	/**
	 * Get a ParsedWebPage instance
	 * @return
     */
	public WebResource getWebResource() {
		return new WebResourcePageImpl(_url, _page.text());
	}
	

}
