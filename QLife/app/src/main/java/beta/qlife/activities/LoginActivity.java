package beta.qlife.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import beta.qlife.ICS.DownloadICSFile;
import beta.qlife.ICS.ParseICS;
import beta.qlife.R;
import beta.qlife.database.CloudDbToPhone;
import beta.qlife.database.GetCloudDb;
import beta.qlife.database.local.DatabaseAccessor;
import beta.qlife.database.local.users.User;
import beta.qlife.database.local.users.UserManager;
import beta.qlife.interfaces.observers.AsyncTaskObserver;
import beta.qlife.utility.Constants;
import beta.qlife.utility.DateChecks;

/**
 * A login screen that offers login to my.queensu.ca via netid/password SSO.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String TAG_REDOWNLOAD_SCHEDULE = "redownload_schedule";

    private UserManager mUserManager;
    private ProgressBar mProgress;
    private ImageView mProgressImage;
    private TextView mProgressText;

    public static String mIcsUrl = "";
    public static String mUserEmail = "";

    private Boolean shouldRedownloadSchedule = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserManager = new UserManager(getBaseContext());
        if (mUserManager.getTable().isEmpty()) {
            browserLogin();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            shouldRedownloadSchedule = bundle.getBoolean(LoginActivity.TAG_REDOWNLOAD_SCHEDULE);
        }
    }

    @Override
    protected void onStart() {
        if (!mUserManager.getTable().isEmpty() && mUserManager.getTable().get(0) != null) {
            attemptAppLogin();
        }

        super.onStart();
    }

    /**
     * Helper method that launches a browser session in app for the user to login
     * to my.queensu.ca.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void browserLogin() {
        final WebView browser = findViewById(R.id.webView);
        browser.getSettings().setSaveFormData(false); //disable autocomplete - more secure, keyboard popup blocks fields
        browser.getSettings().setJavaScriptEnabled(true); //needed to properly display page/scroll to chosen location

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (browser.getUrl().contains(Constants.QUEENS_LOGIN))
                    browser.loadUrl(Constants.GET_QUEENS_BODY_JS);

                browser.evaluateJavascript(Constants.GET_HTML_TAGS_JS,
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                tryProcessHtml(html);
                            }
                        });
            }
        });
        browser.loadUrl(Constants.QUEENS_SOFTWARE_CENTRE);
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
            String indexing = "https://mytimetable.queensu.ca/timetable/";
            int index = html.indexOf(indexing);
            String URL = html.substring(index, index + 200);
            mIcsUrl = URL.substring(0, URL.indexOf(".ics") + 4);
            index = URL.indexOf("/FU/") + 4;
            mUserEmail = URL.substring(index, URL.indexOf("-", index + 1));
            mUserEmail += "@queensu.ca";

            attemptAppLogin();
        } else {
            Log.d(TAG, "tryProcessHtml: Error: could not find calendar link in the page HTML! " + html);
        }

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptAppLogin() {
        final WebView browser = findViewById(R.id.webView);
        browser.setVisibility(View.GONE);

        RelativeLayout progressContainer = findViewById(R.id.login_progress_container);
        progressContainer.setVisibility(View.VISIBLE);

        mProgress = findViewById(R.id.login_progress);

        mProgressImage = findViewById(R.id.login_logo);
        mProgressImage.setImageResource(R.drawable.ic_qtap_logo_svg);

        mProgressText = findViewById(R.id.login_progress_text);
        mProgressText.setText(R.string.login_download_data_from_cloud_message);

        String[] strings = mUserEmail.split("/");
        String netid = strings[strings.length - 1].split("@")[0];

        mUserManager = new UserManager(getApplicationContext());
        if (mUserManager.getTable().isEmpty()) {
            //no one logged in
            addUserSession(netid);
            final Context context = this;

            GetCloudDb getCloudDb = new GetCloudDb(new AsyncTaskObserver() {
                @Override
                public void onTaskCompleted(Object obj) {
                    Log.d("CloudDB", "Done downloading cloud DB");
                }

                @Override
                public void beforeTaskStarted() {
                }

                @Override
                public void duringTask(Object obj) {
                    Log.d("CloudDB", "start downloading cloud DB");

                    if (obj instanceof JSONObject) {
                        JSONObject json = (JSONObject) obj;
                        CloudDbToPhone.cloudToPhoneDB(json, context);
                    }
                }
            });
            getCloudDb.execute(); //get cloud db into phone db
            mProgressText.setText(R.string.login_downloading_class_schedule_message);
            getIcsFile();
        } else {        // if the user has logged in before, see if the schedule is up to date
            User userData = (User) mUserManager.getTable().get(0);
            String date = userData.getDateInit();
            if (mIcsUrl.equals(""))
                mIcsUrl = userData.getIcsURL(); // get the URL from the DB so that we can re-download the schedule and info if we need to

//            mIcsUrl = "https://raw.githubusercontent.com/ruffoa/QLife/master/testCal.ics"; // ToDo: Remove this temporary link

            if (!date.isEmpty()) {
                //if downloaded calendar, but we are close to a term rollover, re-download it (class are probably added by now)
                DateChecks dateChecks = new DateChecks();

                try {
                    // Redownload the schedule if we are close to a new school term, or if the user clicked the "redownload" button in the settings page
                    if (dateChecks.dateIsCloseToNewTerm(date) || shouldRedownloadSchedule) {
                        getIcsFile();
                        updateScheduleDownloadDate();
                    } else   // launch the main activity, we are done here :)
                    {
                        startActivity(new Intent(LoginActivity.this, MainTabActivity.class));
                    }
                } catch (Exception e) {
                    Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                    startActivity(new Intent(LoginActivity.this, MainTabActivity.class));
                }
            } else    // the user never downloaded a schedule successfully, thus we should download
                try {
                    getIcsFile();  // download the new schedule data right now on the main thread
                    updateScheduleDownloadDate();
                } catch (Exception e) {
                    Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                    startActivity(new Intent(LoginActivity.this, MainTabActivity.class));
                }
        }
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
        SimpleDateFormat df = new SimpleDateFormat("MMMM d, yyyy, h:mm aa", Locale.CANADA);
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
            mProgressText.setText(R.string.login_downloading_class_schedule_message);

            DownloadICSFile icsDownloader = new DownloadICSFile(new AsyncTaskObserver() {
                @Override
                public void onTaskCompleted(Object obj) {
                    Log.d("ICS Downloader", "Done downloading the ICS file, starting main activity");
                    startActivity(new Intent(LoginActivity.this, MainTabActivity.class));
                }

                @Override
                public void beforeTaskStarted() {
                }

                @Override
                public void duringTask(Object obj) {
                    Log.d("ICS Downloader", "Starting ICS file download");

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
                            Log.d("ICS Downloader", e.getMessage());
                        } finally {
                            //close streams, end connection
                            try {
                                if (output != null)
                                    output.close();
                                if (input != null)
                                    input.close();
                            } catch (IOException ignored) {
                                Log.d("ICS Downloader", ignored.getMessage());
                            }
                        }

                        final ParseICS parser = new ParseICS(context);

                        Log.d("ICS Downloader", "Starting ICS file parsing");
                        updateLoginMessageOnUiThread(R.string.login_parse_ics_file, 40);
                        parser.parseICSData();

                        Log.d("ICS Downloader", "Starting to get class names");
                        updateLoginMessageOnUiThread(R.string.login_create_class_db_message, 80);
                        parser.getClassDisciplines();
                        Log.d("ICS Downloader", "Done tasks, exiting duringTask function");
                        startActivity(new Intent(LoginActivity.this, MainTabActivity.class));
                    }
                }
            });

            try {
                icsDownloader.execute(mIcsUrl);
            } catch (Exception e) {
                Log.d("ICS Downloader", e.getMessage());
                startActivity(new Intent(LoginActivity.this, MainTabActivity.class));
            }
        }
    }

    private void updateLoginMessageOnUiThread(final Integer messageId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressText.setText(getString(messageId));
            }
        });
    }

    private void updateLoginMessageOnUiThread(final Integer messageId, final Integer progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressText.setText(getString(messageId));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mProgress.setProgress(progress, true);
                }
            }
        });
    }

    private void updateScheduleDownloadDate() {
        SimpleDateFormat df = new SimpleDateFormat("MMMM d, yyyy, h:mm aa", Locale.CANADA);
        String formattedDate = df.format(Calendar.getInstance().getTime());
        User nUser = ((User) mUserManager.getTable().get(0));
        nUser.setDateInit(formattedDate);
        mUserManager.updateRow(nUser.getId(), nUser);
    }
}