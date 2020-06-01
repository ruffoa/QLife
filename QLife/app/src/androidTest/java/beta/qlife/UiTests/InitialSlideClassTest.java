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
public class InitialSlideClassTest {
    @Rule
    public ActivityTestRule<StartupActivity> mStartupActivity = new ActivityTestRule<>(StartupActivity.class);

    @Test
    public void viewsTest() {
        onView(withId(R.id.initial_slide_class));
        onView(withId(R.id.calendar)).check(matches(withDrawable(R.drawable.ic_calendar, R.color.white)));
        onView(withId(R.id.class_title)).check(matches(withText(getString(R.string.slide_schedule_title))));
        onView(withId(R.id.class_text)).check(matches(withText(getString(R.string.slide_schedule_text))));
        onView(withId(R.id.btn_next)).check(matches(withText(getString(R.string.next))));
        onView(withId(R.id.btn_skip)).check(matches(withText(getString(R.string.skip))));
        //TODO add checking dots
    }

    @Test
    public void skipTest() {
        onView(withId(R.id.initial_slide_class));
        onView(withId(R.id.btn_skip)).perform(click());
        onView(withId(R.id.login_activity));
    }

    @Test
    public void nextTest() {
        onView(withId(R.id.initial_slide_class));
        onView(withId(R.id.btn_next)).perform(click());
        onView(withId(R.id.initial_slide_campus));
    }

    private String getString(int id) {
        return getInstrumentation().getTargetContext().getString(id);
    }
}
