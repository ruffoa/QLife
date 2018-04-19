<?php
    if (isset($_GET["year"]) && isset($_GET["month"]) && isset($_GET["day"]) && isset($_GET["room"])){
        $year=$_GET["year"];
        $month=$_GET["month"];
        $day=$_GET["day"];
        $room=$_GET["room"];
        $bookings=file_get_contents("https://queensu.evanced.info/dibsAPI/reservations/$year-$month-$day/$room");
        echo $bookings;
    }
?>