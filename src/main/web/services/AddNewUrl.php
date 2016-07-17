<?php
	require_once './utils.php';
	
	$userSubmitted = $_POST['indexrequest'];
	$isUserRequest = ($userSubmitted != NULL && $userSubmitted == 1);
	if (!$isUserRequest) {
		if (!checkPass()) return;	
	}
	$conn = connect();
	$d = getJsonRequestData();		
	
	$domainName = $d['domain'];
	$pageUrl = $d['url'];
	$crawlerid = $d['crawlerid'];
	
	
	if ($isUserRequest) {
		$crawlerid = 1;	// crawler #1 is for user-submitted urls
		$pageUrl = $_POST['url'];
		$domainName = $pageUrl;
		if (strpos($domainName, "http://") === 0) {
			$domainName = substr($domainName, 7);
		}
		else {
			$pageUrl = "http://" . $pageUrl;
		}
		$domainArr = explode('/', $domainName);
		$domainName = $domainArr[0];
	}
	
	$sqlDomain = '';
	$domainId = 0;
	$sqlGetDomain = "select domainId from domains where domain = '$domainName';";
	$res = mysqli_query($conn, $sqlGetDomain);
	$row = @ mysqli_fetch_assoc($res);
	$initialStatus = 0;
	if ($isUserRequest) $initialStatus = 1;
	if ($row == NULL) {
		$sqlNewDomain = "insert into domains (domain, status, crawlerId, total_crawls, locked) values ('$domainName', $initialStatus, $crawlerid, 0, 0);";
		execSql($conn, $sqlNewDomain);
		$domainId = mysqli_insert_id($conn);
	}
	else {
		$domainId = $row['domainId'];
		
		/* if its a user-submission, we should update the status to 1 to give the domain priority */
		if ($isUserRequest) {
			$sql = "update domains set status = 1, crawlerId = 1 where domainId = $domainId;";
			execSql($conn, $sql);
		}
	}
	
	$initialVerified = 0;
	// if ($isUserRequest) $initialVerified = 1;
	$sqlPage = "insert ignore into pages (domainId, verified, url, pagerank, fail_count) values ($domainId, $initialVerified, '$pageUrl', 0, 0);";	
	execSql($conn, $sqlPage);
	
	if ($isUserRequest) {
		$ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
		$ip .= ';' . $_SERVER['REMOTE_ADDR'];
		$sql = "insert into pagesubmissions (pageId, submitdate, ip) values (LAST_INSERT_ID(), current_time(), '$ip');";
		execSql($conn, $sql);
		header('location: SubmissionComplete.php');
	}
		
	
?>