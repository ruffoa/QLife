package engsoc.qlife.database.local.courses.Course;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import engsoc.qlife.database.local.DatabaseManager;
import engsoc.qlife.database.local.DatabaseRow;

/**
 * Created by Carson on 21/01/2017.
 * Holds all information for the courses table.
 * Manages the courses within the database. Inserts/deletes rows and the entire table.
 */
public class CourseManager extends DatabaseManager {

    public CourseManager(Context context) {
        super(context);
    }

    @Override
    public void insertRow(DatabaseRow row) {
        if (row instanceof Course) {
            Course course = (Course) row;
            ContentValues values = new ContentValues();
            values.put(Course.COLUMN_TITLE, course.getTitle());
            values.put(Course.COLUMN_DESCRIPTION, course.getDesription());
            getDatabase().insert(Course.TABLE_NAME, null, values);
        }
    }

    @Override
    public ArrayList<DatabaseRow> getTable() {
        ArrayList<DatabaseRow> courses = new ArrayList<>();
        //try with resources - automatically closes cursor whether or not its completed normally
        try (Cursor cursor = getDatabase().query(Course.TABLE_NAME, null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                Course course = new Course(cursor.getInt(Course.ID_POS), cursor.getString(Course.TITLE_POS), cursor.getString(Course.DESCRIPTION_POS));
                courses.add(course);
            }
            cursor.close();
            return courses; //return only when the cursor has been closed
        }
    }

    @Override
    public Course getRow(long id) {
        String selection = Course.ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor cursor = getDatabase().query(Course.TABLE_NAME, null, selection, selectionArgs, null, null, null)) {
            Course course = null;
            if (cursor != null && cursor.moveToNext()) {
                course = new Course(cursor.getInt(Course.ID_POS), cursor.getString(Course.TITLE_POS), cursor.getString(Course.DESCRIPTION_POS));
                cursor.close();
            }
            return course;
        }
    }

    @Override
    public void updateRow(DatabaseRow oldRow, DatabaseRow newRow) {
        if (oldRow instanceof Course && newRow instanceof Course) {
            Course oldCourse = (Course) oldRow;
            Course newCourse = (Course) newRow;
            ContentValues values = new ContentValues();
            values.put(Course.COLUMN_TITLE, newCourse.getTitle());
            values.put(Course.COLUMN_DESCRIPTION, newCourse.getDesription());
            String selection = Course.ID + " LIKE ?";
            String selectionArgs[] = {String.valueOf(oldCourse.getId())};
            getDatabase().update(Course.TABLE_NAME, values, selection, selectionArgs);
        }
    }
}