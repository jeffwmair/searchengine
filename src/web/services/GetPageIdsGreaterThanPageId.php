<?php
	require_once './utils.php';

	$d = getJsonRequestData();
	

	$pageIdReceived = $d['pageIdReceived'];
	$limit = $d['limit'];

	// just verified
	$verifiedOnly = 'true';
	$sqlVerified = ($verifiedOnly == 'true') ? " and verified = 1" : "";
	
	$sql = "select pageId from pages where pageId > $pageIdReceived $sqlVerified limit $limit;";

	$conn = connect();
	$res = execSql($conn, $sql);
	echo convertSqlRowsToJson($res);
	
?>