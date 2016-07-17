<?php
	require_once './utils.php';

	$sql = "select extType, ext from validextensions;";	
	$conn = connect();
	$res = mysqli_query($conn, $sql);
	if (!$res) {
		echo "Error getting valid extensions list:" . mysqli_error($conn) . "\n";
	}
	echo convertSqlRowsToJson($res);	

?>