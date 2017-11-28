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

    public static final String GET_DIBS_ROOMS = "https://queensu.evanced.info/dibsAPI/rooms";
    public static final String GET_COMM_CLASS = "https://smith.queensu.ca/bcom/academic_calendar/browse_calendar/2014_15_before/curriculum/courses_instruction.php";
    public static final String GET_ENG_CLASS = "http://calendar.engineering.queensu.ca/content.php?filter%5B27%5D=";
    public static final String FILTER_ENG_CLASS = "&filter%5B29%5D=&filter%5Bcourse_type%5D=-1&filter%5Bkeyword%5D=&filter%5B32%5D=1&filter%5Bcpage%5D=1&cur_cat_oid=2&expand=&navoid=50&search_database=Filter#acalog_template_course_filter";
    public static final String GET_ROOM_BOOKINGS = "https://queensu.evanced.info/dibsAPI/reservations/";
    public static final String GET_DATABASE="http://qtap.engsoc.queensu.ca/database/get_database.php";

    public static final int TIMEOUT = 5000;
}
