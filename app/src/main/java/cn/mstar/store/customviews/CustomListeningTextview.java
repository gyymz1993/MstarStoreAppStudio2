package cn.mstar.store.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.mstar.store.interfaces.OnTextChangedListener;


/**
 * Created by UlrichAbiguime at Shenzhen.
 */
public class CustomListeningTextview  extends TextView

{
    public CustomListeningTextview(Context context) {
        super(context);
    }

    public CustomListeningTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListeningTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomListeningTextview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private OnTextChangedListener listener;

    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        listener = onTextChangedListener;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (listener != null)
            listener.updateText(text.toString());
    }
}
