package engsoc.qlife.interfaces.observers;

/**
 * Created by Carson on 2017-11-26.
 * Interface used to dynamically change post AsyncTask execution function call
 */
public interface AsyncTaskObserver {
    //should be called from onPostExecute()
    void onTaskCompleted(Object obj);

    //should be called in onPreExecute()
    void beforeTaskStarted();

    //should be called in doInBackground()
    void duringTask(Object obj);
}
