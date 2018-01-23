package engsoc.qlife.utility;

/**
 * Created by Carson on 2017-11-26.
 * Static class for constants.
 */
public class Constants {
    public static final String CALENDAR_FILE = "cal.ics";
    public static final String TV = "TV";
    public static final String PROJECTOR = "projector";
    public static final String HTTP = "http";

    public static final String ARC_FULL = "Athletics and Recreation Centre (ARC)";
    public static final String ARC = "ARC";
    public static final String JDUC_FULL = "John Deutsch Centre (JDUC)";
    public static final String JDUC = "JDUC";

    public static final String QUEENS_LOGIN = "login.queensu.ca";
    public static final String QUEENS_SOFTWARE_CENTRE = "http://my.queensu.ca/software-centre";

    public static final String GET_QUEENS_BODY_JS = "javascript:document.getElementById('queensbody').scrollIntoView();";
    public static final String GET_HTML_TAGS_JS = "(function() { return ('<html>'+document." +
            "getElementsByTagName('html')[0].innerHTML+'</html>'); })();";

    public static final String GET_DIBS_ROOMS = "https://queensu.evanced.info/dibsAPI/rooms";
    public static final String GET_COMM_CLASS = "https://smith.queensu.ca/bcom/academic_calendar/browse_calendar/2014_15_before/curriculum/courses_instruction.php";
    public static final String GET_ENG_CLASS = "http://calendar.engineering.queensu.ca/content.php?filter%5B27%5D=";
    public static final String FILTER_ENG_CLASS = "&filter%5B29%5D=&filter%5Bcourse_type%5D=-1&filter%5Bkeyword%5D=&filter%5B32%5D=1&filter%5Bcpage%5D=1&cur_cat_oid=2&expand=&navoid=50&search_database=Filter#acalog_template_course_filter";
    public static final String GET_ROOM_BOOKINGS = "https://queensu.evanced.info/dibsAPI/reservations/";
    public static final String GET_DATABASE = "http://qlife.engsoc.queensu.ca/database/get_database.php";

    public static final int TIMEOUT = 5000;
    public static final int ONE_HOUR = 60 * 60 * 1000;

    public static final String REGEX_NON_NUM = "[^0-9]";

    public static final String ICS_START_EVENT = "BEGIN:VEVENT";
    public static final String ICS_END_EVENT = "END:VEVENT";
    public static final String ICS_REPEAT = "RRULE:FREQ=WEEKLY;";
    public static final String ICS_REPEAT_UNTIL = "UNTIL=";
    public static final String ICS_LOCATION = "LOCATION";
    public static final String ICS_EVENT_START = "DTSTART";
    public static final String ICS_EVENT_END = "DTEND";
    public static final String ICS_SUMMARY = "SUMMARY";
}
