package com.example.biznus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReviewActivity extends AppCompatActivity {

    RatingBar ratingBar;
    Button button;
    EditText review;
    String lister;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ratingBar = findViewById(R.id.ratingBar);
        button = findViewById(R.id.submitButton);
        review = findViewById(R.id.review);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        lister = sharedPreferences.getString("lister", "none");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadReview();
            }
        });
    }

    private void uploadReview() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Submitting review...");
        progressDialog.show();

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Reviews").child(lister);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("lister", lister);
        hashMap.put("review", review.getText().toString());
        hashMap.put("rating", ratingBar.getRating());

        reference1.push().setValue(hashMap);

        Toast.makeText(ReviewActivity.this, "Review Submitted", Toast.LENGTH_SHORT).show();

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        finish();

    }
}