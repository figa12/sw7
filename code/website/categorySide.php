<?php
// Start a session for error reporting
session_start();

$_name ="";

if(isset($_POST["name"]) && $_POST["name"] != "")
{
	// Get the POSTed variables
	$_name = $_POST['name'];




	// Make sure all the fields from the form have inputs	
	include("connect.php");

	// Sanitize the inputs
	$_name = $mysqli->real_escape_string($_name);

	if(!($sqli = $mysqli->prepare( 'INSERT INTO categories (name) 
									VALUES (?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	if(!$sqli->bind_param('s', $_name))
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