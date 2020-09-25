package id.binusian.uas_mcs;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int space, span;

    public GridSpacingItemDecoration(int space, int span) {
        this.space = space;
        this.span = span;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        Rect rect = outRect;

        if (parent.getChildAdapterPosition(view) < span) {
            rect.top = space;
        }
        if (parent.getChildAdapterPosition(view) % span == 0) {
            rect.left = space;
        }
        rect.right = space;
        rect.bottom = space;
    }
}
