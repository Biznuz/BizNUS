package com.example.biznus.Adapter;

import static android.app.PendingIntent.getActivity;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.biznus.Model.Post;
import com.example.biznus.Model.User;
import com.example.biznus.R;
import com.example.biznus.ui.ListingDetailFragment;
import com.example.biznus.ui.account.AccountFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
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

        if (post != null) {
            Glide.with(mContext).load(post.getListImage()).into(holder.post_image);
        } else {
            Log.e("PostAdapter", "Post object is null at position: " + position);
        }

        if (post.getIsSold()) {
            holder.sold.setVisibility(View.VISIBLE);
        }

        if (post.getTitle().equals("")) {
            holder.postTitle.setVisibility(View.GONE);
        } else {
            holder.postTitle.setVisibility(View.VISIBLE);
            holder.postTitle.setText(post.getTitle());
            holder.postPrice.setText("$" + post.getPrice());
            holder.postCondition.setText(post.getCondition());
            holder.username.setText(post.getLister());
            //Log.d("testing", "post: " + post.getListID());
        }

        publisherInfo(holder.image_profile, holder.username, post.getLister());

        if (post.getListID() != null) {
            isLiked(post.getListID(), holder.like);
            totalLikes(holder.likes, post.getListID());
        }


        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("userid", post.getLister());
                editor.apply();

                NavController navController = Navigation.findNavController((FragmentActivity) mContext, R.id.nav_host_fragment_activity_main);
                Bundle bundle = new Bundle();
                bundle.putString("userid", post.getLister());
                navController.navigate(R.id.action_explore_to_accountFragment, bundle);            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("userid", post.getLister());
                editor.apply();

                NavController navController = Navigation.findNavController((FragmentActivity) mContext, R.id.nav_host_fragment_activity_main);
                Bundle bundle = new Bundle();
                bundle.putString("userid", post.getLister());
                navController.navigate(R.id.action_explore_to_accountFragment, bundle);
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("listID", post.getListID());
                editor.apply();

                FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ListingDetailFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("Listing Detail") // Name can be null
                        .commit();
            }
        });



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

        public ImageView image_profile, post_image, like;
        public TextView username, postPrice, postTitle, postDescription, postCondition, likes, sold;
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
            sold = itemView.findViewById(R.id.sold);
            recyclerView = itemView.findViewById(R.id.recycler_view);
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
                if (!snapshot.exists())
                    return;
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
