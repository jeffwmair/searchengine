package com.jwm.ir.robotverifier;

import com.jwm.ir.crawler.UrlUtils;

import java.util.List;

/**
 * Created by Jeff on 2016-07-20.
 */
public class RobotDisallows {

	private final List<String> disallows;

	public RobotDisallows(List<String> disallows) {
			this.disallows = disallows;
	}

	public List<String> getList() {
		return this.disallows;
	}

	public boolean canCrawl(String url) {

		if (disallows.size() == 0) return true;
		if (disallows.contains("/")) return false;

		String resourceOnly = UrlUtils.getPathToUrlResource(url);

		boolean canCrawl = true;
		for(String dis : disallows) {
			if (resourceOnly.startsWith(dis.toLowerCase())) {
				canCrawl = false;
				break;
			}
		}

		return canCrawl;
	}
}
