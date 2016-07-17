<?php
ini_set('default_charset', 'UTF-8');
header('Content-Type: text/html;charset=utf-8');
?>
<!DOCTYPE html>

<html lang="en">
<head>
  	<meta charset="utf-8">
  	<title>Submit a site for indexing</title>
  	<meta name="description" content="Submit a website for indexing">
  	<meta name="author" content="Jeff Mair">
	<link rel="stylesheet" type="text/css" href="css/styles.css">
</head>
<body>
	<div id='nav'>
		<a href='WebSearch.php'>Search</a>
		<a href='Stats.php'>Stats</a>
		<a href='SubmitSite.php'>Submit Site</a>
		<div id='navBorder' ></div>
	</div>
	<p>Enter a page url to index:</p>
	<p style='color:red;font-size:0.8em;font-style:italic'>Note: indexing may not be turned on currently, so your URL may not be indexed very soon as this Search Engine is currently experimental</p>
	<form action="services/AddNewUrl.php" method="post">
		<input type='text' name='url' id='txtSubmitUrl' placeholder="http://..." autocomplete="off" />
		<input type='hidden' name='indexrequest' id='txtIndexRequest' value='1' />
		<input type='submit' id='btnSubmitUrl' value='Submit' style='margin-left:10px' />
	</form>
	<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
	<script type='text/javascript'>
		$(function() {
			$('#txtSubmitUrl').focus();
		});
	</script>
</body>