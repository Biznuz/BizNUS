package com.example.biznus.ui.account;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.biznus.Adapter.MyListingsAdapter;
import com.example.biznus.Adapter.NotificationAdapter;
import com.example.biznus.Adapter.ReviewAdapter;
import com.example.biznus.ChatActivity;
import com.example.biznus.ChatBotActivity;
import com.example.biznus.Decoration.Space;
import com.example.biznus.EditProfileActivity;
import com.example.biznus.LoginActivity;
import com.example.biznus.MainActivity;
import com.example.biznus.Model.Notification;
import com.example.biznus.Model.Post;
import com.example.biznus.Model.Review;
import com.example.biznus.Model.User;
import com.example.biznus.R;
import com.example.biznus.RegisterActivity;
import com.example.biznus.databinding.FragmentAccountBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AccountFragment extends Fragment {

    ImageView profileImage, options, chatbot, leftHighlight, rightHighlight;
    TextView lists, followers, following, fullname, bio, username;
    Button edit_profile, message;

    RecyclerView recyclerView, recyclerViewReviews;
    MyListingsAdapter myListingsAdapter;
    ReviewAdapter reviewAdapter;
    List<Post> postList;
    List<Review> reviewList;

    FirebaseUser firebaseUser;
    String profileId;

    ImageButton listings, reviews;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("userid", "none");

        if (profileId.equals("none")) {
            SharedPreferences prefs1 = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs1.edit();
            editor.putString("userid", firebaseUser.getUid());
            editor.commit();
            profileId = prefs1.getString("userid", "none");
        }

        profileImage = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        lists = view.findViewById(R.id.lists);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        edit_profile = view.findViewById(R.id.edit_profile);
        listings = view.findViewById(R.id.my_listings);
        reviews = view.findViewById(R.id.my_reviews);
        chatbot = view.findViewById(R.id.chatbot);
        message = view.findViewById(R.id.message);


        leftHighlight = view.findViewById(R.id.left_under);
        rightHighlight = view.findViewById(R.id.right_under);
        rightHighlight.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new Space(getContext(), 10));
        postList = new ArrayList<>();
        myListingsAdapter = new MyListingsAdapter(getContext(), postList);
        recyclerView.setAdapter(myListingsAdapter);

        recyclerViewReviews = view.findViewById(R.id.recycler_view_reviews);
        recyclerViewReviews.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        recyclerViewReviews.setLayoutManager(linearLayoutManager2);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(getContext(), reviewList);
        recyclerViewReviews.setAdapter(reviewAdapter);

        myListings();
        userInfo();
        getFollowers();
        getMyListings();

        listings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewReviews.setVisibility(View.GONE);
                leftHighlight.setVisibility(View.VISIBLE);
                rightHighlight.setVisibility(View.GONE);
            }
        });

        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewReviews.setVisibility(View.VISIBLE);
                leftHighlight.setVisibility(View.GONE);
                rightHighlight.setVisibility(View.VISIBLE);
                getReviews();
            }
        });


        if (profileId.equals(firebaseUser.getUid())) {
            edit_profile.setText("Edit Profile");
            message.setText("Logout");
        } else {
            checkFollow();
            reviews.setVisibility(View.VISIBLE);
        }

        // edit profile button
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String button = edit_profile.getText().toString();

                if (button.equals("Edit Profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else if (button.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    addNotifications();
                } else if (button.equals("following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().equals("Logout")) {
                    FirebaseAuth.getInstance().signOut();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users")
                            .child(firebaseUser.getUid()).child("FCMtoken");
                    reference.removeValue();

                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else if (message.getText().toString().equals("Message")) {
                    chat();
                }
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.account_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.logoutButton) {
                            FirebaseAuth.getInstance().signOut();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users")
                                    .child(firebaseUser.getUid()).child("FCMtoken");
                            reference.removeValue();

                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChatBotActivity.class));
            }
        });

        return view;

    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(profileImage);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());

                if (!profileId.equals(firebaseUser.getUid())) {
                    SharedPreferences prefs1 = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs1.edit();
                    editor.remove("userid");
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    edit_profile.setText("following");
                } else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("following");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMyListings() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Listings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    if (post.getLister().equals(profileId)) {
                        i++;
                    }
                }

                lists.setText("" + i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myListings() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Listings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    if (post.getLister().equals(profileId)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myListingsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("notifs", "started following you");
        hashMap.put("listID", "");
        hashMap.put("islist", false);

        reference.push().setValue(hashMap);
    }

    private void getReviews() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Reviews").child(profileId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Review review = snapshot1.getValue(Review.class);
                    reviewList.add(review);
                }

                Collections.reverse(reviewList);
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chat() {
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userid", firebaseUser.getUid());
        editor.commit();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Intent i = new Intent(getContext(), ChatActivity.class);
                i.putExtra("user", (Serializable) user);
                startActivity(i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}