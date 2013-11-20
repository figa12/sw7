<?php
$connection = mysql_connect("localhost", "root", "");
mysql_select_db("webengi", $connection);

$query = mysql_query("SELECT testtext FROM hello", $connection);

while ($row = mysql_fetch_assoc($query)) {
    echo $row['testtext']."</br>";
}
?>
