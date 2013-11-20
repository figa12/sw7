<?php
class Row
{
    public $id;
    public $testtext;
    public $navne;

    public function __construct($_id, $_testtext, $_navne)
    {
        $this->id = $_id;
        $this->testtext = $_testtext;
        $this->navne = $_navne;
    }
    
    public function getStr()
    {
        return $this->id . " " . $this->testtext . " " . $this->navne;
    }
}

function readDb($con)
{
    $rows = array();
    $result = $con->query("SELECT * FROM hello");
    while($row = $result->fetch_assoc())
    {
        $rows[] = new Row($row['id'], $row['testtext'], $row['navne']);
    }
    return $rows;
}

function writeDbRow($con, $testtext, $navne)
{
    $con->query("INSERT INTO hello (testtext, navne) VALUES ('" . $testtext . "', '" . $navne . "')");
    return readDb($con);
}

$con = new mysqli("localhost", "root", "", "webengi");

$rows = readDb($con);

if(isset($_POST['text']) && $_POST['text'] != "" && isset($_POST['navn']) && $_POST['navn'] != "")
{
    $text = $_POST['text'];
    $navn = $_POST['navn'];
    
    echo json_encode(writeDbRow($con, $text, $navn));
}

$con->close();

?>
