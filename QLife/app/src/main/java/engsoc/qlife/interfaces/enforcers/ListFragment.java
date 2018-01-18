package engsoc.qlife.interfaces.enforcers;

/**
 * Created by Carson on 01/08/2017.
 * Interface that defines a fragment that displays a list of items.
 */
public interface ListFragment {

    /**
     * Method that handles iterating through a table and setting the ListView data.
     * Should be called from onCreateView() after the view is inflated.
     */
    void inflateListView();
}
