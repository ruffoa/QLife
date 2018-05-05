<?php
require_once __DIR__ . '/db_config.php';
 function connectDatabase(){
    $conn = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_NAME,DB_PORT);
    if (!$conn){
        echo "There was an error connecting to the database: " . mysqli_connect_error();
        return null;
    }
    return $conn;
 }