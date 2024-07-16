package com.example.biznus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biznus.Adapter.ChatRVAdapter;
import com.example.biznus.Model.ChatsModel;
import com.example.biznus.Model.MsgModel;
import com.example.biznus.ui.account.AccountFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.Distribution;
import com.stripe.model.tax.Registration;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatBotActivity extends AppCompatActivity {

    private RecyclerView idChats;
    private EditText editMsg;
    private FloatingActionButton FABsend;
    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private ArrayList<ChatsModel> chatsModelArrayList;
    private ChatRVAdapter chatRVAdapter;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        idChats = findViewById(R.id.idChats);
        editMsg = findViewById(R.id.EditMsg);
        FABsend = findViewById(R.id.FABsend);
        backButton = findViewById(R.id.back);
        chatsModelArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatsModelArrayList, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        idChats.setLayoutManager(manager);
        idChats.setAdapter(chatRVAdapter);


        FABsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMsg.getText().toString().isEmpty()) {
                    Toast.makeText(ChatBotActivity.this, "Please enter your message.", Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(editMsg.getText().toString());
                editMsg.setText("");

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
    private void getResponse(String message) {
        chatsModelArrayList.add(new ChatsModel(message, USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=182697&key=TMvHvRyY5Rd70gFT&uid=[uid]&msg=" + message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModel> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModel>() {
            @Override
            public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                if (response.isSuccessful()) {
                    MsgModel model = response.body();
                    chatsModelArrayList.add(new ChatsModel(model.getCnt(), BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModel> call, Throwable throwable) {
                chatsModelArrayList.add(new ChatsModel("Please revert your question", BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
            }
        });
    }
}
