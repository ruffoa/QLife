package beta.qlife.interfaces.enforcers;

/**
 * Created by Carson on 29/07/2017.
 * Interface for fragments that are a part of a drawer layout of an activity.
 */
public interface DrawerItem {

    /**
     * Method that will deselect an item in the drawer layout.
     * Should be called from onPause().
     */
    void deselectDrawer();

    /**
     * Method that will select an item in the drawer layout.
     * Should be called from onResume().
     */
    void selectDrawer();
}
