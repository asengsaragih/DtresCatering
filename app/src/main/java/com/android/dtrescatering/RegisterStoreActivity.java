package com.android.dtrescatering;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.dtrescatering.base.Store;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.android.dtrescatering.base.MethodeFunction.longToast;

public class RegisterStoreActivity extends AppCompatActivity {

    private EditText mNamaTokoEditText, mDeskripsiTokoEditText;
    private ImageButton mGambarTokoImageButton;
    private Button mDaftarTokoButton;

    private static final int PICK_IMAGE_REQUEST = 11;
    private Uri mImageUri;

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    FirebaseStorage storage;
    StorageReference storageReference;

    private DatabaseReference mDatabaseRef;

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

    private void uploadFile() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        String randomName = UUID.randomUUID().toString();

        if (mImageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Membuat Toko");
            progressDialog.show();

//            ------------------ untuk masukkan ke dalam database ------------------

            mDatabaseRef = FirebaseDatabase.getInstance().getReference(userId).child("store");
            final Store store = new Store(
                    mNamaTokoEditText.getText().toString().trim(),
                    mDeskripsiTokoEditText.getText().toString().trim(),
                    userId + " " +randomName
            );
            final String dataID = mDatabaseRef.push().getKey();

//            -----------------------------------------------------------------------

            StorageReference ref = storageReference.child("store/" + userId + " " + randomName);
            ref.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDatabaseRef.child(dataID).setValue(store);
                            progressDialog.dismiss();
                            longToast(getApplicationContext(), "Berhasil Membuat Toko");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            longToast(getApplicationContext(), "Gagal Upload : " + e.toString());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading " + (int)progress + "%");
                        }
                    });
        }
    }

    private void mButtonClicked() {
        mDaftarTokoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
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
                    mImageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                        mGambarTokoImageButton.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        longToast(getApplicationContext(), e.toString());
                    }
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

        }
    }

    private String getFileExtention(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
