<?php
	
// Input your information for the database here

// Host name
$host = "figz.dk";

// Database username
$username = "sw7";

// Database password
$password = "sw7";

// Name of database
$database = "sw7";

$mysqli = new mysqli($host, $username, $password, $database);

/* check connection */
if (mysqli_connect_errno()) {
	printf("Connect failed: %s\n", $mysqli->connect_error());
	exit();
}
?>