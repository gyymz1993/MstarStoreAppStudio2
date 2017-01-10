package cn.mstar.store.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
/**
 * 宽度和高度相等的TextView
 * @author duwenjun 2015-7-10
 */
public class SquareTextView extends TextView {

 

	public SquareTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	
	@SuppressLint("NewApi")
	public SquareTextView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO Auto-generated constructor stub
	}



	public SquareTextView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}



	public SquareTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}



	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	        int w = this.getMeasuredHeight();
	        setMeasuredDimension(w, w);
	        setGravity(Gravity.CENTER);
	}
	
}
