# QLife

The app currently supports API version 21 and newer. This unfortunately means that cool Java 8 features like default or static methods in interfaces are not possible.

Each java package has a readme explaining the contents and each java file is documented per Javadoc standards.

## Website

There are files hosted at qtap.engsoc.queensu.ca in the top level of this project. Most of these files are used for general purposes that
need to be accessible to the application, but outside of the application. For example, an HTML page and script that handle submitting
suggestions to improve the app. There is also a privacy policy hosted here per Google Play Developer requirements.

## Cloud Database

A cloud database is hosted with the Queen's Engineering Society's cloud system and made accessible by a public load balancer. A php script, get\_database.php, is used to retrive the database in JSON form. On login, the app makes an HTTP get call to qtap.engsoc.queensu.ca/database/get\_database.php to retrieve that JSON and then parse it and put into the phone's SQLITE database.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Dependencies

##### Ensure these permissions and data are in your AndroidManifest.xml

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    <uses-permission android:name="android.permission.READ_PROFILE" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <uses-permission android:name="android.permission.INTERNET" />
	<meta-data
            android:name="com.google.android.gms.version"
            android:value="@integer/google_play_services_version" />
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="@string/google_maps_key" />

        <meta-data
            android:name="io.fabric.ApiKey"
            android:value="d84ee4ad5d54b10dede20dd1d87deb32e5214421" />

##### Ensure these values are in your android{} field in your app.gradle file


	compileSdkVersion 24
	minSdkVersion 21
	targetSdkVersion 24
	

##### Ensure this is at the top of your app.gradle file

	
	buildscript {
		repositories {
			maven { url 'https://maven.fabric.io/public' }
		}

		dependencies {
			classpath 'io.fabric.tools:gradle:1.+'
		}
	}
	apply plugin: 'com.android.application'
	apply plugin: 'io.fabric'

	repositories {
		maven { url 'https://maven.fabric.io/public' }
	}
	

##### Ensure these are in your dependencies in app.gradle file

	compile('com.crashlytics.sdk.android:crashlytics:2.6.6@aar') {
        transitive = true;
    }
    compile 'com.android.support:appcompat-v7:25.3.1'
    compile 'com.android.support:design:25.3.1'
    compile 'com.google.android.gms:play-services-maps:10.2.1'
    compile 'com.android.support:support-v4:25.3.1'
    compile 'com.android.support:appcompat-v7:25.3.1'
    compile 'com.android.support:cardview-v7:25.3.1'
    compile 'com.android.support:recyclerview-v7:25.3.1'

### Installing

Ensure you have java downloaded from [here](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html) . Accept the license agreement and donwlaod the file associated with your operating system. Extract (if needed), run the .exe and follow the installation wizard.
Now you should install an IDE. For Android development, Android Studio is the best IDE. To download/install, go [here](https://developer.android.com/studio/index.html) .

With Android Studio, comes an Android SDK Manager. This program allows you to see and manage the Android libraries and kits you have installed and what needs updates/installation. This program can be accessed through the update prompt links in Android Studio, as well as on its own.

## Running Tests

Fabric's [Crashlytics](https://try.crashlytics.com/) is used to monitor app failures, fatal or not. Any crash on a phone with an Internet connection is sent to the app's [Fabric dashboard](https://www.fabric.io/queens-eng-soc/android/apps/com.example.alex.qtapandroid/issues?time=last-seven-days&event_type=all&subFilter=state&state=open&build%5B0%5D=top-builds) to be viewed and managed. From here, a small stack trace can be viewed, and issues can be categorized/resolved.

## Deployment

To run the app for debugging and testing purposes, an emulator can be used through Android Studio or a physical Android phone can be used.
To run the app, click the green play button at the top, and then select the physical phone or emulator to be used. 

This application is not yet production worthy, however, for information on deploying it on the Google Play Store, click [here](https://support.google.com/googleplay/android-developer/answer/113469?hl=en).

### Emulator

By default, one emulator is installed with Android Studio. For information on installing other emulator, clcik [here](https://www.embarcadero.com/starthere/xe5/mobdevsetup/android/en/creating_an_android_emulator.html).

To use the emulator, Android Studio 2.0 or higher and Android SDK Tools of 25.0.10 or higher is needed.

The emulator does not support all of the features in a real phone, however. WiFi, Bluetooth, NFC, SD card insert/eject, Device-attached headphones and USB capabilities are not available.
For more information, click [here](https://developer.android.com/studio/run/emulator.html).

### Physical Phone 

Android phones can be used to debug/test the app using a USB cable. The phone must be set properly to allow this:

1. Go to Settings > About Phone
2. Tap Build Number 7 times to unlock Developer Options
3. In Developer Options set phone to allow debugging

After the above is finished, you should be able to run the project in Android Studio and select your phone (when plugged in) to be used. If it doesn't seem to be working, look [here](https://developer.android.com/studio/run/device.html#setting-up).

The application can also be run by simply sending the .apk file to users. This is useful for alpha/beta testing where only a limited number of people will be given the app. To access the .apk, go to the app\build\outputs\apk directory. When an Android phone downloads this file, it will ask to install. You will be taken to a settings page where you can allow an installation from unsecure sources (only once). This must be clicked to install the app).

## Built With

Google Services, Google Services Maps and Crashlytics are used.
Ensure your dependencies in the app build.gradle file are as described above.

### SQLite Database

There is an SQLite database stored on the phone in order to hold user data - if they are logged in and their class schedule. This is secure because the database is held, by default, in memory only accessable by the app. The class schedule is obtained from the user logging into a Solus webview and downloading the ics file, which is then parsed for the information. The data also persists: the database is only deleted and data lost if rows/tables/the database is manually deleted, or the app is uninstalled/app data is deleted.

To create the database, SQLite was used, with a class extending SQLiteOpenHelper to manage the database in memory. DatabaseAccessor establsihes a connection to the SQLite database, and ensures that only one instance of it/DbHelper is open at once. Each table has it's schema defined by a class, where String fields contain the column names, int fields contain the column number each field is in and an attribute contains the value of each column value. One instance of this class is one row in the table.

For each schema class, there is also a manager class that handles queries for that table. This includes insertions, deletions, retrievals and updates. These operations can be done for single rows as well as the full table.

For every table, a CREATE and DELETE SQL string need to be created in SqlStringStatements, and used in DbHelper. Otherwise, the tables will not be initialized, or be able to be deleted.

For *ANY* change to the database schema to be reflected in the actual database, DATABASE_VERSION in DbHelper *MUST* be incremented. (Note it can also be decremented, although outside of testing purposes this should not be done).

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Added some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request

## Versioning

We use Semantic Versioning for versioning. For the versions available,see [the tags on this repo](https://github.com/QueensEngineeringSociety/QTap/tags).

## Authors

Carson Cook - Cloud and phone database, code maintainance
Lachlan Devir - Google Maps page, general basic page layout
Alex Ruffo - ICS file download, parsing
Michael Wang - Minor XML layout additions

See also the list of [contributors](github link) who participated in this project.

## Acknowledgements

Thank you to the Queens Engineering Society Director of IT, Robert Saunders. He has been a valuable source of information, connections and leadership for the team.

The Queens IT Services has been helpful answering questions on what information we are allowed to access and what is secure.
Finally, acknowdledgements go out to Rony Besprozvanny and Zachary Yale for creating the initial iOS QTap that this application is built off of.

##License

MIT License

Copyright (c) 2017 Engineering Society of Queen's University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
