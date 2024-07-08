package com.example.biznus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.biznus.Model.User;

public class ChatActivity extends AppCompatActivity {

    private User receiver;
    TextView name;
    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        name = findViewById(R.id.name);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UsersActivity.class);
                startActivity(i);
                finish();
            }
        });

        getReceiverInfo();
    }

    private void getReceiverInfo() {
        receiver = (User) getIntent().getSerializableExtra("user");
        Log.d("testF", receiver.getUsername());
        name.setText(receiver.getUsername());
    }
}