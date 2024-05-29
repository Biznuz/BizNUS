package com.example.biznus.ui.foryou;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ForYouViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ForYouViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is for you fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}