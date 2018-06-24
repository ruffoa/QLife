package beta.qlife.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import beta.qlife.R;
import beta.qlife.database.local.users.UserManager;
import io.fabric.sdk.android.Fabric;

/**
 * Activity that holds on app start UI/info.
 * Checks/stores if this is the first time the user has run the app.
 * If it is, shows some introduction pages.
 */
public class StartupActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private LinearLayout mDotsLayout;
    private int[] mLayouts;
    private Button mButtonSkip, mButtonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        //Check user already logged in, don't need startup screens
        if (!new UserManager(this).getTable().isEmpty()) {
            launchLogin();
        }

        setContentView(R.layout.activity_startup);

        // layouts of all welcome sliders, can add more layouts
        mLayouts = new int[]{
                R.layout.initial_slide_welcome,
                R.layout.initial_slide_class,
                R.layout.initial_slide_campus,
                R.layout.initial_slide_cloud,
                R.layout.initial_slide_rooms,
                R.layout.initial_slide_it_team
        };
        setViews();
        addBottomDots(0); //add all bottom dots
        setStatusBar(); // making notification bar transparent
    }

    /**
     * Adds dots to the bottom of the introduction pages, individually.
     *
     * @param currentPage The introduction page currently being shown, aka the one to
     *                    add dots to.
     */
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[mLayouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        mDotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml(getString(R.string.ellipses)));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            mDotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive[currentPage]);
        }
    }

    private int getItem(int i) {
        return mViewPager.getCurrentItem() + i;
    }

    /**
     * Method that sends the user to the login screen.
     */
    private void launchLogin() {
        startActivity(new Intent(StartupActivity.this, LoginActivity.class));
        finish();
    }

    //defines the on page change listener for the view pager
    ViewPager.OnPageChangeListener mViewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == mLayouts.length - 1) {
                // last page. make button text to GOT IT
                mButtonNext.setText(getString(R.string.start));
                mButtonSkip.setVisibility(View.GONE);
            } else {
                // there are still pages left
                mButtonNext.setText(getString(R.string.next));
                mButtonSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * Method that makes the status bar change colour to match the screen.
     */
    private void setStatusBar() {
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make status bar change colour
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setViews() {
        mViewPager = findViewById(R.id.view_pager);
        mDotsLayout = findViewById(R.id.layoutDots);
        mButtonSkip = findViewById(R.id.btn_skip);
        mButtonNext = findViewById(R.id.btn_next);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        mViewPager.setAdapter(myViewPagerAdapter);
        mViewPager.addOnPageChangeListener(mViewPagerPageChangeListener);

        mButtonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLogin();
            }
        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page -> if so, home screen will be launched
                int current = getItem(+1);
                if (current < mLayouts.length) {
                    // move to next screen
                    mViewPager.setCurrentItem(current);
                } else {
                    launchLogin();
                }
            }
        });
    }

    /**
     * Class that makes a custom View Pager adapter.
     * No new methods, however overrides and changes (with no super call) instantiateItem(),
     * getCount(), isViewFromObject() and destroyItem().
     * These changes allow for custom layouts, and manipulating those custom layouts.
     */
    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater mLayoutInflater;

        @Override
        @Nullable
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (mLayoutInflater != null) {
                View view = mLayoutInflater.inflate(mLayouts[position], container, false);
                container.addView(view);
                return view;
            }
            return null;
        }

        @Override
        public int getCount() {
            return mLayouts.length;
        }

        @Override
        public boolean isViewFromObject(@Nullable View view, @Nullable Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(@Nullable ViewGroup container, int position, @Nullable Object object) {
            if (container != null && object != null) {
                View view = (View) object;
                container.removeView(view);
            }
        }
    }
}
