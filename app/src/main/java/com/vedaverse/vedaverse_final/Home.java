package com.vedaverse.vedaverse_final;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vedaverse.vedaverse_final.utils.AndroidUtil;

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
                    .whereEqualTo("phone", currentUser.getPhoneNumber())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            String name = documentSnapshot.getString("name");
                            nameTextView.setText(name);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user documents: " + e.getMessage());
                    });

        }
        else {
            Log.d(TAG, "Null");
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
