package engsoc.qlife.ui.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import engsoc.qlife.R;
import engsoc.qlife.ui.recyclerview.list_objects.DataObject;

/**
 * Created by Alex Ruffo on 8/30/2017.
 * * Class that defines the adapter for the RoomsFragment RecyclerView that allows for sectioned elements.
 */

public class SectionedRecyclerView extends RecyclerView.Adapter<SectionedRecyclerView.DataObjectHolder> {
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView label;
        TextView dateTime;
        TextView header;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.SectionedTextView);
            dateTime = itemView.findViewById(R.id.SectionedTextView2);
            header = itemView.findViewById(R.id.textViewHeader);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(SectionedRecyclerView.MyClickListener myClickListener) {
        SectionedRecyclerView.myClickListener = myClickListener;
    }

    public SectionedRecyclerView(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public SectionedRecyclerView.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sectioned_recyclerview_card, parent, false);

        return new SectionedRecyclerView.DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionedRecyclerView.DataObjectHolder holder, int position) {
        String text1 = mDataset.get(position).getmText1();
        String text2 = mDataset.get(position).getmText2();
        String header = mDataset.get(position).getHeader();
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

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public DataObject getItem(int id) {
        return mDataset.get(id);
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
