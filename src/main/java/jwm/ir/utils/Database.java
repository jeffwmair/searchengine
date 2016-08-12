package jwm.ir.utils;

import jwm.ir.domain.Page;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database implements Db {

	final private static Logger log = LogManager.getLogger(Database.class);
	private String _webServiceHost;

	public Database(String webserviceHost) {
		_webServiceHost = webserviceHost;
	}

	@Override
	public void updateSummaries() {
		HttpUtils.httpPost(_webServiceHost, "data", "empty", "UpdateSummaries.php", false);
	}

	@Override
	public void addPerformanceStats(int verifications, int crawls, int indexes) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append(JsonUtils.getJsonItem("verifications", verifications) + ",");
		json.append(JsonUtils.getJsonItem("crawls", crawls) + ",");
		json.append(JsonUtils.getJsonItem("indexes", indexes) + ",");
		int hardcodedWorkers = 1; /// now just single threaded
		json.append(JsonUtils.getJsonItem("workers", hardcodedWorkers));
		json.append("}");
		HttpUtils.httpPost(_webServiceHost, "data", json.toString(), "AddPerformanceStats.php", false);
	}

	@Override
	public void setVerificationStatusForUrls(HashMap<String, Integer> urlVerificationResults) {

		if (urlVerificationResults.size() == 0) return;

		StringBuilder json = new StringBuilder();
		json.append("[");
		for(Map.Entry<String,Integer> item : urlVerificationResults.entrySet()) {
			if (!json.toString().equals("[")) json.append(",");
			json.append("{");
			json.append(JsonUtils.getJsonItem("url", item.getKey()) + ",");
			json.append(JsonUtils.getJsonItem("status", item.getValue()));
			json.append("}");
		}
		json.append("]");
		
		HttpUtils.httpPost(_webServiceHost, "data",
				json.toString(), 
				"SetUrlVerificationStatusMultiple.php", false);
	}

	@Override
	public ArrayList<String> getUnverifiedPagesForVerification() {
		
		Map json = HttpUtils.httpPost(_webServiceHost,
				"crawlerid",
				"1", // worker id - just 1
				"GetUnverifiedPages.php",
				true);
		
		ArrayList<String> pages = new ArrayList<>();
		if (json == null) return pages;
		int pageCount = json.size();
		for(int i = 1; i <= pageCount; i++) {
			ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String, String>>) json.get("root");
			for(HashMap<String,String> item : list) {
				
				String pageUrl = item.get("url");
				pages.add(pageUrl);
			}
		}
		
		return pages;
		
	}

	/*

	Might need this for reference for a while.

	private final int MAX_URLS_FRO_1_DOMAIN_TO_CRAWL = 10;

	@Override
	public List<String> popUrls() {

		Map json = HttpUtils.httpPost(_webServiceHost,
				"crawlerid",
				"1",
				"GetPagesToCrawl.php",
				true);


		HashMap<String, Integer> domainPageCounter = new HashMap<>();
		List<String> retVal = new ArrayList<>();

		if (json == null) return retVal;

		int pageCount = json.size();
		for(int i = 1; i <= pageCount; i++) {
			ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String, String>>) json.get("root");
			for(HashMap<String,String> item : list) {

				String domain = item.get("domain");
				String pageUrl = item.get("url");

				Integer domainPageCount = domainPageCounter.get(domain);
				if (domainPageCount == null) {
					domainPageCount = 1;
				}
				else
				{
					domainPageCount++;
				}

				domainPageCounter.put(domain, domainPageCount);

				if (domainPageCount < MAX_URLS_FRO_1_DOMAIN_TO_CRAWL) {
					retVal.add(pageUrl);
				}
			}
		}
		
		return retVal;
	}
	*/
}
