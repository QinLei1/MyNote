package io.github.mkckr0.mynote.View;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class AutoHideTextView extends AppCompatTextView {

    public AutoHideTextView(Context context) {
        super(context);
    }

    public AutoHideTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoHideTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (TextUtils.isEmpty(text) || TextUtils.getTrimmedLength(text) == 0) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }
}
