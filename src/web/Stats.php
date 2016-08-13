<?php
ini_set('default_charset', 'UTF-8');
header('Content-Type: text/html;charset=utf-8');
?>
<!DOCTYPE html>

<html lang="en">
<head>
  	<meta charset="utf-8">
  	<title>Search Engine Stats</title>
  	<meta name="description" content="Provides some information about number of urls crawled, database size, and so on">
  	<meta name="author" content="Jeff Mair">
	<link rel="stylesheet" type="text/css" href="css/styles.css">
</head>
<body>
	<div id='nav'>
		<a href='WebSearch.php'>Search</a>
		<a href='Stats.php'>Stats</a>
		<a href=''>Submit Site (gone)</a>
		<div id='navBorder' ></div>
	</div>
	<?php

		require_once './services/utils.php';
		
		$d = getJsonRequestData();
		$conn = connect();
		mysqli_set_charset ($conn, "utf8");

		echo "<h1>Search Engine Stats</h1>";
		getNumPagesIndexed($conn, $num, $date);
		echo "<p>Total pages indexed: $num ($date)</p>";
		$numTerms = getTotalTerms($conn);
		echo "<p>Total distinct terms: $numTerms</p>";
		$postingTerms = getTotalPostingTerms($conn);
		echo "<p>Total posting terms: $postingTerms</p>";
		
		$dbSize = (int)getDbSize($conn);
		echo "<p>Database Size (in MB): $dbSize</p>";
		
		?>
		<!-- <h2>Crawlers</h2>
		<table>
			<tr>
				<th>Crawler #</th>
				<th>Mean Time to Page First-Crawl</th>
				<th>Median Time to Page First-Crawl</th>
				<th>Variance Time to Page First-Crawl</th>
			</tr>
			<tr>
				<td>1</td>
				<td>?</td>
				<td>?</td>
				<td>?</td>
			</tr>
		</table> -->
		
		<?php
		
		$items = array();
		$tableSize = 25;
		getTopRankRanks($conn, $items, $tableSize);
		echo "<h2>Top $tableSize Page Rank scores</h2>";
		echo "<table><tr><th>Pos</th><th>PageRank</th><th>Site</th></tr>";
		$i = 1;
		foreach($items as $item) {
			$pr = $item[0];
			$url = $item[1];
			$title = $item[2];
			if ($title == NULL) $title = $url;
			echo "<tr><td>$i</td><td>$pr</td><td><a href='$url'>$title</a></td></tr>";
			$i++;
		}
		echo "</table>";

		
		function getNumPagesIndexed($conn, &$num, &$date) {
			$sql = "select val, updatedate from summarydata_i where item = 'IndexedPageCount';";
			$res = execSql($conn, $sql);
			if ($res != NULL) {
				$row = @ mysqli_fetch_assoc($res);
				$num = $row['val'];
				$date = $row['updatedate'];
			}
		}
		
		function getTotalTerms($conn) {
			$sql = "select count(*) as t from terms;";
			return sqlQuerySingleVal($conn, $sql, 't', 0);
		}
		
		function getDistinctDomains($conn) {
			$sql = "select count(*) as d from domains;";
			return sqlQuerySingleVal($conn, $sql, 'd', 0);
		}
		
		function getTopRankRanks($conn, &$items, $limit) {
			$sql = "select url, title, pagerank as pr from pages where pagerank > 0.00000 order by pagerank desc limit $limit;";
			$res = execSql($conn, $sql);
			while($row = @ mysqli_fetch_assoc($res)) {
				$url = $row['url'];
				$rank = $row['pr'];
				$title = $row['title'];
				$innerArr = array();
				array_push($innerArr, $rank);
				array_push($innerArr, $url);
				array_push($innerArr, $title);
				array_push($items, $innerArr);
			}			
		}
		
		function getTotalPostingTerms($conn) {
			$sql = "select count(*) as pt from pageterms;";
			return sqlQuerySingleVal($conn, $sql, 'pt', 0);
		}
		
		function getDbSize($conn) {
			$sql = "SELECT sum( data_length + index_length ) / 1024 / 1024 as sz FROM information_schema.TABLES where table_schema like '%searchengine%' GROUP BY table_schema;";
			return sqlQuerySingleVal($conn, $sql, 'sz', 0);
		}
	
	?>
</body>
</html>