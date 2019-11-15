package com.android.dtrescatering;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SignupActivity extends AppCompatActivity {

    private TextView mSignInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignInTextView = findViewById(R.id.textView_signup_login);

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
    }

    private void mHideActionBar() {
        getSupportActionBar().hide();
    }
}
