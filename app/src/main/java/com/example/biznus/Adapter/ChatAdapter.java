package com.example.biznus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.biznus.Model.ChatMessage;
import com.example.biznus.Model.Post;
import com.example.biznus.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<ChatMessage> chatMessageList;
    private String receiverProfileImage;
    private String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(Context context, List<ChatMessage> chatMessageList, String receiverProfileImage, String senderId) {
        this.mContext = context;
        this.chatMessageList = chatMessageList;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.send_message_item, parent, false);
            return new ChatAdapter.MessageSentViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.receive_message_item, parent, false);
            return new ChatAdapter.MessageReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(position);
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            MessageSentViewHolder messageSentViewHolder = (MessageSentViewHolder) holder;
            messageSentViewHolder.message.setText(chatMessage.getMessage());
            messageSentViewHolder.timeSent.setText(chatMessage.getTimeSent());
        } else {
            MessageReceivedViewHolder messageReceivedViewHolder = (MessageReceivedViewHolder) holder;
            messageReceivedViewHolder.message.setText(chatMessage.getMessage());
            messageReceivedViewHolder.timeSent.setText(chatMessage.getTimeSent());
            Glide.with(mContext).load(receiverProfileImage).into(messageReceivedViewHolder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageList.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    public class MessageSentViewHolder extends RecyclerView.ViewHolder {

        private TextView message, timeSent;

        public MessageSentViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            timeSent = itemView.findViewById(R.id.timeSent);
        }
    }

    public class MessageReceivedViewHolder extends RecyclerView.ViewHolder {

        private TextView message, timeSent;
        private ImageView profileImage;

        public MessageReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            timeSent = itemView.findViewById(R.id.timeSent);
            profileImage = itemView.findViewById(R.id.image_profile);
        }
    }
}
