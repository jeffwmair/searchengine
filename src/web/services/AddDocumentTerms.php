<?php
	require_once './utils.php';
	if (!checkPass()) return;
	$json = getJsonRequestData();

	$skipTerms = getSkipTerms();
	$terms = $json['ts'];
	$termsStemmed = array();
	for($i = 0; $i < count($terms); $i++) {
		$termInfo = $terms[$i];
		$term = PorterStemmer::Stem($termInfo['t']);
		$tf = $termInfo['tf'];
		if (!in_array($term, $skipTerms)) {
			if (!isset($termsStemmed[$term])) {
				$termsStemmed[$term] = $tf;
			}
			else {
				$current = $termsStemmed[$term];
				$termsStemmed[$term] = $current + $tf;
			}	
		}
	}
	
	$pageId = $json['p'];
		
	$termIds = Array();	
	$conn = connect();
	mysqli_set_charset ($conn, "utf8");
		
	/* put all the terms into the pageterm_input table for processing */
	$sql = "insert ignore into pageterm_input (pageId, term, tf) values ";
	$sqlvalues = "";
	foreach($termsStemmed as $term => $tf) {
		if ($sqlvalues != "") $sqlvalues .= ",";
		$sqlvalues .= " ($pageId, '$term', $tf)";
	}
	$sql .= $sqlvalues . ";";
	
	execSql($conn, $sql);	
	
	$sql = "call insert_terms($pageId);";
	execSql($conn, $sql);	

?>