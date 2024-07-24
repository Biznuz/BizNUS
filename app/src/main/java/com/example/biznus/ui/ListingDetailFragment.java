package com.example.biznus.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.biznus.Adapter.ListDetailAdapter;
import com.example.biznus.Adapter.PostAdapter;
import com.example.biznus.ChatActivity;
import com.example.biznus.CheckoutActivity;
import com.example.biznus.MainActivity;
import com.example.biznus.Model.Post;
import com.example.biznus.Model.User;
import com.example.biznus.R;
import com.example.biznus.ui.account.AccountFragment;
import com.example.biznus.ui.explore.ExploreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.Stripe;
import com.stripe.android.EphemeralKey;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.model.Customer;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCreateParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.collections.ArrayDeque;

public class ListingDetailFragment extends Fragment {

    String listID, lister;
    String PublishableKey = "pk_test_51PbO2lKfpy6OFgrdWf9jxe7JdAxUkMDKmua3iWR6tKWN2oC2veMxkMWS24KcZXi8SPy8jsPUd5hhUX3XvV3C6Mde00FNuYNyjm";
    private RecyclerView recyclerView;
    private ListDetailAdapter listDetailAdapter;
    private List<Post> list;
    private ImageView back_button, chat_button;

    private TextView buyButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_detail, container, false);
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        back_button = view.findViewById(R.id.back_button);
        buyButton = view.findViewById(R.id.buyButton);
        chat_button = view.findViewById(R.id.chat);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        listID = sharedPreferences.getString("listID", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser.getUid().equals(lister)) {
            buyButton.setVisibility(View.GONE);
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        listDetailAdapter = new ListDetailAdapter(getContext(), list);
        recyclerView.setAdapter(listDetailAdapter);

        readPosts();

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MainActivity.class);
                startActivity(i);
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CheckoutActivity.class);
                startActivity(i);
            }
        });

        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser();
            }
        });

        PaymentConfiguration.init(getContext(), PublishableKey);

        return view;
    }

    private void getUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users").child(lister);
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


    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Listings").child(listID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                Post post = snapshot.getValue(Post.class);
                if (post.getIsSold()) {
                    buyButton.setEnabled(false);
                    buyButton.setText("SOLD");
                    buyButton.setTextColor(Color.RED);
                }
                list.add(post);
                lister = post.getLister();

                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("lister", lister);
                editor.apply();

                listDetailAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}