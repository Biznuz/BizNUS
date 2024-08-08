package com.example.biznus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText emailText;
    Button back_button, forgetPassword;
    FirebaseAuth mAuth;
    ProgressBar forgetPasswordProgressBar;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        emailText = findViewById(R.id.email);
        back_button = findViewById(R.id.back_button);
        forgetPassword = findViewById(R.id.forgetPassword);
        forgetPasswordProgressBar = findViewById(R.id.forgetPasswordProgressBar);

        mAuth = FirebaseAuth.getInstance();

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailText.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {
                    ResetPassword();
                } else {
                    emailText.setError("Email field cannot be empty");
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void ResetPassword() {
        forgetPasswordProgressBar.setVisibility(View.VISIBLE);
        forgetPassword.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPasswordActivity.this, "Reset Password Link has been sent to your email.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                forgetPassword.setVisibility(View.VISIBLE);
                forgetPasswordProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
