package beta.qlife.utility;

/**
 * Created by Carson on 2018-01-24.
 * Class used to hold a starting and ending time to represent a slice of time.
 */
public class TimeSlot {
    private String mStart;
    private String mEnd;

    public TimeSlot(String mStart, String mEnd) {
        this.mStart = mStart;
        this.mEnd = mEnd;
    }

    public String getStart() {
        return mStart;
    }

    public void setStart(String mStart) {
        this.mStart = mStart;
    }

    public String getEnd() {
        return mEnd;
    }

    public void setEnd(String mEnd) {
        this.mEnd = mEnd;
    }
}
