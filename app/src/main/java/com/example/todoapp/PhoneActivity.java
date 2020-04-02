package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
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
    FirebaseAuth mAuth;
    String codeFromInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        mAuth = FirebaseAuth.getInstance();
        editPhone = (EditText) findViewById(R.id.editPhone);
        editCode = findViewById(R.id.editCode);
        tv_timer = findViewById(R.id.tv_timer);
        numberField = findViewById(R.id.first_screen);
        numberCode = findViewById(R.id.second_screen);
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.e("TAG", "onVerificationCompleted");
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    signIn(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e("TAG", "onVerificationFailed:" + e.getMessage());
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                codeFromInternet = s;
                Log.e("TAG", "onCodeSent: " + s);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PhoneActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PhoneActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(PhoneActivity.this, "Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void onClickContinue(View view) {
        String phone = editPhone.getText().toString().trim();
        if (phone == null) {
            editPhone.setError("Phone number is required");
            editPhone.requestFocus();
            return;
        } else {
            if (TextUtils.isEmpty(phone) && phone.length() > 9) {
                Toast.makeText(getApplicationContext(), "Enter your phone number", Toast.LENGTH_LONG).show();
                editPhone.setError("Phone number is required");
                editPhone.requestFocus();
                return;
            } else if (phone.length() == 9) {
                phone = "+996" + editPhone.getText().toString();
            }

            numberField.setVisibility(View.INVISIBLE);
            numberCode.setVisibility(View.VISIBLE);
            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tv_timer.setText("Remaining time:" + millisUntilFinished / 1000);
                    tv_timer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    numberField.setVisibility(View.VISIBLE);
                    numberCode.setVisibility(View.INVISIBLE);
                    tv_timer.setVisibility(View.INVISIBLE);
                    Toast.makeText(PhoneActivity.this, "Ваше время истекло", Toast.LENGTH_LONG).show();
                }
            }.start();
            FirebaseAuthSettings firebaseAuthSettings = mAuth.getFirebaseAuthSettings();

            firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phone, codeFromInternet);
            Log.e("TAG", "onClickContinue: " + phone);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    callbacks);
        }
    }

    public void onCodeClick(View view) {
        String code = editCode.getText().toString().trim();

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(getApplicationContext(), "Enter your code", Toast.LENGTH_LONG).show();
            editCode.setError("Code is required");
            editCode.requestFocus();
            return;
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeFromInternet, code);
            signIn(credential);
        }
    }
}