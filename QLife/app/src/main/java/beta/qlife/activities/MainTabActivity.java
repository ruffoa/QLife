package beta.qlife.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import beta.qlife.R;
import beta.qlife.ui.fragments.MapsFragment;
import beta.qlife.ui.fragments.RoomsFragment;
import beta.qlife.utility.Util;
import beta.qlife.database.local.DatabaseAccessor;
import beta.qlife.database.local.users.User;
import beta.qlife.database.local.users.UserManager;
import beta.qlife.interfaces.enforcers.ActivityHasOptionsMenu;
import beta.qlife.ui.fragments.BuildingsFragment;
import beta.qlife.ui.fragments.CafeteriasFragment;
import beta.qlife.ui.fragments.DayFragment;
import beta.qlife.ui.fragments.FoodFragment;
import beta.qlife.ui.fragments.MonthFragment;
import beta.qlife.ui.fragments.StudentToolsFragment;

/**
 * Activity holding most of the app.
 * contains the drawer that navigates user to fragments with map, schedule, info etc.
 */
public class MainTabActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityHasOptionsMenu {

    private boolean mToActivity;

    private DrawerLayout mDrawer;
    private FragmentManager mFragManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        mDrawer = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mFragManager = getSupportFragmentManager();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        displayView(R.id.nav_day); //start at calendar view

        User u = (new UserManager(this)).getRow(1); //only ever one person in database, so ID always 1
        View header = navigationView.getHeaderView(0);// get the existing headerView
        TextView name = header.findViewById(R.id.navHeaderAccountName);
        name.setText(u.getNetid());
    }

    @Override
    public void onBackPressed() {
        mToActivity = false;
        mDrawer.closeDrawer(GravityCompat.START);
        if (mFragManager.getBackStackEntryCount() <= 1) {
            //last item in back stack, so close app
            mToActivity = true;
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mToActivity = false;
        switch (item.getItemId()) {
            case R.id.settings:
                mToActivity = true;
                startActivity(new Intent(MainTabActivity.this, SettingsActivity.class));
                break;
            case R.id.about:
                mToActivity = true;
                startActivity(new Intent(MainTabActivity.this, AboutActivity.class));
                break;
            case R.id.review:
                mToActivity = true;
                startActivity(new Intent(MainTabActivity.this, ReviewActivity.class));
                break;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    /**
     * Logic to decide what fragment to show, based on what drawer item user clicked.
     * will attach new fragment.
     * Contains logic to know if on the home fragment or not, for back pressed logic.
     * changes title of screen as well.
     *
     * @param viewId the ID of the drawer item user clicked.
     */
    private void displayView(int viewId) {
        mToActivity = false;
        boolean isNoMarginFragment = false;
        Fragment fragment = null;

        switch (viewId) {
            case R.id.nav_month:
                fragment = new MonthFragment();
                break;
            case R.id.nav_map:
//                isNoMarginFragment = true;
                fragment = new MapsFragment();
                break;
            case R.id.nav_day:
                fragment = new DayFragment();
                break;
            case R.id.nav_tools:
                fragment = new StudentToolsFragment();
                break;
            case R.id.nav_buildings:
                fragment = new BuildingsFragment();
                break;
            case R.id.nav_cafeterias:
                fragment = new CafeteriasFragment();
                break;
            case R.id.nav_food:
                fragment = new FoodFragment();
                break;
            case R.id.nav_rooms:
                fragment = new RoomsFragment();
                break;

        }

        if (fragment != null) {
            //if chose a fragment, add to back stack
            if (isNoMarginFragment) {
//                removeMarginsFromFragment();
            } else {
                FragmentTransaction ft = mFragManager.beginTransaction();
                ft.addToBackStack(null).replace(R.id.content_frame, fragment);
                ft.commit();
            }
        }
        mDrawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseAccessor.getDatabase().close(); //ensures only one database connection is open at a time
    }

    public boolean isToActivity() {
        return mToActivity;
    }

    @Override
    public void inflateOptionsMenu(Menu menu) {
        Util.inflateOptionsMenu(R.menu.main_tab, menu, getMenuInflater());
    }

//    void removeMarginsFromFragment() {
//        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
//        if ((frameLayout != null)) {
//            final MapsActivity firstFragment = new MapsActivity();
//            firstFragment.setArguments(getIntent().getExtras());
//
//            mFragManager.beginTransaction().addToBackStack(null).replace(R.id.content_frame, firstFragment).commit();
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frameLayout.getWidth(), frameLayout.getHeight());;
//            //left, top, right, bottom
//            params.setMargins(0, 0, 0, 0); //overriding margins to 0
//            frameLayout.setLayoutParams(params);
//        }
//    }

}
