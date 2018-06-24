package beta.qlife.FunctionalityTests;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import beta.qlife.R;
import beta.qlife.activities.MainTabActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class DrawerTest {
    @Rule
    public ActivityTestRule<MainTabActivity> mMainActivity = new ActivityTestRule<>(MainTabActivity.class);

    @Test
    public void testUi() {// Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.drawer_layout));
        onView(withId(R.id.nav_view));
    }

    @Test
    public void testMonth() {
        clickDrawerItem(R.id.nav_month);
        checkBarTitle(R.string.fragment_month);
    }

    @Test
    public void testDay() {
        clickDrawerItem(R.id.nav_day);
        checkBarTitle(R.string.fragment_day);
    }

    @Test
    public void testMap() {
        clickDrawerItem(R.id.nav_map);
        //doesn't have title bar or draw, so check activity loaded and go back to one with drawer
        onView(withId(R.id.map));
        Espresso.pressBack();
    }

    @Test
    public void testBuildings() {
        clickDrawerItem(R.id.nav_buildings);
        checkBarTitle(R.string.fragment_buildings);
    }

    @Test
    public void testCaf() {
        clickDrawerItem(R.id.nav_cafeterias);
        checkBarTitle(R.string.fragment_cafeterias);
    }

    @Test
    public void testFood() {
        clickDrawerItem(R.id.nav_day);
        checkBarTitle(R.string.fragment_day);
    }

    @Test
    public void testRooms() {
        clickDrawerItem(R.id.nav_rooms);
        checkBarTitle(R.string.fragment_ilc_rooms);
    }

    @Test
    public void testTools() {
        clickDrawerItem(R.id.nav_food);
        checkBarTitle(R.string.fragment_food);
    }

    private void clickDrawerItem(int id) {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(id));
    }

    private void checkBarTitle(int stringId) {
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(getInstrumentation().getTargetContext().getString(stringId))));
    }
}
