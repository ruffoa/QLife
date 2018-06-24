package beta.qlife.database.local.contacts.emergency;

import beta.qlife.database.local.DatabaseRow;

/**
 * Created by Carson on 21/06/2017.
 * Schema for phone database EmergencyContact table.
 */
public class EmergencyContact extends DatabaseRow {
    public static final String TABLE_NAME = "EmergencyContacts";

    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_PHONE_NUMBER = "PhoneNumber";
    public static final String COLUMN_DESCRIPTION = "Description";

    public static final int NAME_POS = 1;
    public static final int PHONE_NUMBER_POS = 2;
    public static final int DESCRIPTION_POS = 3;

    private String name, phoneNumber, description;

    public EmergencyContact(long id, String name, String phoneNumber, String description) {
        super(id);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }
}
