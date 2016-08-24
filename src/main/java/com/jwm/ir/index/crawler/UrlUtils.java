package com.jwm.ir.index.crawler;

import com.jwm.ir.utils.AssertUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class UrlUtils {

	final private static Logger log = LogManager.getLogger(UrlUtils.class);

	public static final String ROBOTS_TXT = "robots.txt";

	public static String getRobotsTxtUrl(String webpageUrl) {
		return getDomainFromAbsoluteUrl(webpageUrl) + "/" + ROBOTS_TXT;
	}

	/**
	 * Is this a domain?
	 * @param url
	 * @return
     */
	public static boolean isDomain(String url) {
		return url.equals(getDomainFromAbsoluteUrl(url));
	}

	/**
	 * Get the domain from the url.  Domains here never have trailing slashes
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getDomainFromAbsoluteUrl(String url) {

		AssertUtils.notEmpty(url, "must provide a url");
		AssertUtils.failState(url.startsWith("."), "The url " + url +" is not an absolute url");
		AssertUtils.failState(!url.contains(".") && !url.contains("localhost"), "The url " + url +" is missing an extension");

		String domain;
		if (url.toLowerCase().startsWith("http")) {
			domain = url.split("/")[2];
		}
		else
		{
			domain = url.split("/")[0];
		}

		if (domain.startsWith("www.")) domain = domain.substring(4);
		
		return domain.trim();
		
	}
	
	public static String getAbsoluteUrlFromHyperlink(String currentPageAbsoluteURL, String hyperLink) throws Exception {
		
		String url = null;
		if (hyperLink.startsWith("#")) {
			return null;
		}
		
		String[] urlSplit = currentPageAbsoluteURL.split("/");
		if (hyperLink.contains("?")) {
			return null;
		}
		
		if (hyperLink.toLowerCase().contains("mailto:")) {
			return null;
		}
		
		if (hyperLink.contains("@")) {
			return null;
		}
		
		if (hyperLink.contains("javascript:")) {
			return null;
		}
		
		if (hyperLink.equals("http://")) {
			return null;
		}
		
		if (hyperLink.startsWith("./")) {
			// same folder, just remove the current page resource, appendLine what comes after ./
			url = getPathToUrlResource(currentPageAbsoluteURL) + hyperLink.substring(2); 
		}
		else if (hyperLink.startsWith("..")) {
			int levelsDeep = getLevelDepthOfRelativeLink(0, hyperLink);
			url = getPathToUrlResource(currentPageAbsoluteURL);
			for(int i = 0; i < levelsDeep; i++) {
				url = getPathToUrlResource(url);	
			}
			url = url + hyperLink.substring(3*levelsDeep);
		}
				
		if (hyperLink.startsWith("/")) {
			url = "http://" + urlSplit[2] + hyperLink;
		}
		
		if (hyperLink.startsWith("http://")) {
			url = hyperLink;
		}
		
		if (url == null) {
			// I guess at this point its in the current page's folder
			url = getPathToUrlResource(currentPageAbsoluteURL) + hyperLink;	
		}
		
		
		return url;

	}
	
	public static int getLevelDepthOfRelativeLink(int depth, String link) {
		
		int newDepth = depth;
		if (link.startsWith("..")) {
			newDepth = getLevelDepthOfRelativeLink(depth+1, link.substring(3));
		}
		
		return newDepth;
	}

	public static String getPathToUrlResource(String absoluteUrl) {
		
		String url = absoluteUrl;
				
		if (url.endsWith("/")) {
			url = url.substring(0, url.lastIndexOf("/"));
		}
		
		if (url.equals("http://" + getDomainFromAbsoluteUrl(url))) {
			return url + "/";
		}
		else {
			return url.substring(0, url.lastIndexOf("/")) + "/";			
		}


	}

	public static boolean isValidUrl(String url, 
			List<String> validDomainExtensions ) {
		
		String domain = UrlUtils.getDomainFromAbsoluteUrl(url);
		if (domain == null || (!domain.contains(".") && !domain.equals("localhost"))) {
			log.info( "url '"+url+"' is not valid because the domain is:"+domain);
			return false;
		}
		
		/* check that the domain extension is valid */
		if (domain.contains(".")) {
			String domainExt = domain.substring(domain.lastIndexOf("."));
			if (!validDomainExtensions.contains(domainExt)) {
				log.info( "Domain extension '" + domainExt + "' was not found in the valid extensions list.  For domain: '" + domain + "'");
				return false;
			}
		}
		
		/* check the page extension only if it has one, and if full url is different from the domain */
//		String urlWithoutTrailingSlashString = url;
//		if (urlWithoutTrailingSlashString.endsWith("/")) urlWithoutTrailingSlashString = urlWithoutTrailingSlashString.substring(0, urlWithoutTrailingSlashString.length() - 1);
//		String domainWithHttp = "http://" + domain;
//		String urlWithoutDomain = urlWithoutTrailingSlashString.replace(domainWithHttp, "");
//		if (!urlWithoutTrailingSlashString.equals(domainWithHttp) && urlWithoutDomain.contains(".")) {
//			String pageExtension = urlWithoutDomain.substring(urlWithoutDomain.lastIndexOf("."));
//			if (!validPageExtensions.contains(pageExtension)) {
//				log.info( "Page extension '"+pageExtension+"' was not found in the valid extensions list.  For url: '"+url+"'", false);
//				return false;
//			}
//		}
				
		
		return true;
	}
}
