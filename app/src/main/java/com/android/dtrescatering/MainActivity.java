package com.android.dtrescatering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.dtrescatering.base.Session;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class MainActivity extends AppCompatActivity {

    int[] sampleImages = {
            R.drawable.img_slider_nasi_goreng,
            R.drawable.img_slider_nasi_gudeg,
            R.drawable.img_slider_nasi_padang,
            R.drawable.img_slider_nasi_tumpeng,
            R.drawable.img_slider_nasi_uduk
    };

    private Session session;

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
                startActivity(new Intent(getApplicationContext(), RegisterStoreActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
