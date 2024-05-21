package com.vedaverse.vedaverse_final;

import static android.content.ContentValues.TAG;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vedaverse.vedaverse_final.utils.AndroidUtil;

import java.util.ArrayList;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private TextView nameTextView,curetext;
    private EditText diseasesEditText;
    private ImageButton voiceInputButton;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nameTextView = findViewById(R.id.nameTextView);
        diseasesEditText = findViewById(R.id.disease);
        voiceInputButton = findViewById(R.id.voiceInputButton);
        curetext = findViewById(R.id.centerTextView);


        curetext.setOnClickListener(v -> {
            String diseaseName = diseasesEditText.getText().toString().trim();
            if (!diseaseName.isEmpty()) {
                Intent intent = new Intent(Home.this, curePage.class);
                intent.putExtra("diseaseName", diseaseName);
                startActivity(intent);
            } else {
                Toast.makeText(Home.this, "Please enter a disease name", Toast.LENGTH_SHORT).show();
            }
        });
        Spinner gender = findViewById(R.id.genderSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_item);

        gender.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            if (name != null) {
                                nameTextView.setText(name);
                            } else {
                                nameTextView.setText("Name not found");
                            }
                        } else {
                            Log.e(TAG, "Document does not exist");
                            nameTextView.setText("Document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user document: " + e.getMessage());
                        nameTextView.setText("Error fetching name");
                    });

        } else {
            Log.d(TAG, "Current user is null");
            nameTextView.setText("User not logged in");
        }

        ImageButton logoutDropdownButton = findViewById(R.id.logoutDropdownButton);

        logoutDropdownButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(Home.this, logoutDropdownButton);

            popupMenu.getMenuInflater().inflate(R.menu.logout_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Home.this, Login.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.theme_change) {
                    toggleDarkMode();
                    return true;
                } else if (item.getItemId() == R.id.addcure) {
                    Intent intent = new Intent(Home.this, cureInsert.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    finish();
                    return true;

                } else {
                    return false;
                }
            });
            popupMenu.show();
        });

        voiceInputButton.setOnClickListener(v -> startVoiceInput());

        submit=findViewById(R.id.submitButtonhome);
        submit.setOnClickListener(v -> {
            String disease = diseasesEditText.getText().toString();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("cure")
                    .whereEqualTo("disease", disease)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String cure = document.getString("cure");
                            if (cure != null) {
                                AndroidUtil.showToast(getApplicationContext(),"Found Cure");
                                curetext.setText("Cure: "+cure);
                            } else {
                                AndroidUtil.showToast(getApplicationContext(),"Not found cure");
                                curetext.setText("");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Home.this, "Failed to retrieve data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                diseasesEditText.setText(result.get(0));
            }
        }
    }

    private void toggleDarkMode() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            nameTextView.setTextColor(Color.BLACK);
            AndroidUtil.showToast(getApplicationContext(), "Switched to Light Mode");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nameTextView.setTextColor(Color.WHITE);
            AndroidUtil.showToast(getApplicationContext(), "Switched to Dark Mode");
        }
    }
}
