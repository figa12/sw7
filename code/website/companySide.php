<?php
// Start a session for error reporting
session_start();

$_name = $_logo = "";

if(isset($_POST["name"]) && $_POST["name"] != "")
{

    require("connect.php");

	// Check to see if the type of file uploaded is a valid image type
	function is_valid_type($file)
	{
		// This is an array that holds all the valid image MIME types
		$valid_types = array("image/jpg", "image/jpeg", "image/png");

		if (in_array($file['type'], $valid_types))
			return 1;
		return 0;
	}

	// Just a short function that prints out the contents of an array in a manner that's easy to read
	// I used this function during debugging but it serves no purpose at run time for this example
	function showContents($array)
	{
		echo "<pre>";
		print_r($array);
		echo "</pre>";
	}

	// Set some constants

	// This variable is the path to the image folder where all the images are going to be stored
	// Note that there is a trailing forward slash
	$TARGET_PATH = "images/";

	// Get our POSTed variables
	$_name = $_POST['name'];
	$_logo = $_FILES['logo'];

	// Sanitize our inputs
	$_name = $mysqli->real_escape_string($_name);
	$_logo['name'] = $mysqli->real_escape_string($_logo['name']);

	// Build our target path full string.  This is where the file will be moved do
	// i.e.  images/picture.jpg
	$TARGET_PATH .= $_logo['name'];

	// Make sure all the fields from the form have inputs
	if ( $_name == "" || $_logo['name'] == "" )
	{
		$_SESSION['error'] = "All fields are required";
		header("Location:company.html");
		exit;
	}

	// Check to make sure that our file is actually an image
	// You check the file type instead of the extension because the extension can easily be faked
	if (!is_valid_type($_logo))
	{
		$_SESSION['error'] = "You must upload a jpeg or png";
		header("Location:company.html");
		exit;
	}

	// Here we check to see if a file with that name already exists
	// You could get past filename problems by appending a timestamp to the filename and then continuing
	if (file_exists($TARGET_PATH))
	{
		$_SESSION['error'] = "A file with that name already exists";
		header("Location:company.html");
		exit;
	}

	// Lets attempt to move the file from its temporary directory to its new home
	if (move_uploaded_file($_logo['tmp_name'], $TARGET_PATH))

	if(!($sqli = $mysqli->prepare( 'INSERT INTO companies (name, logo) 
									VALUES (?, ?)')))
	{
		echo "Prepare failed: (" . $mysqli->errno . ") " . $mysqli->error;
	}

	if(!$sqli->bind_param('ss', $_name, $_logo['name']))
	{
		echo "Binding parameters failed: (" . $sqli->errno . ") " . $sqli->error;
	}

	if (!$sqli->execute()) 
	{
	echo "Execute failed: (" . $sqli->errno . ") " . $sqli->error;
	}
		
	$inserted_fid = $mysqli->insert_id;
	printf("Has id %d", $inserted_fid);
		
	$mysqli->close();
}

else
{
die("No uploaded file present"); 
}
?>