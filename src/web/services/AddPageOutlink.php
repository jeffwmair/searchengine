<?php
	require_once './utils.php';
	if (!checkPass()) return;
	$conn = connect();

	$d = getJsonRequestData();
		
	$pageurl = $d['pageurl'];
	$urlout = $d['urlout'];

	$conn = connect();
	$sql = "insert into pagelinks (pageId, destPageId) values 
		((select pageId from pages where url = '$pageurl' limit 1), (select pageId from pages where url = '$urlout' limit 1));";
	execSql($conn, $sql);
?>