package engsoc.qlife.interfaces.enforcers;

/**
 * Created by Carson on 08/08/2017.
 * Interface that defines an Activity accessed from the options menu.
 */
public interface OptionsMenuActivity extends ActivityHasOptionsMenu {
    /**
     * Method that sets the back button in the action bar.
     * Should be called from onCreate() after the view in set.
     * Should call Util.setBackButton().
     */
    void setBackButton();

    /**
     * Method that handles logic for when an item in the options menu is clicked.
     * Should be called from onOptionsItemClick().
     * Should call Util.handleOptionsClick().
     *
     * @param itemId The R.id of the item clicked.
     */
    void handleOptionsClick(int itemId);
}
