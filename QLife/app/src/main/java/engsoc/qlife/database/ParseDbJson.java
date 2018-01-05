package engsoc.qlife.database;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import engsoc.qlife.database.local.buildings.Building;
import engsoc.qlife.database.local.buildings.BuildingManager;
import engsoc.qlife.database.local.cafeterias.Cafeteria;
import engsoc.qlife.database.local.cafeterias.CafeteriaManager;
import engsoc.qlife.database.local.contacts.emergency.EmergencyContact;
import engsoc.qlife.database.local.contacts.emergency.EmergencyContactsManager;
import engsoc.qlife.database.local.contacts.engineering.EngineeringContact;
import engsoc.qlife.database.local.contacts.engineering.EngineeringContactsManager;
import engsoc.qlife.database.local.food.Food;
import engsoc.qlife.database.local.food.FoodManager;

/**
 * Created by Carson on 2017-11-29.
 * Static class with methods used to parse out the JSON received from the cloud database.
 */
public class ParseDbJson {
    /**
     * Method that ties together methods that get specific parts of the cloud database JSON.
     * Only public method.
     *
     * @param json The JSONObject representing the cloud database.
     */
    public static void cloudToPhoneDB(JSONObject json, Context context) {
        emergencyContacts(json, context);
        engineeringContacts(json, context);
        buildings(json, context);
        food(json, context);
        cafeterias(json, context);
    }

    /**
     * Method that retrieves and inserts the cloud emergency contact data into the phone database.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private static void emergencyContacts(JSONObject json, Context context) {
        try {
            JSONArray contacts = json.getJSONArray(EmergencyContact.TABLE_NAME);
            EmergencyContactsManager tableManager = new EmergencyContactsManager(context);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                tableManager.insertRow(new EmergencyContact(contact.getInt(EmergencyContact.ID), contact.getString(EmergencyContact.COLUMN_NAME),
                        contact.getString(EmergencyContact.COLUMN_PHONE_NUMBER), contact.getString(EmergencyContact.COLUMN_DESCRIPTION)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "EMERG: " + e);
        }
    }

    /**
     * Method that retrieves and inserts the cloud engineering contact data into the phone database.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private static void engineeringContacts(JSONObject json, Context context) {
        try {
            JSONArray contacts = json.getJSONArray(EngineeringContact.TABLE_NAME);
            EngineeringContactsManager tableManager = new EngineeringContactsManager(context);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                tableManager.insertRow(new EngineeringContact(contact.getInt(EngineeringContact.ID), contact.getString(EngineeringContact.COLUMN_NAME), contact.getString(EngineeringContact.COLUMN_EMAIL),
                        contact.getString(EngineeringContact.COLUMN_POSITION), contact.getString(EngineeringContact.COLUMN_DESCRIPTION)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "ENG: " + e);
        }
    }

    /**
     * Method that retrieves and inserts the cloud buildings data into the phone database.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private static void buildings(JSONObject json, Context context) {
        try {
            JSONArray buildings = json.getJSONArray(Building.TABLE_NAME);
            BuildingManager manager = new BuildingManager(context);
            for (int i = 0; i < buildings.length(); i++) {
                JSONObject building = buildings.getJSONObject(i);
                //getInt()>0 because SQL has 0/1 there, not real boolean
                manager.insertRow(new Building(building.getInt(Building.ID), building.getString(Building.COLUMN_NAME), building.getString(Building.COLUMN_PURPOSE),
                        building.getInt(Building.COLUMN_BOOK_ROOMS) > 0, building.getInt(Building.COLUMN_FOOD) > 0, building.getInt(Building.COLUMN_ATM) > 0,
                        building.getDouble(Building.COLUMN_LAT), building.getDouble(Building.COLUMN_LON)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "BUILDING: " + e);
        }
    }

    /**
     * Method that retrieves and inserts the cloud food data into the phone database.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private static void food(JSONObject json, Context context) {
        try {
            JSONArray food = json.getJSONArray(Food.TABLE_NAME);
            FoodManager manager = new FoodManager(context);
            for (int i = 0; i < food.length(); i++) {
                JSONObject oneFood = food.getJSONObject(i);
                //getInt()>0 because SQL has 0/1 there, not real boolean
                manager.insertRow(new Food(oneFood.getInt(Food.ID), oneFood.getString(Food.COLUMN_NAME), oneFood.getInt(Food.COLUMN_BUILDING_ID),
                        oneFood.getString(Food.COLUMN_INFORMATION), oneFood.getInt(Food.COLUMN_MEAL_PLAN) > 0, oneFood.getInt(Food.COLUMN_CARD) > 0,
                        oneFood.getDouble(Food.COLUMN_MON_START_HOURS), oneFood.getDouble(Food.COLUMN_MON_STOP_HOURS), oneFood.getDouble(Food.COLUMN_TUE_START_HOURS),
                        oneFood.getDouble(Food.COLUMN_TUE_STOP_HOURS), oneFood.getDouble(Food.COLUMN_WED_START_HOURS), oneFood.getDouble(Food.COLUMN_WED_STOP_HOURS),
                        oneFood.getDouble(Food.COLUMN_THUR_START_HOURS), oneFood.getDouble(Food.COLUMN_THUR_STOP_HOURS), oneFood.getDouble(Food.COLUMN_FRI_START_HOURS),
                        oneFood.getDouble(Food.COLUMN_FRI_STOP_HOURS), oneFood.getDouble(Food.COLUMN_SAT_START_HOURS),
                        oneFood.getDouble(Food.COLUMN_SAT_STOP_HOURS), oneFood.getDouble(Food.COLUMN_SUN_START_HOURS), oneFood.getDouble(Food.COLUMN_SUN_STOP_HOURS)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "FOOD: " + e);
        }
    }

    /**
     * Method that retrieves and inserts the cloud cafeteria data into the phone database.
     *
     * @param json The JSONObject representing the cloud database.
     */
    private static void cafeterias(JSONObject json, Context context) {
        try {
            JSONArray cafs = json.getJSONArray(Cafeteria.TABLE_NAME);
            CafeteriaManager manager = new CafeteriaManager(context);
            for (int i = 0; i < cafs.length(); i++) {
                JSONObject caf = cafs.getJSONObject(i);
                manager.insertRow(new Cafeteria(caf.getInt(Cafeteria.ID), caf.getString(Cafeteria.COLUMN_NAME), caf.getInt(Cafeteria.COLUMN_BUILDING_ID),
                        caf.getDouble(Cafeteria.COLUMN_WEEK_BREAKFAST_START), caf.getDouble(Cafeteria.COLUMN_WEEK_BREAKFAST_STOP),
                        caf.getDouble(Cafeteria.COLUMN_FRI_BREAKFAST_START), caf.getDouble(Cafeteria.COLUMN_FRI_BREAKFAST_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SAT_BREAKFAST_START), caf.getDouble(Cafeteria.COLUMN_SAT_BREAKFAST_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SUN_BREAKFAST_START), caf.getDouble(Cafeteria.COLUMN_SUN_BREAKFAST_STOP),
                        caf.getDouble(Cafeteria.COLUMN_WEEK_LUNCH_START), caf.getDouble(Cafeteria.COLUMN_WEEK_LUNCH_STOP),
                        caf.getDouble(Cafeteria.COLUMN_FRI_LUNCH_START), caf.getDouble(Cafeteria.COLUMN_FRI_LUNCH_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SAT_LUNCH_START), caf.getDouble(Cafeteria.COLUMN_SAT_LUNCH_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SUN_LUNCH_START), caf.getDouble(Cafeteria.COLUMN_SUN_LUNCH_STOP),
                        caf.getDouble(Cafeteria.COLUMN_WEEK_DINNER_START), caf.getDouble(Cafeteria.COLUMN_WEEK_DINNER_STOP),
                        caf.getDouble(Cafeteria.COLUMN_FRI_DINNER_START), caf.getDouble(Cafeteria.COLUMN_FRI_DINNER_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SAT_DINNER_START), caf.getDouble(Cafeteria.COLUMN_SAT_DINNER_STOP),
                        caf.getDouble(Cafeteria.COLUMN_SUN_DINNER_START), caf.getDouble(Cafeteria.COLUMN_SUN_DINNER_STOP)));
            }
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "CAF: " + e);
        }
    }
}
