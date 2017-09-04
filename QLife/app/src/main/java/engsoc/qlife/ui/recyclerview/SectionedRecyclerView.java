package engsoc.qlife.ui.recyclerview;

import android.support.transition.Visibility;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import engsoc.qlife.R;

/**
 * Created by Alex Ruffo on 8/30/2017.
 *  * Class that defines the adapter for the ILCRoomInfoFragment RecyclerView that allows for sectioned elements.
 */

public class SectionedRecyclerView extends RecyclerView.Adapter<SectionedRecyclerView.DataObjectHolder> {
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        TextView header;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.SectionedTextView);
            dateTime = (TextView) itemView.findViewById(R.id.SectionedTextView2);
            header = (TextView) itemView.findViewById(R.id.textViewHeader);
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
        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText(mDataset.get(position).getmText2());
        if (mDataset.get(position).getHeader() != null && mDataset.get(position).getHeader().length() > 0) {
            holder.header.setText(mDataset.get(position).getHeader());
            holder.header.setVisibility(View.VISIBLE);
        }
        else
            holder.header.setVisibility(View.GONE);

    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void addItem(DataObject dataObj) {
        mDataset.add(dataObj);
        notifyItemInserted(mDataset.indexOf(dataObj));
    }


    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public DataObject getItem(int id) {
        return mDataset.get(id);
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
