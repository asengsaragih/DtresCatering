package com.android.dtrescatering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.dtrescatering.base.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.android.dtrescatering.base.MethodeFunction.longToast;

public class SignupActivity extends AppCompatActivity {

    private TextView mSignInTextView;
    private EditText mNamaEditText, mEmailEditText, mPasswordEditText, mRePasswordEditText, mPhoneEditText;
    private Button mSignUpButton;
    private FirebaseAuth auth;
    private String mNamaUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignInTextView = findViewById(R.id.textView_signup_login);
        mNamaEditText = findViewById(R.id.editText_signup_nama);
        mEmailEditText = findViewById(R.id.editText_signup_email);
        mPasswordEditText = findViewById(R.id.editText_signup_password);
        mRePasswordEditText = findViewById(R.id.editText_signup_repassword);
        mPhoneEditText = findViewById(R.id.editText_signup_phone);
        mSignUpButton = findViewById(R.id.button_signup);

        mButtonClicked();

        mHideActionBar();
    }

    private void mButtonClicked() {
        mSignInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignUpUser();
            }
        });
    }

    private void mSignUpUser() {
        final String nama = mNamaEditText.getText().toString().toLowerCase();
        final String email = mEmailEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();
        final String rePassword = mRePasswordEditText.getText().toString();
        final String phone = mPhoneEditText.getText().toString();

        auth = FirebaseAuth.getInstance();

        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword) || TextUtils.isEmpty(phone)) {
            longToast(getApplicationContext(), "Field Tidak Boleh Kosong");
        } else {

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                longToast(getApplicationContext(), "Gagal Daftar " + task.getException());
                            } else {
                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                    mNamaUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                                } else {
                                    startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                                }

                                databaseReference = FirebaseDatabase.getInstance().getReference(mNamaUser);
                                User user = new User(nama, email, password, rePassword, phone);
                                databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void mHideActionBar() {
        getSupportActionBar().hide();
    }
}
