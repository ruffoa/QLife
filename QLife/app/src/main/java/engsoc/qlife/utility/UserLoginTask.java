package engsoc.qlife.utility;

import android.os.AsyncTask;

import engsoc.qlife.interfaces.AsyncTaskObserver;

/**
 * Class used to log the user in asynchronously. This login process is not actually asynchronous. This
 * need to be refactored; however, early attempts have caused a strange crash.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Void> {
    private AsyncTaskObserver mObserver;
    private String mNetid;

    public UserLoginTask(String email, AsyncTaskObserver observer) {
        //email right now is a url with netid inside, parsing for the netid
        String[] strings = email.split("/");
        mNetid = strings[strings.length - 1].split("@")[0];
        mObserver = observer;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mObserver.onTaskCompleted(mNetid);
    }
}