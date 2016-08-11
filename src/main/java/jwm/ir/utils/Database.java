package jwm.ir.utils;

import jwm.ir.domain.Page;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database implements Db {

	final private static Logger log = LogManager.getLogger(Database.class);
	private String _webServiceHost;
	private final int MAX_URLS_FRO_1_DOMAIN_TO_CRAWL = 10;

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
	public synchronized void addDocumentTerms(String json, long pageId) {
		
		/* TODO: could improve performance if I can get the indexers indexing concurrently -- ie, don't make this method synchronized;
		 * I made it synchronized because the indexers were deadlocking in the insert_terms mysql procedure.
		 * 
		 */
		HttpUtils.httpPost(_webServiceHost, "data", json, "AddDocumentTerms.php", false);
	}

	@Override
	public List<String> getPageLinks(List<String> pageIds) {

		StringBuilder json = new StringBuilder();
		json.append("[");
		for (String pageId1 : pageIds) {
			if (!json.toString().equals("[")) json.append(",");
			json.append("{");
			json.append(JsonUtils.getJsonItem("id", pageId1));
			json.append("}");
		}
		json.append("]");
		
		Map jsonOut = HttpUtils.httpPost(_webServiceHost, "data", json.toString(), "GetPageOutLinkPageIds.php", true);
		if (jsonOut != null && jsonOut.size() > 0) {
			List<HashMap<String, String>> maps = (ArrayList<HashMap<String, String>>) jsonOut.get("root");
			List<String> pageLinks = new ArrayList<>();
			for (HashMap<String, String> map : maps) {
				String pageId = map.get("id");
				String destPageId = map.get("destId");
				pageLinks.add(pageId + "," + destPageId);
			}
			return pageLinks;
		}
		else {
			return new ArrayList<>();
		}
	}

	public void save(Object entity) {

	}

	@Override
	public Page getPage(String url) {
		return null;
	}

	@Override
	public String[] getPageIdsGreaterThanPageId(String lagePageReceived, int limit) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append(JsonUtils.getJsonItem("pageIdReceived", lagePageReceived) + ",");
		json.append(JsonUtils.getJsonItem("limit", Integer.toString(limit)));
		json.append("}");
		Map jsonOut = HttpUtils.httpPost(_webServiceHost, "data", json.toString(), "GetPageIdsGreaterThanPageId.php", true);
		log.debug("pagerank-getPageIdsGreater request:"+json);
		log.debug("pagerank-getPageIdsGreater response:"+jsonOut);
		if (jsonOut != null && jsonOut.size() > 0) {
			ArrayList<HashMap<String, String>> maps = (ArrayList<HashMap<String, String>>) jsonOut.get("root");
			String[] ids = new String[maps.size()];
			for(int i = 0; i < maps.size(); i++) {
				ids[i] = maps.get(i).get("pageId");
			}
			return ids;
		}
		else {
			return new String[0];
		}
	}

	@Override
	public void updatePageRanks(Map<Long,Double> pageRanks) {
		
		if (pageRanks.size() == 0) return;
		
		StringBuilder json = new StringBuilder();
		json.append("[");
		for(Map.Entry<Long,Double> item : pageRanks.entrySet()) {
			if (!json.toString().equals("[")) json.append(",");
			json.append("{");
			json.append(JsonUtils.getJsonItem("id", item.getKey()) + ",");
			json.append(JsonUtils.getJsonItem("pr", item.getValue()));
			json.append("}");
		}
		json.append("]");
		
		HttpUtils.httpPost(_webServiceHost, "data", json.toString(), "UpdatePageRanks.php", false);
	}

	@Override
	public long getPageIdFromUrl(String url) {
		url = HttpUtils.cleanUrl(url);
		Map json = HttpUtils.httpPost(_webServiceHost, "url", url, "GetPageIdFromUrl.php", true);
		String id_s = json.get("pageId").toString();
		return Integer.parseInt(id_s);
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
