<?php
	require_once './utils.php';
	$d = getJsonRequestData();
	
	$url = $d['url'];
	$url = str_replace("'", "\'", $url);
	$status = $d['status'];

	if ($status == 0) {
		/* this will become redundant since they are marked as -1 until the verification results come back */
		$sql = "update pages set verified = -1 where url = '$url';";
	}
	else {
		$sql = "update pages set verified = 1 where url = '$url';";
	}

	$conn = connect();
	$res = mysqli_query($conn, $sql);
	execSql($conn, $sql);
	
?>