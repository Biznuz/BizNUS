package com.example.biznus.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biznus.Adapter.PostAdapter;
import com.example.biznus.R;
import com.example.biznus.databinding.FragmentExploreBinding;
import com.example.biznus.Model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ExploreFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;

    private FragmentExploreBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        ExploreViewModel exploreViewModel =
//                new ViewModelProvider(this).get(ExploreViewModel.class);
//
//        binding = FragmentExploreBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        return root;
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        recyclerView = view.findViewById((R.id.recycler_view));
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);

        readLists();

        return view;
    }

    private void readLists() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Listings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();
                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    Post post = snapshots.getValue(Post.class);
                    postLists.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }

}