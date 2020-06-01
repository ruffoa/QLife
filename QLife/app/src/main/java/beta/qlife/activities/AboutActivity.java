package beta.qlife.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import beta.qlife.R;
import beta.qlife.interfaces.enforcers.OptionsMenuActivity;
import beta.qlife.utility.Util;

/**
 * Created by Carson on 06/06/2017.
 * Displays text providing information about the app.
 */
public class AboutActivity extends AppCompatActivity implements OptionsMenuActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setBackButton();
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
        Util.inflateOptionsMenu(R.menu.about_menu, menu, getMenuInflater());
    }
}
