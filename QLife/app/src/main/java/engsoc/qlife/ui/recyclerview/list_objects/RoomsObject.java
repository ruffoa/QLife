package engsoc.qlife.ui.recyclerview.list_objects;

/**
 * Created by Carson on 2018-01-24.
 * Used to hold information for each ILC rooms for the rooms RecyclerView.
 */
public class RoomsObject extends DataObject {
    private String mName;
    private String mDescription;
    private int mId;
    private boolean mTv;
    private String mHeader;

    public RoomsObject(String name, String description, int id, boolean tv, String header) {
        mName = name;
        mDescription = description;
        mId = id;
        mTv = tv;
        mHeader = header;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public boolean isTv() {
        return mTv;
    }

    public void setTv(boolean mTv) {
        this.mTv = mTv;
    }

    public String getHeader() {
        return mHeader;
    }

    public void setHeader(String header) {
        mHeader = header;
    }
}
