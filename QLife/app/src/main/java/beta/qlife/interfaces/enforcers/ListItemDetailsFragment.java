package beta.qlife.interfaces.enforcers;

/**
 * Created by Carson on 28/07/2017.
 * Interface for fragments that show the details
 * of an item after one was clicked in a fragment list.
 */
public interface ListItemDetailsFragment {
    /**
     * Method that will put phone database table information into the views of a list fragment.
     * Called from onCreateView() after the view has been inflated.
     */
    void addDataToViews();
}
