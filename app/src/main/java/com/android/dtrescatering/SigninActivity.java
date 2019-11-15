package com.android.dtrescatering;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.dtrescatering.base.Session;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.android.dtrescatering.base.MethodeFunction.longToast;
import static com.android.dtrescatering.base.MethodeFunction.shortToast;

public class SigninActivity extends AppCompatActivity {

    private TextView mSignUpTextView;
    private Button mSignInButton;
    private EditText mEmailEditText, mPasswordEditText;
    private FirebaseAuth auth;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        session = new Session(this);
        if (session.loggedIn()){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mSignUpTextView = findViewById(R.id.textView_signin_new_account);
        mSignInButton = findViewById(R.id.button_signin);
        mEmailEditText = findViewById(R.id.editText_signin_email);
        mPasswordEditText = findViewById(R.id.editText_signin_password);

        mButtonClick();

        mHideActionBar();
    }

    private void mButtonClick() {
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignIn();
            }
        });
    }

    private void mSignIn() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        auth = FirebaseAuth.getInstance();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        longToast(getApplicationContext(), "Gagal Login");
                    } else {
                        session.setLoggedin(true);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void mHideActionBar() {
        getSupportActionBar().hide();
    }
}
