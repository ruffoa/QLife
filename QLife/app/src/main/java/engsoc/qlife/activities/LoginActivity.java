package engsoc.qlife.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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

import engsoc.qlife.ICS.DownloadICSFile;
import engsoc.qlife.ICS.ParseICS;
import engsoc.qlife.R;
import engsoc.qlife.database.GetCloudDb;
import engsoc.qlife.database.local.DatabaseAccessor;
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
            (new GetCloudDb(LoginActivity.this)).execute(); //get cloud db into phone db
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
}