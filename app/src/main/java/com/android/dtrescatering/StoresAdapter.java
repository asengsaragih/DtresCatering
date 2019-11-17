package com.android.dtrescatering;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.dtrescatering.base.Item;
import com.android.dtrescatering.base.Store;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.ViewHolder> {

    private ClickHandler mClickHandler;
    private Context mContext;
    private ArrayList<Store> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;
    private View mEmptyView;

    public StoresAdapter(Context context, ArrayList<Store> data, ArrayList<String> dataId, View emptyView, ClickHandler handler) {
        mContext = context;
        mData = data;
        mDataId = dataId;
        mEmptyView = emptyView;
        mClickHandler = handler;
        mSelectedId = new ArrayList<>();
    }

    public void updateEmptyView() {
        if (mData.size() == 0)
            mEmptyView.setVisibility(View.VISIBLE);
        else
            mEmptyView.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_main_store, parent, false);
        return new StoresAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Store store = mData.get(position);
        holder.namaTextView.setText(store.getNama());
        holder.deskripsiTextView.setText(store.getDeskripsi());
        holder.jamBukaTextView.setText(store.getJamBuka() + " - ");
        holder.jamTutupTextView.setText(store.getJamTutup());
        holder.itemView.setSelected(mSelectedId.contains(mDataId.get(position)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void toggleSelection(String dataId) {
        if (mSelectedId.contains(dataId))
            mSelectedId.remove(dataId);
        else
            mSelectedId.add(dataId);
        notifyDataSetChanged();
    }

    public int selectionCount() {
        return mSelectedId.size();
    }

    public void resetSelection() {
        mSelectedId = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedId() {
        return mSelectedId;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView namaTextView;
        final TextView deskripsiTextView;
        final TextView jamBukaTextView;
        final TextView jamTutupTextView;

        ViewHolder(View itemView) {
            super(itemView);

            namaTextView = itemView.findViewById(R.id.textView_list_main_stores_nama);
            deskripsiTextView = itemView.findViewById(R.id.textView_list_main_stores_desc);
            jamBukaTextView = itemView.findViewById(R.id.textView_list_main_stores_jam_buka);
            jamTutupTextView = itemView.findViewById(R.id.textView_list_main_stores_jam_tutup);

            itemView.setFocusable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            mClickHandler.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return mClickHandler.onItemLongClick(getAdapterPosition());
        }
    }

    interface ClickHandler {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }
}
