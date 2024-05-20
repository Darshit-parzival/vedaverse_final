package com.vedaverse.vedaverse_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NumberPhone extends AppCompatActivity {

    private EditText phoneNumberInput;
    private Button btnSubmit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_phone);

        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        btnSubmit = findViewById(R.id.btnSubmit);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSubmit.setOnClickListener(v -> addPhoneNumberToUser());
    }

    private void addPhoneNumberToUser() {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(NumberPhone.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .update("phoneNumber", phoneNumber)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            redirectToHome();
                        } else {
                            Toast.makeText(NumberPhone.this, "Failed to add phone number", Toast.LENGTH_SHORT).show();
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
