package cn.mstar.store.customviews;

import cn.mstar.store.R;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

/**分享popupwindow
 * @author wenjundu 2015-7-17
 *
 */
public class SharePopup extends PopupWindow implements android.view.View.OnClickListener{

	private OnDialogListener listener;
	private View mPopView;
	public SharePopup(Context context, OnDialogListener listener) {  
        super(context);  
        this.listener=listener;
        LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopView= inflater.inflate(R.layout.dialog_share_modul, null);
        this.setContentView(mPopView);
        this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
			//分享到微信
			TextView wechat=(TextView) mPopView.findViewById(R.id.share_wechat);
			//发送给朋友
			TextView sendFrineds=(TextView) mPopView.findViewById(R.id.share_friends);
			//新浪分享
			TextView sina=(TextView) mPopView.findViewById(R.id.share_sina);
			//发送给qq好友
			TextView QQ=(TextView) mPopView.findViewById(R.id.share_qq);
			//发送到qq空间
			TextView QQZone=(TextView) mPopView.findViewById(R.id.share_zone);
		
        wechat.setOnClickListener(this);
        sendFrineds.setOnClickListener(this);
        sina.setOnClickListener(this);
        QQ.setOnClickListener(this);
        QQZone.setOnClickListener(this);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 点击外面的控件也可以使得PopUpWindow dimiss
        this.setOutsideTouchable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);//0xb0000000
       // ColorDrawable dw = new ColorDrawable(0x00000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);//半透明颜色
    } 
	/**
	 * Dialog按钮回调接口
	 *
	 */
	public interface OnDialogListener {

		public void onShareWechat();//微信
		
		public void onShareFriends();//朋友圈
		
		public void onShareSina();//新浪

		public void onShareQQ();//qq好友
		public void onShareQQZone();//qq空间

	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.share_wechat:
			listener.onShareWechat();
			break;
		case R.id.share_friends:
			listener.onShareFriends();
			break;
		case R.id.share_sina:
			listener.onShareSina();
			break;
		case R.id.share_qq:
			listener.onShareQQ();
			break;
		case R.id.share_zone:
			listener.onShareQQZone();
			break;
		}
		dismiss();
	}
}
