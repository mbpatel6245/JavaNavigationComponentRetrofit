package com.example.myapplication.itemlist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.event.OnEventClickListener;
import com.example.myapplication.model.Content;
import com.example.myapplication.model.ItemData;
import com.example.myapplication.network.APIClient;
import com.example.myapplication.network.APIInterface;
import com.example.myapplication.util.SwipeUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 */
public class ItemListFragment extends Fragment implements OnEventClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    APIInterface apiInterface;
    RecyclerView recyclerView;
    MyItemRecyclerViewAdapter myAdapter;
    SwipeRefreshLayout swl;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemListFragment newInstance(int columnCount) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        // Set the adapter
       // if (view instanceof RecyclerView) {
            Context context = view.getContext();
             recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.addItemDecoration(
                    new DividerItemDecoration(context, 1));
       // }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fetchAPICall();
        swl=view.findViewById(R.id.swipeRefresh);
        swl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swl.setRefreshing(false);
                fetchAPICall();
            }
        });
        swl.setRefreshing(true);
    }

    private void fetchAPICall(){
        /**
         GET List Resources
         **/
        Call<ItemData> call = apiInterface.doGetListResources();
        call.enqueue(new Callback<ItemData>() {
            @Override
            public void onResponse(Call<ItemData> call, Response<ItemData> response) {
                swl.setRefreshing(false);

                Log.d("TAG",response.code()+"");

                String displayResponse = "";

                ItemData resource = response.body();
                ArrayList itemData=new ArrayList();
                if(resource.getStatus()){
                    itemData= resource.getContent();
                }

                myAdapter=new MyItemRecyclerViewAdapter(itemData,ItemListFragment.this);
                recyclerView.setAdapter(myAdapter);
                setSwipeForRecyclerView();
            }

            @Override
            public void onFailure(Call<ItemData> call, Throwable t) {
                swl.setRefreshing(false);
                call.cancel();
            }
        });
    }

    @Override
    public void onEventClick(View view, Content mContent) {
        Bundle bundle = new Bundle();
        bundle.putString("title", mContent.getMediaTitleCustom());
        bundle.putString("date", mContent.getMediaDate().getDateString());
        bundle.putString("link", mContent.getMediaUrl());
        Navigation.findNavController(view).navigate(R.id.action_itemlist_to_itemdetail,bundle);
    }

    private void setSwipeForRecyclerView() {

        SwipeUtil swipeHelper = new SwipeUtil(0, ItemTouchHelper.LEFT, getActivity()) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                myAdapter = (MyItemRecyclerViewAdapter)recyclerView.getAdapter();
                myAdapter.pendingRemoval(swipedPosition);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                myAdapter = (MyItemRecyclerViewAdapter) recyclerView.getAdapter();
                if (myAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeHelper);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        //set swipe label
        swipeHelper.setLeftSwipeLable("Archive");
        //set swipe background-Color
        swipeHelper.setLeftcolorCode(ContextCompat.getColor(getActivity(), R.color.swipebackground));
    }

}