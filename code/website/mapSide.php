<?php
// Start a session for error reporting
session_start();

$_exhibid ="";

if(isset($_POST["exhibid"]) && $_POST["exhibid"] != "")
{
	// Get the POSTed variables
	$_exhibid = $_POST['exhibid'];




	// Make sure all the fields from the form have inputs	
	include("connect.php");

	// Sanitize the inputs
	$_exhibid = $mysqli->real_escape_string($_name);

	if(!($sqli = $mysqli->prepare( 'INSERT INTO users (exhibid) 
									VALUES (?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	if(!$sqli->bind_param('s', $_exhibid))
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