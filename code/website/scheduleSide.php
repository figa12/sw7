<?php
// Start a session for error reporting
session_start();

$_boothid = $_name = $_starttime = $_endtime = "";

if((isset($_POST["boothid"]) && $_POST["boothid"] != "") && 
	(isset($_POST["name"]) && $_POST["name"] != "") && 
	(isset($_POST["starttime"]) && $_POST["starttime"] != "") &&
	(isset($_POST["endtime"]) && $_POST["endtime"] != ""))
{
	// Get the POSTed variables
	$_boothid = $_POST['boothid'];
	$_name = $_POST['name'];
	$_starttime = $_POST['starttime'];
	$_endtime = $_POST['endtime'];


	// Make sure all the fields from the form have inputs	
	include("connect.php");

    // Sanitize the inputs
	$_boothid = $mysqli->real_escape_string($_boothid);
	$_name = $mysqli->real_escape_string($_name);
	$_starttime = strtotime($mysqli->real_escape_string($_starttime));
	$_endtime = strtotime($mysqli->real_escape_string($_endtime));

	//var_dump($_starttime);
	//die($_starttime);

	if(!($sqli = $mysqli->prepare( 'INSERT INTO schedule (boothid, name, starttime,endtime) 
									VALUES (?, ?, FROM_UNIXTIME(?), FROM_UNIXTIME(?))')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	if(!$sqli->bind_param('isii', $_boothid, $_name, $_starttime, $_endtime))
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
die("Something has gone wrong"); 
}
?>