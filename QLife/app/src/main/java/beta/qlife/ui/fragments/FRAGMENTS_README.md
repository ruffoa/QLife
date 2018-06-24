# Fragments

These classes are fragments that have specific page UI, and are controlled by activities.

Every fragment here is attached to MainTabActivity, so they all have an options menu that
allows access to the Settings, About and Review activities.

Since each fragment is attached to an activity that has an action bar, and they all have different
titles for the action bar, all of these fragments implement IQLActionbarFragment. All of these fragments
are also accessed using the drawer (some from fragments that come from the drawer) so they all also
implement IQLDrawerItem.

## BuildingsFragment

This fragment shows a list of Queen's buildings; each card has a building's name, name and if it has food available.

Clicking on a card will send the user to OneBuildingFragment.

This fragment is accessed from the drawer layout.

Since BuildingsFragment shows a ListView that goes to another fragment when a card is clicked, it implements
IQLListFragmentWithChild.

It queries the phone database for the full Buildings table in order to populate the ListView.

## CafeteriasFragment

This fragment shows a list of Queen's cafeterias; the name and its hours.

This fragment is accessed from the drawer layout.

Since CafeteriasFragment shows a static ListView, it implements IQLListFragment.

It queries the phone database for the full Cafeterias table in order to populate the ListView.

## DayFragment

This fragment shows a list of classes for the set day; showing the name, start/end time and location. It
also allows the day to be moved forwards or backwards using two buttons.

When a class' card is clicked, the user is sent to EventInfoFragment. Clicking the next/prev buttons does not
perform a fragment transaction, it merely changes the current DayFragment day.

This fragment is accessed from the drawer layout.

DayFragment uses a RecyclerView instead of a ListView, so it does implement IQLListFragment, but
it does not actually use one of the methods.

It queries the phone database for the full OneClass table in order to populate the RecyclerView.

## EmergContactsFragment

This fragment shows a list of Queen's emergency contacts; the name a name and a telephone number.

This fragment is accessed from StudentToolsFragment.

Since EmergContactsFragment shows a static ListView, it implements IQLListFragment.

It queries the phone database for the full EmergContacts table in order to populate the ListView.

## EngContactsFragment

This fragment shows a list of Queen's engineering contacts; the name, position, email and name.

This fragment is accessed from StudentToolsFragment.

Since EngContactsFragment shows a static ListView, it implements IQLListFragment.

It queries the phone database for the full EngContacts table in order to populate the ListView.

## EventInfoFragment

This fragment shows the details of a class; the name, location, start/end times and a Google map with
a marker where the building is.

This fragment is accessed by clicking a class card in DayFragment.

Since EventInfoFragment shows the details of a ListView card, it implements IQLListItemDetailsFragment. It
also has a MapView, so it implements IQLMapView.

It does not need to access the phone database as all shown information is sent from DayFragment.

## FoodFragment

This fragment shows a list of Queen's food establishments; each card has the name, building and payment methods.

Clicking on a card will send the user to OneFoodFragment.

This fragment is accessed from the drawer layout.

Since FoodFragment shows a ListView that goes to another fragment when a card is clicked, it implements
IQLListFragmentWithChild.

It queries the phone database for the full Food table in order to populate the ListView.

## MonthFragment

This fragment shows a DatePicker where the user an choose a date to view their schedule for.

Clicking on a day will send the user to DayFragment, with that date.

This fragment is accessed from the drawer layout.

## OneBuildingFragment

This fragment shows the details of a building; the name, food establishments, if it has an ATM/bookable
rooms, a name and a Google map with a marker at the building location.

Clicking on a card in BuildingsFragment will access this fragment.

Since OneBuildingFragment shows the details of a ListView card, it implements IQLListItemDetailsFragment. It
also has a MapView, so it implements IQLMapView.

It does not need to access the phone database as all shown information is sent from BuildingsFragment.

## OneBuildingFragment

This fragment shows the details of a food establishment; the name, building, information, payment methods and a Google map
with a marker at the building location.

Clicking on a card in FoodFragment will access this fragment.

Since OneFoodFragment shows the details of a ListView card, it implements IQLListItemDetailsFragment. It
also has a MapView, so it implements IQLMapView.

It does not need to access the phone database as all shown information is sent from FoodFragment.

## StudentToolsFragment

This fragment provides information pertinent to students.

StudentToolsFragment is accessed from the drawer.

Clicking on the contacts buttons sends the user to the corresponding contacts fragment. Clicking on the other
buttons sends the user to a corresponding web page in their phone's default browser.