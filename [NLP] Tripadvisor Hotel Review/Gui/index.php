<?php

define("DB_NAME", "db_tripadvisor");
define("DB_USER", "root");
define("DB_PASSWD", "");
define("DB_ADDRESS", "127.0.0.1");
define("DB_PORT", "3306");

$GLOBALS['mysqli'] = mysqli_connect(DB_ADDRESS, DB_USER, DB_PASSWD, DB_NAME);
$GLOBALS['request_url'] = $_SERVER['REQUEST_SCHEME'] . '://localhost/';

error_reporting(0);

function print_header()
{
	global $request_url;
	?>
	<!DOCTYPE html>
	<html>
	<head>
		<title>Progetto TripAdvisor</title>
		<link rel="stylesheet" type="text/css" href="<?php echo $request_url . 'css/css-mint.min.css'; ?>">
		<link rel="stylesheet" href="<?php echo $request_url; ?>css/imagehover.min.css">
		<script type="text/javascript" src="<?php echo $request_url; ?>scripts/Chart.js"></script>
		<script type="text/javascript">
			function switch_model(model) {
				if (model === 'bernoulli'){
					document.getElementById('bernoulli_btn').className = 'btn warning';
					document.getElementById('multinomial_btn').className = 'btn warning line';
					document.getElementById('model_sentiment_multinomial').setAttribute('style', 'display: none;');
					document.getElementById('model_sentiment_bernoulli').setAttribute('style', 'display: block;');
					document.getElementById('model_overall_multinomial').setAttribute('style', 'display: none;');
					document.getElementById('model_overall_bernoulli').setAttribute('style', 'display: block;');
				}else{
					document.getElementById('multinomial_btn').className = 'btn warning';
					document.getElementById('bernoulli_btn').className = 'btn warning line';
					document.getElementById('model_sentiment_bernoulli').setAttribute('style', 'display: none;');
					document.getElementById('model_sentiment_multinomial').setAttribute('style', 'display: block;');
					document.getElementById('model_overall_bernoulli').setAttribute('style', 'display: none;');
					document.getElementById('model_overall_multinomial').setAttribute('style', 'display: block;');
				}
			}
		</script>
		<style>
			header.header {
				background-color: #589442;
				border-bottom: 1px solid #589442;
			}

			.thumbnail-title {
				bottom: 0;
			}

			#hcard-container {
				margin-top: 53px;
				margin-left: 10%;
				margin-right: 10%;
			}

			#index-bar {
				float: left;
				width: 100%;
				text-align: center;
				margin-bottom: 20px;
			}

			.hcard {
				display: block;
				width: 400px;
				height: 280px;
				position: relative;
				margin-bottom: 20px;
				margin-right: 20px;
				float: left;
			}

			.hcard-wrapper {
				display: block;
				width: 100%;
				height: 100%;
			}

			.hcard img {
				width: 100%;
				height: 100%;
				position: absolute;
				display: block;			
			}

			textarea {
				background-color: #fff;
				padding: 10px;
				margin: 8px 0;
				border: 1px solid #ccc;
				width: 280px;
				border-radius: 2px;
				font-weight: 300;
				font-size: .88em;
				color: #444;
				display: block;
				transition: all .2s ease-in;
			}

			textarea:focus {
				outline: 0;
				border-color: #5C9DED;
				box-shadow: 0 0 3px #4B8CDC;
			}

			figcaption > p {
				color: #ffffff;
				font-size: 130%;
				font-style: italic;
			}

			figcaption > h3 {
				color: #ffffff;
				text-shadow: -1px 0 #589442, 0 1px #589442, 1px 0 #589442, 0 -1px #589442;
			}

			a {
				color: #ffffff;
				text-decoration: underline;
			}

			a:hover {
				color: #000000;
			}

			td {
				text-align: center;
				vertical-align: middle;
			}

			.tr {
				border: 1px solid #589442;
			}


			th.thead {
				background-color: #589442;
				font-weight: 400;
				text-align: left;
				color: #ffffff;
				text-align: center;
			}

			table tbody>tr:nth-child(even) {
				background-color: #b2e89e;
			}

			/* TripAdvisor logo class */
			.sprite-rating_rr {
				background-image: url(/sprites/hotel_review_pack.png);
				background-position: left -192px;
				width: 90px;
				height: 18px;
				background-repeat: no-repeat;
				display: block;
				overflow: hidden;
				margin-right: 4px;
				line-height: 18px;
				float: right;
			}

			.sprite-rating_rr_fill {
				background-image: url(/sprites/hotel_review_pack.png);
				background-position: left -174px;
				background-repeat: no-repeat;
				line-height: 18px;
				display: block;
				width: 71px !important;
				height: 18px !important;
				vertical-align: text-top;
			}

		</style>
	</head>
	<header class="header">
		<div class="logo">
			<h1>TripAdvisor Bayesian Review Evaluator</h1>
		</div>
		<div class="nav-right">
			<nav class="navbar">
				<ul>
					<li><a href="<?php echo $request_url ?>results">Risultati generali</a></li>
					<li><a href="<?php echo $request_url ?>home/page/1">Testing Set Hotels</a></li>
					<!--<li><a href="<?php echo $request_url ?>review/new">Aggiungi nuova recensione</a></li>-->
					<li><a href="<?php echo $request_url ?>demo">Visualizza risultatii demo</a></li>
				</ul>
			</nav>
		</div>
	</header><br>
	<?php
}

function print_footer()
{
	?>
	</html>
	<?php
}

function rand_shuffle(&$vector)
{
	$max = count($vector);
	for($i = 0; $i < $max; $i++){
		$id = mt_rand(0, $max - 1);
		$tmp = $vector[$id];
		$vector[$id] = $vector[$i];
		$vector[$i] = $tmp;
	}
}

function print_page_navbar($prefix_url, $current, $first, $end)
{
	global $request_url;
	$prefix_url = $request_url . $prefix_url;
	echo '<div id="index-bar"><ul class="pagination">';
	if($current != $first){
		echo '<li class="prev"><a href="' . $prefix_url . ($current - 1) . '">Prev</a></li>';
	}
	for($i = 1; $i <= $end; $i++){
		if($current == $i){
			echo '<li class="active"><a href="' . $prefix_url . $current . '">' . $current . '</a></li>';
		}else{
			echo '<li><a href="' . $prefix_url . $i . '">' . $i . '</a></li>';
		}
	}
	if($current != $end){
		echo '<li class="next"><a href="' . $prefix_url . ($current + 1) . '">Next</a></li>';
	}
	echo '</ul></div>';
}

function get_hotel_count()
{
	global $mysqli;
	$query = "SELECT COUNT(DISTINCT hotel_id) AS count FROM tb_result;";
	$result = mysqli_result($mysqli, $query);
	if(!$result){
		echo "Query error: " . mysqli_error($mysqli) . " - LINE: " . __LINE__ . "<br>";
		return false;
	}
	$tmp = mysqli_fetch_assoc($result);
	return $tmp['count'];
}

function show_hotel_cards($page = 1)
{
	global $request_url;
	// load images from folder
	$image = scandir(__DIR__ . "/img");
	array_shift($image);
	array_shift($image);
	rand_shuffle($image);
	$hotel = get_hotel_list($page -1, 15);
	?>
	<div id="hcard-container">
		<?php
		print_page_navbar('home/page/', $page, 1, 20);
		$i = 0;
		foreach($hotel as $id => $value){
			?>
			<div class="hcard imghvr-fade">
				<div class="hcard-wrapper thumbnail">
					<img src="<?php echo $request_url; ?>img/<?php echo $image[$i] ?>">
					<span class="thumbnail-title">Hotel <?php echo $id ?></span>
					<a href="<?php echo $request_url . 'review/' . $id; ?>"><figcaption style="background-color: rgba(0, 0, 0, 0.7);">
						<!--<span class="rate sprite-rating_rr">
							<img class="sprite-rating_rr_fill rating_rr_fill" width="81" property="ratingValue" content="4.5" src="https://static.tacdn.com/img2/x.gif" alt="4.5 su 5 stelle">
						</span>-->
						<h3><?php echo 'Hotel ' . $id?></h3>
						<p>
						Recensioni analizzate: <?php echo $value['review_count']; ?><br>
						Recensioni positive: <?php echo $value['positive'] . ' (' . round(($value['positive'] / $value['review_count']) * 100.0, 2) . '%)'; ?><br>
						Recensioni negative: <?php echo $value['negative'] . ' (' . round(($value['negative'] / $value['review_count']) * 100.0, 2) . '%)'; ?><br>
						Sentiment medio: <?php echo $value['average_sentiment']; ?><br>
						Overall medio:  <?php echo $value['average_overall']; ?>
						</p>

					</figcaption></a>
				</div>
			</div>
			<?php
			$i++;
		}
	?>
	</div><br>
	<?php
	print_page_navbar('home/page/', $page, 1, 20);
}

function get_confusion_matrix($hotel_id = null)
{
	global $mysqli;
	$query =
		"SELECT
			overall,
			estimate_overall_bernoulli,
			estimate_overall_multinomial
		FROM tb_result";
	if(!empty($hotel_id)){
		$query .= " WHERE hotel_id = $hotel_id;";
	}else{
		$query .= ";";
	}
	$result = mysqli_query($mysqli, $query);
	if(!$result){
		echo "Query error: " . mysqli_error($mysqli) . " - LINE: " . __LINE__ . "<br>";
		return false;
	}
	$bernoulli_mat = array();
	$multinomial_mat = array();
	while(($tmp_result = mysqli_fetch_assoc($result)) !== NULL){
		if(empty($bernoulli_mat[$tmp_result['overall']][$tmp_result['estimate_overall_bernoulli']])){
			$bernoulli_mat[$tmp_result['overall']][$tmp_result['estimate_overall_bernoulli']] = 1;
		}else{
			$bernoulli_mat[$tmp_result['overall']][$tmp_result['estimate_overall_bernoulli']]++;
		}
		if(empty($multinomial_mat[$tmp_result['overall']][$tmp_result['estimate_overall_multinomial']])){
			$multinomial_mat[$tmp_result['overall']][$tmp_result['estimate_overall_multinomial']] = 1;
		}else{
			$multinomial_mat[$tmp_result['overall']][$tmp_result['estimate_overall_multinomial']]++;
		}
	}
	mysqli_free_result($result);
	return array(
		'bernoulli' => $bernoulli_mat,
		'multinomial' => $multinomial_mat
	);
}

function get_sentiment_confusion_matrix($hotel_id = null)
{
	$result = get_confusion_matrix($hotel_id);
	for($i = 1; $i <= 2; $i++){
		for($j = 1; $j <= 2; $j++){
			$result['bernoulli'][$i][$j] =
				(($result['bernoulli'][$i][$j] > 0) ? $result['bernoulli'][$i][$j] : 0);
			$result['multinomial'][$i][$j] =
				(($result['multinomial'][$i][$j] > 0) ? $result['multinomial'][$i][$j] : 0);
		}
	}
	return $result;
}

function get_overall_confusion_matrix($hotel_id = null)
{
	global $mysqli;
	mysqli_select_db($mysqli, 'db_tripadvisor_overall');
	$result = get_confusion_matrix($hotel_id);
	mysqli_select_db($mysqli, 'db_tripadvisor');
	for($i = 1; $i <= 5; $i++){
		for($j = 1; $j <= 5; $j++){
			$result['bernoulli'][$i][$j] =
				(($result['bernoulli'][$i][$j] > 0) ? $result['bernoulli'][$i][$j] : 0);
			$result['multinomial'][$i][$j] =
				(($result['multinomial'][$i][$j] > 0) ? $result['multinomial'][$i][$j] : 0);
		}
	}
	return $result;
}

function get_avg_overall($hotel_id)
{
	global $mysqli;
	mysqli_select_db($mysqli, 'db_tripadvisor_overall');
	if(empty($hotel_id)){
		$query =
			"SELECT AVG(overall) AS avarage_overall
			FROM tb_result;
			";
	}else{
		$query =
			"SELECT AVG(overall) AS avarage_overall
			FROM tb_result
			WHERE hotel_id = $hotel_id
			GROUP BY hotel_id
			";
	}
	$result = mysqli_query($mysqli, $query);
	if(!$result){
		echo $query;
		//echo "Query error: " . mysqli_error($mysqli) . " - LINE: " . __LINE__ . "<br>";
		return false;
	}
	$tmp_result = mysqli_fetch_assoc($result);
	mysqli_select_db($mysqli, 'db_tripadvisor');
	return round($tmp_result['avarage_overall'], 1);
}

function get_hotel_data($hotel_id)
{
	global $mysqli;
	if(empty($hotel_id)){
		$query =
			"SELECT
			tab1.review_count AS review_count,
			tab2.negative AS negative,
			tab3.average_sentiment AS average_sentiment
		FROM (
			SELECT
				COUNT(review_id) AS review_count
			FROM tb_result
		) tab1,
		(
			SELECT
				COUNT(review_id) AS negative
			FROM tb_result
			WHERE overall = 1
		) tab2,
		(
			SELECT
				avg(overall) AS average_sentiment
			FROM tb_result
		) tab3;";
	}else{
		$query =
			"SELECT
			tab1.review_count AS review_count,
			tab2.negative AS negative,
			tab3.average_sentiment AS average_sentiment
		FROM (
			SELECT
				COUNT(review_id) AS review_count
			FROM tb_result
			WHERE hotel_id = $hotel_id
			GROUP BY hotel_id
		) tab1,
		(
			SELECT
				COUNT(review_id) AS negative
			FROM tb_result
			WHERE overall = 1 AND hotel_id = $hotel_id
			GROUP BY hotel_id
		) tab2,
		(
			SELECT
				avg(overall) AS average_sentiment
			FROM tb_result
			WHERE hotel_id = $hotel_id
			GROUP BY hotel_id
		) tab3;";
	}
	$result = mysqli_query($mysqli, $query);
	if(!$result){
		echo $query;
		//echo "Query error: " . mysqli_error($mysqli) . " - LINE: " . __LINE__ . "<br>";
		return false;
	}
	$tmp_result = mysqli_fetch_assoc($result);
	return array(
		'review_count' => $tmp_result['review_count'],
		'negative' => $tmp_result['negative'],
		'average_sentiment' => ((round($tmp_result['average_sentiment']) > 1) ? 'positivo' : 'negativo'),
	);
}

function get_hotel_list($start, $offset)
{
	global $mysqli;
	// get for each hotel number of review
	$query =
		"SELECT
			tab1.hotel_id AS hotel_id,
			tab1.review_count AS review_count,
			tab2.negative AS negative
		FROM (
			SELECT
				hotel_id AS hotel_id,
				COUNT(review_id) AS review_count
			FROM tb_result
			GROUP BY hotel_id
		) tab1,
		(
			SELECT
				hotel_id AS hotel_id,
				COUNT(review_id) AS negative
			FROM tb_result
			WHERE overall = 1
			GROUP BY hotel_id
		) tab2,
		(
			SELECT
				hotel_id AS hotel_id,
				AVG(overall) AS avarage_sentiment
			FROM tb_result
			GROUP BY hotel_id
		) tab3
		WHERE tab1.hotel_id = tab2.hotel_id AND tab2.hotel_id = tab3.hotel_id
		GROUP BY tab1.hotel_id
		LIMIT $start, $offset
		;";
	$result = mysqli_query($mysqli, $query);
	if(!$result){
		echo "Query error: " . mysqli_error($mysqli) . " - LINE: " . __LINE__ . "<br>";
		return false;
	}
	$hotel = array();
	while(($tmp = mysqli_fetch_assoc($result)) !== null){
		$hotel[$tmp['hotel_id']] = array(
			'review_count' => $tmp['review_count'],
			'positive' => ($tmp['review_count'] - $tmp['negative']),
			'negative' => $tmp['negative'],
			'average_sentiment' => ((round($tmp['average_sentiment']) > 1) ? 'positivo' : 'negativo'),
		);
	}
	mysqli_free_result($result);
	mysqli_select_db($mysqli, 'db_tripadvisor_overall');
	$query =
		"SELECT
			tb_result.hotel_id AS hotel_id,
			AVG(tb_result.overall) AS average_overall
		FROM tb_result
		GROUP BY hotel_id
		LIMIT $start, $offset
		;";
	$result = mysqli_query($mysqli, $query);
	if(!$result){
		echo "Query error: " . mysqli_error($mysqli) . " - LINE: " . __LINE__ . "<br>";
		return false;
	}
	while(($tmp = mysqli_fetch_assoc($result)) !== null){
		$hotel[$tmp['hotel_id']]['average_overall'] = round($tmp['average_overall'], 1);
	}
	mysqli_free_result($result);
	return $hotel;
}

function print_new_review_form()
{
	?>
	<form action="review/add" method="post">
		<textarea name="review_text" style="width: 60%; margin-top: 63px; margin-left: 20%; margin-right: 20%;" rows="20" cols="100" placeholder="Inserisci di seguito la tua recensione..." autofocus></textarea>
		<input style="margin-right: 20%; float: right;" type="submit" value="Valuta recensione">
	</form>
	<?php
}

function print_hotel_result($hotel_id)
{
	$sum = function($vector){
		return array_reduce($vector, function($carry, $item){
			return $carry + $item;
		}, 0);
	};

	//$hotel = get_hotel_data($hotel_id);
	$conf_mat = get_sentiment_confusion_matrix($hotel_id);

	$precision['bernoulli'][2] = round($conf_mat['bernoulli'][2][2] / ($conf_mat['bernoulli'][2][2] + $conf_mat['bernoulli'][1][2]), 4) * 100;
	$recall['bernoulli'][2] = round($conf_mat['bernoulli'][2][2]/ ($conf_mat['bernoulli'][2][2] + $conf_mat['bernoulli'][2][1]), 4) * 100;

	$precision['bernoulli'][1] = round($conf_mat['bernoulli'][1][1] / ($conf_mat['bernoulli'][1][1] + $conf_mat['bernoulli'][2][1]), 4) * 100;
	$recall['bernoulli'][1] = round($conf_mat['bernoulli'][1][1]/ ($conf_mat['bernoulli'][1][1] + $conf_mat['bernoulli'][1][2]), 4) * 100;

	$f1_score['bernoulli'][2] = (2 * $precision['bernoulli'][2] * $recall['bernoulli'][2]) / ($precision['bernoulli'][2] + $recall['bernoulli'][2]);
	$f1_score['bernoulli'][1] = (2 * $precision['bernoulli'][1] * $recall['bernoulli'][1]) / ($precision['bernoulli'][1] + $recall['bernoulli'][1]);

	$avg_precision['bernoulli'] = round(($precision['bernoulli'][1] + $precision['bernoulli'][2]) / 2, 2);
	$avg_recall['bernoulli'] = round(($recall['bernoulli'][1] + $recall['bernoulli'][2]) / 2, 2);

	$avg_f1_score['bernoulli'] = round(($f1_score['bernoulli'][2] + $f1_score['bernoulli'][1]) / 2, 2);

	$precision['multinomial'][2] = round($conf_mat['multinomial'][2][2] / ($conf_mat['multinomial'][2][2] + $conf_mat['multinomial'][1][2]), 4) * 100;
	$recall['multinomial'][2] = round($conf_mat['multinomial'][2][2]/ ($conf_mat['multinomial'][2][2] + $conf_mat['multinomial'][2][1]), 4) * 100;

	$precision['multinomial'][1] = round($conf_mat['multinomial'][1][1] / ($conf_mat['multinomial'][1][1] + $conf_mat['multinomial'][2][1]), 4) * 100;
	$recall['multinomial'][1] = round($conf_mat['multinomial'][1][1]/ ($conf_mat['multinomial'][1][1] + $conf_mat['multinomial'][1][2]), 4) * 100;

	$f1_score['multinomial'][2] = (2 * $precision['multinomial'][2] * $recall['multinomial'][2]) / ($precision['multinomial'][2] + $recall['multinomial'][2]);
	$f1_score['multinomial'][1] = (2 * $precision['multinomial'][1] * $recall['multinomial'][1]) / ($precision['multinomial'][1] + $recall['multinomial'][1]);

	$avg_precision['multinomial'] = round(($precision['multinomial'][1] + $precision['multinomial'][2]) / 2, 2);
	$avg_recall['multinomial'] = round(($recall['multinomial'][1] + $recall['multinomial'][2]) / 2, 2);

	$avg_f1_score['multinomial'] = round(($f1_score['multinomial'][2] + $f1_score['multinomial'][1]) / 2, 2);

	$f1_score['bernoulli'] = round(($conf_mat['bernoulli'][2][2] + $conf_mat['bernoulli'][1][1]) / ($conf_mat['bernoulli'][2][2] + $conf_mat['bernoulli'][2][1] + $conf_mat['bernoulli'][1][1] + $conf_mat['bernoulli'][1][2]), 4) * 100;
	$f1_score['multinomial'] = round(($conf_mat['multinomial'][2][2] + $conf_mat['multinomial'][1][1]) / ($conf_mat['multinomial'][2][2] + $conf_mat['multinomial'][2][1] + $conf_mat['multinomial'][1][1] + $conf_mat['multinomial'][1][2]), 4) * 100;

	$general = get_hotel_data($hotel_id);
	$general['avarage_overall'] = get_avg_overall($hotel_id);
	?>
	<button id="multinomial_btn" class="btn warning line" style="float: right;" onclick="switch_model('multinomial');">Multinomiale</button>
	<button id="bernoulli_btn" class="btn warning" style="float: right;" onclick="switch_model('bernoulli');">Bernoulliano</button>

	<?php
	foreach(['bernoulli', 'multinomial'] as $model) {
		?>
		<div id="model_sentiment_<?php echo $model; ?>" style="display: <?php echo (($model === 'bernoulli') ? 'block' : 'none'); ?>">
			<div class="gr-row">
				<div class="col-span-3" style="display: flex; justify-content: center;">
					<div style="width: 636px; align-self: center; display: inline-block;">
						<?php
						if (!empty($hotel_id)) {
							echo '<h3>Hotel ' . $hotel_id . '</h3>';
						}
						?>
						<p>
							Recensioni analizzate: <?php echo $general['review_count']; ?><br>
							Recensioni
							positive: <?php echo ($general['review_count'] - $general['negative']) . ' (' . (round(($general['review_count'] - $general['negative']) / $general['review_count'], 4) * 100) . '%)'; ?>
							<br>
							Recensioni
							negative: <?php echo $general['negative'] . ' (' . (round($general['negative'] / $general['review_count'], 4) * 100) . '%)'; ?>
							<br>
							Sentiment medio: <?php echo $general['average_sentiment']; ?><br>
							Overall medio: <?php echo $general['avarage_overall']; ?>
						</p>
					</div>
				</div>
			</div>
			<h2 class="align-center" style="margin-top: 0;">Modello <?php echo (($model === 'bernoulli') ? 'Bernoulliano' : 'Multinomiale'); ?></h2>
			<h4 class="align-center" style="margin-bottom: 0;">Stima del sentiment</h4>
			<div class="gr-row">
				<div class="col-span-3" style="display: flex; justify-content: center;">
					<p style="width: 636px; align-self: center; display: inline-block;">
						Vero positivo: <?php echo round($conf_mat[$model][2][2] / $general['review_count'], 4) * 100; ?>
						%<br>
						Falso
						negativo: <?php echo round($conf_mat[$model][2][1] / $general['review_count'], 4) * 100; ?>%<br>
						Falso
						positivo: <?php echo round($conf_mat[$model][1][2] / $general['review_count'], 4) * 100; ?>%<br>
						Vero negativo: <?php echo round($conf_mat[$model][1][1] / $general['review_count'], 4) * 100; ?>
						%<br>
					</p>
				</div>
				<div class="col-span-3" style="display: flex; justify-content: center;">
					<p style="width: 636px; align-self: center; display: inline-block;">
						F1 score: <?php echo $f1_score[$model]; ?>%<br>
						<a style="color: #000000;" onclick="update_sentiment_<?php echo $model; ?>_graph(0);">Media F1
							score</a>: <?php echo $avg_f1_score[$model]; ?>
						%<br>
						Media Precision: <?php echo $avg_precision[$model]; ?>%<br>
						Media Recall: <?php echo $avg_recall[$model]; ?>%<br>
					</p>
				</div>
			</div>
			<div class="gr-row">
				<div class="col-span-3" style="height: 420px; display: flex; justify-content: center;">
					<table style="border: 0px; align-self: center;">
						<thead style="background-color: transparent;">
						<tr>
							<th style="background-color: transparent; border: 0px;"></th>
							<th class="thead">Previsione positiva</th>
							<th class="thead">Previsione negativa</th>
							<th style="border: 0px; background-color: transparent;"></th>
						</tr>
						</thead>
						<tr class="tr">
							<th class="thead"><a onclick="update_sentiment_<?php echo $model; ?>_graph(1)">Condizione positiva</a></th>
							<td><?php echo $conf_mat[$model][2][2]; ?></td>
							<td><?php echo $conf_mat[$model][2][1]; ?></td>
							<td><?php echo $conf_mat[$model][2][2] + $conf_mat[$model][2][1]; ?></td>
						</tr>
						<tr class="tr">
							<th class="thead"><a onclick="update_sentiment_<?php echo $model; ?>_graph(2)">Condizione negativa</a></th>
							<td><?php echo $conf_mat[$model][1][2]; ?></td>
							<td><?php echo $conf_mat[$model][1][1]; ?></td>
							<td><?php echo $conf_mat[$model][1][2] + $conf_mat[$model][1][1]; ?></td>
						</tr>
					</table>
				</div>
				<div class="col-span-3" style="height: 420px; display: flex; justify-content: center;">
					<canvas id="sentiment_<?php echo $model; ?>" style="align-self: center;" width="400" height="400"></canvas>
					<script>
						var cnvs_sentiment_<?php echo $model; ?> = document.getElementById('sentiment_<?php echo $model; ?>');
						var datasetsArray_<?php echo $model; ?> = [
							[<?php echo $avg_precision[$model] . ',' . $avg_recall[$model]; ?>],
							[<?php echo (round($conf_mat[$model][2][2] / ($conf_mat[$model][2][2] + $conf_mat[$model][2][1]), 4) * 100) . ',' .
								(round($conf_mat[$model][2][1] / ($conf_mat[$model][2][2] + $conf_mat[$model][2][1]), 4) * 100); ?>],
							[<?php echo (round($conf_mat[$model][1][2] / ($conf_mat[$model][1][1] + $conf_mat[$model][1][2]), 4) * 100) . ',' .
								(round($conf_mat[$model][1][1] / ($conf_mat[$model][1][1] + $conf_mat[$model][1][2]), 4) * 100); ?>]
						];
						var labelsArray_<?php echo $model; ?> = [
							['Media Precision', 'Media Recall'],
							['Vero positivo', 'Falso negativo'],
							['Falso positivo', 'Vero negativo']
						];
						var sentimentBackgroundColorArray_<?php echo $model; ?> = [
							['rgba(54, 162, 235, 0.2)', 'rgba(255, 99, 132, 0.2)'],
							['rgba(0, 230, 0, 0.2)', 'rgba(255, 99, 132, 0.2)']
						];
						var sentimentBorderColorArray_<?php echo $model; ?> = [
							['rgba(54, 162, 235, 1)', 'rgba(255, 99, 132, 1)'],
							['rgba(0, 230, 0, 1)', 'rgba(255, 99, 132, 1)']
						];
						var chart_sentiment_<?php echo $model; ?> = new Chart(cnvs_sentiment_<?php echo $model; ?>, {
							type: 'bar',
							data: {
								labels: labelsArray_<?php echo $model; ?>[0],
								datasets: [
									{
										backgroundColor: sentimentBackgroundColorArray_<?php echo $model; ?>[0],
										borderColor: sentimentBorderColorArray_<?php echo $model; ?>[0],
										borderWidth: 1,
										data: datasetsArray_<?php echo $model; ?>[0]
									}
								]
							},
							options: {
								responsive: false,
								scales: {
									yAxes: [{
										ticks: {
											max: 100,
											min: 0,
											stepSize: 10
										}
									}]
								}
							}
						});
						function update_sentiment_<?php echo $model; ?>_graph(id) {
							if (id == 0) {
								chart_sentiment_<?php echo $model; ?>.data.datasets[0].backgroundColor = sentimentBackgroundColorArray_<?php echo $model; ?>[0];
								chart_sentiment_<?php echo $model; ?>.data.datasets[0].borderColor = sentimentBorderColorArray_<?php echo $model; ?>[0];
							} else {
								chart_sentiment_<?php echo $model; ?>.data.datasets[0].backgroundColor = sentimentBackgroundColorArray_<?php echo $model; ?>[1];
								chart_sentiment_<?php echo $model; ?>.data.datasets[0].borderColor = sentimentBorderColorArray_<?php echo $model; ?>[1];
							}
							chart_sentiment_<?php echo $model; ?>.data.datasets[0].data = datasetsArray_<?php echo $model; ?>[id];
							chart_sentiment_<?php echo $model; ?>.data.labels = labelsArray_<?php echo $model; ?>[id];
							chart_sentiment_<?php echo $model; ?>.update();
						}
					</script>
				</div>
			</div>
		</div>
		<?php
	}
		?>


	<h4 class="align-center" style="margin-bottom: 0;">Stima overall</h4>
	<?php
	$conf_mat = get_overall_confusion_matrix($hotel_id);
	// calculate precision
	$precision = array();
	$recall = array();
	for($i = 1; $i <= 5; $i++){
		$precision['bernoulli'][$i] = round($conf_mat['bernoulli'][$i][$i] / $sum($conf_mat['bernoulli'][$i]), 4) * 100;
		$partial_sum = 0;
		for($j = 1; $j <= 5; $j++){
			$partial_sum += $conf_mat['bernoulli'][$j][$i];
		}
		$recall['bernoulli'][$i] = round($conf_mat['bernoulli'][$i][$i] / $partial_sum, 4) * 100;

		$precision['multinomial'][$i] = round($conf_mat['multinomial'][$i][$i] / $sum($conf_mat['multinomial'][$i]), 4) * 100;
		$partial_sum = 0;
		for($j = 1; $j <= 5; $j++){
			$partial_sum += $conf_mat['multinomial'][$j][$i];
		}
		$recall['multinomial'][$i] = round($conf_mat['multinomial'][$i][$i] / $partial_sum, 4) * 100;
	}

	$precision['bernoulli'] = round($sum($precision['bernoulli']) / 5, 2);
	$precision['multinomial'] = round($sum($precision['multinomial']) / 5, 2);

	$recall['bernoulli'] = round($sum($recall['bernoulli']) / 5, 2);
	$recall['multinomial'] = round($sum($recall['multinomial']) / 5, 2);

	for($i = 1; $i <= 5; $i++){
		$partial_sum = array();
		for($j = 1; $j <= 5; $j++){
			$partial_sum['bernoulli'] += $conf_mat['bernoulli'][$i][$j];
			$partial_sum['multinomial'] += $conf_mat['multinomial'][$i][$j];
		}
		for($j = 1; $j <= 5; $j++){
			$overall_datasets['bernoulli'][$i][] = round($conf_mat['bernoulli'][$i][$j]/$partial_sum['bernoulli'], 4) * 100;
			$overall_datasets['multinomial'][$i][] = round($conf_mat['multinomial'][$i][$j]/$partial_sum['multinomial'], 4) * 100;
		}
	}
	$overall_datasets['bernoulli'][0] = [$precision['bernoulli'], $recall['bernoulli']];
	$overall_datasets['multinomial'][0] = [$precision['multinomial'], $recall['multinomial']];

	$partial_sum_num = 0;
	$partial_sum_dem = 0;
	for($i = 1; $i <= 5; $i++){
		$partial_sum_dem += $sum($conf_mat['bernoulli'][$i]);
		$partial_sum_num += $conf_mat['bernoulli'][$i][$i];
	}
	$f1_score['bernoulli'] = round($partial_sum_num / $partial_sum_dem, 4) * 100;

	$partial_sum_num = 0;
	$partial_sum_dem = 0;
	for($i = 1; $i <= 5; $i++){
		$partial_sum_dem += $sum($conf_mat['multinomial'][$i]);
		$partial_sum_num += $conf_mat['multinomial'][$i][$i];
	}
	$f1_score['multinomial'] = round($partial_sum_num / $partial_sum_dem, 4) * 100;

	foreach(['bernoulli', 'multinomial'] as $model) {
		?>
		<div id="model_overall_<?php echo $model ?>" style="display: <?php echo (($model === 'bernoulli') ? 'block' : 'none'); ?>">
			<div class="gr-row">
				<div class="col-span-3" style="display: flex; justify-content: center;">
					<p style="width: 636px; align-self: center; display: inline-block;">
						F1 score: <?php echo $f1_score[$model]; ?>%<br>
						<a style="color: #000000;" onclick="update_overall_<?php echo $model ?>_graph(0);">Media F1
							score</a>: <?php echo round((2 * $precision[$model] * $recall[$model]) / ($precision[$model] + $recall[$model]), 2); ?>
						%<br>
						Media Precision: <?php echo $precision[$model]; ?>%<br>
						Media Recall: <?php echo $recall[$model]; ?>%<br>
					</p>
				</div>
			</div>
			<div style="display: flex; justify-content: center;">
				<canvas id="overall_<?php echo $model ?>" style="align-self: center;" width="400" height="400"></canvas>
				<script>
					var cnvs_overall_<?php echo $model ?> = document.getElementById('overall_<?php echo $model ?>');

					var overallDatasetsArray_<?php echo $model ?> = <?php echo json_encode($overall_datasets[$model])?>;
					var overallLabelsArray_<?php echo $model ?> = [
						['Media Precision', 'Media Recall'],
						['1', '2', '3', '4', '5']
					];
					var overallBackgroundColorArray_<?php echo $model ?> = [
						['rgba(54, 162, 235, 0.2)', 'rgba(255, 99, 132, 0.2)'],
						['rgba(255, 77, 77, 0.2)', 'rgba(51, 153, 255, 0.2)', 'rgba(0, 204, 102, 0.2)', 'rgba(255, 153, 0, 0.2)', 'rgba(255, 51, 204, 0.2)']
					];
					var overallBorderColorArray_<?php echo $model ?> = [
						['rgba(54, 162, 235, 1)', 'rgba(255, 99, 132, 1)'],
						['rgba(255, 77, 77, 1)', 'rgba(51, 153, 255, 1)', 'rgba(0, 204, 102, 1)', 'rgba(255, 153, 0, 1)', 'rgba(255, 51, 204, 1)']
					];
					var chart_overall_<?php echo $model ?> = new Chart(cnvs_overall_<?php echo $model ?>, {
						type: 'bar',
						data: {
							labels: overallLabelsArray_<?php echo $model ?>[0],
							datasets: [
								{
									backgroundColor: overallBackgroundColorArray_<?php echo $model ?>[0],
									borderColor: overallBorderColorArray_<?php echo $model ?>[0],
									borderWidth: 1,
									data: overallDatasetsArray_<?php echo $model ?>[0]
								}
							]
						},
						options: {
							responsive: false,
							scales: {
								yAxes: [{
									ticks: {
										max: 100,
										min: 0,
										stepSize: 10
									}
								}]
							}
						}
					});
					function update_overall_<?php echo $model; ?>_graph(id) {
						if (id == 0) {
							chart_overall_<?php echo $model; ?>.data.datasets[0].backgroundColor = overallBackgroundColorArray_<?php echo $model ?>[0];
							chart_overall_<?php echo $model; ?>.data.datasets[0].borderColor = overallBorderColorArray_<?php echo $model ?>[0];
							chart_overall_<?php echo $model; ?>.data.labels = overallLabelsArray_<?php echo $model ?>[0];
						} else {
							chart_overall_<?php echo $model; ?>.data.datasets[0].backgroundColor = overallBackgroundColorArray_<?php echo $model ?>[1];
							chart_overall_<?php echo $model; ?>.data.datasets[0].borderColor = overallBorderColorArray_<?php echo $model ?>[1];
							chart_overall_<?php echo $model; ?>.data.labels = overallLabelsArray_<?php echo $model ?>[1];
						}
						chart_overall_<?php echo $model; ?>.data.datasets[0].data = overallDatasetsArray_<?php echo $model ?>[id];
						chart_overall_<?php echo $model; ?>.update();
					}
				</script>
			</div>
			<table style="border: 0px; margin-top: 20px; margin-left: 20%; margin-right: 20%;">
				<thead style="background-color: transparent;">
				<tr>
					<th style="background-color: transparent; border: 0px;"></th>
					<th class="thead">Previsione overall 1</th>
					<th class="thead">Previsione overall 2</th>
					<th class="thead">Previsione overall 3</th>
					<th class="thead">Previsione overall 4</th>
					<th class="thead">Previsione overall 5</th>
					<th style="background-color: transparent; border: 0px;"></th>
				</tr>
				</thead>
				<?php
				for ($i = 0; $i < count($conf_mat['bernoulli']); $i++) {
					?>
					<tr class="tr">
						<th class="thead"><a onclick="update_overall_<?php echo $model ?>_graph('<?php echo($i + 1) ?>')">Condizione
								overall <?php echo($i + 1); ?></a></th>
						<?php
						for ($j = 0; $j < count($conf_mat[$model][($i + 1)]); $j++) {
							?>
							<td><?php echo $conf_mat[$model][($i + 1)][($j + 1)]; ?></td>
							<?php
						}
						?>
						<td><?php echo $sum($conf_mat[$model][($i + 1)]); ?></td>
					</tr>
					<?php
				}
				?>
			</table>
		</div>
		<?php
	}
}

function print_demo_result()
{
	global $mysqli;
	$query =
		"
		SELECT
			id_review AS id_review,
			estimate_sentiment AS estimate_sentiment,
			estimate_overall AS estimate_overall
		FROM tb_result_demo;
		";
	$result = mysqli_query($mysqli, $query);
	if(!$result){
		echo "Query error: " . mysqli_error($mysqli) . " - LINE: " . __LINE__ . "<br>";
	}
	$hotel = array();
	while(($tmp = mysqli_fetch_assoc($result)) !== null){
		$hotel[] = $tmp;
	}
	?>
	<div class="gr-row">
	<div class="col-span-6" style="display: flex; justify-content: center;">
	<table style="align-self: center; border: 0px; margin-top: 20px; margin-left: 20%; margin-right: 20%;">
		<thead style="background-color: transparent;">
			<tr>
				<th class="thead">ID recensione</th>
				<th class="thead">Sentiment stimato</th>
				<th class="thead">Overall stimato</th>
			</tr>
		</thead>
		<?php
		for($i = 0; $i < count($hotel); $i++){
			?>
			<tr class="tr">
				<td><?php echo $hotel[$i]['id_review']; ?></td>
				<td><?php echo $hotel[$i]['estimate_sentiment']; ?></td>
				<td><?php echo $hotel[$i]['estimate_overall']; ?></td>
			</tr>
			<?php
		}
		?>
	</table>
	</div>
	</div>
	<?php
}

if(preg_match('/home\/page\/([0-9]+)$/', $_GET['route'], $matches) === 1){
	print_header();
	show_hotel_cards($matches[1]);
	print_footer();
}elseif(preg_match('/review\/new/', $_GET['route']) === 1) {
	print_header();
	print_new_review_form();
	print_footer();
}elseif(preg_match('/review\/([0-9]+)$/', $_GET['route'], $matches) === 1) {
	print_header();
	print_hotel_result($matches[1]);
	print_footer();
}elseif(preg_match('/results/', $_GET['route']) === 1) {
	print_header();
	print_hotel_result(null);
	print_footer();
}elseif(preg_match('/demo/', $_GET['route']) === 1) {
	print_header();
	print_demo_result();
	print_footer();
}else{
	print_header();
	show_hotel_cards();
	print_footer();
}

