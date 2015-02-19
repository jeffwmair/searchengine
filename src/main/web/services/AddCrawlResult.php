<?php
	require_once './utils.php';
	
	if (!checkPass()) return;	
	$conn = connect();

	$d = getJsonRequestData();
	
	$pageUrl = $d['url'];
	$crawlTime = $d['time'];
	$crawlStatus = $d['success'];
	$pageTitle = $d['title'];
	$pageDescription = $d['description'];
	
	$pageUrl = str_replace("'", "\'", $pageUrl);
	
	mysqli_set_charset ($conn, "utf8");
		
	$domainId = 0;
	$sqlGetDomain = "select domainId from pages where url = '$pageUrl';";
	$res = mysqli_query($conn, $sqlGetDomain);
	$row = @ mysqli_fetch_assoc($res);
	$domainId = $row['domainId'];
	
	$sqlDomain = "update domains set status = 0, last_crawl = '$crawlTime', total_crawls = (total_crawls+1) where domainId = $domainId;";
	$pageFailIncrement = $crawlStatus;
	if ($pageFailIncrement == 0) $pageFailIncrement = -1;
	$updateTitle = '';
	if ($pageTitle != NULL) {
		$updateTitle = " title = '" . str_replace("'", "\'", substr($pageTitle,0,100)) . "',";
	}
	
	$updateDesc = '';
	if ($pageDescription != NULL) {
		$updateDesc = " description = '" . str_replace("'", "\'", substr($pageDescription,0,100)) . "',";
	}
	$sqlPage = "update pages set $updateDesc $updateTitle last_crawl = '$crawlTime', fail_count = greatest(0,(fail_count - $pageFailIncrement)) where url = '$pageUrl';";
	
	$res1 = mysqli_query($conn, $sqlDomain);
	if (!$res1) {
		echo "Error during insert/update of domain for page '$pageUrl'" . mysqli_error($conn) . "\n";
		echo "SQL:" . $sqlDomain . "\n";
		echo "Finding DomainId:" . $sqlGetDomain . "\n";
	}
	
	$res2 = mysqli_query($conn, $sqlPage);
	if (!$res2) {
		echo "Error during insert/update of page '$pageUrl'" . mysqli_error($conn) . "\n";
		echo $sqlPage;
	}
		
	
?>