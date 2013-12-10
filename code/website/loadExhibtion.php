<?php
	include("connect.php");

	$exhibitionid = $_GET['name'];
	$exhibit = array();
	$booths = array();
	$coordinates = array();
	$edges = array();

//LOAD EXHIBITION
	if(!($sqli = $mysqli->prepare('SELECT id, name,address,zip,country,description,logo from exhib where name = "'.$exhibitionid.'"')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	if (!$sqli->execute()) 
	{
		echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
	}
	if(!$sqli->bind_result($_exhibid,$_exhibname,$_exhibaddress,$_exhibzip,$_exhibcountry,$_exhibdescription,$_exhiblogo))
	{
		echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
	}
	while ($sqli->fetch()) {
		$exhibit = array(
			"dbid" => $_exhibid,
			"name" => $_exhibname,
			"address" => $_exhibaddress,
			"zip" => $_exhibzip,
			"country" => $_exhibcountry,
			"description" => $_exhibdescription,
			"logo" => $_exhiblogo);
	}

//LOAD BOOTHS
	if(!($sqli = $mysqli->prepare('SELECT id, exhibid, categoryid,companyid,name,description from booths where exhibid = "'.$exhibit["dbid"].'"')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	if (!$sqli->execute()) 
	{
		echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	if(!$sqli->bind_result($_boothid,$_boothexhibid,$_boothcategory,$_boothcompany,$_boothname,$_boothdescription))
	{
		echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	while ($sqli->fetch()) {
		$tmp = array(
			"dbid" => $_boothid,
			"exhibid" => $_boothexhibid,
			"category" => $_boothcategory,
			"company" => $_boothcompany,
			"name" => $_boothname,
			"description" => $_boothdescription);
		array_push($booths, $tmp);
	}

//LOAD COORDINATES
	if(!($sqli = $mysqli->prepare('SELECT id, x,y,isroad,boothid,exhibid from coordinates where exhibid = "'.$exhibit["dbid"].'"')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}
	if (!$sqli->execute()) 
	{
		echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	if(!$sqli->bind_result($_coordid,$_coordx,$_coordy,$_coordisroad,$_coordboothid,$_coordexhibid))
	{
		echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	while ($sqli->fetch()) {
		$tmp = array(
			"dbid" => $_coordid,
			"x" => $_coordx,
			"y" => $_coordy,
			"isroad" => $_coordisroad,
			"boothid" => $_coordboothid,
			"exhibid" => $_coordexhibid);
		array_push($coordinates, $tmp);
	}

//LOAD EDGES
	$coordIdString = "(";
	foreach ($coordinates as $value) {
		$coordIdString .= $value["dbid"].",";
	}
	$coordIdString = substr($coordIdString, 0, -1).")";
	if(!($sqli = $mysqli->prepare('SELECT id,weight,vertexA,vertexB from edges where vertexA in '.$coordIdString.' or vertexB in '.$coordIdString.'')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}
	if (!$sqli->execute()) 
	{
		echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
	}
	if(!$sqli->bind_result($_edgesid,$_edgesweight,$_vertexA,$_vertexB))
	{
		echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
	}
	while ($sqli->fetch()) {
		$tmp = array(
			"dbid" => $_edgesid,
			"weight" => $_edgesweight,
			"vertexA" => $_vertexA,
			"vertexB" => $_vertexB);
		array_push($edges, $tmp);
	}

	$mysqli->close();

	$fullLoad = array(
		"exhibition" => $exhibit,
		"booths" => $booths,
		"coordinates" => $coordinates,
		"edges" => $edges);
	
	$returnvar = json_encode($fullLoad);

	echo $returnvar;
?>