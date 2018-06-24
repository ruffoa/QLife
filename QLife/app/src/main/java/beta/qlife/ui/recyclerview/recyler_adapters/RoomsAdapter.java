package beta.qlife.ui.recyclerview.recyler_adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import beta.qlife.R;
import beta.qlife.ui.recyclerview.list_objects.DataObject;
import beta.qlife.ui.recyclerview.list_objects.RoomsObject;

/**
 * Created by Carson on 2018-01-24.
 * RecyclerView adapter for the ILC rooms page.
 */
public class RoomsAdapter extends RecyclerViewAdapter<RoomsAdapter.RoomsObjectHolder> {
    public RoomsAdapter(ArrayList<? extends DataObject> mData) {
        super(mData);
    }

    @Override
    @NonNull
    public RoomsObjectHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sectioned_recyclerview_card, parent, false);

        return new RoomsObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsObjectHolder holder, int position) {
        RoomsObject roomObj = (RoomsObject) mData.get(position);
        String text1 = roomObj.getName();
        String text2 = roomObj.getDescription();
        String header = roomObj.getHeader();
        if (text1 != null)
            holder.label.setText(text1);
        else
            holder.label.setVisibility(View.GONE);
        if (text2 != null)
            holder.dateTime.setText(text2);
        else
            holder.dateTime.setVisibility(View.GONE);
        if (header != null && header.length() > 0) {
            holder.header.setText(header);
            holder.header.setVisibility(View.VISIBLE);
        } else
            holder.header.setVisibility(View.GONE);

    }

    protected class RoomsObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView label;
        private TextView dateTime;
        private TextView header;

        RoomsObjectHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.SectionedTextView);
            dateTime = itemView.findViewById(R.id.SectionedTextView2);
            header = itemView.findViewById(R.id.textViewHeader);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}
