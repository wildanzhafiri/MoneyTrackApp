package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

	private Button loginButton, signupButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
		if (currentUser != null) {
			Intent intent = new Intent(WelcomeActivity.this, DashboardActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		setContentView(R.layout.activity_welcome);

		loginButton = findViewById(R.id.loginButton);
		signupButton = findViewById(R.id.signupButton);

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});

		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
				startActivity(intent);
			}
		});
	}
}