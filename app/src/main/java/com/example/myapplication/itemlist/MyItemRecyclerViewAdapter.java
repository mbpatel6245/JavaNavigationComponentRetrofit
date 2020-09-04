package com.example.myapplication.itemlist;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.event.OnEventClickListener;
import com.example.myapplication.model.Content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Content}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<Content> dataList;
    private List<Content> itemsPendingRemoval;

    private static final int PENDING_REMOVAL_TIMEOUT = 2000; // 3sec
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Content, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnable, to cancel the removal

    OnEventClickListener mListener;

    public MyItemRecyclerViewAdapter(List<Content> dataList, OnEventClickListener mListener) {
        this.dataList = dataList;
        this.mListener = mListener;
        itemsPendingRemoval = new ArrayList<>();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, final int position) {

        final Content data = dataList.get(position);

        if (itemsPendingRemoval.contains(data)) {
            /** show swipe layout and hide regular layout */
            itemViewHolder.regularLayout.setVisibility(View.GONE);
            itemViewHolder.swipeLayout.setVisibility(View.VISIBLE);
            itemViewHolder.undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    undoOpt(data);
                }
            });
        } else {
            /** show regular layout and hide swipe layout*/
            itemViewHolder.regularLayout.setVisibility(View.VISIBLE);
            itemViewHolder.swipeLayout.setVisibility(View.GONE);
            itemViewHolder.listItem.setText(data.getMediaTitleCustom());
        }
        itemViewHolder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onEventClick(view, dataList.get(position));
            }
        });
    }

    private void undoOpt(Content customer) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(customer);
        pendingRunnables.remove(customer);
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        itemsPendingRemoval.remove(customer);
        // this will rebind the row in "normal" state
        notifyItemChanged(dataList.indexOf(customer));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void pendingRemoval(int position) {

        final Content data = dataList.get(position);
        if (!itemsPendingRemoval.contains(data)) {
            itemsPendingRemoval.add(data);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            //create, store and post a runnable to remove the data
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(dataList.indexOf(data));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(data, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        Content data = dataList.get(position);
        if (itemsPendingRemoval.contains(data)) {
            itemsPendingRemoval.remove(data);
        }
        if (dataList.contains(data)) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        Content data = dataList.get(position);
        return itemsPendingRemoval.contains(data);
    }
}