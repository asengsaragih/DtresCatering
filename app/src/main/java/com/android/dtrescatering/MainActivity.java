package com.android.dtrescatering;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
