package beta.qlife.ICS;

import java.util.Calendar;
import java.util.Date;

import beta.qlife.interfaces.observers.AsyncTaskObserver;
import beta.qlife.utility.Constants;
import beta.qlife.utility.async.DownloadTextTask;

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
            if (val.contains("COMM")) { // this one is a bit more complex, as you have to perform a php post request with the right inputs for it to return back anything useful
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                if (cal.get(Calendar.MONTH) < 8)    // if the current month is before september (it is currently still the previous school year)
                    year --;
                String yearStr = "year=" + year + "_" + (year % 100 + 1);
                htmlStr = postText(Constants.GET_COMM_CLASS, yearStr);
            }
            else //non eng/comm courses come here too, just get ignored // ToDo: Get ArtSci courses to work properly
                htmlStr = getText(Constants.GET_ENG_CLASS + val + Constants.FILTER_ENG_CLASS);
            return htmlStr;
        } catch (Exception e) {
            return null;
        }
    }
}


