<?php
	
// Input your information for the database here

// Host name
$host = "localhost";

// Database username
$username = "root";

// Database password
$password = "";

// Name of database
$database = "test";

$mysqli = new mysqli($host, $username, $password, $database);

/* check connection */
if (mysqli_connect_errno()) {
	printf("Connect failed: %s\n", $mysqli->connect_error());
	exit();
}
?>