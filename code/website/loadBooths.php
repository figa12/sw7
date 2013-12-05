<?php
	include("connect.php");
	$testvar = array();



	if(!($sqli = $mysqli->prepare('SELECT id, name from booths')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}


	if (!$sqli->execute()) 
	{
		echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	if(!$sqli->bind_result($_ids,$_names))
	{
		echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	while ($sqli->fetch()) {
		$tmpArr = array(
			"id" => $_ids,
			"name" => $_names);
		array_push($testvar, $tmpArr);
	}

	$mysqli->close();

	$returnvar = json_encode($testvar);

	echo $returnvar;
?>