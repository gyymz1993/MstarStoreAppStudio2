package cn.mstar.store.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 管理Toast
 *
 * @author wenjundu 2015-07-02
 *
 */
public class CustomToast {
	private static Toast toast;

	// 自带toast显示
	public static void makeToast(Context context, String content, int duration) {
		if (toast != null) {
			toast.cancel();
			toast = Toast.makeText(context, content, duration);
			//toast.setText(content);
		} else {
			toast = Toast.makeText(context, content, duration);
		}
		toast.show();
	}

	public static void mToast(Context mContext,
			String message) {
		// TODO Auto-generated method stub
//	Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	public static void mSystemToast (Context mContext, String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}
}
