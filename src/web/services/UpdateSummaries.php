<?php
	require_once './utils.php';
	if (!checkPass()) return;
	$conn = connect();
	
	$sqlGetIndexedPageCount = "select count(distinct p.pageid) as c from pages p
		join pageterms pt on p.pageid = pt.pageid
		where p.verified = 1;";
	$res = mysqli_query($conn, $sqlGetIndexedPageCount);
	$row = @ mysqli_fetch_assoc($res);
	
	if ($row != NULL) {
		$count = $row['c'];
		$sql = "delete from summarydata_i where item = 'IndexedPageCount';";
		$res = mysqli_query($conn, $sql);
		if (!$res) {
			echo "Error in $sql :" . mysqli_error($conn) . "\n";
		}
		$sql = "insert into summarydata_i (item, val, updatedate) values ('IndexedPageCount', $count, current_time());";
		$res = mysqli_query($conn, $sql);
		if (!$res) {
			echo "Error in $sql :" . mysqli_error($conn) . "\n";
		}
	}

?>