package com.example.biznus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biznus.Adapter.UserAdapter;
import com.example.biznus.Listener.UserListener;
import com.example.biznus.Model.ChatMessage;
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
import java.util.Arrays;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {

    ImageView close;
    DatabaseReference reference;
    RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private List<String> users;
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
        users = new ArrayList<>();
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

        DatabaseReference followings = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");

        DatabaseReference myChats = FirebaseDatabase.getInstance().getReference()
                .child("Chats").child(firebaseUser.getUid());

        DatabaseReference chats = FirebaseDatabase.getInstance().getReference()
                .child("Chats");

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userid", firebaseUser.getUid());
        editor.commit();
        userList.clear();
        users.clear();

        chats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                        ChatMessage chatMessage = snapshot2.getValue(ChatMessage.class);
                        if (chatMessage.getReceiverId().equals(firebaseUser.getUid())) {
                            reference = FirebaseDatabase.getInstance().getReference("Registered Users")
                                    .child(chatMessage.getSenderId());

                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    if (users.size() == 0) {
                                        users.add(user.getUserid());
                                        userList.add(user);
                                        userAdapter.notifyDataSetChanged();
                                    } else if (!users.contains(user.getUserid())) {
                                        users.add(user.getUserid());
                                        userList.add(user);
                                        userAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ChatMessage chatMessage = snapshot1.getValue(ChatMessage.class);
                    reference = FirebaseDatabase.getInstance().getReference("Registered Users")
                            .child(chatMessage.getReceiverId());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (users.size() == 0) {
                                users.add(user.getUserid());
                                userList.add(user);
                                userAdapter.notifyDataSetChanged();
                            } else if (!users.contains(user.getUserid())) {
                                users.add(user.getUserid());
                                userList.add(user);
                                userAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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