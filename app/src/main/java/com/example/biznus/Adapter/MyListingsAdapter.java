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

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getListID());
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
        public TextView postPrice, postTitle, postCondition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            postPrice = itemView.findViewById(R.id.post_price);
            postTitle = itemView.findViewById(R.id.post_title);
            postCondition = itemView.findViewById(R.id.condition);
        }
    }
}
