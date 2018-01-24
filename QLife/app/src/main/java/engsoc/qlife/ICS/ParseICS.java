package engsoc.qlife.ICS;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.database.local.courses.Course.Course;
import engsoc.qlife.database.local.courses.Course.CourseManager;
import engsoc.qlife.database.local.courses.OneClass.OneClass;
import engsoc.qlife.database.local.courses.OneClass.OneClassManager;
import engsoc.qlife.interfaces.observers.AsyncTaskObserver;
import engsoc.qlife.utility.Constants;

/**
 * Created by Alex on 1/18/2017.
 * Class to parse the ICS file.
 */
public class ParseICS {
    private OneClassManager mOneClassManager;
    private CourseManager mCourseManager;
    private Context mContext;

    public ParseICS(Context context) {
        mContext = context;
        mOneClassManager = new OneClassManager(context);
        mCourseManager = new CourseManager(context);
    }

    /**
     * Method that opens and reads the ics file in the phone memory.
     *
     * @return List of Strings that contain each line of the ics file.
     */
    private ArrayList<String> readDownloadedIcs() {
        ArrayList<String> mLines = new ArrayList<>();
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
            Log.e("HELLOTHERE", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("HELLOTHER", "Can not read file: " + e.toString());
        }
        return mLines;
    }

    /**
     * Method that reads a downloaded calendar file and inserts the parsed events into
     * the phone database.
     */
    public void parseICSData() {
        if (mOneClassManager.getTable().isEmpty()) {
            mOneClassManager.deleteTable(OneClass.TABLE_NAME);

            boolean isEvent = false;
            String loc = "", name = "";
            int hour = 0, minute = 0, year = 0;
            int startHour = 0, startMinute = 0, startDay = 0, startMonth = 0;
            boolean repeatWeekly = false;
            String rDayStr = "", rMonStr = "", rYrStr = "";
            int courseId = 1;

            List<String> lines = readDownloadedIcs();
            SimpleTimeZone pdt = createTimezone();

            for (String line : lines) {
                if (line.contains(Constants.ICS_START_EVENT)) {
                    isEvent = true;
                } else if (line.contains(Constants.ICS_END_EVENT)) {
                    //end of event information, so parse and put into database
                    isEvent = false;
                    String eventStartTime = Integer.toString(startHour) + ":" + Integer.toString(startMinute);
                    String eventEndTime = Integer.toString(hour) + ":" + Integer.toString(minute);

                    Course course = new Course(mCourseManager.getTable().size() + 1, name);
                    mCourseManager.insertRow(course);

                    Calendar day = new GregorianCalendar(pdt);
                    day.set(year, startMonth, startDay);
                    addOneClass(day, courseId, name, loc, eventStartTime, eventEndTime);

                    if (repeatWeekly) {
                        Calendar startCal = new GregorianCalendar(pdt);
                        Calendar endCal = new GregorianCalendar(pdt);
                        startCal.set(year, startMonth - 1, startDay);
                        endCal.set(Integer.parseInt(rYrStr), Integer.parseInt(rMonStr) - 1, Integer.parseInt(rDayStr) + 1);
                        addAllClasses(startCal, endCal, courseId, name, loc, eventStartTime, eventEndTime);
                    }
                    repeatWeekly = false;
                    courseId++;
                } else if (line.contains((Constants.ICS_REPEAT))) {
                    repeatWeekly = true;
                    if (line.contains(Constants.ICS_REPEAT_UNTIL)) {
                        //time event stops repeating
                        String rTime = line.replaceAll(Constants.REGEX_NON_NUM, "");
                        rDayStr = rTime.substring(6, 8);
                        rMonStr = rTime.substring(4, 6);
                        rYrStr = rTime.substring(0, 4);
                    }
                } else if (isEvent) {
                    if (line.contains(Constants.ICS_LOCATION))
                        loc = line.substring(9);
                    else if (line.contains(Constants.ICS_EVENT_START)) {
                        //start time of event information
                        String sTime = line.replaceAll(Constants.REGEX_NON_NUM, "");
                        startHour = Integer.parseInt(sTime.substring(8, 10));
                        startMinute = Integer.parseInt(sTime.substring(10, 12));
                        startDay = Integer.parseInt(sTime.substring(6, 8));
                        startMonth = Integer.parseInt(sTime.substring(4, 6));
                        year = Integer.parseInt(sTime.substring(0, 4));
                    } else if (line.contains(Constants.ICS_EVENT_END)) {
                        //end of event information
                        String eventTime = line.replaceAll(Constants.REGEX_NON_NUM, "");
                        hour = Integer.parseInt(eventTime.substring(8, 10));
                        minute = Integer.parseInt(eventTime.substring(10, 12));
                    } else if (line.contains(Constants.ICS_SUMMARY)) {
                        //take part of string that is the course code
                        name = (line.substring(line.lastIndexOf(":") + 1, line.indexOf(" ", line.indexOf(" ") + 1)));
                    }
                }
            }
        }
    }

    /**
     * Helper method that adds all classes between two dates to the phone database. Used for
     * classes that repeat weekly.
     *
     * @param startDay  The class' starting day.
     * @param endDay    The class' ending day.
     * @param courseId  The ID of the course the classes are for.
     * @param name      The name of the class.
     * @param loc       The location of the class.
     * @param startTime The starting time of the class.
     * @param endTime   The ending time of the class.
     */
    private void addAllClasses(Calendar startDay, Calendar endDay, int courseId, String name,
                               String loc, String startTime, String endTime) {
        startDay.add(Calendar.DATE, 7);
        Date endDate = endDay.getTime();
        Date startDate = startDay.getTime();

        //add all events between start and end dates
        while (startDate.before(endDate)) {
            addOneClass(startDay, courseId, name, loc, startTime, endTime);
            //move to next week to add next event
            startDay.add(Calendar.DATE, 7);
            startDate = startDay.getTime();
        }
    }

    /**
     * Helper method that inserts a OneClass row into the phone database. Inserts one class for each
     * actual class - e.g. each lecture in each week has one instance.
     *
     * @param day      Calendar holding the day when the class takes place
     * @param courseId The ID of the course this class is for.
     * @param name     The name of the class.
     * @param loc      The location of the class.
     * @param start    The start time of the class.
     * @param end      The end time of the class.
     */
    private void addOneClass(Calendar day, int courseId, String name, String loc, String start, String end) {
        int startDay = day.get(Calendar.DAY_OF_MONTH);
        int startMonth = day.get(Calendar.MONTH);
        int startYear = day.get(Calendar.YEAR);

        OneClass newClass = new OneClass(mOneClassManager.getTable().size() + 1, name, loc, start,
                end, Integer.toString(startDay),
                Integer.toString(startMonth + 1), Integer.toString(startYear));
        newClass.setBuildingID(15);       //TODO delete later, this is temporary
        newClass.setCourseID(courseId);
        mOneClassManager.insertRow(newClass);
    }

    private SimpleTimeZone createTimezone() {
        SimpleTimeZone pdt = new SimpleTimeZone(-8 * Constants.ONE_HOUR, TimeZone.getDefault().getID());
        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * Constants.ONE_HOUR);
        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * Constants.ONE_HOUR);
        return pdt;
    }

    /**
     * Method that sets the name of a class in the database.
     *
     * @param htmlRes   The HTML of the page that contains the name of the class
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
                String className = parseClassName(htmlRes.substring(index));

                c.setName(className);
                c.setSetName(true);
                mCourseManager.updateRow(c.getId(), c);
            }
        }
    }

    /**
     * Helper method that parses out useless information from a class name.
     *
     * @param className The full class name to be stripped down.
     * @return The stripped version of the class name.
     */
    private String parseClassName(String className) {
        Pattern pattern = Pattern.compile("[A-Z]+ [0-9]+ "); //matches '<discipline> <course code> '
        Matcher matcher = pattern.matcher(className);
        if (matcher.find()) {
            //remove ' F(or W) | <credits>' at end
            className = className.substring(matcher.end(), className.indexOf(" |") - 2);
        } else {
            //just removes F/W
            className = className.substring(0, className.length() - 2);
        }
        return className;
    }

    /**
     * Method that retrieves the class disciplines and passes them to another
     * method to be properly parsed and entered into the phone database.
     */
    public void getClassDisciplines() {
        ArrayList<DatabaseRow> courses = mCourseManager.getTable();
        ArrayList<String> disciplines = new ArrayList<>();

        for (DatabaseRow data : courses) {
            Course course = (Course) data;
            String temp = course.getCode().substring(0, course.getCode().indexOf(" "));
            if (!(disciplines.contains(temp) && course.isSetName())) {
                disciplines.add(temp);
            }
        }

        if (!disciplines.isEmpty()) {
            for (final String discipline : disciplines) {
                if (discipline.length() > 0) {
                    GetCourseInfo cInfo = new GetCourseInfo(new AsyncTaskObserver() {
                        @Override
                        public void onTaskCompleted(Object obj) {
                            if (obj != null && obj.getClass() == String.class) {
                                String result = (String) obj;
                                addClassName(result, discipline);
                            }
                        }

                        @Override
                        public void beforeTaskStarted() {
                        }

                        @Override
                        public void duringTask(Object obj) {
                        }
                    });
                    cInfo.execute(discipline);
                }
            }
        }
    }
}
