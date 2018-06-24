# Activities

In this package all of the app's activities can be found. These define the main pages in the application, and are used to control what UI is shown (based on what fragments and xml layoutsare used).

## AboutActivity

AboutActivity is simply a wall of text that gives the user some information about the app and how it works.

It has an options menu allowing access to the Settings and Review activities. It also has a back button
that when clicked acts the same as pressing the phone's back button.

As this activity is accessed from the options menu, it implements IQLOptionsMenuActivity.

## LoginActivity

LoginActivity forces the user to login to Solus before they can access the app.

This activity shows a web view of Solus and allows the user to log into Solus. Upon Solus log in, the URL of the ICS file is found by manually looking for the user's NetID on the page, since the URL contains the NetID. Then, that link is
used to download the ICS file. After this point, the user is logged into the application. The user is given a loading screen to allow for the time to download and parse the file into the phone SQLite database.
When the file is parsed, the user is sent to MainTabActivity.

LoginActivity queries the phone database to see if there is an ongoing user sessions, and if not, insert a new session 
when the user logs in.

LoginActivity currently has an AsyncTask subclass that calls the AsyncTask DownloadICSFile class and starts a user session.
This process is not actually asynchronous however. Early attempt at removing the subclass have resulted in strange errors
so it currently is still there.

## MainTabActivity

This activity is where most of the time is spent in the app.

It contains a drawer that allows access to the Day, Month, Buildings, Cafeterias, Food and Student Tools fragments, as well as
the Maps activity. It also has an options menu that give access the Settings, Review and About activities.

As this activity has an options menu, it implements IQLActivityHasOptionsMenu.

The aforementioned fragments in the drawer are all attached to MainTabActivity.

The initial screen when entering MainTabActivity is the Day fragment.

## MapsActivity

This activity shows a Google maps view with markers at each building on the Queen's campus.

As MapsActivity has a MapView, it implements IQLMapView. MapsActivity does not implement IQLDrawerItem even though
it is accessed from the drawer, as it does not use the methods in that interface.

It queries the phone database for building names, latitudes and longitudes, which are used to set the markers
in the map.

## ReviewActivity

ReviewActivity displays text prompting the user to review the app or suggestion improvements.

It has an options menu that gives access to the Settings and About activities, as well as a back button.
It also has a button that sends the user to the Google Play Store to review the app, and a button 
that shows a web view of a suggestion form.

As ReviewActivity is access from an options menu, it implements IQLOptionsMenuActivity.

When the user submits proper suggestion form data, the suggestion is inserted into the cloud database.

## SettingsActivity

SettingsActivity shows the logged in user, the last time a login was performed and provides a logout button.

It has an options menu that gives access to the About and Review activities as well as a back button. When the 
user clicks the logout button they are sent to StartupActivity.

As SettingsActivity is accessed from an options menu, it implements IQLOptionsMenuActivity.

This activity deletes the entire phone database when the user clicks the logout button.

## StartupActivity

This activity is shown the first time the user launches the app. It contains a few slides with some pictures and text that describes the uses of the app and gives a bit of an introduction.

When the user skips the screens or continues past the last slide they are sent to LoginActivity to login to the app.