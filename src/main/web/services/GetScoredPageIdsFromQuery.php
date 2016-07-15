<?php
	
	require_once './utils.php';
	header('Content-Type: text/json; charset=utf-8');
	ini_set('max_execution_time', 120); 
	$conn = connect();
	mysqli_set_charset ($conn, "utf8");
	
	$useFastAlg = $_GET['fast'];
	$queryString = $_GET['q'];
	$queryOrig = explode(' ', $queryString);
	$query = array();
	foreach($queryOrig as $i => $term) {
		$query[$i] = strtolower(PorterStemmer::Stem($term));
	}

	$numDocs = getNumDocsIndexed($conn);
	
	$documents = array();
	$documentDetails = array();
	$allTerms = array();
	
	/* map of distinct query terms and their frequencies - used in the following calculations */
	$queryTermMap = array();
	foreach ($query as $i => $qt) {
		if (!array_key_exists($qt, $queryTermMap)) {
			$queryTermMap[$qt] = 0;
		}
		$queryTermMap[$qt] = $queryTermMap[$qt] + 1;
		
		// this will populate temp session data in qry_pageterms
		getDocIdsContainingQueryTerm($conn, $qt, $documents, $documentDetails);
	} 
	
	// start collecting all the distinct terms needed by the query vector
	// that is, every term used in all the docs containing the query terms
	$allTermIds = array();
	foreach($documents as $pageId) {
		getAllTermsUsedInDoc($conn, $pageId, $allTermIds);
	}
	
	ksort($allTermIds);

	// we are workign with ids here, so get the ids for the query terms
	$queryTermIds = getTermIdsFromQuery($conn, $query);

	/* get the vector for the query */
	// $fastSearch = True;
	$fastSearch = $useFastAlg == '1';
	if ($fastSearch == False) {
		$queryVector = getQueryVectorFromQueryTerms($allTermIds, $queryTermIds);		
	}


	/* 	
	* now score the docs one at a time (as opposed to getting all their vectors first, which is too expensive)
	 */
	$documentScores = array();
	$pageIds = array();
	$batch_size = 5; 
	foreach($documents as $i => $pageId) {
		// try to get blocks of pages scored at a time instead of just 1...
		array_push($pageIds, $pageId);
		if (count($pageIds) == $batch_size) {
			if ($fastSearch) {
				fastScorePages($conn, $queryTermIds, $allTermIds, $pageIds, $numDocs, $documentScores, $documentDetails);				
			}
			else {
				scorePages($conn, $queryVector, $allTermIds, $pageIds, $numDocs, $documentScores);	
			}

			unset($pageIds);
			$pageIds = array();
		}
	}
	
	if (count($pageIds) > 0) {
		if ($fastSearch) {
			fastScorePages($conn, $queryTermIds, $allTermIds, $pageIds, $numDocs, $documentScores, $documentDetails);				
		}
		else {
			scorePages($conn, $queryVector, $allTermIds, $pageIds, $numDocs, $documentScores);	
		}
		unset($pageIds);//done
	}
	
		
	/* now we have all the scored docs */
	
	arsort($documentScores);
		
	$documentScoresLimited = array();
	$i = 1;
	$maxResults = 50;
	foreach($documentScores as $pageId => $score) {
		$details = $documentDetails[$pageId];
		array_push($details, $score);
		$documentScoresLimited[$i++] = $details;
		if ($i == $maxResults) break;
	}

	/* DONE! */
	/*
	0 = url,
	1 = title,
	2 = desc,
	3 = pr,
	4 = score (including pr)
	*/
	$jsonOut = json_encode($documentScoresLimited);
	if (!$jsonOut) echo json_last_error();
	echo $jsonOut;
	
	function scorePages($conn, $queryVector, $allTermIds, $pageIds, $numDocs, &$documentScores) {
		
		$docVectors = getDocumentVectorsForPages($conn, $allTermIds, $pageIds, $numDocs);
			
		foreach($docVectors as $pageId => $dv) {
			$score = calcCosineSimilarity($queryVector, $dv);
			/* here we incorporate in the PageRank */
			$score = 0.8 * $score + 0.2 * $documentDetails[$pageId][3];
			if ($score > 0.001) {
				$documentScores[$pageId] = $score;
			}
		}
	}
	
	function fastScorePages($conn, $queryTerms, $allTermIds, $pageIds, $numDocs, &$documentScores, $docDetails) {
		$interimScores = array();
		$docsTermIds = getTermsForDocuments($conn, $pageIds);
		foreach($queryTerms as $qt => $f) {
			// w = tf * idf
			// tf = if f == 0, 0; else 1 + log10(f)
			// idf = log10(N/df)
			$q_tf = ($f == 0) ? 0 : (1 + log($f, 10));
			$q_idf = log(($numDocs/ $allTermIds[$qt]), 10);
			
			$w_tq = $q_idf * $q_tf;
			foreach($pageIds as $pageId) {
				$docTermIds = $docsTermIds[$pageId];
				$tf = $docTermIds[$qt];
				$d_tf = ($tf == 0) ? 0 : (1 + log($tf, 10));
				$w_td = $q_idf * $d_tf;
				// logMsg($conn, 'PageId:' . $pageId . ', TermId:' . $qt . ', Wtd:' . $w_td);
				if (count($interimScores) == 0 || $interimScores[$pageId] == NULL) {
					$pageQtScore = array();
					$pageQtScore[$qt] = $w_td;
					$interimScores[$pageId] = $pageQtScore;
				}
				else {
					$pageQtScore = $interimScores[$pageId];
					$pageQtScore[$qt] = $w_td;
					$interimScores[$pageId] = $pageQtScore;
				}
			}
		}
		
		foreach($interimScores as $pageId => $pageQtScore) {
			foreach($pageQtScore as $qt => $score) {
				// $dt = $pageIds[$pageId];
				$length_doc = count($docsTermIds[$pageId]);//count($pageIds[$pageId]);
				if ($length_doc > 0) {
					// $nrmlScore = $score / ((1.0)*$length_doc);
					// pagerank
					$score = 0.8 * $score + 0.2 * $docDetails[$pageId][3];
					// add to the overall score of this page
					// $documentScores[$pageId] = $nrmlScore + $documentScores[$pageId];
					// Update 2013-12-28: remoevd the normalization part as it didn't make sense
					if (count($documentScores) == 0 || $documentScores[$pageId] == null) {
				        $documentScores[$pageId] = 0;
					}
					$documentScores[$pageId] = $documentScores[$pageId] + $score;
				}
			}
		}
	}
	
	function logMsg($conn, $msg) {
		$sql = "insert into summarydata_i (item, val, updatedate) values ('$msg', 0, current_time());";
		execSql($conn, $sql);
	}
	

	function getTermIdsFromQuery($conn, $queryTerms) {
		
		$queryParam = "(";
		foreach($queryTerms as $term) {
			if ($queryParam != "(") $queryParam .= ",";
			$queryParam .= "'" . $term . "'";
		}
		$queryParam .= ")";
		$sql = "select termId, term from terms where term in $queryParam;";
		$res = execSql($conn, $sql);
		
		$queryTermIds = array();
		$tempQueryTerms = array();
		while ($row = @ mysqli_fetch_assoc($res))
		{
			$term = $row['term'];
			$termId = $row['termId'];					
			$tempQueryTerms[$term] = $termId;
		}
		
		// now get the term ids and number of instances of the termid in the query -- this is needed for the 
		// query vector calculation
		if (count($queryTerms) > 0 && count($tempQueryTerms) > 0) {
			foreach($queryTerms as $term) {
				$termId = $tempQueryTerms[$term];
				if (!isset($queryTermIds[$termId])) {
					$queryTermIds[$termId] = 0;
				}
				$queryTermIds[$termId] = $queryTermIds[$termId] + 1;
			}
		}
		
		return $queryTermIds;
	}
	
	function getAllTermsUsedInDoc($conn, $docId, &$terms) {

		/* I think i don't need to order the terms here, as long as my code iterates through
		them in order consistently; the ordering in the query slows it down significantly */
		// $sql = "select t.termId, t.document_frequency 
		// 	from pageterms pt
		// 	join terms t on pt.termId = t.termId
		// 	where pageId = $docId
		// 	order by term;";
		$sql = "select t.termId, t.document_frequency 
			from pageterms pt
			join terms t on pt.termId = t.termId
			where pageId = $docId;";
		$res = execSql($conn, $sql);		
		while ($row = @ mysqli_fetch_assoc($res))
		{
			$termId = $row['termId'];
			$df = $row['document_frequency'];
			if (!isset($terms[$termId])) {
				$terms[$termId] = (int)$df;
			}
		}
	}

	function getDocIdsContainingQueryTerm($conn, $term, &$documents, &$documentDetails) {
		
		$sql = "select p.pageId, p.url, p.title, p.description, p.pagerank
		    from terms t
			join pageterms pt on t.termid = pt.termid
			join pages p on pt.pageid = p.pageid
			where t.term = '$term'";

		$res = execSql($conn, $sql);			
		while ($row = @ mysqli_fetch_assoc($res))
		{
			$pageId = $row['pageId'];
			if (!in_array($pageId, $documents)) {
				$url = $row['url'];
				$desc = $row['description'];
				$title = $row['title'];
				$pr = $row['pagerank'];
				$details = array();
				$details[0] = $url;
				$details[1] = $title;
				$details[2] = $desc;
				$details[3] = $pr;
				$documentDetails[(int)$pageId] = $details;
				array_push($documents, (int)$pageId);
			}
		}
	}
		
	/**
	* Calculate cosine similarity between two vectors
	* @param $queryVector - array of vector components
	* @param $docVector - array of vector components
	*/
	function calcCosineSimilarity($queryVector, $docVector) {
		
		$numerator = 0.0;
		$denomQry = 0.0;
		$denomDoc = 0.0;
		
		$sz = count($queryVector);
			
		for ($i = 0; $i < $sz; $i++) {
			$numerator += ($queryVector[$i] * $docVector[$i]);
			$denomDoc += pow($docVector[$i], 2);
			$denomQry += pow($queryVector[$i], 2);
		}
		
		$denom = sqrt($denomDoc * $denomQry);
		$similarityScore = $numerator / $denom;

		return $similarityScore;
	}
	
	/**
	*	Gets an array that represents a document vector
	*/
	function getDocumentVectorsForPages($conn, $allTermsWithDfs, $pageIds, $numDocs) {
		
		$docsVects = array();
		$docsTermIds = getTermsForDocuments($conn, $pageIds);
		foreach($docsTermIds as $pageId => $docTermIds) {
			$pageVector = array();
			$i = 0;
			foreach($allTermsWithDfs as $termId => $df) {
				if ($docTermIds[$termId] == NULL) {
					$pageVector[$i++] = 0;
				}
				else {
					// for performance, made this all inline, even though its ugly
					// w = tf * idf
					// tf = if f == 0, 0; else 1 + log10(f)
					// idf = log10(N/df)
					$pageVector[$i++] = (1 + log($docTermIds[$termId], 10)) * (log(($numDocs / $df), 10));
				}
			}
			$docsVects[$pageId] = $pageVector;
		}	
		return $docsVects;
	}
	
	function getTermsForDocuments($conn, $pageIds) {
		
		$pageIdsIn = '(';
		foreach($pageIds as $pageId) {
			if ($pageIdsIn != '(') $pageIdsIn .= ',';
			$pageIdsIn .= $pageId;
		}
		$pageIdsIn .= ')';
		
		$sql = "select pageId, termId, term_frequency from pageterms where pageId in $pageIdsIn;";
		$res = execSql($conn, $sql);
		$docsTerms = array();
		while ($row = @ mysqli_fetch_assoc($res))
		{
			$pageId = $row['pageId'];
			$termId = $row['termId'];
			$tf = $row['term_frequency'];
			$detail = array();
			if (!isset($docsTerms[$pageId])) {
				$detail[$termId] = (int)$tf;
				$docsTerms[$pageId] = $detail;
			}
			else {
				$detail = $docsTerms[$pageId]; // already there, so get it and add the new term
				$detail[$termId] = (int)$tf;
				$docsTerms[$pageId] = $detail; // and add it back
			}
		}
		return $docsTerms;
	}
	
	/**
	* Gets a query vector (array) from a sorted array of terms
	*
	*/
	function getQueryVectorFromQueryTerms($allTermIds, $queryTermIds) {

		$queryScore = Array();
		foreach($allTermIds as $termId => $df) {
			if (isset($queryTermIds[$termId])) {
				$score = 1 + log($queryTermIds[$termId], 10);
				array_push($queryScore, $score);
			}
			else {
				array_push($queryScore, 0.0);
			}
		}

		return $queryScore;
	}
	
	function getNumDocsIndexed($conn) {
		$num = 0;
		$sql = "select val from summarydata_i where item = 'IndexedPageCount';";
		$num = sqlQuerySingleVal($conn, $sql, 'val', 0);
		return $num;
	}
	
?>
