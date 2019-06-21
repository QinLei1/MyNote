package io.github.mkckr0.mynote.View;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import io.github.mkckr0.mynote.R;

public class SoundTextView extends AppCompatTextView {
    private void init(Context context) {
        setTextSize(32f);
        setForeground(getResources().getDrawable(R.drawable.shape_rectangle, context.getTheme()));
    }

    public SoundTextView(Context context) {
        super(context);
        init(context);
    }

    public SoundTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SoundTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
