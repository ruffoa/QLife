package engsoc.qlife.ui.recyclerview.recyler_adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import engsoc.qlife.R;
import engsoc.qlife.ui.recyclerview.list_objects.DataObject;
import engsoc.qlife.ui.recyclerview.list_objects.DayObject;

/**
 * Created by Carson on 2018-01-24.
 * Class used to hold information specific to DayFragment classes.
 */
public class DayAdapter extends RecyclerViewAdapter<DayAdapter.DayObjectHolder> {

    public DayAdapter(ArrayList<? extends DataObject> mData) {
        super(mData);
    }

    @Override
    public void onBindViewHolder(@NonNull DayObjectHolder holder, int position) {
        DayObject data = (DayObject) mData.get(position);
        holder.label.setText(data.getName());
        holder.dateTime.setText(data.getWhere());
    }

    @Override
    @NonNull
    public DayObjectHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_events_card, parent, false);

        return new DayObjectHolder(view);
    }

    protected class DayObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView label;
        private TextView dateTime;

        DayObjectHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.textView);
            dateTime = itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}
