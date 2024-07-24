package com.example.biznus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biznus.Adapter.ChatAdapter;
import com.example.biznus.Model.ChatMessage;
import com.example.biznus.Model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private User receiver;
    TextView name;
    ImageView backButton;
    EditText message;
    FrameLayout sendButton;

    private List<ChatMessage> chatMessageList;
    private ChatAdapter chatAdapter;
    private SharedPreferences prefs;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        name = findViewById(R.id.name);
        message = findViewById(R.id.message);
        backButton = findViewById(R.id.back_button);
        sendButton = findViewById(R.id.send);
        recyclerView = findViewById(R.id.recycler_view);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UsersActivity.class);
                startActivity(i);
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        getReceiverInfo();
        init();
        readMessages();
        listenMessages();
    }

    private void init() {
        prefs = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        chatMessageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getApplicationContext(), chatMessageList,
                prefs.getString("imageurl", "none"), prefs.getString("userid", "none"));
        recyclerView.setAdapter(chatAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
    }

    private void sendMessage() {
        String messageId = databaseReference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("messageId", messageId);
        hashMap.put("senderId", prefs.getString("userid", "none"));
        hashMap.put("receiverId", receiver.getUserid());
        hashMap.put("message", message.getText().toString());
        hashMap.put("timeStamp", new Date());
        databaseReference.child(prefs.getString("userid", "none"))
                .child(messageId).updateChildren(hashMap);
        message.setText(null);

        readMessages();
        listenMessages();
    }

    private void listenMessages() {
        databaseReference.child(receiver.getUserid()).addChildEventListener(childEventListener);
    }
    private final ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
            if (chatMessage != null) {
                chatMessageList.add(chatMessage);
                chatMessage.setTimeSent(getDateFormat(chatMessage.getTimeStamp()));
                chatMessage.setTimeStamp(chatMessage.getTimeStamp());
                Collections.sort(chatMessageList, (obj1, obj2) -> obj1.getTimeStamp().compareTo(obj2.getTimeStamp()));
                chatAdapter.notifyItemInserted(chatMessageList.size() - 1);
                recyclerView.smoothScrollToPosition(chatMessageList.size() - 1);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private void readMessages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(prefs.getString("userid", "none"));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessageList.clear();
                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    ChatMessage chatMessage = snapshots.getValue(ChatMessage.class);
                    chatMessage.setTimeSent(getDateFormat(chatMessage.getTimeStamp()));
                    chatMessage.setTimeStamp(chatMessage.getTimeStamp());
                    if (chatMessage.getSenderId().equals(prefs.getString("userid", "none")) &&
                    chatMessage.getReceiverId().equals(receiver.getUserid())) {
                        chatMessageList.add(chatMessage);
                    }

                }
                Collections.sort(chatMessageList, (obj1, obj2) -> obj1.getTimeStamp().compareTo(obj2.getTimeStamp()));
                if (chatMessageList.size() == 0) {
                    chatAdapter.notifyDataSetChanged();
                } else {
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(chatMessageList.size() - 1);
                }
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getReceiverInfo() {
        receiver = (User) getIntent().getSerializableExtra("user");
        name.setText(receiver.getUsername());
    }

    private String getDateFormat(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}