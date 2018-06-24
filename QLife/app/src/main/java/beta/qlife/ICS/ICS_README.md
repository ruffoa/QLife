# ICS

This package has classes that deal with downloading, parsing and managing data in the ICS file.

## DownloadICSFile

This class downloads the ICS file. A URL is passed into a background task method, and an HTTP connection is established. The file is then downloaded in the background.
When the download is complete, the ParseICS class is used to turn the ICS file into relevant data.

## ICSToBuilding

This class is used to convert between the ICS file building name short forms and the building names in the phone database.

## ParseICS

This class parses out class information from the ICS file and inserts it into the phone database.