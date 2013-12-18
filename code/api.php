<?php
header('Content-Type: text/html; charset=utf-8');
if (isset($_POST["RequestCode"]) && $_POST["RequestCode"] != ""){

    $requestCode = $_POST["RequestCode"];

    #Connect to Database
    $con = new mysqli("figz.dk","sw7","sw7", "sw7");
    
    #Check connection
    if ($con->connect_errno) {
        echo 'Database connection error: ' . $con->connect_error;
        exit();
    }

    $con->query("SET NAMES 'utf8'");

    

    if (isset($_POST['Type'])) {

        switch ($_POST['Type']) {
        case "GetFeeds":
            getFeeds($con, $_POST['UserId'], $_POST['Limit']);
            break;

        case "CreateUser":
            createUser($con, $_POST['ExhibId']);
            break;

        case "GetNewFeeds":
            getNewFeeds($con, $_POST['UserId'], $_POST['TimeStamp']);
            break;

        case "CheckFeeds":
            checkFeeds($con, $_POST['UserId'], $_POST['TimeStamp']);
            break;

        case "GetOldFeeds":
            getOldFeeds($con, $_POST['UserId'], $_POST['TimeStamp'], $_POST['Limit']);
            break;

        case "GetSchedule":
            getSchedule($con, $_POST['UserId'], $_POST['TimeStamp']);
            break;

        case "GetExhibitionInfo":
            getExhib($con, $_POST['ExhibId']);
            break;

        case "GetCategories":
            getCategories($con, $_POST['UserId']);
            break;

        case "SetCategories":
            setCategories($con, $_POST['UserId'], $_POST['BoothIds']);
            break;

        case "GetFloorPlan":
            getFloorPlan($con, $_POST['UserId']);
            break;

        case "BoothSeen":
            boothSeen($con, $_POST['UserId'], $_POST['BoothId']);
            break;
        }
        
    }

    else{
        echo "Could not complete query. Missing type"; 
    }
    
} else {
    echo "Missing request code!";
}

function echoResult($requestCode, $data) {
    $result = new stdClass();
    $result->RequestCode = $requestCode;
    $result->Data = $data;
    echo json_encode($result);
}

function getFeeds($con, $userId, $limit) {

    global $requestCode;

    #Escape special characters to avoid SQL injection attacks
    $userId    = $con->real_escape_string($userId);
    $limit     = $con->real_escape_string($limit);

    $query =   "SELECT feeds.id, feeds.boothid, feeds.header, feeds.description, feeds.feedtime, userbooths.userid, companies.logo, booths.name ".
        "FROM feeds ".
        "LEFT JOIN userbooths ".
        "ON feeds.boothid = userbooths.boothid ".
        "LEFT JOIN booths ".
        "ON feeds.boothid = booths.id ".
        "LEFT JOIN companies ".
        "ON booths.companyid = companies.id ".
        "WHERE userid = ".$userId." ".
        "AND sub = 1 ".
        "ORDER BY feeds.feedtime DESC ".
        "LIMIT ".$limit;

    $result = $con->query($query);

    $json = array();
    
    while($row = $result->fetch_object()) {
        
        array_push($json, $row);
    }

    echoResult($requestCode, $json);
}

function getNewFeeds($con, $userId, $timeStamp) {

    global $requestCode;
    
    #Escape special characters to avoid SQL injection attacks
    $userId    = $con->real_escape_string($userId);
    $timeStamp = $con->real_escape_string($timeStamp);

    $query =   "SELECT feeds.id, feeds.boothid, feeds.header, feeds.description, feeds.feedtime, userbooths.userid, companies.logo, booths.name ".
        "FROM feeds ".
        "LEFT JOIN userbooths ".
        "ON feeds.boothid = userbooths.boothid ".
        "LEFT JOIN booths ".
        "ON feeds.boothid = booths.id ".
        "LEFT JOIN companies ".
        "ON booths.companyid = companies.id ".
        "WHERE userid = ".$userId." ".
        "AND sub = 1 ".
        "AND UNIX_TIMESTAMP(feeds.feedtime) > ".$timeStamp." ".
        "ORDER BY feeds.feedtime DESC";

    $result = $con->query($query);

    $json = array();
    
    while($row = $result->fetch_object()) {
        
        array_push($json, $row);
    }
    
    echoResult($requestCode, $json);
}

function checkFeeds($con, $userId, $timeStamp) {

    global $requestCode;
    
    #Escape special characters to avoid SQL injection attacks
    $userId    = $con->real_escape_string($userId);
    $timeStamp = $con->real_escape_string($timeStamp);

    $query =   "SELECT feeds.id ".
        "FROM feeds ".
        "LEFT JOIN userbooths ".
        "ON feeds.boothid = userbooths.boothid ".
        "WHERE userid = ".$userId." ".
        "AND sub = 1 ".
        "AND UNIX_TIMESTAMP(feeds.feedtime) > ".$timeStamp;

    $result = $con->query($query);

    $num = $result->num_rows;

    $json = array();
    
    $obj = new stdClass();
    $obj->num = $num;
    $row = $obj;
    
    array_push($json, $row);
    
    echoResult($requestCode, $json);
}

function getOldFeeds($con, $userId, $timeStamp, $limit) {

    global $requestCode;

    #Escape special characters to avoid SQL injection attacks
    $userId    = $con->real_escape_string($userId);
    $timeStamp = $con->real_escape_string($timeStamp);
    $limit     = $con->real_escape_string($limit);

    $query =    "SELECT feeds.id, feeds.boothid, feeds.header, feeds.description, feeds.feedtime, userbooths.userid, companies.logo, booths.name ".
        "FROM feeds ".
        "LEFT JOIN userbooths ".
        "ON feeds.boothid = userbooths.boothid ".
        "LEFT JOIN booths ".
        "ON feeds.boothid = booths.id ".
        "LEFT JOIN companies ".
        "ON booths.companyid = companies.id ".
        "WHERE userid = ".$userId." ".
        "AND sub = 1 ".
        "AND UNIX_TIMESTAMP(feeds.feedtime) < ".$timeStamp." ".
        "ORDER BY feeds.feedtime DESC ".
        "LIMIT ".$limit;

    $result = $con->query($query);

    $json = array();
    
    while($row = $result->fetch_object()) {
        
        array_push($json, $row);
    }
    
    echoResult($requestCode, $json);
}

function getSchedule($con, $userId, $timeStamp) {

    global $requestCode;
    
    #Escape special characters to avoid SQL injection attacks
    $userId    = $con->real_escape_string($userId);
    $timeStamp = $con->real_escape_string($timeStamp);

    $query =    "SELECT schedule.id, schedule.name AS eventname, schedule.starttime, schedule.endtime, schedule.boothid, userbooths.userid, booths.name AS boothname ".
        "FROM schedule ".
        "LEFT JOIN userbooths ".
        "ON schedule.boothid = userbooths.boothid ".
        "LEFT JOIN booths ".
        "ON schedule.boothid = booths.id ".
        "WHERE userid = ".$userId." ".
        "AND DATEDIFF(schedule.starttime, FROM_UNIXTIME(".$timeStamp.")) > -1 ".
        "ORDER BY schedule.starttime ASC";

    $result = $con->query($query);

    $json = array();
    
    while($row = $result->fetch_object()) {
        
        array_push($json, $row);
    }
    
    echoResult($requestCode, $json);
}

function getExhib($con, $exhibId) {

    global $requestCode;
    
    #Escape special characters to avoid SQL injection attacks
    $exhibId    = $con->real_escape_string($exhibId);

    $query =  "SELECT * ".
              "FROM exhib ".
              "WHERE id = ".$exhibId;

    $result = $con->query($query);

    $json = array();
    
    while($row = $result->fetch_object()) {
        
        array_push($json, $row);
    }
    
    echoResult($requestCode, $json);
}


function getCategories($con, $userId) {

    global $requestCode;

    #Escape special characters to avoid SQL injection attacks
    $userId = $con->real_escape_string($userId);

    $query = "SELECT DISTINCT categories.id, categories.name ".
        "FROM categories ".
        "LEFT JOIN booths ".
        "ON booths.categoryid = categories.id ".
        "WHERE booths.exhibid = (SELECT exhibid FROM users WHERE id = ".$userId.")";

    $result = $con->query($query);
    
    $json = array();
    
    while($row = $result->fetch_object()) {
        
        array_push($json, $row);
    }
    
    foreach($json as $item) {

        $item->booths = array();

        $query =  "SELECT booths.id, booths.name, booths.description, companies.logo ".
                  "FROM booths ".
                  "LEFT JOIN companies ".
                  "ON booths.companyid = companies.id ".
                  "WHERE booths.categoryid = ".$item->id;

        $newResult = $con->query($query);
        
        while($newRow = $newResult->fetch_object()) {

            $query = "SELECT sub ".
                "FROM userbooths ".
                "WHERE userbooths.boothid = ".$newRow->id." ".
                "AND userbooths.userid = ".$userId;

            $tempResult = $con->query($query);

            $subbed = $tempResult->fetch_object();
            
            $newRow->sub = $subbed->sub > 0 ? true : false;
            
            $item->booths[] = $newRow;
        }
    }

    echoResult($requestCode, $json);
}

function getFloorPlan($con, $userId) {

    global $requestCode;
    $obj;

    #Escape special characters to avoid SQL injection attacks
    $userId = $con->real_escape_string($userId);

    $exhibId = $con->query("SELECT exhibid FROM users WHERE id =".$userId);
    $exhibId = $exhibId->fetch_row();
    $exhibId = $exhibId[0];

    ///////////////////////////////////////////////

    $query = "SELECT id, x, y ".
             "FROM coordinates ".
             "WHERE isroad = 1 ".
             "AND exhibid = ".$exhibId;

    $result = $con->query($query);

    $json = array();
    
    while($row = $result->fetch_object()) {
        
        array_push($json, $row);
    }

    $obj->nodes = $json;

    ///////////////////////////////////////////////

    foreach ($obj->nodes as $node)
    {
        $nodes.=$node->id;
        $nodes.=",";
    }
    $nodes = substr($nodes, 0, -1);
    
    $query = "SELECT DISTINCT id, weight, vertexA, vertexB ".
             "FROM edges ".
        "WHERE vertexA IN (".$nodes.") AND vertexB IN (".$nodes.")";

    $result = $con->query($query);

    $json = array();
    
    while($row = $result->fetch_object()) {
        
        array_push($json, $row);
    }

    $obj->edges = $json;

    //////////////////////////////////////////////

    $query = "SELECT DISTINCT booths.id AS id, booths.name AS name, booths.description AS description, companies.logo AS logo, userbooths.sub AS sub ".
        "FROM booths ".
        "LEFT JOIN companies ".
        "ON booths.companyid = companies.id ".
        "LEFT JOIN userbooths ".
        "ON userbooths.userid = ".$userId." ".
        "WHERE booths.exhibid = ".$exhibId." ".
        "AND userbooths.boothid = booths.id";

    $result = $con->query($query);

    $json = array();
    
    while($row = $result->fetch_object()) {
        $row->sub = $row->sub > 0 ? true : false;
        array_push($json, $row);
    }

    $obj->booths = $json;

    ///////////////////////////////////////////////

    foreach($obj->booths as $booth) {

        $nodes = array();
    
        $query = "SELECT id ".
                 "FROM coordinates ".
                 "WHERE boothid = ".$booth->id." AND isroad = 1";

        $result = $con->query($query);

        while($row = $result->fetch_row()) {
            
            $nodes = array_merge($nodes, $row);
        }

        $booth->nodeIds = $nodes;

        $query = "SELECT x ".
                 "FROM coordinates ".
                 "WHERE boothid = ".$booth->id." ".
                 "AND isroad = 0";

        $result = $con->query($query);

        $xCords = array();

        while($row = $result->fetch_row()) {
            
            $xCords = array_merge($xCords, $row);
        }

        $query = "SELECT y ".
                 "FROM coordinates ".
                 "WHERE boothid = ".$booth->id." ".
                 "AND isroad = 0";

        $result = $con->query($query);

        $yCords = array();

        while($row = $result->fetch_row()) {
            
            $yCords = array_merge($yCords, $row);
        }

        $booth->right   = number_format(max($xCords), 8);
        $booth->left    = number_format(min($xCords), 8);
        $booth->top     = number_format(max($yCords), 8);
        $booth->bottom  = number_format(min($yCords), 8);

    }

    ///////////////////////////////////////////////    

    echoResult($requestCode, $obj);
}

/* --------------------WRITE QUERIES--------------------- */

function createUser($con, $exhibId) {

    global $requestCode;
    
    #Escape special characters to avoid SQL injection attacks
    $exhibId = $con->real_escape_string($exhibId);

    $con->query("INSERT INTO users VALUES(NULL, ".$exhibId.")");

    $userId = $con->insert_id;

    $query = "INSERT INTO userbooths (boothid, userid, seen, sub) ".
        "SELECT booths.id AS boothid, users.id AS userid, '0', '1' ".
        "FROM booths ".
        "LEFT JOIN users ".
        "ON ".$userId." = users.id";

    $con->query($query);

    $obj;
    $obj->userId = $userId;
    $obj->exhibId = $exhibId;

    echoResult($requestCode, $obj);
}

function setCategories($con, $userId, $boothIds) {

    global $requestCode;

    #Escape special characters to avoid SQL injection attacks
    $userId = $con->real_escape_string($userId);

    $query =  "UPDATE userbooths ".
              "SET sub = IF(boothid IN (".$boothIds."), 1, IF(boothid NOT IN (".$boothIds."), 0, sub)) ".
              "WHERE userid = ".$userId;

    $con->query($query);

    echoResult($requestCode, new stdClass());
}

function boothSeen($con, $userid, $boothid) {

    global $requestCode;
    #Escape special characters to avoid SQL injection attacks
    $userid = $con->real_escape_string($userid);
    $boothid = $con->real_escape_string($boothid);

    $query =  "UPDATE userbooths ".
              "SET seen = seen + 1 ".
              "WHERE userid = ".$userid." AND boothid = ".$boothid;

    $con->query($query);

    echoResult($requestCode, new stdClass());
}

?>
