<?php
	require_once './utils.php';
	if (!checkPass()) return;
	$d = getJsonRequestData();
		
	$conn = connect();
	$len = count($d);
	for ($i = 0; $i < $len; $i++) {
		$item = $d[$i];
		$pageId = $item['id'];
		$rank = $item['pr'];
		
		$sql = "update pages set pagerank = $rank where pageId = $pageId;";
		execSql($conn, $sql);
	}

?>