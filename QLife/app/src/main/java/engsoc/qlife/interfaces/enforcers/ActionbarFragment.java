package engsoc.qlife.interfaces.enforcers;

/**
 * Created by Carson on 01/08/2017.
 * Interface that enforces a Fragment to change the Action bar title.
 */
public interface ActionbarFragment {

    /**
     * This method should get the Activity the implementing Fragment is attached to and
     * call Util.setActionbarTitle().
     * Should be called from onCreateView() after the view has been inflated.
     */
    void setActionbarTitle();
}
