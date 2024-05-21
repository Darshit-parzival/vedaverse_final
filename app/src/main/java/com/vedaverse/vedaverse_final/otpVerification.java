package com.vedaverse.vedaverse_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class otpVerification extends AppCompatActivity {

    private static final String TAG = "otpVerification";
    ProgressBar progressBar;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    Long timeout = 60L;
    Button vbtn, sub;
    EditText nm, email, otpEditText;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextView nmlbl,emlbl;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        vbtn = findViewById(R.id.verifyButton);
        sub = findViewById(R.id.submitButton);
        nm = findViewById(R.id.nameEditText);
        email = findViewById(R.id.emailEditText);
        otpEditText = findViewById(R.id.otpEditText);
        nmlbl=findViewById(R.id.nop);
        emlbl=findViewById(R.id.emop);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String countryCode = intent.getStringExtra("COUNTRY_CODE");
        String mobileNumber = intent.getStringExtra("MOBILE_NUMBER");

        Log.d(TAG, "Country Code: " + countryCode);
        Log.d(TAG, "Mobile Number: " + mobileNumber);

        sendOtp(countryCode + mobileNumber, false);

        vbtn.setOnClickListener(v -> {
            String code = otpEditText.getText().toString().trim();
            if (code.isEmpty()) {
                otpEditText.setError("Enter OTP");
                otpEditText.requestFocus();
                return;
            }
            verifyCode(code);
        });
    }

    void sendOtp(String num, boolean resend) {

        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(num)
                .setTimeout(timeout, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential);
                        signin(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e(TAG, "onVerificationFailed: " + e.getMessage(), e);
                        Toast.makeText(getApplicationContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        Log.d(TAG, "onCodeSent: " + s);
                        Toast.makeText(getApplicationContext(), "Sent Successfully", Toast.LENGTH_LONG).show();

                    }
                });

        if (resend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, code);
        signin(credential);
    }


    void signin(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        nm.setVisibility(View.VISIBLE);
                        email.setVisibility(View.VISIBLE);
                        sub.setVisibility(View.VISIBLE);
                        nmlbl.setVisibility(View.VISIBLE);
                        emlbl.setVisibility(View.VISIBLE);

                        sub.setOnClickListener(v -> {
                            String name = nm.getText().toString().trim();
                            String emailText = email.getText().toString().trim();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                String phoneNumber = currentUser.getPhoneNumber();

                                if (name.isEmpty()) {
                                    nm.setError("Enter Name");
                                    nm.requestFocus();
                                    return;
                                }
                                if (emailText.isEmpty()) {
                                    email.setError("Enter Email");
                                    email.requestFocus();
                                    return;
                                }

                                Map<String, Object> user = new HashMap<>();
                                user.put("name", name);
                                user.put("email", emailText);
                                user.put("phone", phoneNumber);

                                db.collection("users").document(userId)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");

                                            Intent nextActivity = new Intent(otpVerification.this, Home.class);
                                            startActivity(nextActivity);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                            }
                        });

                    } else {
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(otpVerification.this, "Verification Failed", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
