package jwm.ir.utils;

import jwm.ir.domain.Page;

import java.text.SimpleDateFormat;
import java.util.*;

public class Database implements Db {

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
	public synchronized void addDocumentTerms(String json, int pageId) {
		
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
		for(int i = 0; i < pageIds.size(); i++) {
			if (!json.toString().equals("[")) json.append(",");
			json.append("{");
			json.append(JsonUtils.getJsonItem("id", pageIds.get(i)));
			json.append("}");
		}
		json.append("]");
		
		Map jsonOut = HttpUtils.httpPost(_webServiceHost, "data", json.toString(), "GetPageOutLinkPageIds.php", true);
		if (jsonOut != null && jsonOut.size() > 0) {
			List<HashMap<String, String>> maps = (ArrayList<HashMap<String, String>>) jsonOut.get("root");
			List<String> pageLinks = new ArrayList<String>();
			for(int i = 0; i < maps.size(); i++) {
				String pageId = maps.get(i).get("id");
				String destPageId = maps.get(i).get("destId");
				pageLinks.add(pageId + "," + destPageId);
			}
			return pageLinks;
		}
		else {
			return new ArrayList<>();
		}
	}

	private List<String> validDomainExtensions;

	@Override
	public List<String> getValidDomainExtensions() {

		if (validDomainExtensions == null) {
			validDomainExtensions = new ArrayList<>();
			Map jsonOut = HttpUtils.httpPost(_webServiceHost, "data", "", "GetValidExtensionsAll.php", true);
			ArrayList<HashMap<String, String>> maps = (ArrayList<HashMap<String, String>>) jsonOut.get("root");
			for(int i = 0; i < maps.size(); i++) {
				String extType = maps.get(i).get("extType");
				String ext = maps.get(i).get("ext");
				if (extType.equals("1")) {
					validDomainExtensions.add(ext);
				}
			}
		}

		return validDomainExtensions;
	}

	@Override
	public void startTransaction() {

	}

	@Override
	public void commitTransaction() {

	}

	@Override
	public void save(Object entity) {

	}

	@Override
	public void saveEach(Object... entities) {

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
	public void updatePageRanks(HashMap<Integer,Double> pageRanks) {
		
		if (pageRanks.size() == 0) return;
		
		StringBuilder json = new StringBuilder();
		json.append("[");
		for(Map.Entry<Integer,Double> item : pageRanks.entrySet()) {
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
	public int getPageIdFromUrl(String url) {
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
	public void addCrawlResult(String url, String pageTitle, String pageDesc, Date crawlTime, boolean successful) {
		url = HttpUtils.cleanUrl(url);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append(JsonUtils.getJsonItem("url", url) + ",");
		if (pageTitle != null && pageTitle.length() > 5) {
			json.append(JsonUtils.getJsonItem("title",  pageTitle) + ",");	
		}
		if (pageDesc != null) {
			json.append(JsonUtils.getJsonItem("description", pageDesc) + ",");
		}
		json.append(JsonUtils.getJsonItem("time", sdf.format(crawlTime)) + ",");
		json.append(JsonUtils.getJsonItem("success", (successful) ? "1" : "0"));
		json.append("}");
		
		HttpUtils.httpPost(_webServiceHost,
				"data", 
				json.toString(), 
				"AddCrawlResult.php",
				false);

	}

	@Override
	public ArrayList<String> getUnverifiedPagesForVerification() {
		
		Map json = HttpUtils.httpPost(_webServiceHost,
				"crawlerid",
				"1", // worker id - just 1
				"GetUnverifiedPages.php",
				true);
		
		ArrayList<String> pages = new ArrayList<String>();
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
}
