# Local

This package and sub-packages define how the phone SQLite database is created and worked with.

## DatabaseAccessor

DatabaseAccessor provides an interface to DbHelper to open and get a connection to the phone database.

## DatabaseManager

DatabaseManager is an abstract class that acts as a parent to all phone database managers.

It fully implements row and table delete functions as they are the same for all managers but leaves get row, get table
insert row and update row functions for the child to implement. Inorder to have the same prototype for all children
classes, a parent for the table class is used: DatabaseRow.

## DatabaseRow

DatabaseRow is an abstract class that acts as a parent to all phone database table classes.

It implements ID variables and functions as all table classes have those attributes; however the main use of DatabaseRow
is allowing DatabaseManager to use polymorphism and abstract methods.

## DbHelper

DbHelper is used to manage creation/deletion of tables and the overall phone database itself.
It extends SQLiteOpenHelper and uses some of it's methods to create, delete and update tables.

It references SqlStringStatements in order to create, delete and update tables.

DbHelper is a Singleton to ensure that there is only one connection to the phone database at a time, helping
avoid errors.

## Courses

This sub-package holds the schema defining the current Courses table, and holds the manager controlling the data within that table.

The schema is defined by having `public static final` strings that represent column tables, and corresponding integers that show the order of those columns. Then, there are class attributes that hold the information of each entry's field, when a query is made.

The manager class inserts, retrieves, updates and deletes rows, as well as retrieving or deleting whole table in a useful format. More functionality can be built as needed. Specific rows are found with SQLite queries
that include a String array that defines what columns are being returned as well as a value to match. Individual rows are retrieved and returned in the schema class form, with the class attributes holding the data.
Updates/insertions are sent using a schema class parameter to hold information.

The courses table also references the OneClass table, as defined by the OneClass class, with it's own manager. This is treated as another ID field in the parent table, Courses, and that ID is used to lookup a specific class in the OneClass table.

## SqlStringStatements

This class simply holds a bunch of `public static final` strings that are used in SQLite queries to create or delete tables in the database.

## Phone Database Table and Manager Classes

The sub-packages within database/local each contain a table and a manager class. The table class defines the column schema for
the corresponding table and one instance represents one row. The manager class handles querying that table.

### Buildings

Information on Queen's buildings such as name, has food to buy, hours, position etc.

### Cafeterias

Information on Queen's cafeterias such as name and hours.

### Contacts

This package actually has two sub-packages, one for emergency contacts and one for engineering. Emergency contacts
have name, telephone and name. Engineering contacts have name, email, position and name.

## Courses

This package has two sub-packages, one for a course and one for an individual class. Individual classes have the start
and end times, location, date etc. Course is currently unused, but is intended to tie together individual classes
to represent the full, actual course.