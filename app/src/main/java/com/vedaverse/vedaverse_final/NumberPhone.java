package com.vedaverse.vedaverse_final;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vedaverse.vedaverse_final.utils.AndroidUtil;

import java.util.concurrent.TimeUnit;

public class NumberPhone extends AppCompatActivity {

    private EditText phoneNumberInput;
    private EditText verificationCodeInput;
    private Button btnSubmit;
    private Button btnVerify;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_phone);

        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        verificationCodeInput = findViewById(R.id.verificationCodeInputggl);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnVerify = findViewById(R.id.btnVerifyggl);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSubmit.setOnClickListener(v -> sendVerificationCode());

        btnVerify.setOnClickListener(v -> verifyCode());
    }

    private void sendVerificationCode() {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            AndroidUtil.showToast(getApplicationContext(),"Please enter a phone number");
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    AndroidUtil.showToast(getApplicationContext(),"Verification Faild: "+e.getMessage());
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    NumberPhone.this.verificationId = verificationId;
                    AndroidUtil.showToast(getApplicationContext(),"Code sent");
                }
            };

    private void verifyCode() {
        String code = verificationCodeInput.getText().toString().trim();
        if (code.isEmpty()) {
            AndroidUtil.showToast(getApplicationContext(),"Please enter the verification code");
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        updatePhoneNumber();
                    } else {
                        AndroidUtil.showToast(getApplicationContext(),"Verification failed");
                    }
                });
    }

    private void updatePhoneNumber() {
        FirebaseUser user = mAuth.getCurrentUser();
        String phoneNumber = phoneNumberInput.getText().toString().trim();

        if (user != null) {
            db.collection("users").document(user.getUid())
                    .update("phone", phoneNumber)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            redirectToHome();
                        } else {
                            AndroidUtil.showToast(getApplicationContext(),"Failed to add phone number");
                        }
                    });
        }
    }

    private void redirectToHome() {
        Intent intent = new Intent(NumberPhone.this, Home.class);
        startActivity(intent);
        finish();
    }
}
