package com.android.dtrescatering;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.dtrescatering.base.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;

import static com.android.dtrescatering.base.MethodeFunction.longToast;
import static com.android.dtrescatering.base.MethodeFunction.shortToast;

public class StoreActivity extends AppCompatActivity {

    private RecyclerView mStoreItemRecycleView;
    private FloatingActionButton mAddItemFloatingButton;
    private View mEmptyView;
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
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        mStoreItemRecycleView = findViewById(R.id.recycleview_item_store);
        mAddItemFloatingButton = findViewById(R.id.fab_add_item_store);
        mEmptyView = findViewById(R.id.empty_view_item_store);

        mButtonClicked();

        mShowItem();

    }

    private void mShowItem() {
        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("items");;
        mDatabase.addChildEventListener(childEventListener);

        mStoreItemRecycleView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mStoreItemRecycleView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        mStoreItemRecycleView.addItemDecoration(divider);

        mAdapter = new ItemStoreAdapter(this, mData, mDataId, mEmptyView, new ItemStoreAdapter.ClickHandler() {
            @Override
            public void onItemClick(int position) {
                if (mActionMode != null) {
                    mAdapter.toggleSelection(mDataId.get(position));
                    if (mAdapter.selectionCount() == 0)
                        mActionMode.finish();
                    else
                        mActionMode.invalidate();
                    return;
                }

                String item = mData.get(position).toString();
                Toast.makeText(getApplicationContext(), item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(int position) {
                if (mActionMode != null) return false;

                mAdapter.toggleSelection(mDataId.get(position));
//                mActionMode = StoreActivity.this.startSupportActionMode(mActionModeCallback);
                mActionMode = StoreActivity.this.startSupportActionMode(mActionModeCallback);
                return true;
            }
        });

        mStoreItemRecycleView.setAdapter(mAdapter);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.catalog_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(String.valueOf(mAdapter.selectionCount()));
            menu.findItem(R.id.action_edit).setVisible(mAdapter.selectionCount() == 1);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    editItem();
                    return true;

                case R.id.action_delete:
                    deleteItem();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mAdapter.resetSelection();
        }
    };

    private void editItem() {
        final String currentItemId = mAdapter.getSelectedId().get(0);
        Item selectedItem = mData.get(mDataId.indexOf(currentItemId));

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.dialog_item_store, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Edit Item Toko");

        final EditText name = (EditText) view.findViewById(R.id.editText_dialog_item_nama);
        name.setText(selectedItem.getNama());
        final EditText price = (EditText) view.findViewById(R.id.editText__dialog_item_harga);
        price.setText(selectedItem.getHarga());

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("items");

        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                mDatabaseRef.child(currentItemId).setValue(new Item(
                        name.getText().toString(), price.getText().toString()
                ));
                mActionMode.finish();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                mActionMode.finish();
            }
        });

        builder.show();
    }

    private void deleteItem() {
        final ArrayList<String> selectedIds = mAdapter.getSelectedId();
        int message = selectedIds.size() == 1 ? R.string.delete_item : R.string.delete_items;

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("items");

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String currentPetId : selectedIds) {
                            mDatabaseRef.child(currentPetId).removeValue();
                        }
                        mActionMode.finish();
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mActionMode.finish();
                    }
                });
        builder.create().show();
    }

    private void mButtonClicked() {
        mAddItemFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });
    }

    private void saveItem() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.dialog_item_store, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Item Toko");

        final EditText name = (EditText) view.findViewById(R.id.editText_dialog_item_nama);
        final EditText price = (EditText) view.findViewById(R.id.editText__dialog_item_harga);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("items");
        final String dataID = mDatabaseRef.push().getKey();

        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                mDatabaseRef.child(dataID).setValue(new Item(name.getText().toString(), price.getText().toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialogInterface.dismiss();
                        shortToast(getApplicationContext(), "Berhasil Menyimpan Data");
                    }
                });
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

//    private ArrayList<String> getStoreId() {
//        return
//    }

//    private void getStoreId() {
//        final List<String> storeIdList = new ArrayList<>();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("store").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot == null) {
//                    for (DataSnapshot postSnapsot: dataSnapshot.getChildren()) {
//                        storeIdList.add(postSnapsot.getKey());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
