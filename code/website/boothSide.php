<?php
// Start a session for error reporting
session_start();

$_categoryid = $_companyid = $_name = $_description = "";

if((isset($_POST["categoryid"]) && $_POST["categoryid"] != "") && 
	(isset($_POST["companyid"]) && $_POST["companyid"] != "") && 
	(isset($_POST["description"]) && $_POST["description"] != "") &&
	(isset($_POST["name"]) && $_POST["name"] != ""))
{
	// Get the POSTed variables
	$_categoryid = $_POST['categoryid'];
	$_companyid = $_POST['companyid'];
	$_name = $_POST['name'];
	$_description = $_POST['description'];



	// Make sure all the fields from the form have inputs	
	include("connect.php");

	// Sanitize the inputs
	$_categoryid = $mysqli->real_escape_string($_categoryid);
	$_companyid = $mysqli->real_escape_string($_companyid);
	$_name = $mysqli->real_escape_string($_name);
	$_description = $mysqli->real_escape_string($_description);

	if(!($sqli = $mysqli->prepare( 'INSERT INTO booths (categoryid, companyid, name, description) 
									VALUES (?, ?, ?, ?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	if(!$sqli->bind_param('ddss', $_categoryid, $_companyid, $_name, $_description))
	{
		echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	if (!$sqli->execute()) 
	{
		echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
	}
				
	$mysqli->close();
}

else
{
die("FAILED!!!!"); 
}
?>