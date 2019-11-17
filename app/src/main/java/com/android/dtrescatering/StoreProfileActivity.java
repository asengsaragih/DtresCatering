package com.android.dtrescatering;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import com.android.dtrescatering.base.Item;
import com.android.dtrescatering.base.Store;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StoreProfileActivity extends AppCompatActivity {

    private RecyclerView mRecycleViewDetailStore;
    private View mEmptyView;
    private TextView mNamaTextView;
    private TextView mDescTextView;
    private TextView mJamTextView;

    private ItemStoreAdapter mAdapter;

    private ArrayList<Item> mData;
    private ArrayList<String> mDataId;

    private ActionMode mActionMode;

    private DatabaseReference mDatabase;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Item.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.updateEmptyView();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(Item.class));
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mDataId.remove(pos);
            mData.remove(pos);
            mAdapter.updateEmptyView();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);

//        private RecyclerView mRecycleViewDetailStore;
//        private View mEmptyView;
//        private TextView mNamaTextView;
//        private TextView mDescTextView;
//        private TextView mJamTextView;

        mRecycleViewDetailStore = findViewById(R.id.recycleView_store_detail_items);
        mEmptyView = findViewById(R.id.textView_store_detail_emptyview);
        mNamaTextView = findViewById(R.id.textView_store_detail_nama);
        mDescTextView = findViewById(R.id.textView_store_detail_desc);
        mJamTextView = findViewById(R.id.textView__store_detail_jam);

        mShowItem(savedInstanceState);
    }

    private void mShowItem(Bundle bundle) {
        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Stores").child(getStoreId(bundle)).child("items");
        mDatabase.addChildEventListener(childEventListener);

        mRecycleViewDetailStore.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecycleViewDetailStore.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        mRecycleViewDetailStore.addItemDecoration(divider);

        mAdapter = new ItemStoreAdapter(StoreProfileActivity.this, mData, mDataId, mEmptyView, new ItemStoreAdapter.ClickHandler() {
            @Override
            public void onItemClick(int position) {
                String item = mDataId.get(position).toString();
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

        mRecycleViewDetailStore.setAdapter(mAdapter);
    }

    private String getStoreId(Bundle savedInstanceState) {
        String storeId;
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle == null) {
                return storeId = null;
            } else {
                return storeId = bundle.getString("storeId");
            }
        } else {
            return storeId = (String) savedInstanceState.getSerializable("storeId");
        }
    }
}
