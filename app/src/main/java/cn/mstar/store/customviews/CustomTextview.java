package cn.mstar.store.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by UlrichAbiguime at Shenzhen.
 */
public class CustomTextview extends TextView {
    private static int NBLINES = 1;

    public CustomTextview(Context context) {
        super(context);
    }

    static {
    }

    public CustomTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        NBLINES = Integer.valueOf(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "maxLines"));
        this.isInEditMode();
    }

    public CustomTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        NBLINES = Integer.valueOf(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "maxLines"));
        this.isInEditMode();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomTextview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        NBLINES = Integer.valueOf(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "maxLines"));
        this.isInEditMode();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (NBLINES == 0)
            NBLINES  = 1;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                initStuffs();
            }
        }, 50);
    }


    private void initStuffs() {

//        CustomToast.mT(getContext(), "nblines "+ getLineCount());
        if (getLineCount() > NBLINES) {
            int count = getText().toString().length() / getLineCount();
            String text = getText().toString();
            setText(text.toString().substring(0, count *NBLINES - 4)+"...");
        }
    }
}
