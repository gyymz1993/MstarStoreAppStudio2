package cn.mstar.store.customviews;

import cn.mstar.store.R;
import cn.mstar.store.entity.PayType;
import cn.mstar.store.utils.L;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

public class PayModePopup extends PopupWindow{
	private OnDialogListener listener;
	private View mPopView;
	private ListView paylistview;
	private List<PayType> paylist;
	private Context context;
	public PayModePopup(Context context,List<PayType> paylist,OnDialogListener listener){
		this.listener=listener;
		this.paylist=paylist;
		this.context=context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopView= inflater.inflate(R.layout.popup_pay_mode, null);

        this.setContentView(mPopView);

        this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 点击外面的控件也可以使得PopUpWindow dimiss
        this.setOutsideTouchable(true);
        //ColorDrawable dw = new ColorDrawable(0xffffffff);//0xb0000000
        ColorDrawable dw = new ColorDrawable(0x00000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);//半透明颜色
		initUI();
	}

	private void initUI() {
		paylistview=(ListView)getContentView().findViewById(R.id.pay_list);
		paylistview.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return paylist.size();
			}

			@Override
			public Object getItem(int position) {
				return paylist.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView=null;
				if(convertView==null){
					textView=new TextView(context);
					textView.setTextColor(context.getResources().getColor(android.R.color.black));
					textView.setTextSize(14);
					//设置文本居中
					textView.setGravity(Gravity.CENTER);
					//设置文本域的范围
					textView.setPadding(0,20,0,20);
					//设置文本在一行内显示（不换行）
					textView.setSingleLine(true);
				}else{
					textView = (TextView) convertView;
					}
				textView.setText(paylist.get(position).getTypeName());
				L.e(paylist.get(position).getTypeName());
				return textView;
			}
		});
		paylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(listener!=null){
				listener.onPayType(paylist.get(position));
				dismiss();
			}

			}
		});
	}
	/**
	 * Dialog按钮回调接口
	 *
	 */
	public interface OnDialogListener {

		public void onPayType(PayType payType);//支付方式


	}
	
}
