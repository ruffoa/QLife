package engsoc.qlife.interfaces;

/**
 * Created by Carson on 2017-11-26.
 * Interface used to dynamically change post AsyncTask execution function call
 */
public interface AsyncTaskObserver {
    void onTaskCompleted(Object obj);

    void beforeTaskStarted();

    void duringTask(Object obj);
}
