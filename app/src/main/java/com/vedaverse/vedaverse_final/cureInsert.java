package com.vedaverse.vedaverse_final;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class cureInsert extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText diseaseEditText, diseaseCureEditText;
    private Button submitButton, imgbtn;
    private FirebaseFirestore db;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cure_insert);

        db = FirebaseFirestore.getInstance();

        diseaseEditText = findViewById(R.id.diseaseEditText);
        diseaseCureEditText = findViewById(R.id.diseaseCureEditText);
        submitButton = findViewById(R.id.submitButton);
        imgbtn=findViewById(R.id.chooseImageButton);



        submitButton.setOnClickListener(v -> {
            String disease = diseaseEditText.getText().toString();
            String cure = diseaseCureEditText.getText().toString();

//            if (imageUri != null) {
//                uploadImageToStorage(disease, cure);
//            } else {
//                addCureToFirestore(disease, cure, null);
//            }
                    if (imageUri == null) {
                        addCureToFirestore(disease, cure);
            }
        });
    }



//    private void uploadImageToStorage(String disease, String cure) {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        final StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
//
//        imageRef.putFile(imageUri)
//                .addOnSuccessListener(taskSnapshot -> {
//                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                        addCureToFirestore(disease, cure, uri.toString());
//                    }).addOnFailureListener(e -> {
//                        Toast.makeText(cureInsert.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(cureInsert.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }



    private void addCureToFirestore(String disease, String cure ) {
        Map<String, Object> data = new HashMap<>();
        data.put("disease", disease);
        data.put("cure", cure);
//        if (imageUrl != null) {
//            data.put("img", imageUrl);
//        }

        db.collection("cure")
                .add(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(cureInsert.this, "Data added successfully", Toast.LENGTH_SHORT).show();

                        diseaseEditText.setText("");
                        diseaseCureEditText.setText("");
                    } else {
                        Toast.makeText(cureInsert.this, "Failed to add data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
        }
    }
}
