<?php
	require_once './utils.php';
	$d = getJsonRequestData();
	$crawlerId = $d['crawlerId'];
	$conn = connect();
	mysqli_set_charset ($conn, "utf8");
	
	// execSql($conn, "START TRANSACTION");
	// 
	// $sql = "create temporary table tempdomain ( domainid bigint not null);";
	// execSql($conn, $sql);
	// 	
	// /* get the domain to transfer */
	// $sql = "insert into tempdomain select domainId from (select d.domainId, d.crawlerId, d.last_crawl 
	// 	from domains d join pages p on p.domainid = d.domainid and p.verified = 1
	// 	where crawlerId != $crawlerId and d.last_crawl is null and d.locked = 0 ) as tbl 
	// 	order by domainId limit 10;";
	// execSql($conn, $sql);	
	// 
	// /* update the domain */
	// $sql = "update domains set crawlerId = $crawlerId, locked = 1 where domainId in (select domainId from tempdomain);";
	// execSql($conn, $sql);
	// 
	// $sql = "select domainId from tempdomain limit 1;";
	// $res = execSql($conn, $sql, 'domainId', 0);
	// 
	// execSql($conn, "COMMIT");
	
	$res = execSql($conn, "select 0 as domainId;");
	
	echo convertSqlRowsToJson($res);	
	
?>