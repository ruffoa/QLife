package engsoc.qlife.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import engsoc.qlife.ICS.DownloadICSFile;
import engsoc.qlife.ICS.ParseICS;
import engsoc.qlife.R;
import engsoc.qlife.database.GetCloudDb;
import engsoc.qlife.database.local.DatabaseAccessor;
import engsoc.qlife.database.local.buildings.Building;
import engsoc.qlife.database.local.buildings.BuildingManager;
import engsoc.qlife.database.local.cafeterias.Cafeteria;
import engsoc.qlife.database.local.cafeterias.CafeteriaManager;
import engsoc.qlife.database.local.contacts.emergency.EmergencyContact;
import engsoc.qlife.database.local.contacts.emergency.EmergencyContactsManager;
import engsoc.qlife.database.local.contacts.engineering.EngineeringContact;
import engsoc.qlife.database.local.contacts.engineering.EngineeringContactsManager;
import engsoc.qlife.database.local.food.Food;
import engsoc.qlife.database.local.food.FoodManager;
import engsoc.qlife.database.local.users.User;
import engsoc.qlife.database.local.users.UserManager;
import engsoc.qlife.interfaces.AsyncTaskObserver;
import engsoc.qlife.utility.Constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A login screen that offers login to my.queensu.ca via netid/password SSO.
 */
public class LoginActivity extends AppCompatActivity {
    private View mProgressView;
    private View mLoginFormView;
    private UserManager mUserManager;
    private ProgressDialog mProgressDialog;

    public static String mIcsUrl = "";
    public static String mUserEmail = "";

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        UserManager mUserManager = new UserManager(getBaseContext());
        if (mUserManager.getTable().isEmpty()) {
            //not logged in, show software centre login screen
            final WebView browser = findViewById(R.id.webView);
            browser.getSettings().setSaveFormData(false); //disable autocomplete - more secure, keyboard popup blocks fields
            browser.getSettings().setJavaScriptEnabled(true); // needed to properly display page / scroll to chosen location

            browser.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (browser.getUrl().contains("login.queensu.ca"))
                        browser.loadUrl("javascript:document.getElementById('queensbody').scrollIntoView();");

                    browser.evaluateJavascript("(function() { return ('<html>'+document." +
                                    "getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                    tryProcessHtml(html);
                                }
                            });
                }

            });
            browser.loadUrl("http://my.queensu.ca/software-centre");
        } else {
            attemptLogin();
        }
    }

    /**
     * Parses the html code to look for the ics file. Needed because
     * there are multiple web pages sent through this activity when logging in.
     *
     * @param html String representation of the html code of a webpage.
     */
    public void tryProcessHtml(String html) {
        if (html != null && html.contains("Class Schedule")) {
            html = html.replaceAll("\n", "");
            int index = html.indexOf("Class Schedule");
            html = html.substring(index);
            String indexing = "Your URL for the Class Schedule Subscription pilot service is ";
            index = html.indexOf(indexing) + indexing.length();
            String URL = html.substring(index, index + 200);
            mIcsUrl = URL.substring(0, URL.indexOf(".ics") + 4);
            index = URL.indexOf("/FU/") + 4;
            mUserEmail = URL.substring(index, URL.indexOf("-", index + 1));
            mUserEmail += "@queensu.ca";

            attemptLogin();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        showProgress(true);
        String[] strings = mUserEmail.split("/");
        String netid = strings[strings.length - 1].split("@")[0];

        mUserManager = new UserManager(getApplicationContext());
        if (mUserManager.getTable().isEmpty()) {
            addUserSession(netid);
            final Context context = this;
            GetCloudDb getCloudDb = new GetCloudDb(new AsyncTaskObserver() {
                @Override
                public void onTaskCompleted(Object obj) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void beforeTaskStarted() {
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setMessage("Downloading cloud database. Please wait...");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                }

                @Override
                public void duringTask(Object obj) {
                    if (obj instanceof JSONObject) {
                        JSONObject json = (JSONObject) obj;
                        cloudToPhoneDB(json);
                    }
                }
            });
            getCloudDb.execute(); //get cloud db into phone db
            getIcsFile();
        } else {        // if the user has logged in before, see if the schedule is up to date
            User userData = (User) mUserManager.getTable().get(0);
            String date = userData.getDateInit();

            if (!date.isEmpty()) { // if the user has previously downloaded a schedule
                Calendar cal = Calendar.getInstance();  // initialize a calendar variable to today's date
                Calendar lastWeek = Calendar.getInstance();
                lastWeek.add(Calendar.DAY_OF_YEAR, -7); // initialize a calendar variable to one week ago
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, hh:mm aa", Locale.ENGLISH);

                try {
                    cal.setTime(sdf.parse(date));   // try to parse the date that the user last got a schedule for
                    if (cal.after(lastWeek)) {      // if it has been more than a week since the data was downloaded...
                        getIcsFile();  // download the new schedule data in the background, this will be updated on next run, or when the day view is refreshed.
                    }
                } catch (Exception e) {
                    Log.d("HELLOTHERE", e.getMessage());

                }
            } else    // the user never downloaded a schedule successfully, thus we should download
                try {
                    getIcsFile();  // download the new schedule data right now on the main thread
                } catch (Exception e) {
                    Log.d("HELLOTHERE", e.getMessage());
                }
        }
        showProgress(false);
        startActivity(new Intent(LoginActivity.this, MainTabActivity.class));
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseAccessor.getDatabase().close(); //ensure only one database connection is ever open
    }

    /**
     * Method that adds the user and their information to the User table in the phone, logging them in.
     */
    private void addUserSession(String netid) {
        SimpleDateFormat df = new SimpleDateFormat("MMMM d, yyyy, hh:mm aa", Locale.CANADA);
        String formattedDate = df.format(Calendar.getInstance().getTime());
        User newUser = new User(1, netid, "", "", formattedDate, mIcsUrl); //ID of 1 as only ever 1 user logged in
        mUserManager = new UserManager(LoginActivity.this);
        mUserManager.insertRow(newUser);
    }

    /**
     * Method that starts an asynchronous task that downloads and parses the ICS file.
     */
    private void getIcsFile() {
        if (mIcsUrl != null && mIcsUrl.contains(".ics")) {
            final Context context = getApplicationContext();
            DownloadICSFile icsDownloader = new DownloadICSFile(new AsyncTaskObserver() {
                @Override
                public void onTaskCompleted(Object obj) {
                }

                @Override
                public void beforeTaskStarted() {
                }

                @Override
                public void duringTask(Object obj) {
                    if (obj instanceof HttpURLConnection) {
                        HttpURLConnection connection = (HttpURLConnection) obj;
                        InputStream input = null;
                        FileOutputStream output = null;
                        try {
                            // download the file
                            input = connection.getInputStream();
                            output = getApplicationContext().openFileOutput(Constants.CALENDAR_FILE, MODE_PRIVATE);

                            byte data[] = new byte[4096]; //why 4096
                            int count;
                            while ((count = input.read(data)) != -1) {
                                output.write(data, 0, count);
                            }
                        } catch (Exception e) {
                            Log.d("HELLOTHERE", e.getMessage());
                        } finally {
                            //close streams, end connection
                            try {
                                if (output != null)
                                    output.close();
                                if (input != null)
                                    input.close();
                            } catch (IOException ignored) {
                                Log.d("HELLOTHERE", ignored.getMessage());
                            }
                        }

                        final ParseICS parser = new ParseICS(context);
                        parser.parseICSData();
                        parser.getClassTypes();
                    }
                }
            });

            try {
                icsDownloader.execute(mIcsUrl).get();
            } catch (Exception e) {
                Log.d("HELLOTHERE", e.getMessage());
            }
        }
    }


    /**
     * Method that ties together methods that get specific parts of the cloud database JSON.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private void cloudToPhoneDB(JSONObject json) {
        emergencyContacts(json);
        engineeringContacts(json);
        buildings(json);
        food(json);
        cafeterias(json);
    }

    /**
     * Method that retrieves and inserts the cloud emergency contact data into the phone database.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private void emergencyContacts(JSONObject json) {
        try {
            JSONArray contacts = json.getJSONArray(EmergencyContact.TABLE_NAME);
            EmergencyContactsManager tableManager = new EmergencyContactsManager(this);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                tableManager.insertRow(new EmergencyContact(contact.getInt(EmergencyContact.ID), contact.getString(EmergencyContact.COLUMN_NAME),
                        contact.getString(EmergencyContact.COLUMN_PHONE_NUMBER), contact.getString(EmergencyContact.COLUMN_DESCRIPTION)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "EMERG: " + e);
        }
    }

    /**
     * Method that retrieves and inserts the cloud engineering contact data into the phone database.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private void engineeringContacts(JSONObject json) {
        try {
            JSONArray contacts = json.getJSONArray(EngineeringContact.TABLE_NAME);
            EngineeringContactsManager tableManager = new EngineeringContactsManager(this);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                tableManager.insertRow(new EngineeringContact(contact.getInt(EngineeringContact.ID), contact.getString(EngineeringContact.COLUMN_NAME), contact.getString(EngineeringContact.COLUMN_EMAIL),
                        contact.getString(EngineeringContact.COLUMN_POSITION), contact.getString(EngineeringContact.COLUMN_DESCRIPTION)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "ENG: " + e);
        }
    }

    /**
     * Method that retrieves and inserts the cloud buildings data into the phone database.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private void buildings(JSONObject json) {
        try {
            JSONArray buildings = json.getJSONArray(Building.TABLE_NAME);
            BuildingManager manager = new BuildingManager(this);
            for (int i = 0; i < buildings.length(); i++) {
                JSONObject building = buildings.getJSONObject(i);
                //getInt()>0 because SQL has 0/1 there, not real boolean
                manager.insertRow(new Building(building.getInt(Building.ID), building.getString(Building.COLUMN_NAME), building.getString(Building.COLUMN_PURPOSE),
                        building.getInt(Building.COLUMN_BOOK_ROOMS) > 0, building.getInt(Building.COLUMN_FOOD) > 0, building.getInt(Building.COLUMN_ATM) > 0,
                        building.getDouble(Building.COLUMN_LAT), building.getDouble(Building.COLUMN_LON)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "BUILDING: " + e);
        }
    }

    /**
     * Method that retrieves and inserts the cloud food data into the phone database.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private void food(JSONObject json) {
        try {
            JSONArray food = json.getJSONArray(Food.TABLE_NAME);
            FoodManager manager = new FoodManager(this);
            for (int i = 0; i < food.length(); i++) {
                JSONObject oneFood = food.getJSONObject(i);
                //getInt()>0 because SQL has 0/1 there, not real boolean
                manager.insertRow(new Food(oneFood.getInt(Food.ID), oneFood.getString(Food.COLUMN_NAME), oneFood.getInt(Food.COLUMN_BUILDING_ID),
                        oneFood.getString(Food.COLUMN_INFORMATION), oneFood.getInt(Food.COLUMN_MEAL_PLAN) > 0, oneFood.getInt(Food.COLUMN_CARD) > 0,
                        oneFood.getDouble(Food.COLUMN_MON_START_HOURS), oneFood.getDouble(Food.COLUMN_MON_STOP_HOURS), oneFood.getDouble(Food.COLUMN_TUE_START_HOURS),
                        oneFood.getDouble(Food.COLUMN_TUE_STOP_HOURS), oneFood.getDouble(Food.COLUMN_WED_START_HOURS), oneFood.getDouble(Food.COLUMN_WED_STOP_HOURS),
                        oneFood.getDouble(Food.COLUMN_THUR_START_HOURS), oneFood.getDouble(Food.COLUMN_THUR_STOP_HOURS), oneFood.getDouble(Food.COLUMN_FRI_START_HOURS),
                        oneFood.getDouble(Food.COLUMN_FRI_STOP_HOURS), oneFood.getDouble(Food.COLUMN_SAT_START_HOURS),
                        oneFood.getDouble(Food.COLUMN_SAT_STOP_HOURS), oneFood.getDouble(Food.COLUMN_SUN_START_HOURS), oneFood.getDouble(Food.COLUMN_SUN_STOP_HOURS)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "FOOD: " + e);
        }
    }

    /**
     * Method that retrieves and inserts the cloud cafeteria data into the phone database.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private void cafeterias(JSONObject json) {
        try {
            JSONArray cafs = json.getJSONArray(Cafeteria.TABLE_NAME);
            CafeteriaManager manager = new CafeteriaManager(this);
            for (int i = 0; i < cafs.length(); i++) {
                JSONObject caf = cafs.getJSONObject(i);
                manager.insertRow(new Cafeteria(caf.getInt(Cafeteria.ID), caf.getString(Cafeteria.COLUMN_NAME), caf.getInt(Cafeteria.COLUMN_BUILDING_ID),
                        caf.getDouble(Cafeteria.COLUMN_WEEK_BREAKFAST_START), caf.getDouble(Cafeteria.COLUMN_WEEK_BREAKFAST_STOP),
                        caf.getDouble(Cafeteria.COLUMN_FRI_BREAKFAST_START), caf.getDouble(Cafeteria.COLUMN_FRI_BREAKFAST_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SAT_BREAKFAST_START), caf.getDouble(Cafeteria.COLUMN_SAT_BREAKFAST_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SUN_BREAKFAST_START), caf.getDouble(Cafeteria.COLUMN_SUN_BREAKFAST_STOP),
                        caf.getDouble(Cafeteria.COLUMN_WEEK_LUNCH_START), caf.getDouble(Cafeteria.COLUMN_WEEK_LUNCH_STOP),
                        caf.getDouble(Cafeteria.COLUMN_FRI_LUNCH_START), caf.getDouble(Cafeteria.COLUMN_FRI_LUNCH_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SAT_LUNCH_START), caf.getDouble(Cafeteria.COLUMN_SAT_LUNCH_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SUN_LUNCH_START), caf.getDouble(Cafeteria.COLUMN_SUN_LUNCH_STOP),
                        caf.getDouble(Cafeteria.COLUMN_WEEK_DINNER_START), caf.getDouble(Cafeteria.COLUMN_WEEK_DINNER_STOP),
                        caf.getDouble(Cafeteria.COLUMN_FRI_DINNER_START), caf.getDouble(Cafeteria.COLUMN_FRI_DINNER_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SAT_DINNER_START), caf.getDouble(Cafeteria.COLUMN_SAT_DINNER_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SUN_DINNER_START), caf.getDouble(Cafeteria.COLUMN_SUN_DINNER_STOP)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "CAF: " + e);
        }
    }
}