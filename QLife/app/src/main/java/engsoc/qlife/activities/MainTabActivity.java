package engsoc.qlife.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import engsoc.qlife.R;
import engsoc.qlife.ui.fragments.RoomsFragment;
import engsoc.qlife.utility.Util;
import engsoc.qlife.database.local.DatabaseAccessor;
import engsoc.qlife.database.local.users.User;
import engsoc.qlife.database.local.users.UserManager;
import engsoc.qlife.interfaces.IQLActivityHasOptionsMenu;
import engsoc.qlife.ui.fragments.BuildingsFragment;
import engsoc.qlife.ui.fragments.CafeteriasFragment;
import engsoc.qlife.ui.fragments.DayFragment;
import engsoc.qlife.ui.fragments.FoodFragment;
import engsoc.qlife.ui.fragments.MonthFragment;
import engsoc.qlife.ui.fragments.StudentToolsFragment;

/**
 * Activity holding most of the app.
 * contains the drawer that navigates user to fragments with map, schedule, info etc.
 */
public class MainTabActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IQLActivityHasOptionsMenu {

    private boolean mToActivity;

    private DrawerLayout mDrawer;
    private FragmentManager mFragManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mFragManager = getSupportFragmentManager();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        displayView(R.id.nav_day); //start at calendar view

        User u = (new UserManager(this)).getRow(1); //only ever one person in database
        View header = navigationView.getHeaderView(0);// get the existing headerView
        TextView name = header.findViewById(R.id.navHeaderAccountName);
        name.setText(u.getNetid());
    }

    @Override
    public void onBackPressed() {
        mToActivity = false;
        mDrawer.closeDrawer(GravityCompat.START);
        if (mFragManager.getBackStackEntryCount() <= 1) { //last item in back stack, so close app
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
        Fragment fragment = null;
        switch (viewId) {
            case R.id.nav_month:
                fragment = new MonthFragment();
                break;
            case R.id.nav_map:
                mToActivity = true;
                startActivityForResult(new Intent(MainTabActivity.this, MapsActivity.class), 1);
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
            FragmentTransaction ft = mFragManager.beginTransaction();
            ft.addToBackStack(null).replace(R.id.content_frame, fragment);
            ft.commit();
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
}
