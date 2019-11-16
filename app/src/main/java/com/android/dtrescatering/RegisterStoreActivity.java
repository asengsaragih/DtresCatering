package com.android.dtrescatering;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.android.dtrescatering.base.MethodeFunction.longToast;

public class RegisterStoreActivity extends AppCompatActivity {

    private EditText mNamaTokoEditText, mDeskripsiTokoEditText;
    private ImageButton mGambarTokoImageButton;
    private Button mDaftarTokoButton;

    private static final int ACCESS_MULTIPLE_STORAGE = 10;
    private static final int PICK_IMAGE_REQUEST = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_store);

        mNamaTokoEditText = findViewById(R.id.editText_register_store_name);
        mDeskripsiTokoEditText = findViewById(R.id.editText_register_store_description);
        mGambarTokoImageButton = findViewById(R.id.imageButton_register_store_picture);
        mDaftarTokoButton = findViewById(R.id.button_register_store);

        mButtonClicked();
    }

    private void mButtonClicked() {
        mDaftarTokoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mGambarTokoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOpenFilePicker();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    Uri mImageUri = data.getData();
                    Picasso.with(RegisterStoreActivity.this).load(mImageUri).into(mGambarTokoImageButton);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void mOpenFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (getPackageManager().resolveActivity(sIntent, 0) != null) {
            chooserIntent = Intent.createChooser(sIntent, "Open File");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }

        try {
            startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
        } catch (android.content.ActivityNotFoundException e) {
//                methodeFunction.toastMessage(RegisterStoreActivity.this, getString(R.string.warning_open_file_chooser));
        }
    }

    public void readStoragePermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, ACCESS_MULTIPLE_STORAGE);
    }
}
