<?php
// Start a session for error reporting
session_start();

if(isset($_POST["nodes"]) && $_POST["nodes"] != "" &&
	isset($_POST["edges"]) && $_POST["edges"] != "" &&
	isset($_POST["booths"]) && $_POST["booths"] != "")
{
	include("connect.php");


	$nodes = stripslashes($_POST["nodes"]);
	$edges = stripslashes($_POST["edges"]);
	$booths = stripslashes($_POST["booths"]);
	$exhib = stripslashes($_POST["exhibit"]);
	$companies = stripslashes($_POST["companies"]);
	$categories = stripslashes($_POST["categories"]);

	$nodesData = json_decode($nodes,true);
	$edgesData = json_decode($edges,true);
	$boothsData = json_decode($booths,true);
	$exhibData = json_decode($exhib,true);
	$companiesData = json_decode($companies,true);;
	$categoriesData = json_decode($categories,true);;
	$returnNodesIds = array();
	$returnBoothsIds = array();
	$returnCompanyIds = array();
	$returnCategoryIds = array();
	$returnExhibId = '0';
//exhib insert
	if(!($sqli = $mysqli->prepare( 'INSERT INTO exhib (id,name,address,zip,country,description,logo) 
									VALUES (?, ?, ?, ?, ?, ?, ?) on duplicate key update name=values(name),address=values(address),zip=values(zip),country=values(country),description=values(description),logo=values(logo)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}
	// Sanitize the inputs
	$_idval = $exhibData["id"];
	$_nameval = $exhibData["name"];
	$_addressval = $exhibData["address"];
	$_zipval = $exhibData["zip"];
	$_countryval = $exhibData["country"];
	$_descriptionval = $exhibData["description"];
	$_logoval = $exhibData["logo"];


	if(!$sqli->bind_param('ississs', $_idval, $_nameval,$_addressval,$_zipval,$_countryval,$_descriptionval,$_logoval))
	{
		echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	if (!$sqli->execute()) 
	{
		echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
	}
		
	$returnExhibId = $_idval == NULL ? $mysqli->insert_id : $_idval;
//Companies insert
if(isset($_POST["companies"]) && $_POST["companies"] != "")
{
	if(!($sqli = $mysqli->prepare( 'INSERT INTO companies (name,logo) 
									VALUES (?, ?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}
	foreach ($companiesData as $value) {
		// Sanitize the inputs
		$_nameval = $value["name"];
		$_logoval = $value["logo"];

		if(!$sqli->bind_param('ss', $_nameval,$_logoval))
		{
			echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
		}

		if (!$sqli->execute()) 
		{
			echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
		}
			
		$return = $mysqli->insert_id;
		array_push($returnCompanyIds, $return);		
	}
}

//Categories insert
if(isset($_POST["categories"]) && $_POST["categories"] != "")
	{
	if(!($sqli = $mysqli->prepare( 'INSERT INTO categories (name) 
									VALUES (?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}
	foreach ($categoriesData as $value) {
		// Sanitize the inputs
		$_nameval = $value["name"];

		if(!$sqli->bind_param('s', $_nameval))
		{
			echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
		}

		if (!$sqli->execute()) 
		{
			echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
		}
			
		$return = $mysqli->insert_id;
		array_push($returnCategoryIds, $return);		
	}
}


//Booths insert
	if(!($sqli = $mysqli->prepare( 'INSERT INTO booths (id, exhibid,categoryid,companyid,name,description) 
									VALUES (?, ?, ?, ?, ?, ?) on duplicate key update categoryid=values(categoryid),companyid=values(companyid),name=values(name),description=values(description)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	foreach ($boothsData as $value) {
		// Sanitize the inputs
		$_exhibidval = $returnExhibId;
		$_idval = $value["dbid"];
		if($value["newCat"])
		{
			$_categoryval = $returnCategoryIds[$value["category"]];
		}
		else {
		 	$_categoryval = $value["category"];
		 }
		if($value["newComp"])
		{
			$_companyidval = $returnCompanyIds[$value["company"]];
		}
		else {
		 	$_companyidval = $value["company"];
		 }
		$_nameval = $value["id"];
		$_descriptionval = "No description";//$value["description"];

		if(!$sqli->bind_param('iiiiss',$_idval, $_exhibidval,$_categoryval,$_companyidval,$_nameval,$_descriptionval))
		{
			echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
		}

		if (!$sqli->execute()) 
		{
			echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
		}
			//DELETE COORDINATES
			if($_idval != NULL)
			{
				$sqli = $mysqli->prepare("DELETE FROM coordinates WHERE boothid = ?");
				$sqli->bind_param('i', $_idval);
				$sqli->execute();
			}
			
		$return = $_idval == NULL ? $mysqli->insert_id : $_idval;
		array_push($returnBoothsIds, $return);
	}
//Booth coordinates
	if(!($sqli = $mysqli->prepare( 'INSERT INTO coordinates (x, y, isroad, boothid, exhibid) 
									VALUES (?, ?, ?, ?, ?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	$boothCounter = 0;
	foreach ($boothsData as $value) {
		// Sanitize the inputs
		$_xval = $mysqli->real_escape_string($value["topLeft"]["x"]);
		$_yval = $mysqli->real_escape_string($value["topLeft"]["y"]);
		$_isroadval = $mysqli->real_escape_string('0');
		$_boothidval = $mysqli->real_escape_string($returnBoothsIds[$boothCounter]);
		$_exhibidval = $returnExhibId;

		if(!$sqli->bind_param('ddiii', $_xval,$_yval,$_isroadval,$_boothidval,$_exhibidval))
		{
			echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
		}

		if (!$sqli->execute()) 
		{
			echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
		}
		$boothCounter++;
	}

	$boothCounter = 0;
	foreach ($boothsData as $value) {
		// Sanitize the inputs
		$_xval = $mysqli->real_escape_string($value["bottomRight"]["x"]);
		$_yval = $mysqli->real_escape_string($value["bottomRight"]["y"]);
		$_isroadval = $mysqli->real_escape_string('0');
		$_boothidval = $mysqli->real_escape_string($returnBoothsIds[$boothCounter]);
		$_exhibidval = $returnExhibId;

		if(!$sqli->bind_param('ddiii', $_xval,$_yval,$_isroadval,$_boothidval,$_exhibidval))
		{
			echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
		}

		if (!$sqli->execute()) 
		{
			echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
		}
		$boothCounter++;
	}


//Nodes insert
	if(!($sqli = $mysqli->prepare( 'INSERT INTO coordinates (x, y, isroad, boothid, exhibid) 
									VALUES (?, ?, ?, ?, ?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	foreach ($nodesData as $value) {

		//var_dump($value);

		// Sanitize the inputs
		$_xval = $mysqli->real_escape_string($value["location"]["x"]);
		$_yval = $mysqli->real_escape_string($value["location"]["y"]);
		$_isroadval = $mysqli->real_escape_string('1');
		$_boothidDBval = 0;
		for ($i=0; $i < count($boothsData); $i++) { 
			if($boothsData[$i]["id"] == $value["boothId"])
			{
				$_boothidDBval = $i;
			}
		}


		$_boothidval = $value["boothId"] == NULL ? NULL : $mysqli->real_escape_string($returnBoothsIds[$_boothidDBval]);
		$_exhibidval = $returnExhibId;

		if(!$sqli->bind_param('ddiii', $_xval, $_yval, $_isroadval, $_boothidval, $_exhibidval))
		{
			echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
		}

		if (!$sqli->execute()) 
		{
			echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
		}
			
		$return = $mysqli->insert_id;
		array_push($returnNodesIds, $return);
	}
	

//Edges insert
	if(!($sqli = $mysqli->prepare( 'INSERT INTO edges (weight, vertexA, vertexB) 
									VALUES (?, ?, ?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	foreach ($edgesData as $value) {

		// Sanitize the inputs
		$_weight = $mysqli->real_escape_string($value["weight"]);
		$_fromVar = 0;
		$_toVar = 0;

		for ($i=0; $i < count($nodesData); $i++) { 
			if($nodesData[$i]["name"] == $value["from"])
			{
				$_fromVar = $i;
			}
			if($nodesData[$i]["name"] == $value["to"])
			{
				$_toVar = $i;
			}
		}

		$_vertexA = $mysqli->real_escape_string($returnNodesIds[$_fromVar]);
		$_vertexB = $mysqli->real_escape_string($returnNodesIds[$_toVar]);

		if(!$sqli->bind_param('dii',$_weight,$_vertexA,$_vertexB))
		{
			echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
		}

		if (!$sqli->execute()) 
		{
			echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
		}
	}
				
	$mysqli->close();
	echo $concatString;
	
	}
?>