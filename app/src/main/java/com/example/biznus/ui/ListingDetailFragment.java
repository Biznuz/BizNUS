package com.example.biznus.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.biznus.CheckoutActivity;
import com.example.biznus.MainActivity;
import com.example.biznus.Model.Post;
import com.example.biznus.R;
import com.example.biznus.ui.account.AccountFragment;
import com.example.biznus.ui.explore.ExploreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.api.Distribution;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.collections.ArrayDeque;

public class ListingDetailFragment extends Fragment {

    String listID, CustomerId, EphericalKey, ClientSecret;
    String PublishableKey = "pk_test_51PbO2lKfpy6OFgrdWf9jxe7JdAxUkMDKmua3iWR6tKWN2oC2veMxkMWS24KcZXi8SPy8jsPUd5hhUX3XvV3C6Mde00FNuYNyjm";
    String SecretKey = "sk_test_51PbO2lKfpy6OFgrdn1wzG78eFrtLppXiMWi3AtNKyYbdZAkaIecn3ATFiTr1mMNApXt7akduUgxcDjuopOg1qOof00XmE6INPm";

    PaymentSheet paymentSheet;
    private RecyclerView recyclerView;
    private ListDetailAdapter listDetailAdapter;
    private List<Post> list;
    private ImageView back_button;

    private TextView buyButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_detail, container, false);
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        back_button = view.findViewById(R.id.back_button);
        buyButton = view.findViewById(R.id.buyButton);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        listID = sharedPreferences.getString("listID", "none");

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


//
        PaymentConfiguration.init(getContext(), PublishableKey);
//
//        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
//
//            onPaymentResult(paymentSheetResult);
//        });
//        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            CustomerId = object.getString("id");
//                            Toast.makeText(getContext(), CustomerId, Toast.LENGTH_SHORT).show();
//
//                            getEmphericalKey(CustomerId);
//
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> header = new HashMap<>();
//                header.put("Authorization", "Bearer " + SecretKey);
//                return header;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        requestQueue.add(request);

        return view;
    }


//    private void paymentFlow() {
//
//        paymentSheet.presentWithPaymentIntent(ClientSecret, new PaymentSheet.Configuration("BizNUS", new PaymentSheet.CustomerConfiguration(
//                CustomerId, EphericalKey
//        )));
//        Log.d("paypay", "errorrr");
//    }
//    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
//        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
//            Toast.makeText(getContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void getEmphericalKey(String customerId) {
//        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            EphericalKey = object.getString("id");
//                            Toast.makeText(getContext(), CustomerId, Toast.LENGTH_SHORT).show();
//
//                            getClientSecret(CustomerId, EphericalKey);
//
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> header = new HashMap<>();
//                header.put("Authorization", "Bearer " + SecretKey);
//                header.put("Stripe-Version", "2024-06-20");
//                return header;
//            }
//
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("customer", CustomerId);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        requestQueue.add(request);
//    }
//
//    private void getClientSecret(String customerId, String ephericalKey) {
//        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            ClientSecret = object.getString("client_secret");
//
//                            Toast.makeText(getContext(), ClientSecret, Toast.LENGTH_SHORT).show();
//
//
//
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> header = new HashMap<>();
//                header.put("Authorization", "Bearer " + SecretKey);
//                return header;
//            }
//
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("customer", CustomerId);
//                params.put("amount", "100" + "00");
//                params.put("currency", "SGD");
//                params.put("automatic_payment_methods[enabled]", "true");
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        requestQueue.add(request);
//    }

    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Listings").child(listID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                Post post = snapshot.getValue(Post.class);
                list.add(post);

                listDetailAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}