package engsoc.qlife.ui.recyclerview.list_objects;

/**
 * Created by Carson on 2018-01-24.
 * Class used to hold information used in DayFragment.
 */
public class DayObject extends DataObject {

    private String mClassCode;
    private String mWhere;
    private String mName;
    private long mClassId;
    private boolean mHasName;

    public DayObject(String classCode, String where, long classId, boolean hasName, String name) {
        mClassCode = classCode;
        mWhere = where;
        mClassId = classId;
        mHasName = hasName;
        mName = name;
    }

    public String getClassCode() {
        return mClassCode;
    }

    public void setClassCode(String mClassCode) {
        this.mClassCode = mClassCode;
    }

    public String getWhere() {
        return mWhere;
    }

    public void setWhere(String mWhere) {
        this.mWhere = mWhere;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public long getClassId() {
        return mClassId;
    }

    public void setClassId(long mClassId) {
        this.mClassId = mClassId;
    }

    public boolean isHasName() {
        return mHasName;
    }

    public void setHasName(boolean mHasName) {
        this.mHasName = mHasName;
    }
}
