package engsoc.qlife.ICS;

import engsoc.qlife.interfaces.AsyncTaskObserver;
import engsoc.qlife.utility.Constants;
import engsoc.qlife.utility.async.DownloadTextTask;

/**
 * Created by Alex on 8/6/2017.
 * Asynchronously goes to course web pages and parses for the class title.
 */
public class GetCourseInfo extends DownloadTextTask<String, String> {

    GetCourseInfo(AsyncTaskObserver observer) {
        super(observer);
    }

    @Override
    protected String backgroundTask(String val) {
        try {
            String htmlStr;

            //call php script on server that gets info from cloud database
            if (val.contains("COMM"))
                htmlStr = getText(Constants.GET_COMM_CLASS);
            else //non eng/comm courses come here too, just get ignored
                htmlStr = getText(Constants.GET_ENG_CLASS + val + Constants.FILTER_ENG_CLASS);
            return htmlStr;
        } catch (Exception e) {
            return null;
        }
    }
}


