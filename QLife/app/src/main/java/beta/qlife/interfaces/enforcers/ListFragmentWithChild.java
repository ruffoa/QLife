package beta.qlife.interfaces.enforcers;

import android.os.Bundle;
import android.view.View;

/**
 * Created by Carson on 07/08/2017.
 * Interface that defines an ListFragment that shows details when an item is clicked.
 */

public interface ListFragmentWithChild extends ListFragment {

    /**
     * Method that handles logic for when an item within the ListView is clicked.
     * Should be called from onListItemClick().
     *
     * @param view View that holds the item that was clicked.
     */
    void onListItemChosen(View view);

    /**
     * Method that handles packing a Bundle with the data for the details of the item clicked on.
     * Should be called from onListItemChosen().
     *
     * @param view View that holds the children Views holding the data to pack.
     * @return Bundle holding all the data for the new fragment that will display this data.
     */
    Bundle setDataForOneItem(View view);
}
