package com.vedaverse.vedaverse_final;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class curePage extends AppCompatActivity {

    private TextView buttonNameTextView;
    private TextView buttonDescriptionTextView;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cure_page);

        db = FirebaseFirestore.getInstance();

        buttonNameTextView = findViewById(R.id.buttonNameTextView);
        buttonDescriptionTextView = findViewById(R.id.buttonDescriptionTextView);

        Intent intent = getIntent();
        if (intent != null) {
            String diseaseName = intent.getStringExtra("diseaseName");

            db.collection("cure")
                    .whereEqualTo("disease", diseaseName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String cureName = document.getString("cure");
                                String cureDescription = document.getString("description");

                                buttonNameTextView.setText(cureName);
                                buttonDescriptionTextView.setText(cureDescription);
                            }
                        }
                    });
        }
    }
}
