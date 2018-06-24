package beta.qlife.interfaces.enforcers;

import android.view.Menu;

/**
 * Created by Carson on 08/08/2017.
 * Interface that defines an Activity that has an options menu.
 */
public interface ActivityHasOptionsMenu {

    /**
     * Method that handles adding items to the options menu.
     * @param menu Menu to be inflated.
     *
     * Should be called from onCreateOptionsMenu().
     *
     * Should call Util.inflateOptionsMenu().
     */
    void inflateOptionsMenu(Menu menu);
}
