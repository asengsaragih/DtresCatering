package com.android.dtrescatering;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.android.dtrescatering.base.MethodeFunction.shortToast;

public class BookingActivity extends AppCompatActivity {

    TextView lokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        lokasi = findViewById(R.id.textView_tes_lokasi);


        lokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderButton();
            }
        });
    }

    private void mGetAddress() {
        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
//                    rawLocation = location.getLatitude();
////                    // Do it all with location
////                    Log.d("My Current location", "Lat : " + location.getLatitude() + " Long : " + location.getLongitude());
////                    // Display in Toast
////                    Toast.makeText(MainActivity.this,
////                            "Lat : " + location.getLatitude() + " Long : " + location.getLongitude(),
////                            Toast.LENGTH_LONG).show();
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(BookingActivity.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL



                        lokasi.setText(address + "n" + city + "n" + state + "n" + country + "n" + postalCode + "n" + knownName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



//        addresses = geocoder.getFromLocation()
    }

    private void orderButton() {
        if (ActivityCompat.checkSelfPermission(BookingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BookingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BookingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            shortToast(this, "Tekan lagi untuk melanjutkan");
            return;
        } else {
            // Write you code here if permission already given.
            mGetAddress();
        }
    }


}
