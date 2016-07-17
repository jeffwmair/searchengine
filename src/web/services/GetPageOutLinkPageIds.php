<?php
	require_once './utils.php';

	$d = getJsonRequestData();
	$conn = connect();
	
	$jsonOut = "[";
	for ($i = 0; $i < count($d); $i++) {
		$item = $d[$i];
		$pageId = $item['id'];
		$sql = "select pl.pageId as 'id', pl.destPageId as 'destId' from pagelinks " 
			. " pl join pages p on pl.destPageId = p.pageId "
			. " where p.verified = 1 and pl.pageId = $pageId;";
		$res = execSql($conn, $sql);
		while ($row = @ mysqli_fetch_assoc($res))
		{
			if ($jsonOut != '[') $jsonOut .= ',';
			$jsonOut .= stripslashes(json_encode($row));
		}
		
	}
	$jsonOut .= "]";	
	echo $jsonOut;	
?>