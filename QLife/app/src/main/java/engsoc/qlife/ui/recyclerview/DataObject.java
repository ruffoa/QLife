package engsoc.qlife.ui.recyclerview;

/**
 * Created by Alex Ruffo on 3/29/2017.
 * Object used to send information between RecyclerView classes for ILC rooms.
 */
public class DataObject {
    private String mText1;
    private String mText2;
    private String mHeader;
    private int mId;
    private long mClassId;
    private boolean mHasTV;
    private boolean mHasName;
    private String mDescription;

    public DataObject(String text1, String text2) {
        mText1 = text1;
        mText2 = text2;
    }

    public DataObject(String text1, String text2, int id, boolean hasTV, String header) {
        mText1 = text1;
        mText2 = text2;
        mId = id;
        mHasTV = hasTV;
        mHeader = header;
    }
    public DataObject(String text1, String text2, long classId) {
        mText1 = text1;
        mText2 = text2;
        mClassId = classId;
    }

    public DataObject(String text1, String text2, long classId, boolean hasName) {
        mText1 = text1;
        mText2 = text2;
        mClassId = classId;
        mHasName = hasName;
    }

    public DataObject(String text1, String text2, int id, boolean hasTV, String header, String Description) {
        mText1 = text1;
        mText2 = text2;
        mId = id;
        mHasTV = hasTV;
        mHeader = header;
        mDescription = Description;
    }

    public String getmText1() {
        return mText1;
    }

    public int getID() {
        try {
            return mId;
        } catch (Exception e) {
            return -1;
        }
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }
    public void setmDescription(String description) {
        this.mDescription = description;
    }
    public String getDescription() {return mDescription; }
    public long getClassId(){return mClassId; }
    public boolean getHasTV() {return mHasTV; }
    public boolean getHasName() {return mHasName; }
    public String getHeader() {return mHeader; }
    public void setHeader(String header) {this.mHeader = header; }
    public void setHasTV(boolean hasTV) {this.mHasTV = hasTV; }
    public void setHasName(boolean hasName) {this.mHasName = hasName; }
}