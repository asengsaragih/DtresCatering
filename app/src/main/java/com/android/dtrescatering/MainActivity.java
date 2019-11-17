package com.android.dtrescatering;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.dtrescatering.base.Item;
import com.android.dtrescatering.base.Session;
import com.android.dtrescatering.base.Store;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

import static com.android.dtrescatering.base.MethodeFunction.longToast;

public class MainActivity extends AppCompatActivity {

    //tes upload branch baru
    //tes upload branch baru

    int[] sampleImages = {
            R.drawable.img_slider_nasi_goreng,
            R.drawable.img_slider_nasi_gudeg,
            R.drawable.img_slider_nasi_padang,
            R.drawable.img_slider_nasi_tumpeng,
            R.drawable.img_slider_nasi_uduk
    };

    private Session session;

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private RecyclerView mStoreRecycleView;
    private View mEmptyView;
    private StoresAdapter mAdapter;

    private ArrayList<Store> mData;
    private ArrayList<String> mDataId;

    private ActionMode mActionMode;

    private DatabaseReference mDatabase;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Store.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.updateEmptyView();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(Store.class));
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
        setContentView(R.layout.activity_main);

        mStoreRecycleView = findViewById(R.id.recycleView_main_stores);
        mEmptyView = findViewById(R.id.textView_main_empty_view);

        session = new Session(this);
        if (!session.loggedIn()){
            //for logout
            logout();
        }

        mShowCarousel();

        mShowStores();
    }

    private void mShowStores() {
        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Stores");
        mDatabase.addChildEventListener(childEventListener);

        mStoreRecycleView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mStoreRecycleView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
//        mStoreRecycleView.addItemDecoration(divider);

        mAdapter = new StoresAdapter(this, mData, mDataId, mEmptyView, new StoresAdapter.ClickHandler() {
            @Override
            public void onItemClick(int position) {
                String item = mDataId.get(position).toString();

                Intent intent = new Intent(MainActivity.this, StoreProfileActivity.class);
                intent.putExtra("storeId", item);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

        mStoreRecycleView.setAdapter(mAdapter);
    }

    private void mDetailStore(Store store) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stores");
    }

    private void mShowCarousel() {
        CarouselView carouselView;

        carouselView = (CarouselView) findViewById(R.id.carousel_main);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(MainActivity.this, SigninActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_store:
                checkStoreExist();
                break;
            case R.id.action_logout:
                session.setLoggedin(false);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                startActivity(new Intent(this, SigninActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkStoreExist() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference check = reference.child("Users").child(userId).child("storeId");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mengecek Toko");
        progressDialog.show();

        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), StoreActivity.class));
                } else {
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), RegisterStoreActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });
    }
}
