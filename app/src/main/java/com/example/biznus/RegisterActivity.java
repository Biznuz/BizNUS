package com.example.biznus;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextUsername;
    Button registerButton;

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    FirebaseDatabase database;
    DatabaseReference reference;

    private static final String TAG= "RegisterActivity";

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent i = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(i);
//            finish();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");

        // dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextUsername = findViewById(R.id.lister);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.loading);
        textView = findViewById(R.id.login);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String username = editTextUsername.getText().toString();


                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Valid email required");
                    editTextEmail.requestFocus();
                } else {
                    registerUser(username, email, password);
                }
            }

//                if (TextUtils.isEmpty(username)) {
//                    Toast.makeText(RegisterActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
//                    editTextUsername.setError("Username is required");
//                    editTextUsername.requestFocus();
//                }
//                if (TextUtils.isEmpty(email)) {
//                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
//                    editTextEmail.setError("Email is required");
//                    editTextEmail.requestFocus();
//                }
//                if (TextUtils.isEmpty(password)) {
//                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
//                    editTextPassword.setError("Password is required");
//                    editTextPassword.requestFocus();
//                }
//                if (password.length() < 6) {
//                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
//                    editTextPassword.setError("Password too weak");
//                    editTextPassword.requestFocus();
//                }

        });



        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();
                registerButton.setEnabled(!email.isEmpty() && !username.isEmpty() && password.length() >= 6);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // ignore
            }
        };

        editTextUsername.addTextChangedListener(afterTextChangedListener);
        editTextEmail.addTextChangedListener(afterTextChangedListener);
        editTextPassword.addTextChangedListener(afterTextChangedListener);

    }

    private void registerUser(String username, String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            //String userid = firebaseUser.getUid();

                            // write to user data
                            ReadWriteUserData writeUserData = new ReadWriteUserData(username);
                            reference = FirebaseDatabase.getInstance().getReference("Registered Users");
                            reference.child(firebaseUser.getUid()).setValue(writeUserData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        // send verification email
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "Account created",
                                                Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                editTextPassword.setError("Password too weak");
                                editTextPassword.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                editTextPassword.setError("Invalid Credentials");
                                editTextPassword.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                editTextPassword.setError("User already registered");
                                editTextPassword.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

    }
}