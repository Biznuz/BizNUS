package com.example.biznus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biznus.Model.ChatsModel;
import com.example.biznus.R;

import java.util.ArrayList;

public class ChatRVAdapter extends RecyclerView.Adapter {

    private ArrayList<ChatsModel> chatsModelArrayList;
    private Context context;
    private static final int USER_TYPE = 0;
    private static final int BOT_TYPE = 1;

    public ChatRVAdapter(ArrayList<ChatsModel> chatsModelArrayList, Context context) {
        this.chatsModelArrayList = chatsModelArrayList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        ChatsModel chatsModel = chatsModelArrayList.get(position);
        if ("user".equals(chatsModel.getSender())) {
            return USER_TYPE;
        } else {
            return BOT_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == USER_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_msg_rv_item, parent, false);
            return new UserViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_msg_rv_item, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatsModel chatsModel = chatsModelArrayList.get(position);

        if (holder.getItemViewType() == USER_TYPE) {
            ((UserViewHolder) holder).userMsg.setText(chatsModel.getMessage());
        } else {
            ((BotViewHolder) holder).botMsg.setText(chatsModel.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatsModelArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView userMsg;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userMsg = itemView.findViewById(R.id.userMsg);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {

        TextView botMsg;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botMsg = itemView.findViewById(R.id.botMsg);
        }
    }
}
