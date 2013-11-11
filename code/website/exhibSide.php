<?php
// Start a session for error reporting
session_start();

$_name = $_address = $_zip = $_country = $_description ="";

if(isset($_POST["name"]) && $_POST["name"] != "" &&
	isset($_POST["address"]) && $_POST["address"] != "" &&
	isset($_POST["zip"]) && $_POST["zip"] != "" &&
	isset($_POST["country"]) && $_POST["country"] != "" &&
	isset($_POST["description"]) && $_POST["description"] != "")
{
	// Get the POSTed variables
	$_name = $_POST['name'];
	$_address = $_POST['address'];
	$_zip = $_POST['zip'];
	$_country = $_POST['country'];
	$_description = $_POST['description'];




	// Make sure all the fields from the form have inputs	
	include("connect.php");

	// Sanitize the inputs
	$_name = $mysqli->real_escape_string($_name);
	$_address = $mysqli->real_escape_string($_address);
	$_zip = $mysqli->real_escape_string($_zip);
	$_country = $mysqli->real_escape_string($_country);
	$_description = $mysqli->real_escape_string($_description);

	if(!($sqli = $mysqli->prepare( 'INSERT INTO exhibs (name, address, zip, country, description) 
									VALUES (?,?,?,?,?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	if(!$sqli->bind_param('sssss', $_name, $_address, $_zip, $_country, $_description))
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