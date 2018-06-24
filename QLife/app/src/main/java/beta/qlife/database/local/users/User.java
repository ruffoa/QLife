package beta.qlife.database.local.users;

import beta.qlife.database.local.DatabaseRow;

/**
 * Created by Carson on 29/01/2017.
 * Defines the schema for the Courses table. Currently holds a field for the class title,
 * room number, class time and ID.
 */
public class User extends DatabaseRow {
    //table schema
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_NETID = "netid";
    public static final String COLUMN_FIRST_NAME = "firstName";
    public static final String COLUMN_LAST_NAME = "lastName";
    public static final String COLUMN_DATE_INIT = "dateInit";
    public static final String COLUMN_ICS_URL = "icsURL";

    //column number each field ends up in
    public static final int NETID_POS = 1;
    public static final int FIRST_NAME_POS = 2;
    public static final int LAST_NAME_POS = 3;
    public static final int DATE_INIT_POS = 4;
    public static final int ICS_URL_POS = 5;

    //fields in database
    private String netid;
    private String firstName;
    private String lastName;
    private String dateInit;
    private String icsURL;

    public User(int id, String netid, String firstName, String lastName, String dateInit, String icsURL) {
        super(id);
        this.netid = netid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateInit = dateInit;
        this.icsURL = icsURL;
    }

    public String getNetid() {
        return netid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDateInit() {
        return dateInit;
    }

    public String getIcsURL() {
        return icsURL;
    }
}
