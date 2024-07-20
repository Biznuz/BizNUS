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
import com.example.biznus.R;
import com.example.biznus.ui.ListingDetailFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.N;

import java.util.List;

public class MyListingsAdapter extends RecyclerView.Adapter<MyListingsAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> mPost;

    public MyListingsAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_listings, parent, false);
        return new MyListingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPost.get(position);

        Glide.with(mContext).load(post.getListImage()).into(holder.post_image);
        holder.postTitle.setText(post.getTitle());
        holder.postPrice.setText("$" + post.getPrice());
        holder.postCondition.setText(post.getCondition());

        if (post.getIsSold()) {
            holder.sold.setVisibility(View.VISIBLE);
        }

        if (post.getListID() != null) {
            isLiked(post.getListID(), holder.like);
            holder.like.setImageResource(R.drawable.baseline_favorite_24);
            totalLikes(holder.likes, post.getListID());
        }

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("listID", post.getListID());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container, new ListingDetailFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView post_image, like;
        public TextView postPrice, postTitle, postCondition, likes, sold;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            postPrice = itemView.findViewById(R.id.post_price);
            postTitle = itemView.findViewById(R.id.post_title);
            postCondition = itemView.findViewById(R.id.condition);
            likes = itemView.findViewById(R.id.likes);
            sold = itemView.findViewById(R.id.sold);
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
}
