package com.example.biznus.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.biznus.Model.Post;
import com.example.biznus.Model.User;
import com.example.biznus.R;
import com.example.biznus.ui.ListingDetailFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class ListDetailAdapter extends RecyclerView.Adapter<ListDetailAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> mPost;
    private FirebaseUser firebaseUser;

    public ListDetailAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_detail_item, parent, false);
        return new ListDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);

        Glide.with(mContext).load(post.getListImage()).into(holder.post_image);
        holder.postTitle.setText(post.getTitle());
        holder.postPrice.setText("$" + post.getPrice());
        holder.postCondition.setText("Condition: " + post.getCondition());
        holder.description.setText("Description: " + post.getDescription());
        holder.username.setText(post.getLister());

        if (post.getListID() != null) {
            holder.like.setImageResource(R.drawable.baseline_favorite_24);
            totalLikes(holder.likes, post.getListID());
        }
        publisherInfo(holder.image_profile, holder.username, post.getLister());
        isLiked(post.getListID(), holder.like);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getListID())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications(post.getLister(), post.getListID());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getListID())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView post_image, like, image_profile;
        public TextView postPrice, postTitle, postCondition, likes, description, username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            postPrice = itemView.findViewById(R.id.post_price);
            postTitle = itemView.findViewById(R.id.post_title);
            postCondition = itemView.findViewById(R.id.condition);
            likes = itemView.findViewById(R.id.likes);
            description = itemView.findViewById(R.id.description);
            username = itemView.findViewById(R.id.username);
            image_profile = itemView.findViewById(R.id.image_profile);
        }
    }

    private void isLiked(String listID, final ImageView imageView) {
        final FirebaseUser firebaseUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (listID != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Likes")
                    .child(listID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(firebaseUser1.getUid()).exists()) {
                        imageView.setImageResource(R.drawable.baseline_favorite_24);
                        imageView.setTag("liked");
                    } else {
                        imageView.setImageResource(R.drawable.ic_favorite);
                        imageView.setTag("like");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void totalLikes(TextView likes, String listId) {
        if (listId != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Likes").child(listId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    likes.setText(snapshot.getChildrenCount() + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void publisherInfo(ImageView image_profile, TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageurl() == null) {
                    Glide.with(mContext).load(R.mipmap.ic_launcher).into(image_profile);
                } else {
                    Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                }
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void addNotifications(String userid, String listid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("notifs", "liked your listing");
        hashMap.put("listID", listid);
        hashMap.put("islist", true);

        reference.push().setValue(hashMap);
    }
}
