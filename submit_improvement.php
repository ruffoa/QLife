<?php
require_once __DIR__ . '/database/db_connect.php';
$db = connectDatabase();
$sql="INSERT INTO Improvements(Name,Email,Message) 
        values('Carson', '14cdwc@queensu.ca', 'ais;dfj;')";
if (mysqli_query($db, $sql)){
    echo "Thanks for your suggestion!";
}
else {
    die("There was an error your suggestion: ".mysqli_error($db));
}
?>
