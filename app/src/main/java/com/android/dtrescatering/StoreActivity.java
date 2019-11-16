package com.android.dtrescatering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.dtrescatering.base.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.android.dtrescatering.base.MethodeFunction.longToast;
import static com.android.dtrescatering.base.MethodeFunction.shortToast;

public class StoreActivity extends AppCompatActivity {

    private RecyclerView mStoreItemRecycleView;
    private FloatingActionButton mAddItemFloatingButton;
    private View mEmptyView;

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
