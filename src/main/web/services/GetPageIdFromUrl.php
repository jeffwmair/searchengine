<?php
	require_once './utils.php';

	$url = getRequestData('url');
	$conn = connect();
	$sql = "select pageId from pages where url = '$url';";
	$res = mysqli_query($conn, $sql);
	if (!$res) {
		echo "Error getting pageId for '$url'" . mysqli_error($conn) . "\n";
		echo "SQL:" . $sql . "\n";
		return;
	}
	$row = @ mysqli_fetch_assoc($res);
	$pageId = $row['pageId'];
	echo "{\"pageId\":\"$pageId\"}";
	
?>