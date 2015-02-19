<?php
	require_once './utils.php';
	
	$conn = connect();
	mysqli_set_charset ($conn, "utf8");
	$d = getJsonRequestData();
	
	$sourcePage = $d['containingPage'];
	$pageLinks = $d['links'];
	
	
	$len = count($pageLinks);
	for ($i = 0; $i < $len; $i++) {	
		
		$entry = $pageLinks[$i];
		
		$domainName = $entry['domain'];
		$pageUrl = $entry['url'];
		$crawlerid = $entry['crawlerid'];
		
		$sql = "call insert_url('$sourcePage', '$domainName', '$pageUrl', $crawlerid);";
		execSql($conn, $sql);
		
	}
	
	
	
?>