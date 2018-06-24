package beta.qlife.database.local.rooms;
import beta.qlife.database.local.DatabaseRow;

/**
 * Created by Alex on 7/28/2017.
 * Class defines schema for table of ILC rooms in phone database.
 */
public class Room extends DatabaseRow {
    public static final String TABLE_NAME = "ILCRoomInfo";

    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_BUILDING_ID = "BuildingID";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_MAP_URL = "Map";
    public static final String COLUMN_PIC_URL = "Picture";
    public static final String COLUMN_ROOM_ID = "RoomID";

    public static final int BUILDING_ID_POS = 1;
    public static final int DESCRIPTION_POS = 2;
    public static final int MAP_URL_POS = 3;
    public static final int NAME_POS = 4;
    public static final int PIC_URL_POS = 5;
    public static final int ROOM_ID_POS = 6;

    private String name, mapUrl, description, picUrl;
    private int roomId, buildingId;

    public Room(long id, int buildingId, String description, String mapUrl, String name, String picUrl, int roomId) {
        super(id);
        this.name = name;
        this.buildingId = buildingId;
        this.description = description;
        this.mapUrl = mapUrl;
        this.picUrl = picUrl;
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getDescription() {
        return description;
    }
}


