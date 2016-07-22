package jwm.ir.crawler;

import jwm.ir.message.WebResource;
import jwm.ir.message.WebResourcePageImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Jeff on 2016-07-20.
 */
public class ResourceFetcher {

    final private static Logger log = LogManager.getLogger(ResourceFetcher.class);
	private Document page;

	void crawl(String url) {
		try
		{
			Connection.Response response =
				Jsoup
					.connect(url)
					.timeout(10000)
					.header("Accept-Charset", "UTF-8")
					.header("user-agent", "JeffMairFriendlyWebCrawler")
					.execute();
			int code = response.statusCode();
			if (code == 200) {
				page = response.parse();
				return;
			}
			else if (code < 400 && code > 499) {
				log.error("HTTP " + code + ": " + url);
				return;
			}
			else {
				// 400 series
				return;
			}

		}
		catch(Exception ex) {
			log.error("Error loading web page: " + url +  ex.toString());
		}
	}

	public String getPageTitle() {
		Elements titleEls = page.select("head title");
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
		Elements meta = page.select("head meta");
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
		Elements metaEls = page.select("meta");
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

		HashMap<String, String> links = new HashMap<>();
		Elements linkElements = page.select("a");
		for(Element el : linkElements)  {
			String anchorText = el.text();
			String url = null;
			try {
				url = UrlUtils.getAbsoluteUrlFromHyperlink(url, el.attr("href"));
			} catch (Exception e) {
				log.error("Error getHyperlinks(); " + e.toString());
			}
			if (url != null)  {
				log.info("url is:"+url);

				if (!links.containsKey(url) && anchorText.length() > 2) {
					log.info("url is accepted (not found yet and length > 2)");

					boolean isValidUrl = false;
						isValidUrl = UrlUtils.isValidUrl(url, validPageExtensions, validDomainExtensions);
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
	public WebResource getWebResource(String url) {
		crawl(url);
		return new WebResourcePageImpl(url, page.text());
	}

}
