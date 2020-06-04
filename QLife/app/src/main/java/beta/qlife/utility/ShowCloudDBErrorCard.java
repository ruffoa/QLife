package beta.qlife.utility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import beta.qlife.R;
import beta.qlife.database.local.DatabaseRow;
import beta.qlife.database.local.buildings.BuildingManager;

public class ShowCloudDBErrorCard {

    public void showCloudDBErrorCard(View view, String lookupName, final Activity activity) {
        TextView cardTitle = view.findViewById(R.id.notification_card_title);
        TextView cardDescription = view.findViewById(R.id.notification_card_description);
        ImageView cardImage = view.findViewById(R.id.notification_card_image);
        MaterialButton primaryAction = view.findViewById(R.id.notification_card_button_primary);
        MaterialCardView card = view.findViewById(R.id.notification_card);

        cardTitle.setText(activity.getString(R.string.connection_error_card_title, lookupName));
        cardDescription.setText(R.string.cloud_db_not_found);
        cardImage.setImageResource(R.drawable.server_error);
        primaryAction.setText(R.string.cloud_db_not_found_email_action);

        primaryAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(
                        "mailto:" + "doit@engsoc.queensu.ca" +
                                "?subject=Qlife Cloud DB Error" + // fix for GMail clients, which ignore the intent subject / text fields :/
                                "&body=**Autofilled%20from%20QLife%20Beta%20app**%0D%0A%0D%0A%0A%0A%0AThe%20Qlife%20cloud%20DB%20appears%20to%20be%20offline%20or%20not%20responding.%20%20Please%20check%20on%20the%20DB%20server%20and%20make%20sure%20it%20is%20online.%0A%0A%0A__________________________________________________%0A%0A"
                ));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Qlife Cloud DB Error");
                intent.putExtra(Intent.EXTRA_TEXT, "**Autofilled from QLife Beta app**\n\nThe Qlife cloud DB appears to be offline or not responding.  Please check on the DB server and make sure it is online.\n\n_____________________________________________________________\n\n");
                activity.startActivity(Intent.createChooser(intent, "Choose an Email client:"));
            }
        });

        card.setVisibility(View.VISIBLE);
    }
}
