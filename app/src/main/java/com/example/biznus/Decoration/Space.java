package com.example.biznus.Decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.N;

public class Space extends RecyclerView.ItemDecoration {
    private final int spacing;

    public Space(Context context, int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.bottom = spacing;
        if (parent.getChildAdapterPosition(view) < 2) {
            outRect.top = spacing;
        }
    }
}

