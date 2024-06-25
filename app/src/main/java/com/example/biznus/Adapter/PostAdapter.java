package com.example.biznus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.biznus.Model.Post;
import com.example.biznus.Model.User;
import com.example.biznus.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);

        Glide.with(mContext).load(post.getListImage()).into(holder.post_image);

        if (post.getTitle().equals("")) {
            holder.postTitle.setVisibility(View.GONE);
        } else {
            holder.postTitle.setVisibility(View.VISIBLE);
            holder.postTitle.setText(post.getTitle());
            holder.postPrice.setText("$" + post.getPrice());
            holder.postCondition.setText(post.getCondition());
            holder.username.setText(post.getLister());
        }

        publisherInfo(holder.image_profile, holder.username, post.getLister());


        // post.getId() is null
        isLiked(post.getListId(), holder.like);
        totalLikes(holder.likes, post.getListId());



        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("liked")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getListId())
                            .child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getListId())
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

        public ImageView image_profile, post_image, like;
        public TextView username, postPrice, postTitle, postDescription, postCondition, likes;
        public RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            likes = itemView.findViewById(R.id.likes);
            username = itemView.findViewById(R.id.username);
            postPrice = itemView.findViewById(R.id.post_price);
            postTitle = itemView.findViewById(R.id.post_title);
            postDescription = itemView.findViewById(R.id.description);
            postCondition = itemView.findViewById(R.id.condition);
            recyclerView = itemView.findViewById(R.id.recycler_view);
        }
    }

    private void isLiked(String listId, ImageView imageView) {
        FirebaseUser firebaseUser1 = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(listId);
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

    private void totalLikes(TextView likes, String listId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(listId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + "likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
}
