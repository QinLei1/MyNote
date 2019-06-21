package io.github.mkckr0.mynote;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemRecyclerViewDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public ItemRecyclerViewDecoration(int space) {
        super();
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = space;
        outRect.right = space;
        outRect.top = space / 2;
        outRect.bottom = space / 2;
    }
}
