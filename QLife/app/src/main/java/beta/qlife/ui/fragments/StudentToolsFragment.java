package beta.qlife.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import beta.qlife.R;
import beta.qlife.utility.Util;
import beta.qlife.interfaces.enforcers.ActionbarFragment;
import beta.qlife.interfaces.enforcers.DrawerItem;


/**
 * Created by Carson on 02/12/2016.
 * Holds information pertinent to students
 */
public class StudentToolsFragment extends Fragment implements ActionbarFragment, DrawerItem {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student_tools, container, false);
        setActionbarTitle();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            final FragmentManager fm = activity.getSupportFragmentManager();

            CardView emergContactsCard = v.findViewById(R.id.emerg_contacts_card);
            CardView engContactsCard = v.findViewById(R.id.eng_contacts_card);
            CardView counsellingCard = v.findViewById(R.id.counselling_card);
            CardView careerCard = v.findViewById(R.id.career_card);
            CardView solusCard = v.findViewById(R.id.solus_card);
            CardView outlookCard = v.findViewById(R.id.outlook_card);
            CardView onqCard = v.findViewById(R.id.onq_card);
            CardView applyCard = v.findViewById(R.id.engsoc_apply_card);

            emergContactsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fm.beginTransaction().addToBackStack(null).replace(R.id.content_frame, new EmergContactsFragment()).commit();
                }
            });
            engContactsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fm.beginTransaction().addToBackStack(null).replace(R.id.content_frame, new EngContactsFragment()).commit();
                }
            });
            counsellingCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBrowser(getString(R.string.counselling_url));
                }
            });
            careerCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBrowser(getString(R.string.career_url));
                }
            });

            solusCard.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startBrowser(getString(R.string.solus_url));
                }
            });
            outlookCard.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startBrowser(getString(R.string.microsoft_url));
                }
            });
            onqCard.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startBrowser(getString(R.string.onq_url));
                }
            });
            applyCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBrowser(getString(R.string.apply_url));
                }
            });
        }
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

    /**
     * Method that starts the default Internet browser at a given URL.
     *
     * @param url The URL to start browsing session.
     */
    private void startBrowser(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.fragment_student_tools), (AppCompatActivity) getActivity());
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_tools, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_tools, true);
    }
}