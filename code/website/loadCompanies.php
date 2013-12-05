<?php
	include("connect.php");
	$testvar = array();



	if(!($sqli = $mysqli->prepare('SELECT id,name,logo from companies')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}


	if (!$sqli->execute()) 
	{
		echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	if(!$sqli->bind_result($_ids,$_companies,$_logos))
	{
		echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	while ($sqli->fetch()) {
		$tmpArr = array(
			"id" => $_ids,
			"name" => $_companies,
			"logo" => $_logos);
		array_push($testvar, $tmpArr);
	}

	$mysqli->close();

	$returnvar = json_encode($testvar);

	echo $returnvar;
?>