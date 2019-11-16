package com.android.dtrescatering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.dtrescatering.base.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import static com.android.dtrescatering.base.MethodeFunction.longToast;

public class MainActivity extends AppCompatActivity {

    int[] sampleImages = {
            R.drawable.img_slider_nasi_goreng,
            R.drawable.img_slider_nasi_gudeg,
            R.drawable.img_slider_nasi_padang,
            R.drawable.img_slider_nasi_tumpeng,
            R.drawable.img_slider_nasi_uduk
    };

    private Session session;

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new Session(this);
        if (!session.loggedIn()){
            //for logout
            logout();
        }

        mShowCarousel();
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkStoreExist() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference check = reference.child(userId).child("store");

        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
                progressDialog.setTitle("Mengecek Toko");
                progressDialog.show();

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
