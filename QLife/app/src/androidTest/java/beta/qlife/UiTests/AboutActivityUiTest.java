package beta.qlife.UiTests;

import android.content.Context;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import beta.qlife.R;
import beta.qlife.activities.AboutActivity;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests for the AboutActivity UI and UI flow.
 *
 * ASSERTION: Assumes the user is logged in.
 */
@RunWith(AndroidJUnit4.class)
public class AboutActivityUiTest {

    private Context mContext;

    @Before
    public void initContext(){
        mContext = getInstrumentation().getTargetContext();
    }

    @Rule
    public ActivityTestRule<AboutActivity> mAboutActivityRule = new ActivityTestRule<>(AboutActivity.class);

    @Test
    public void infoShown() {
        onView(ViewMatchers.withId(R.id.info_text)).check(matches(withText(validInfoString())));
    }

    @Test
    public void goToSettings(){
        openOptionsMenu();
        onView(withText(getString(R.string.activity_settings))).perform(click());
        onView(withId(R.id.settings_activity));
    }

    @Test
    public void goToReview(){
        openOptionsMenu();
        onView(withText(getString(R.string.activity_review))).perform(click());
        onView(withId(R.id.activity_review));
    }

    private String getString(int id){
        return mContext.getString(id);
    }

    private String validInfoString(){
        return getString(R.string.about_app);
    }

    private void openOptionsMenu(){
        openActionBarOverflowOrOptionsMenu(mContext);
    }
}
