package engsoc.qlife.ICS;

import android.content.Context;
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

import engsoc.qlife.R;

/**
 * Created by Alex on 8/6/2017.
 */

public class getCourseInfo extends AsyncTask<String, Void, String> {

    /**
     * Created by Alex Ruffo on 23/09/2017.
     * Async task that downloads and parses the cloud database into the phone database.
     */

    private static final String TAG_SUCCESS = "Success";

    private Context mContext;

    public getCourseInfo(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... info) {
        try {
//            Calendar cal;
//            cal = Calendar.getInstance();
            String courseType = info[0];
            String htmlStr;
            String getDescFlag = info[1];
            //call php script on server that gets info from cloud database
            if (courseType.contains("COMM"))
                htmlStr = getHTML(mContext.getString(R.string.cal_get_comm_class_names), 5000);
            else
                htmlStr = getHTML(mContext.getString(R.string.cal_get_eng_class_names) + courseType + mContext.getString(R.string.cal_get_eng_class_names_end), 5000);

            if (getDescFlag.contains("description")) {
                if (courseType.contains("ELEC")) {
                    htmlStr = getHTML(mContext.getString(R.string.cal_get_elec_core_course_descriptions), 5000);
                }
                else if (courseType.contains("CMPE")) {
                    htmlStr = getHTML(mContext.getString(R.string.cal_get_cmpe_core_course_descriptions), 5000);
                }
            }
            return htmlStr;

            //            if (json != null) {
//                return json;
//            }
        } catch (
                Exception e)

        {
            Log.d("HELLOTHERE", "Error: " + e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    private String getHTML(String url, int timeout) {
        HttpURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-length", "0");
            con.setRequestProperty("Connection", "close");
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
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


//    public JSONArray getRoomTimes(JSONArray json, int roomID) {
//        try {
//            Calendar cal;
//            cal = Calendar.getInstance();
//
//            String jsonStr = getJSON(mContext.getString(R.string.dibs_get_rooms_times) + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "-" + roomID, 5000);
//            JSONArray json = new JSONArray(jsonStr);
//            return json;
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}


