package engsoc.qlife.ui.recyclerview;

/**
 * Created by Alex Ruffo on 3/29/2017.
 */

public class DataObject {
    private String mText1;
    private String mText2;
    private String mHeader;
    private int mId;
    private boolean mHasTV;

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
    public boolean getHasTV() {return mHasTV; }
    public String getHeader() {return mHeader; }
    public void setHeader(String header) {this.mHeader = header; }
    public void setHasTV(boolean hasTV) {this.mHasTV = hasTV; }
}