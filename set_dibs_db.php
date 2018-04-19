<?php
    //connect to DB
    require_once __DIR__ . '/db_connect.php';
    $db = connectDatabase();

    for ($i=0; $i<100; $i++) { //<100 so future room IDs are in loop - need update if more rooms come - break when hit room with API call ID
        date_default_timezone_set("US/Eastern");
        $year=date("Y");
        $month=date("n");
        $day=date("j");
        $bookings=file_get_contents("https://queensu.evanced.info/dibsAPI/reservations/$year-$month-$day/$i");
        if ($bookings!="[]"){ //bad i values, not room IDs
            $res=mysqli_query($db,"INSERT INTO Dibs(roomID,jsonData) values($i,'$bookings')");
        }
    }
?>