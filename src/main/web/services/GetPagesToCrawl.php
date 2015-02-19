<?php
	require_once './utils.php';
	$crawlerid = getRequestData('crawlerid');
	
	// variables
	$limit = 50;
	$minDomainCrawlRateMinutes = 120;
	$failedPagePenaltyInMin = 240;
	$minPageCrawlRateMinutes = 60 * 24 * 14;
	
	$conn = connect();
		
	/* put the pages into the temp table */
	$sql = "select d.domain, p.url, d.status, d.last_crawl, p.last_crawl
			from domains d
				join pages p on d.domainId = p.domainId
			where d.status >= 0 and p.verified = 1 and d.crawlerId = $crawlerid and 
			(
			(d.status = 1) 
			or 
			(d.last_crawl is null) 
			or 
	       	(
			(hour(timediff(now(),d.last_crawl))*60 + minute(timediff(now(),d.last_crawl))) > $minDomainCrawlRateMinutes)
	        or
	        ( ( 60*(hour(timediff(now(),p.last_crawl))) + (minute(timediff(now(),p.last_crawl))) ) > ($minPageCrawlRateMinutes + p.fail_count * $failedPagePenaltyInMin) ) )
			order by 
				(case when d.last_crawl is null then '0000-00-00' else d.last_crawl end) asc
				, d.last_crawl asc
				, (case when p.last_crawl is null then '0000-00-00' else p.last_crawl end) asc
				, d.total_crawls desc
		limit $limit;";
	$res = execSql($conn, $sql);

	echo convertSqlRowsToJson($res);	

?>