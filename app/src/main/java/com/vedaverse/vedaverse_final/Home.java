package com.vedaverse.vedaverse_final;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity {

    private TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nameTextView = findViewById(R.id.nameTextView);

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
                    finish();
                    return true;
                } else {
                    return false;
                }
            });

            popupMenu.show();
        });
    }
}
