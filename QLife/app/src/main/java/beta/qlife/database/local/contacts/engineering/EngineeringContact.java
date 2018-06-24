package beta.qlife.database.local.contacts.engineering;

import beta.qlife.database.local.DatabaseRow;

/**
 * Created by Carson on 21/06/2017.
 * Defines schema for phone database table EngineeringContact
 */
public class EngineeringContact extends DatabaseRow {
    public static final String TABLE_NAME = "EngineeringContacts";

    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_POSITION = "Position";
    public static final String COLUMN_DESCRIPTION = "Description";

    public static final int NAME_POS = 1;
    public static final int EMAIL_POS = 2;
    public static final int POSITION_POS = 3;
    public static final int DESCRIPTION_POS = 4;

    private String name, email, position, description;

    public EngineeringContact(long id, String name, String email, String position, String description) {
        super(id);
        this.name = name;
        this.email = email;
        this.position = position;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPosition() {
        return position;
    }

    public String getDescription() {
        return description;
    }
}
