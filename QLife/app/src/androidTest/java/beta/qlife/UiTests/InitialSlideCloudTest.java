package beta.qlife.UiTests;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import beta.qlife.R;
import beta.qlife.activities.StartupActivity;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static beta.qlife.Matchers.TestMatchers.withDrawable;

@RunWith(AndroidJUnit4.class)
public class InitialSlideCloudTest {
    @Rule
    public ActivityTestRule<StartupActivity> mStartupActivity = new ActivityTestRule<>(StartupActivity.class);

    @Test
    public void viewsTest() {
        onView(withId(R.id.initial_slide_cloud));
        onView(withId(R.id.cloud_pic)).check(matches(withDrawable(R.drawable.ic_cloud_outline, R.color.white)));
        onView(withId(R.id.cloud_title)).check(matches(withText(getString(R.string.slide_cloud_title))));
        onView(withId(R.id.cloud_text)).check(matches(withText(getString(R.string.slide_cloud_text))));
        onView(withId(R.id.btn_next)).check(matches(withText(getString(R.string.next))));
        onView(withId(R.id.btn_skip)).check(matches(withText(getString(R.string.skip))));
        //TODO add checking dots
    }

    @Test
    public void skipTest() {
        onView(withId(R.id.initial_slide_cloud));
        onView(withId(R.id.btn_skip)).perform(click());
        onView(withId(R.id.login_activity));
    }

    @Test
    public void nextTest() {
        onView(withId(R.id.initial_slide_cloud));
        onView(withId(R.id.btn_next)).perform(click());
        onView(withId(R.id.initial_slide_rooms));
    }

    private String getString(int id) {
        return getInstrumentation().getTargetContext().getString(id);
    }
}
