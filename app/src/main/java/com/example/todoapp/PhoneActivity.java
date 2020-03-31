package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
    private EditText editPhone;
    private EditText editCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private TextView tv_timer;
    LinearLayout numberField;
    LinearLayout numberCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        editPhone = findViewById(R.id.editPhone);
        editCode = findViewById(R.id.editCode);
        tv_timer = findViewById(R.id.tv_timer);
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.e("TAG", "onVerificationCompleted");
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e("TAG", "onVerificationFailed:" + e.getMessage());

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PhoneActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PhoneActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(PhoneActivity.this, "Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }

    public void onClickContinue(View view) {
        if (editPhone.getText().toString().trim().matches("")) {
            Toast.makeText(getApplicationContext(), "Enter your phone number", Toast.LENGTH_LONG).show();
        } else {
            String phone = editPhone.getText().toString().trim();
            numberField = findViewById(R.id.first_screen);
            numberCode = findViewById(R.id.second_screen);
            numberField.setVisibility(View.INVISIBLE);
            numberCode.setVisibility(View.VISIBLE);
            new CountDownTimer(60000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    tv_timer.setText("Remaining time:"+ millisUntilFinished/1000);
                }

                @Override
                public void onFinish() {
                    numberField.setVisibility(View.VISIBLE);
                    numberCode.setVisibility(View.INVISIBLE);

                }
            };
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    callbacks);
        }

    }

    public void onCodeClick(View view) {

        if (editCode.getText().toString().trim().matches("")){
            Toast.makeText(getApplicationContext(), "Enter your code", Toast.LENGTH_LONG).show();
        }else
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

    }
}