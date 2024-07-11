package com.example.biznus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biznus.Adapter.ChatAdapter;
import com.example.biznus.Model.ChatMessage;
import com.example.biznus.Model.Post;
import com.example.biznus.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

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
    }

//    private void listenMessages() {
//        databaseReference.child(prefs.getString("userid", "none")).addChildEventListener(eventListener);
//    }
//    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
//        if (error != null) {
//            return;
//        }
//        if (value != null) {
//            int size = chatMessageList.size();
//            for (DocumentChange documentChange : value.getDocumentChanges()) {
//                if (documentChange.getType() == DocumentChange.Type.ADDED) {
//                    ChatMessage chatMessage = new ChatMessage();
//                    chatMessage.setSenderId(documentChange.getDocument().getString("senderId"));
//                    chatMessage.setReceiverId(documentChange.getDocument().getString("receiverId"));
//                    chatMessage.setMessage(documentChange.getDocument().getString("message"));
//                    chatMessage.setTimeSent(getDate(documentChange.getDocument().getDate("timeStamp")));
//                    chatMessage.setDate(documentChange.getDocument().getDate("timeStamp"));
//                    chatMessageList.add(chatMessage);
//                }
//            }
//            Collections.sort(chatMessageList, (obj1, obj2) -> obj1.getDate().compareTo(obj2.getDate()));
//            if (size == 0) {
//                chatAdapter.notifyDataSetChanged();
//            } else {
//                chatAdapter.notifyItemRangeInserted(chatMessageList.size(), chatMessageList.size());
//                recyclerView.smoothScrollToPosition(chatMessageList.size() - 1);
//            }
//            recyclerView.setVisibility(View.VISIBLE);
//        }
//    };

    private void readMessages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(prefs.getString("userid", "none"));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessageList.clear();
                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    ChatMessage chatMessage = snapshots.getValue(ChatMessage.class);
//                    Log.d("dateTest", getDateFormat(chatMessage.getDate()));
//                    chatMessage.setTimeSent(getDateFormat(chatMessage.getDate()));
//                    chatMessage.setDate(chatMessage.getDate());
                    if (chatMessage.getSenderId().equals(prefs.getString("userid", "none")) &&
                    chatMessage.getReceiverId().equals(receiver.getUserid())) {
                        chatMessageList.add(chatMessage);
                    }

                }
//                Collections.sort(chatMessageList, (obj1, obj2) -> obj1.getDate().compareTo(obj2.getDate()));
                if (chatMessageList.size() == 0) {
                    chatAdapter.notifyDataSetChanged();
                } else {
                    chatAdapter.notifyItemRangeInserted(chatMessageList.size(), chatMessageList.size());
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