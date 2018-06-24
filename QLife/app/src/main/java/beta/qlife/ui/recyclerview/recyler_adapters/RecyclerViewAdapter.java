package beta.qlife.ui.recyclerview.recyler_adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import beta.qlife.ui.recyclerview.list_objects.DataObject;

import java.util.ArrayList;

/**
 * Created by Alex on 3/29/2017.
 * Class that defines the adapter for the DayFragment RecyclerView.
 */
public abstract class RecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    ArrayList<? extends DataObject> mData;
    static MyClickListener mClickListener;

    public void setOnItemClickListener(MyClickListener myClickListener) {
        RecyclerViewAdapter.mClickListener = myClickListener;
    }

    public RecyclerViewAdapter(ArrayList<? extends DataObject> data) {
        mData = data;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public DataObject getItem(int id) {
        return mData.get(id);
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}