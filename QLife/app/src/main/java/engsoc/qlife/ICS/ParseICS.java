package engsoc.qlife.ICS;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.database.local.courses.Course.Course;
import engsoc.qlife.database.local.courses.Course.CourseManager;
import engsoc.qlife.database.local.courses.OneClass.OneClass;
import engsoc.qlife.database.local.courses.OneClass.OneClassManager;
import engsoc.qlife.database.local.users.User;
import engsoc.qlife.database.local.users.UserManager;
import engsoc.qlife.interfaces.AsyncTaskObserver;
import engsoc.qlife.ui.fragments.StudentToolsFragment;
import engsoc.qlife.utility.Constants;

/**
 * Created by Alex on 1/18/2017.
 * Class to parse the ICS file.
 */
public class ParseICS {
    private final String TAG = StudentToolsFragment.class.getSimpleName();
    private OneClassManager mOneClassManager;
    private CourseManager mCourseManager;

    private Context mContext;

    public ParseICS(Context context) {
        this.mContext = context;
    }

    /**
     * Method that opens and reads the ics file in the phone memory.
     *
     * @return List of Strings that contain each line of the ics file.
     */
    private List<String> readDownloadFile() {
        List<String> mLines = new ArrayList<>();
        try {
            InputStream inputStream = mContext.openFileInput(Constants.CALENDAR_FILE);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;

                while ((receiveString = bufferedReader.readLine()) != null) {
                    mLines.add(receiveString);
                }

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }
        return mLines;
    }

    public void parseICSData() {
        UserManager userManager = new UserManager(this.mContext);
        ArrayList<DatabaseRow> userTable = userManager.getTable();
        mOneClassManager = new OneClassManager(mContext);
        mCourseManager = new CourseManager(mContext);

        if (mOneClassManager.getTable().isEmpty()) {
            mOneClassManager.deleteTable(OneClass.TABLE_NAME);

            boolean isEvent = false;
            String loc = "", name = "";
            int hour = 0, minute = 0, year = 0;
            int sHour = 0, sMinute = 0, sDay = 0, sMonth = 0;
            boolean repeatWeekly = false;
            String rDayStr = "", rMonStr = "", rYrStr = "";

            List<String> lines = readDownloadFile();
            int test = 1;

            for (String string : lines) {
                if (string.contains("BEGIN:VEVENT")) {
                    isEvent = true;
                } else if (string.contains("END:VEVENT")) {
                    isEvent = false;

                    String tempTime = Integer.toString(sHour) + ":" + Integer.toString(sMinute);
                    String tempEndTime = Integer.toString(hour) + ":" + Integer.toString(minute);

                    Course course = new Course(mCourseManager.getTable().size() + 1, name);
                    mCourseManager.insertRow(course);

                    OneClass one = new OneClass(mOneClassManager.getTable().size() + 1,
                            name, loc, tempTime, tempEndTime, Integer.toString(sDay), Integer.toString(sMonth), Integer.toString(year));
                    one.setBuildingID(15);       // TODO delete later, this is temporary
                    one.setCourseID(test);
                    mOneClassManager.insertRow(one);

                    if (repeatWeekly) {
                        // get the supported ids for GMT-08:00 (Pacific Standard Time)
                        String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
                        // if no ids were returned, something is wrong. get out.
                        if (ids.length == 0)
                            System.exit(0);

                        // create a Pacific Standard Time time zone
                        SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);

                        // set up rules for Daylight Saving Time
                        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
                        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

                        // create a GregorianCalendar with the Pacific Daylight time zone
                        // and the current date and time
                        Calendar cal2 = new GregorianCalendar(pdt);
                        Calendar cal = new GregorianCalendar(pdt);

                        cal.set(year, sMonth - 1, sDay);

                        cal2.set(Integer.parseInt(rYrStr), Integer.parseInt(rMonStr) - 1, Integer.parseInt(rDayStr) + 1);

                        Date endDate = cal2.getTime();
                        Date date1;
                        cal.add(Calendar.DATE, 7);
                        date1 = cal.getTime();

                        while (date1.before(endDate)) {
                            sDay = cal.get(Calendar.DAY_OF_MONTH);
                            sMonth = cal.get(Calendar.MONTH);
                            year = cal.get(Calendar.YEAR);

                            one = new OneClass(mOneClassManager.getTable().size() + 1,
                                    name, loc, tempTime, tempEndTime, Integer.toString(sDay), Integer.toString(sMonth + 1), Integer.toString(year));
                            one.setBuildingID(15);       // delete later, this is temporary
                            one.setCourseID(test);
                            mOneClassManager.insertRow(one);
                            cal.add(Calendar.DATE, 7);
                            date1 = cal.getTime();
                        }
                    }
                    repeatWeekly = false;
                    test++;

                } else if (string.contains(("RRULE:FREQ=WEEKLY;"))) {
                    repeatWeekly = true;

                    if (string.contains("UNTIL=")) {
                        String rTime = string.replaceAll("[^0-9]", "");
                        rDayStr = rTime.substring(6, 8);
                        rMonStr = rTime.substring(4, 6);
                        rYrStr = rTime.substring(0, 4);
                    }

                } else if (isEvent) {
                    if (string.contains("LOCATION"))
                        loc = string.substring(9);
                    else if (string.contains("DTSTART")) {
                        String sTime = string.replaceAll("[^0-9]", "");
                        sHour = Integer.parseInt(sTime.substring(8, 10));
                        sMinute = Integer.parseInt(sTime.substring(10, 12));
                        sDay = Integer.parseInt(sTime.substring(6, 8));
                        sMonth = Integer.parseInt(sTime.substring(4, 6));
                        year = Integer.parseInt(sTime.substring(0, 4));
                    } else if (string.contains("DTEND")) {
                        String eTime = string.replaceAll("[^0-9]", "");
                        hour = Integer.parseInt(eTime.substring(8, 10));
                        minute = Integer.parseInt(eTime.substring(10, 12));

                    } else if (string.contains("SUMMARY")) {
                        //take part of string that is the course code
                        name = (string.substring(string.lastIndexOf(":") + 1, string.indexOf(" ", string.indexOf(" ") + 1)));
                    }
                }
            }
            SimpleDateFormat df = new SimpleDateFormat("MMMM d, yyyy, hh:mm aa", Locale.CANADA);
            String formattedDate = df.format(Calendar.getInstance().getTime());

            User user = (User) userTable.get(0); //only ever 1 user
            String uName = user.getFirstName();
            String uLastName = user.getLastName();
            String uNetID = user.getNetid();
            String uURL = user.getIcsURL();

            User nUser = new User(1, uNetID, uName, uLastName, formattedDate, uURL); //only one user ever logged in, so ID is 1
            userManager.updateRow(user, nUser);
        }
    }

    /**
     * Method that sets the name of a class in the database.
     *
     * @param htmlRes The HTML of the page that contains the name of the class
     * @param classType The type of class - for example, APSC.
     */
    private void addClassName(String htmlRes, String classType) {
        if (htmlRes == null || htmlRes.length() == 0)
            return;

        CourseManager mCourseManager = new CourseManager(mContext);
        ArrayList<DatabaseRow> courses = mCourseManager.getTable();

        for (DatabaseRow course : courses) {
            Course c = (Course) course;
            if (c.getCode().contains(classType) && htmlRes.contains(c.getCode())) {
                int index = htmlRes.indexOf(c.getCode());
                String className = htmlRes.substring(index);
                className = className.substring(0, className.indexOf("|"));
                Course backup = c;

                c.setName(className);
                c.setSetName(true);
                mCourseManager.updateRow(backup, c);
            }
        }
    }

    public void getClassTypes() {
        mOneClassManager = new OneClassManager(mContext);
        mCourseManager = new CourseManager(mContext);
        ArrayList<DatabaseRow> courses = mCourseManager.getTable();
        ArrayList<String> types = new ArrayList<>();

        for (DatabaseRow data : courses) {
            Course c = (Course) data;
            String temp = c.getCode().substring(0, c.getCode().indexOf(" "));
            if (!types.contains(temp) && !c.isSetName())
                //string builder not used, as string becomes too long for the builder
                types.add(temp);
        }

        if (!types.isEmpty()) {
            for (final String str : types) {
                if (str.length() > 0) {
                    GetCourseInfo cInfo = new GetCourseInfo(new AsyncTaskObserver() {
                        @Override
                        public void onTaskCompleted(Object obj) {
                            if (obj != null && obj.getClass() == String.class) {
                                String result = (String) obj;
                                addClassName(result, str);
                            }
                        }

                        @Override
                        public void beforeTaskStarted() {
                        }

                        @Override
                        public void duringTask(Object obj) {
                        }
                    });
                    cInfo.execute(str);
                }
            }
        }
    }
}
