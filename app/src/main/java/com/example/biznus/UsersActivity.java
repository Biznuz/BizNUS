package com.example.biznus;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biznus.Adapter.NotificationAdapter;
import com.example.biznus.Adapter.UserAdapter;
import com.example.biznus.Listener.UserListener;
import com.example.biznus.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {

    ImageView close;
    DatabaseReference reference;
    RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        close = findViewById(R.id.close);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, this);
        recyclerView.setAdapter(userAdapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUsers();
    }

    private void getUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Registered Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    Log.d("testFFF", "User found: " + user.getId());
                    Log.d("testFFF", "firebaseuser id: " + firebaseUser.getUid());
                    if (firebaseUser.getUid().equals(user.getId())) {
                        Log.d("testFFF", "Same user");
                        continue;
                    }
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                Log.d("testFFF", "User list updated, size: " + userList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("testFFF", "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onUserClicked(User user) {
        Intent i = new Intent(getApplicationContext(), ChatActivity.class);
        i.putExtra("user", (Serializable) user);
        startActivity(i);
        finish();
    }
}