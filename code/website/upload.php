<?php
$folder = "images/";
$logo = $_FILES['filename'];
$name = unixFriendly($_POST['finalName']);
$type = explode('/',$logo['type']);
if (is_uploaded_file($_FILES['filename']['tmp_name']))  
{   
    if (move_uploaded_file($logo['tmp_name'], $folder.$name.'.'.$type[1])) {
         //Echo "File uploaded";
        echo ($name.'.'.$type[1]);
        return;
    } 
    else {
         Echo "File not moved to destination folder. Check permissions";
         return;
    }
} 
else {
     Echo "File is not uploaded.";
     return;
};

function unixFriendly($string) {
    $string = ereg_replace(" ","_",$string);
    $string = ereg_replace("[^a-zA-Z0-9_]","",$string);
    $string = strtolower($string);
    return $string;
} 
?>