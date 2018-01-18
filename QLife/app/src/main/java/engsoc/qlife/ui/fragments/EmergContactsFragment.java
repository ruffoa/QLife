package engsoc.qlife.ui.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import engsoc.qlife.R;
import engsoc.qlife.interfaces.enforcers.ListFragment;
import engsoc.qlife.utility.Util;
import engsoc.qlife.database.local.DatabaseRow;
import engsoc.qlife.database.local.contacts.emergency.EmergencyContact;
import engsoc.qlife.database.local.contacts.emergency.EmergencyContactsManager;
import engsoc.qlife.interfaces.enforcers.ActionbarFragment;
import engsoc.qlife.interfaces.enforcers.DrawerItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Carson on 12/06/2017.
 * Fragment that displays emergency contact information held in cloud database
 */
public class EmergContactsFragment extends android.support.v4.app.ListFragment implements ActionbarFragment, DrawerItem, ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        setActionbarTitle();
        inflateListView();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectDrawer();
    }

    @Override
    public void onPause() {
        super.onPause();
        deselectDrawer();
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.fragment_emerg_contacts), (AppCompatActivity) getActivity());
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_tools, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_tools, true);
    }

    @Override
    public void inflateListView() {
        ArrayList<HashMap<String, String>> emergContactsList = new ArrayList<>();
        ArrayList<DatabaseRow> contacts = (new EmergencyContactsManager(getActivity().getApplicationContext())).getTable();
        for (DatabaseRow row : contacts) {
            emergContactsList.add(packEmergContactsMap(row));
        }
        ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), emergContactsList,
                R.layout.emerg_contacts_list_item, new String[]{EmergencyContact.COLUMN_NAME, EmergencyContact.COLUMN_PHONE_NUMBER,
                EmergencyContact.COLUMN_DESCRIPTION}, new int[]{R.id.name, R.id.number, R.id.description});
        setListAdapter(adapter);
    }

    /**
     * Helper method that packs a hash-map containing a key and value for each piece of contact information.
     *
     * @param row The contact information to pack for.
     * @return The packed map.
     */
    private static HashMap<String, String> packEmergContactsMap(DatabaseRow row) {
        EmergencyContact contact = (EmergencyContact) row;
        HashMap<String, String> map = new HashMap<>();
        map.put(EmergencyContact.COLUMN_NAME, contact.getName());
        map.put(EmergencyContact.COLUMN_PHONE_NUMBER, contact.getPhoneNumber());
        map.put(EmergencyContact.COLUMN_DESCRIPTION, contact.getDescription());
        return map;
    }
}
