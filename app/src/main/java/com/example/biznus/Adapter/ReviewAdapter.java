package com.example.biznus.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.biznus.Model.Review;
import com.example.biznus.Model.User;
import com.example.biznus.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context mContext;
    private List<Review> mList;

    public ReviewAdapter(Context mContext, List<Review> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_item, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = mList.get(position);
        holder.review.setText(review.getReview());
        getUserInfo(holder.profileImage, holder.username, review.getUserid());
        holder.ratingBar.setRating(review.getRating());
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView review, username;
        RatingBar ratingBar;
        ImageView profileImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            review = itemView.findViewById(R.id.review);
            username = itemView.findViewById(R.id.username);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            profileImage = itemView.findViewById(R.id.image_profile);
        }
    }
}
