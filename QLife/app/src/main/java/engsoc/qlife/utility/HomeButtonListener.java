package engsoc.qlife.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import engsoc.qlife.interfaces.observers.OnHomePressedListener;

/**
 * Created by Carson on 10/08/2017.
 * Class to detect when the user clicks the home button.
 */
public class HomeButtonListener {
    private Context mContext;
    private IntentFilter mFilter;
    private OnHomePressedListener mListener;
    private InnerReceiver mReceiver;

    public HomeButtonListener(Context context) {
        mContext = context;
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    /**
     * Method that makes defines the actions to take for this listener when the home button is pressed.
     *
     * @param listener OnHomePressedListener interface that holds the methods called
     *                 when the home button is clicked.
     */
    public void setOnHomePressedListener(OnHomePressedListener listener) {
        mListener = listener;
        mReceiver = new InnerReceiver();
    }

    public void startListening() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter);
        }
    }

    public void stopListening() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

    /**
     * Class that listens to Intents from the phone and when a home
     * button intent is used, calls the listener methods.
     */
    class InnerReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null && mListener != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        mListener.onHomePressed();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        mListener.onHomeLongPressed();
                    }
                }
            }
        }
    }
}
