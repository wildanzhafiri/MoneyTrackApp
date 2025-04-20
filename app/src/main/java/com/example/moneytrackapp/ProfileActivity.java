package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView imageProfile;
    TextInputEditText editUsername;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageProfile = findViewById(R.id.profile_image);
        editUsername = findViewById(R.id.username_input);
        saveButton = findViewById(R.id.btn_save_profile);

        //  Event when save button is clicked
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = Objects.requireNonNull(editUsername.getText()).toString().trim();
                Toast.makeText(ProfileActivity.this, "Saved: " + username, Toast.LENGTH_SHORT).show();

                // send username to SettingsActivity
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                intent.putExtra("username_key", username);
                startActivity(intent);
            }
        });

        // Event when image profile is clicked
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Change profile photo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
