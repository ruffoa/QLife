# Database

In this package and sub-packages the SQLite phone database and cloud SQL database are defined and worked with.

## GetCloudDb

This AsyncTask class retrieves a JSON object representing the cloud database using a PHP script available on the
QLife website. It then parses the JSON and inserts the data into the appropriate tables in the phone database.

This process is done every time a user logs in; it starts after a successful login and is partially responsible for the 
resulting loading screen. When the database work is completed, the loading screen ends and the user is sent to
MainTabActivity.

