package beta.qlife.database.local;

import beta.qlife.database.local.rooms.Room;
import beta.qlife.database.local.buildings.Building;
import beta.qlife.database.local.cafeterias.Cafeteria;
import beta.qlife.database.local.contacts.emergency.EmergencyContact;
import beta.qlife.database.local.contacts.engineering.EngineeringContact;
import beta.qlife.database.local.courses.Course.Course;
import beta.qlife.database.local.courses.OneClass.OneClass;
import beta.qlife.database.local.food.Food;
import beta.qlife.database.local.users.User;

/**
 * Created by Carson on 18/02/2017.
 * Class to hold SQL statements. All are public static final.
 * Referenced from DbHelper only, this is just to limit the size of DbHelper.
 */
public class SqlStringStatements {

    public static final String PHONE_DATABASE_NAME = "QTAP_PHONE.db";

    //create table statements
    static final String CREATE_COURSES = "CREATE TABLE " + Course.TABLE_NAME + "(" +
            Course.ID + " INTEGER PRIMARY KEY," + Course.COLUMN_CODE + " TEXT," + Course.COLUMN_NAME
            + " TEXT," + Course.COLUMN_SET_NAME + " INTEGER);";

    static final String CREATE_USERS = "CREATE TABLE " + User.TABLE_NAME + "(" +
            User.ID + " INTEGER PRIMARY KEY," + User.COLUMN_NETID + " TEXT," +
            User.COLUMN_FIRST_NAME + " TEXT," + User.COLUMN_LAST_NAME + " TEXT," +
            User.COLUMN_DATE_INIT + " TEXT," + User.COLUMN_ICS_URL + " TEXT);";

    static final String CREATE_CLASSES = "CREATE TABLE " + OneClass.TABLE_NAME + "(" +
            OneClass.ID + " INTEGER PRIMARY KEY," + OneClass.COLUMN_CLASS_TYPE + " TEXT," +
            OneClass.COLUMN_BUILDING_ID + " INT," + OneClass.COLUMN_ROOM_NUM + " TEXT," +
            OneClass.COLUMN_START_TIME + " TEXT," + OneClass.COLUMN_END_TIME + " TEXT," +
            OneClass.COLUMN_DAY + " TEXT," + OneClass.COLUMN_MONTH + " TEXT," + OneClass.COLUMN_YEAR +
            " TEXT," + OneClass.COLUMN_COURSE_ID + " INT," + OneClass.COLUMN_HAS_NAME + " TEXT);";

    static final String CREATE_ENGINEERING_CONTACTS = "CREATE TABLE " + EngineeringContact.TABLE_NAME + "(" + EngineeringContact.ID +
            " INTEGER PRIMARY KEY," + EngineeringContact.COLUMN_NAME + " TEXT," + EngineeringContact.COLUMN_EMAIL + " TEXT,"
            + EngineeringContact.COLUMN_POSITION + " TEXT," + EngineeringContact.COLUMN_DESCRIPTION + " TEXT);";

    static final String CREATE_EMERGENCY_CONTACTS = "CREATE TABLE " + EmergencyContact.TABLE_NAME + "(" + EmergencyContact.ID +
            " INTEGER PRIMARY KEY," + EngineeringContact.COLUMN_NAME + " TEXT," + EmergencyContact.COLUMN_PHONE_NUMBER + " TEXT,"
            + EmergencyContact.COLUMN_DESCRIPTION + " TEXT);";

    static final String CREATE_BUILDINGS = "CREATE TABLE " + Building.TABLE_NAME + "(" + Building.ID + " INTEGER PRIMARY KEY," +
            Building.COLUMN_NAME + " TEXT," + Building.COLUMN_PURPOSE + " TEXT," + Building.COLUMN_BOOK_ROOMS + " INTEGER," + Building.COLUMN_FOOD
            + " INTEGER," + Building.COLUMN_ATM + " INTEGER," + Building.COLUMN_LAT + " REAL," + Building.COLUMN_LON + " REAL);";

    static final String CREATE_FOOD = "CREATE TABLE " + Food.TABLE_NAME + "(" + Food.ID + " INTEGER PRIMARY KEY," + Food.COLUMN_NAME + " TEXT," + Food.COLUMN_MEAL_PLAN +
            " INTEGER," + Food.COLUMN_CARD + " INTEGER," + Food.COLUMN_INFORMATION + " TEXT," + Food.COLUMN_BUILDING_ID + " INTEGER," + Food.COLUMN_MON_START_HOURS + " REAL," +
            Food.COLUMN_MON_STOP_HOURS + " REAL," + Food.COLUMN_TUE_START_HOURS + " REAL," + Food.COLUMN_TUE_STOP_HOURS + " REAL," + Food.COLUMN_WED_START_HOURS + " REAL," +
            Food.COLUMN_WED_STOP_HOURS + " REAL," + Food.COLUMN_THUR_START_HOURS + " REAL," + Food.COLUMN_THUR_STOP_HOURS + " REAL," + Food.COLUMN_FRI_START_HOURS + " REAL," +
            Food.COLUMN_FRI_STOP_HOURS + " REAL," + Food.COLUMN_SAT_START_HOURS + " REAL," + Food.COLUMN_SAT_STOP_HOURS + " REAL," + Food.COLUMN_SUN_START_HOURS + " REAL," +
            Food.COLUMN_SUN_STOP_HOURS + " REAL);";

    static final String CREATE_CAFETERIAS = "CREATE TABLE " + Cafeteria.TABLE_NAME + "(" + Cafeteria.ID + " INTEGER PRIMARY KEY," + Cafeteria.COLUMN_NAME + " TEXT," +
            Cafeteria.COLUMN_BUILDING_ID + " INTEGER," + Cafeteria.COLUMN_WEEK_BREAKFAST_START + " REAL," +
            Cafeteria.COLUMN_WEEK_BREAKFAST_STOP + " REAL," + Cafeteria.COLUMN_FRI_BREAKFAST_START + " REAL," + Cafeteria.COLUMN_FRI_BREAKFAST_STOP + " REAL," + Cafeteria.COLUMN_SAT_BREAKFAST_START + " REAL," +
            Cafeteria.COLUMN_SAT_BREAKFAST_STOP + " REAL," + Cafeteria.COLUMN_SUN_BREAKFAST_START + " REAL," + Cafeteria.COLUMN_SUN_BREAKFAST_STOP + " REAL," + Cafeteria.COLUMN_WEEK_LUNCH_START + " REAL," +
            Cafeteria.COLUMN_WEEK_LUNCH_STOP + " REAL," + Cafeteria.COLUMN_FRI_LUNCH_START + " REAL," + Cafeteria.COLUMN_FRI_LUNCH_STOP + " REAL," + Cafeteria.COLUMN_SAT_LUNCH_START + " REAL," +
            Cafeteria.COLUMN_SAT_LUNCH_STOP + " REAL," + Cafeteria.COLUMN_SUN_LUNCH_START + " REAL," + Cafeteria.COLUMN_SUN_LUNCH_STOP + " REAL," + Cafeteria.COLUMN_WEEK_DINNER_START + " REAL,"
            + Cafeteria.COLUMN_WEEK_DINNER_STOP + " REAL," + Cafeteria.COLUMN_FRI_DINNER_START + " REAL," + Cafeteria.COLUMN_FRI_DINNER_STOP + " REAL," + Cafeteria.COLUMN_SAT_DINNER_START + " REAL,"
            + Cafeteria.COLUMN_SAT_DINNER_STOP + " REAL," + Cafeteria.COLUMN_SUN_DINNER_START + " REAL," + Cafeteria.COLUMN_SUN_DINNER_STOP + " REAL);";

    static final String CREATE_ILC_ROOM_INFO = "CREATE TABLE " + Room.TABLE_NAME + "(" + Room.ID + " INTEGER PRIMARY KEY," +
            Room.COLUMN_BUILDING_ID + " INTEGER," + Room.COLUMN_DESCRIPTION + " TEXT," + Room.COLUMN_MAP_URL + " TEXT," + Room.COLUMN_NAME + " TEXT," + Room.COLUMN_PIC_URL
            + " TEXT," + Room.COLUMN_ROOM_ID + " INTEGER);";

    //Delete table statements
    static final String DELETE_COURSES = "DROP TABLE IF EXISTS " + Course.TABLE_NAME;
    static final String DELETE_USERS = "DROP TABLE IF EXISTS " + User.TABLE_NAME;
    static final String DELETE_CLASSES = "DROP TABLE IF EXISTS " + OneClass.TABLE_NAME;
    static final String DELETE_ENGINEERING_CONTACTS = "DROP TABLE IF EXISTS " + EngineeringContact.TABLE_NAME;
    static final String DELETE_EMERGENCY_CONTACTS = "DROP TABLE IF EXISTS " + EmergencyContact.TABLE_NAME;
    static final String DELETE_BUILDINGS = "DROP TABLE IF EXISTS " + Building.TABLE_NAME;
    static final String DELETE_FOOD = "DROP TABLE IF EXISTS " + Food.TABLE_NAME;
    static final String DELETE_CAFETERIAS = "DROP TABLE IF EXISTS " + Cafeteria.TABLE_NAME;
    static final String DELETE_ILC_ROOM_INFO = "DROP TABLE IF EXISTS " + Room.TABLE_NAME;
}
