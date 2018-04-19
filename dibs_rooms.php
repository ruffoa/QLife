<?php
    //connect to DB
    require_once __DIR__ . '/database/db_connect.php';

    $db = connectDatabase();
    if (isset($_GET["room"])){
        $room=$_GET["room"];
        $bookings=mysqli_query($db,"SELECT * FROM Dibs WHERE roomID=$room");
        $data = mysqli_fetch_array($bookings);
        echo $data["jsonData"];
    }
?>
