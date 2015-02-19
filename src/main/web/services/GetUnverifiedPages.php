<?php
	require_once './utils.php';
	header('Content-Type: text/json; charset=utf-8');
	$crawlerid = getRequestData('crawlerid');
	$conn = connect();
	mysqli_set_charset ($conn, "utf8");
	
	mysqli_query($conn, "START TRANSACTION");
	
	$sql = "create temporary table tmpids ( rid bigint not null,
		PRIMARY KEY (rid));";
	mysqli_query($conn, $sql);
	
	/* put the pages into a temp table */
	$sql = "insert into tmpids select p.pageId from domains d
		join pages p 
		on d.domainId = p.domainId
		where d.crawlerId = $crawlerid and p.verified = 0 limit 250;";
	mysqli_query($conn, $sql);
	
	/* mark them all as -1 in case the verification fails */
	$sql = "update pages set verified = -1 where pageId in (select rid from tmpids);";
	mysqli_query($conn, $sql);

	/* then return those same pages for verification */
	$sql = "select d.domain, p.url from domains d
		join pages p on d.domainId = p.domainId
		join tmpids tmp on tmp.rid = p.pageid order by d.domain;";
	$res = mysqli_query($conn, $sql);
	if (!$res) {
		echo "Error getting unverified pages:" . mysqli_error($conn) . "\n";
		mysqli_query($conn, "ROLLBACK");		
	}
	else {
		mysqli_query($conn, "COMMIT");		
	}	
	echo convertSqlRowsToJson($res);	
?>