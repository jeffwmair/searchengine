<?php
ini_set('default_charset', 'UTF-8');
header('Content-Type: text/html;charset=utf-8');
?>
<!DOCTYPE html>

<html lang="en">
<head>
  	<meta charset="utf-8">
  	<title>Vector-Space Model Web Search Engine</title>
  	<meta name="description" content="Jeff Mair's Vector-Space Search Engine">
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
	<div id='searchbox'>
		<input type='text' id='txtSearch' placeholder='Search here' />
	</div>
	<div id='results'>

	</div>
	<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
	<script type='text/javascript'>
		var start;
		$(function() {
			$('#txtSearch').keypress(inputKeypress);
			$('#txtSearch').focus();
		});
		
		function inputKeypress(e) {
			if (e.which == 13) {
				loadResults();
			}
		}
		function loadResults() {
			var query = escape($('#txtSearch').val());
			$.getJSON("services/GetScoredPageIdsFromQuery.php?fast=1&q=" + query, showResults);
			$('#results').html('');
			$('#results').append('<img alt="loading" src="images/ajax-loader.gif" />');
			start = new Date().getTime();
		}
		
		function showResults(a, b, data) {
			$('#results').html('');
			var now = new Date().getTime();
			var time = Math.round((now - start) / 1000.0);
			var divResult = $('#results');
			divResult.append('<h3>Results:</h3>');
			divResult.append('<label class="querytime">Query time: '+time+'s</label>');
			divResult.append('<div style="clear:both"></div>');
			var d = data.responseJSON;

			$.each(d, function( index, value ) {

				var url = value[0];
				var title = value[1];
				var desc = value[2];
				var pr = value[3];
				var score = value[4];

				if (desc === null) desc = '';
				if (title === null) title = url;
				var result = $('<div class="r"></div>');
				var urlLink = $('<a href="' + url + '">' + title + '</a>')
				var urlLabel = $('<label class="url">'+url+'</label>');
				var pageDescription = $('<p>'+desc+'</p>');
				
				var tip = "Score is 0.8*A + 0.2*B, where A is the Cosine Similarity of the document and query vectors and B is the PageRank";
				var scoreLabel = $('<label>Score:&nbsp;&nbsp;&nbsp;' + parseFloat(score).toFixed(7) + '</label>');
				var prLabel = $('<label>PageRank:' + parseFloat(pr).toFixed(7) + '</label>');
				
				var linkBox = $('<div class="links"></div>');
				var scoreBox = $('<div class="scores" title="'+tip+'"></div>');
				
				scoreBox.append(scoreLabel);
				scoreBox.append(prLabel);
							
				linkBox.append(urlLink);
				linkBox.append(urlLabel);
				linkBox.append(pageDescription);				
			
				result.append(linkBox);
				result.append(scoreBox);
				result.append("<div style='clear:both'></div>");
				
				divResult.append(result);
			});
		}
		
	</script>
</body>