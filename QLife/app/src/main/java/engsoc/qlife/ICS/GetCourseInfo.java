package engsoc.qlife.ICS;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alex on 8/6/2017.
 * Asynchronously goes to course web pages and parses for the class title.
 */
public class GetCourseInfo extends AsyncTask<String, Void, String> {
    private static final int TIMEOUT = 5000;
    private static final String GET_COMM_CLASS = "https://smith.queensu.ca/bcom/academic_calendar/browse_calendar/2014_15_before/curriculum/courses_instruction.php";
    private static final String GET_ENG_CLASS = "http://calendar.engineering.queensu.ca/content.php?filter%5B27%5D=";
    private static final String FILTER_ENG_CLASS = "&filter%5B29%5D=&filter%5Bcourse_type%5D=-1&filter%5Bkeyword%5D=&filter%5B32%5D=1&filter%5Bcpage%5D=1&cur_cat_oid=2&expand=&navoid=50&search_database=Filter#acalog_template_course_filter";

    @Override
    protected String doInBackground(String... info) {
        try {
            String courseType = info[0];
            String htmlStr;

            //call php script on server that gets info from cloud database
            if (courseType.contains("COMM"))
                htmlStr = getHTML(GET_COMM_CLASS);
            else
                htmlStr = getHTML(GET_ENG_CLASS + courseType + FILTER_ENG_CLASS);

            return htmlStr;
        } catch (Exception e) {
            Log.d("HELLOTHERE", "Error: " + e);
        }
        return null;
    }

    private String getHTML(String url) {
        HttpURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-length", "0");
            con.setRequestProperty("Connection", "close");
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.setConnectTimeout(TIMEOUT);
            con.setReadTimeout(TIMEOUT);
            con.connect();
            int status = con.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    return sb.toString();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            Log.d("HELLOTHERE", "bad io " + e);
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }
}


