<?php
	require_once './utils.php';
	$conn = connect();
	$d = getJsonRequestData();

	for ($i = 0; $i < count($d); $i++) {
		
		$item = $d[$i];
		$url = $item['url'];
		$url = str_replace("'", "\'", $url);
		$status = $item['status'];
		
		if ($status == 0) {
			$sql = "update pages set verified = -1 where url = '$url';";
		}
		else {
			$sql = "update pages set verified = 1 where url = '$url';";
		}		

		$res = mysqli_query($conn, $sql);
		execSql($conn, $sql);
	}
	
?>