package com.example.biznus.ui.foryou;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.biznus.databinding.FragmentForYouBinding;

public class ForYouFragment extends Fragment {

    private FragmentForYouBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ForYouViewModel forYouViewModel =
                new ViewModelProvider(this).get(ForYouViewModel.class);

        binding = FragmentForYouBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textForYou;
        forYouViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}