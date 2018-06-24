package beta.qlife.database.local.buildings;

import beta.qlife.database.local.DatabaseRow;

/**
 * Created by Carson on 26/06/2017.
 * Class that defines schema for Buildings table in phone database.
 */
public class Building extends DatabaseRow {
    public static final String TABLE_NAME = "Buildings";
    //columns
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_PURPOSE = "Purpose";
    public static final String COLUMN_BOOK_ROOMS = "BookRooms";
    public static final String COLUMN_FOOD = "Food";
    public static final String COLUMN_ATM = "ATM";
    public static final String COLUMN_LAT = "Lat";
    public static final String COLUMN_LON = "Lon";

    //order of columns
    public static final int NAME_POS = 1;
    public static final int PURPOSE_POS = 2;
    public static final int BOOK_ROOKS_POS = 3;
    public static final int FOOD_POS = 4;
    public static final int ATM_POS = 5;
    public static final int LAT_POS = 6;
    public static final int LON_POST = 7;

    //fields
    private String name, purpose;
    private boolean bookRooms, food, atm;
    private double lat, lon;

    public Building(long id, String name, String purpose, boolean bookRooms, boolean food, boolean atm, double lat, double lon) {
        super(id);
        this.name = name;
        this.purpose = purpose;
        this.bookRooms = bookRooms;
        this.food = food;
        this.atm = atm;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurpose() {
        return purpose;
    }

    public boolean getBookRooms() {
        return bookRooms;
    }

    public boolean getFood() {
        return food;
    }

    public void setFood(boolean food) {
        this.food = food;
    }

    public boolean getAtm() {
        return atm;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
