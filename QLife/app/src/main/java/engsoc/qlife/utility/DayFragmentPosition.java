package engsoc.qlife.utility;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;

import engsoc.qlife.interfaces.observers.OnHomePressedListener;

/**
 * Created by Carson on 2018-01-24.
 * Data structure used to remember the day for which DayFragment instances should show
 */
public class DayFragmentPosition {
    private int mInstances = 0;

    private HomeButtonListener mHomeListener;
    private SparseIntArray mArray;

    public DayFragmentPosition(Context context) {
        mArray = new SparseIntArray();
        mHomeListener = new HomeButtonListener(context);
        mHomeListener.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                mArray.put(mInstances, 0);
                Log.d("HELLOTHERE", "SDF");
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
    }

    public int getInstanceChange() {
        if (mArray != null) {
            return mArray.get(mInstances);
        }
        return 0;
    }

    public void changeInstance(int change) {
        if (mInstances >= 0 && mArray != null) {
            mArray.put(mInstances, change);
        }
    }

    public void addInstance() {
        mInstances++;
        if (mInstances > 0 && mArray != null)
            mArray.put(mInstances, 0);
    }

    public void removeInstance() {
        if (mInstances > 0 && mArray != null) {
            mArray.delete(mInstances);
            mInstances--;
        }
    }

    public void homeStartListening() {
        if (mHomeListener != null)
            mHomeListener.startListening();
    }

    public void homeStopListening() {
        if (mHomeListener != null)
            mHomeListener.stopListening();
    }
}
