package com.android.dtrescatering;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.dtrescatering.base.Store;
import com.android.dtrescatering.base.User;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.android.dtrescatering.base.MethodeFunction.longToast;
import static com.android.dtrescatering.base.MethodeFunction.shortToast;

public class RegisterStoreActivity extends AppCompatActivity {

    private EditText mNamaTokoEditText, mDeskripsiTokoEditText, mOpenTokoEditText, mClosedTokoEditText;
//    private ImageButton mGambarTokoImageButton;
    private Button mDaftarTokoButton;

//    private static final int PICK_IMAGE_REQUEST = 11;
//    private Uri mImageUri;

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

//    FirebaseStorage storage;
//    StorageReference storageReference;

    private DatabaseReference mDatabaseRef;
//    private StorageReference mStorageRef;
//    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_store);

        mNamaTokoEditText = findViewById(R.id.editText_register_store_name);
        mDeskripsiTokoEditText = findViewById(R.id.editText_register_store_description);
        mOpenTokoEditText = findViewById(R.id.editText_register_store_open);
        mClosedTokoEditText = findViewById(R.id.editText_register_store_closed);
//        mGambarTokoImageButton = findViewById(R.id.imageButton_register_store_picture);
        mDaftarTokoButton = findViewById(R.id.button_register_store);

        mButtonClicked();
    }

    private void mButtonClicked() {
        mDaftarTokoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                uploadFile();
                saveStore();
            }
        });

        mOpenTokoEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker(mOpenTokoEditText);
            }
        });

        mClosedTokoEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker(mClosedTokoEditText);
            }
        });

//        mGambarTokoImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mOpenFilePicker();
//            }
//        });
    }

    private void timePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        final int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int timeHours, int timeMinutes) {
                String minutesString = "";
                String hourString = "";

                if (timeMinutes == 0) {
                    minutesString = "00";
                } else {
                    minutesString = String.valueOf(timeMinutes);
                }

                if (timeHours == 0) {
                    hourString = "00";
                } else {
                    hourString = String.valueOf(timeHours);
                }

                editText.setText(hourString + ":" + minutesString);
            }
        }, hours, minutes, true);

        timePickerDialog.setTitle("Pilih Jam");
        timePickerDialog.show();
    }

    private void saveStore() {
        String nama = mNamaTokoEditText.getText().toString().trim();
        String desc = mDeskripsiTokoEditText.getText().toString().trim();
        String open = mOpenTokoEditText.getText().toString().trim();
        String close = mClosedTokoEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(open) || TextUtils.isEmpty(close)) {
            shortToast(getApplicationContext(), "Form TIdak Boleh Kosong");
            return;
        }

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Stores");
        final String dataID = mDatabaseRef.push().getKey();
//        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("store");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("storeId", dataID);

        Store store = new Store(
                mNamaTokoEditText.getText().toString().trim(),
                mDeskripsiTokoEditText.getText().toString().trim(),
                mOpenTokoEditText.getText().toString().trim(),
                mClosedTokoEditText.getText().toString().trim()

        );

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Membuat Toko");
        progressDialog.show();

        mDatabaseRef.child(userId).setValue(store).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                longToast(getApplicationContext(), "Berhasil Membuat Toko");
                finish();
            }
        });

        ref.updateChildren(values);
    }



//    private void uploadFile() {
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
//        String randomName = UUID.randomUUID().toString();
//
//        if (mImageUri != null) {
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Membuat Toko");
//            progressDialog.show();
//
////            ------------------ untuk masukkan ke dalam database ------------------
//
//            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("store");
//            final String dataID = mDatabaseRef.push().getKey();
//
////            -----------------------------------------------------------------------
//
//            final StorageReference ref = storageReference.child("store/" + userId + " " + randomName);
//            ref.putFile(mImageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Uri downloadUri = taskSnapshot.getUploadSessionUri();
//                            Store store = new Store(
//                                    mNamaTokoEditText.getText().toString().trim(),
//                                    mDeskripsiTokoEditText.getText().toString().trim(),
//                                    ref.getDownloadUrl().toString()
//                            );
//                            mDatabaseRef.child(dataID).setValue(store);
//                            progressDialog.dismiss();
//                            longToast(getApplicationContext(), "Berhasil Membuat Toko");
//                            finish();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            longToast(getApplicationContext(), "Gagal Upload : " + e.toString());
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//                            progressDialog.setMessage("Uploading " + (int)progress + "%");
//                        }
//                    });
//
////            final ProgressDialog progressDialog = new ProgressDialog(this);
////            progressDialog.setTitle("Membuat Toko");
////            progressDialog.show();
////
////            mStorageRef = FirebaseStorage.getInstance().getReference("store");
////            mDatabaseRef = FirebaseDatabase.getInstance().getReference("store");
////
////            final String dataID = mDatabaseRef.push().getKey();
////
////            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
////                    + "." + getFileExtension(mImageUri));
////
////            mUploadTask = fileReference.putFile(mImageUri)
////                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
////                        @Override
////                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////                            Handler handler = new Handler();
////                            handler.post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    progressDialog.setProgress(0);
////                                }
////                            });
////
////                            Store store = new Store(
////                                    mNamaTokoEditText.getText().toString().trim(),
////                                    mDeskripsiTokoEditText.getText().toString().trim(),
////                                    taskSnapshot.getUploadSessionUri().toString()
////                            );
////
////                            mDatabaseRef.child(dataID).setValue(store);
////
////                            progressDialog.dismiss();
////                            longToast(getApplicationContext(), "Berhasil Membuat Toko");
////                            finish();
////
////                        }
////                    })
////                    .addOnFailureListener(new OnFailureListener() {
////                        @Override
////                        public void onFailure(@NonNull Exception e) {
////                            progressDialog.dismiss();
////                            longToast(getApplicationContext(), e.getMessage());
////                        }
////                    })
////                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
////                        @Override
////                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
////                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
////                            progressDialog.setProgress((int) progress);
////                        }
////                    });
//        }
//    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case PICK_IMAGE_REQUEST:
//                    mImageUri = data.getData();
//                    try {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
//                        mGambarTokoImageButton.setImageBitmap(bitmap);
//                    } catch (IOException e) {
//                        longToast(getApplicationContext(), e.toString());
//                    }
//                    break;
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }

//    private void mOpenFilePicker() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//
//        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
//        sIntent.addCategory(Intent.CATEGORY_DEFAULT);
//
//        Intent chooserIntent;
//        if (getPackageManager().resolveActivity(sIntent, 0) != null) {
//            chooserIntent = Intent.createChooser(sIntent, "Open File");
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intent});
//        } else {
//            chooserIntent = Intent.createChooser(intent, "Open file");
//        }
//
//        try {
//            startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
//        } catch (android.content.ActivityNotFoundException e) {
//
//        }
//    }
//
//    private String getFileExtension(Uri uri){
//        ContentResolver cR = getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(cR.getType(uri));
//    }
}
