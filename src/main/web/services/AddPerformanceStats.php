<?php
	require_once './utils.php';
	$conn = connect();

	$d = getJsonRequestData();
	
	$verificationCount = $d['verifications'];
	$crawlCount = $d['crawls'];
	$indexCount = $d['indexes'];
	$workers = $d['workers'];
		
	$sql = "insert into summarydata_i (item, val, updatedate) values 
		('VerificationCount [$workers]', $verificationCount, current_time())
		,('CrawlCount [$workers]', $crawlCount, current_time())
		,('IndexCount [$workers]', $indexCount, current_time());";
	$res = mysqli_query($conn, $sql);
	if (!$res) {
		echo "Error in $sql :" . mysqli_error($conn) . "\n";
	}
	
?>