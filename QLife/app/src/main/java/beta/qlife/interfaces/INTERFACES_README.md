# Interfaces

This package holds interfaces used to define how classes work. These are meant to be help creating
new classes; when you implement the interface you are forced to implement methods that will help the creator
to remember to complete specific functionality.

## IQLActionbarFragment

For a fragment that needs to change its activity's Actionbar code.

## IQLActivityHasOptionsMenu

For an activity that has an options menu.

## IQLDrawerItem

For an activity or fragment that is accessed from the drawer.

## IQLListFragment

For a fragment that displays a list of information, likely using a ListView

## IQLListFragmentWithChild

For a fragment that displays a list of information that when an item is clicked, displays another fragment.

Extends IQLListFragment.

## IQLListItemDetailsFragment

For a fragment that is accessed from another fragment's list.

## IQLMapView

For a fragment or activity that uses a MapView.

## IQLOptionsMenuActivity

For a fragment that is accessed from the options menu.