package beta.qlife.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import beta.qlife.R;
import beta.qlife.database.local.DatabaseAccessor;
import beta.qlife.database.local.DatabaseRow;
import beta.qlife.database.local.SqlStringStatements;
import beta.qlife.database.local.users.User;
import beta.qlife.database.local.users.UserManager;
import beta.qlife.interfaces.enforcers.OptionsMenuActivity;
import beta.qlife.utility.Util;

/**
 * Activity for the settings. Can see NetID, time since calendar was last synced and can logout here
 */
public class SettingsActivity extends AppCompatActivity implements OptionsMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearData(v);
                Toast.makeText(SettingsActivity.this, getString(R.string.logged_out), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, StartupActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        Button redownloadSchedule = findViewById(R.id.redownload_schedule);
        redownloadSchedule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.putExtra(LoginActivity.TAG_REDOWNLOAD_SCHEDULE, true);
                startActivity(intent);
                finishAffinity();
            }
        });

        setBackButton();
        setTextViews();
    }

    /**
     * Method that will remove all information from the last session. Deletes the
     * database, clears Internet information and clears the back button stack.
     *
     * @param v The view that holds the app context.
     */
    private void clearData(View v) {
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        WebView web = new WebView(getApplicationContext());
        web.clearFormData();
        web.clearHistory();
        web.clearCache(true);

        v.getContext().deleteDatabase(SqlStringStatements.PHONE_DATABASE_NAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseAccessor.getDatabase().close(); //ensure only one database connection is ever open
    }

    /**
     * Method that sets the current user and last login time in the TextViews.
     */
    private void setTextViews() {
        UserManager mUserManager = new UserManager(this.getApplicationContext());
        ArrayList<DatabaseRow> users = mUserManager.getTable();
        User user = (User) users.get(0); //only ever one user in database
        TextView netID = findViewById(R.id.netID);
        TextView date = findViewById(R.id.login_date);
        date.setText(user.getDateInit());
        netID.setText(user.getNetid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        handleOptionsClick(item.getItemId());
        return true;
    }

    @Override
    public void setBackButton() {
        Util.setBackButton(getSupportActionBar());
    }

    @Override
    public void handleOptionsClick(int itemId) {
        Util.handleOptionsClick(this, itemId);
    }

    @Override
    public void inflateOptionsMenu(Menu menu) {
        Util.inflateOptionsMenu(R.menu.settings_menu, menu, getMenuInflater());
    }
}