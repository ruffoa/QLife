package engsoc.qlife.Matchers;

import android.view.View;

import org.hamcrest.Matcher;

/**
 * Class to implement own match assertions for Espresso.
 */
public class TestMatchers {
    public static Matcher<View> withDrawable(final int id) {
        return new DrawableMatcher(id);
    }

    //assertion for image view with no drawable attached
    public static Matcher<View> noDrawable() {
        return new DrawableMatcher(-1);
    }
}
