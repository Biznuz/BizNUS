package com.example.biznus.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.biznus.Model.Notification;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biznus.Model.Post;
import com.example.biznus.Model.User;
import com.example.biznus.R;
import com.example.biznus.ui.ListingDetailFragment;
import com.example.biznus.ui.account.AccountFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context mContext;
    private List<Notification> mList;

    public NotificationAdapter(Context mContext, List<Notification> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = mList.get(position);
        holder.notifs.setText(notification.getNotifs());
        getUserInfo(holder.profileImage, holder.username, notification.getUserid());

        if (notification.isIslist()) {
            holder.list_image.setVisibility(View.VISIBLE);
            getListImage(holder.list_image, notification.getListID());
        } else {
            holder.list_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isIslist()) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("listID", notification.getListID());
                    editor.apply();

                    NavController navController = Navigation.findNavController((FragmentActivity) mContext, R.id.nav_host_fragment_activity_main);
                    Bundle bundle = new Bundle();
                    bundle.putString("listID", notification.getListID());
                    navController.navigate(R.id.action_notification_to_listDetail, bundle);
//
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,
//                            new ListingDetailFragment()).commit();
                } else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileId", notification.getUserid());
                    editor.apply();

                    NavController navController = Navigation.findNavController((FragmentActivity) mContext, R.id.nav_host_fragment_activity_main);
                    Bundle bundle = new Bundle();
                    bundle.putString("profileId", notification.getUserid());
                    navController.navigate(R.id.action_notification_to_accountFragment, bundle);

//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,
//                            new AccountFragment()).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void getUserInfo(ImageView imageView, TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getListImage(ImageView imageView, String listID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Listings")
                .child(listID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getListImage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profileImage, list_image;
        public TextView username, notifs;
        public ViewHolder(@NonNull View view) {
            super(view);

            profileImage = view.findViewById(R.id.image_profile);
            list_image = view.findViewById(R.id.list_image);
            username = view.findViewById(R.id.username);
            notifs = view.findViewById(R.id.notifs);
        }
    }
}
