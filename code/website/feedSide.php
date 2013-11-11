<?php
// Start a session for error reporting
session_start();

$_header = $_description = $_id = "";

if((isset($_POST["header"]) && $_POST["header"] != "") && (isset($_POST["description"]) && $_POST["description"] != "") && (isset($_POST["id"]) && $_POST["id"] != ""))
{
	// Get the POSTed variables
	$_header = $_POST['header'];
	$_description = $_POST['description'];
	$_id = $_POST['id'];



	// Make sure all the fields from the form have inputs	
	include("connect.php");

    // Sanitize the inputs
	$_header = $mysqli->real_escape_string($_header);
	$_description = $mysqli->real_escape_string($_description);
	$_id = $mysqli->real_escape_string($_id);

	if(!($sqli = $mysqli->prepare( 'INSERT INTO feeds (header, description, boothid) 
									VALUES (?, ?, ?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	if(!$sqli->bind_param('ssd', $_header, $_description, $_id))
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
die("No uploaded file present"); 
}
?>