package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button sendCodeButton, submitCodeButton;
    private EditText[] codeInputs;
    private String generatedCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.emailInput);
        sendCodeButton = findViewById(R.id.sendCodeButton);
        submitCodeButton = findViewById(R.id.submitCodeButton);

        codeInputs = new EditText[6];
        codeInputs[0] = findViewById(R.id.codeInput1);
        codeInputs[1] = findViewById(R.id.codeInput2);
        codeInputs[2] = findViewById(R.id.codeInput3);
        codeInputs[3] = findViewById(R.id.codeInput4);
        codeInputs[4] = findViewById(R.id.codeInput5);
        codeInputs[5] = findViewById(R.id.codeInput6);

        // Set up automatic focus change for code input fields
        setupCodeInputs();

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Masukkan email Anda", Toast.LENGTH_SHORT).show();
                } else {
                    // Generate random 6-digit code
                    generatedCode = generateRandomCode();
                    
                    // Show the verification code in a dialog
                    showVerificationCodeDialog(generatedCode);
                }
            }
        });

        submitCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect all digits from the input fields
                StringBuilder enteredCode = new StringBuilder();
                for (EditText codeInput : codeInputs) {
                    enteredCode.append(codeInput.getText().toString());
                }

                // Check if the entered code is empty
                if (enteredCode.length() < 6) {
                    Toast.makeText(ForgotPasswordActivity.this, "Masukkan kode verifikasi lengkap", Toast.LENGTH_SHORT).show();
                } 
                // Check if the entered code matches the generated code
                else if (generatedCode.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Silakan kirim kode verifikasi terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if (enteredCode.toString().equals(generatedCode)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Kode verifikasi benar", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to change password screen
                    Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Kode verifikasi salah", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(code);
    }

    private void showVerificationCodeDialog(String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kode Verifikasi");
        builder.setMessage("Kode verifikasi untuk reset password adalah: " + code);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            // Auto-fill first digit to improve UX
            if (!code.isEmpty()) {
                codeInputs[0].setText(String.valueOf(code.charAt(0)));
                codeInputs[0].setSelection(1);
                codeInputs[1].requestFocus();
            }
        });
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupCodeInputs() {
        // Auto-focus next input when typing
        for (int i = 0; i < codeInputs.length - 1; i++) {
            final int currentIndex = i;
            codeInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) {
                        codeInputs[currentIndex + 1].requestFocus();
                    }
                }
            });
        }

        // Add backspace key handler to go back to previous field
        for (int i = 1; i < codeInputs.length; i++) {
            final int currentIndex = i;
            codeInputs[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && codeInputs[currentIndex].getText().toString().isEmpty()) {
                    codeInputs[currentIndex - 1].requestFocus();
                    return true;
                }
                return false;
            });
        }
    }
} 