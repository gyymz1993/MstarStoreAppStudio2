package cn.mstar.store.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.activity.ProductListActivity;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.entity.ProductType;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.utils.DensityUtil;

/**产品筛选pop
 * @author wenjundu 
 *
 */
public class FilterPopup extends PopupWindow implements OnClickListener{
	private View mPopView;
	private TextView btnReset,btnConfirm;//重置 确定
	private ListView listView;
	private OnDialogListener listener;
	// 存储产品List
	Context mContext;
	ProductListActivity mActivity;

	LinearLayout normsLay,normsLay1;

	public FilterPopup(Context context,int height,List<String> shapes,List<ProductType> types){
		super(context);
		this.mContext=context;
		this.mActivity= (ProductListActivity) context;
		this.shapes=shapes;
		this.types=types;
		init(context,height);
	}
	public FilterPopup(Context context,int height) {
		super(context);
		this.mContext=context;
		this.mActivity= (ProductListActivity) context;
		init(context,height);
	}

	public void init(Context context,int height){
		storeId = MyApplication.getInstance().storeId;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopView= inflater.inflate(R.layout.dialog_filter_modul, null);
		btnReset=(TextView) mPopView.findViewById(R.id.reset_tv);
		btnConfirm=(TextView) mPopView.findViewById(R.id.confirm_tv);
		normsLay= (LinearLayout) mPopView.findViewById(R.id.norms_layout);
		normsLay1= (LinearLayout) mPopView.findViewById(R.id.norms_layout1);
		this.setContentView(mPopView);
		this.setWidth(getWindowWidth()-150);
		this.setHeight(height);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 点击外面的控件也可以使得PopUpWindow dimiss
		this.setOutsideTouchable(false);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.Fiter_PopupAnimation);
		ColorDrawable dw = new ColorDrawable(0xffffffff);//0xb0000000
		// ColorDrawable dw = new ColorDrawable(0x00000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);//半透明颜色


		LoadingGoodsLayout();
		LoadingGoodsLayout1();
		tv_price_30= (TextView) mPopView.findViewById(R.id.id_tv_price_30);
		tv_price_50= (TextView) mPopView.findViewById(R.id.id_tv_price_50);
		tv_price_100= (TextView) mPopView.findViewById(R.id.id_tv_price_100);
		tv_price_seach=(TextView)mPopView.findViewById(R.id.id_tv_price_seach);
		id_btn_seach= (Button) mPopView.findViewById(R.id.id_btn_seach);
		id_pirce_min= (EditText) mPopView.findViewById(R.id.id_pirce_min);
		id_pirce_max= (EditText) mPopView.findViewById(R.id.id_pirce_max);
		tv_price_100.setOnClickListener(this);
		tv_price_30.setOnClickListener(this);
		tv_price_50.setOnClickListener(this);
		tv_price_seach.setOnClickListener(this);
		id_btn_seach.setOnClickListener(this);
	}


	public int getWindowWidth(){
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}



	TextView tv_price_30,tv_price_50,tv_price_100,tv_price_seach;
	Button id_btn_seach;
	EditText id_pirce_min,id_pirce_max;
	private void cleanTextView(){
		id_pirce_min.getText().clear();
		id_pirce_max.getText().clear();
	}
	private void  resetOnebg(int id){
		cleanTextView();
		resetAllBg();
		if (id==R.id.id_tv_price_30){
			tv_price_30.setBackgroundResource(R.drawable.ed_shape_red);
			tv_price_30.setTextColor(mContext.getResources().getColor(R.color.red));
		}
		if (id==R.id.id_tv_price_50){
			tv_price_50.setBackgroundResource(R.drawable.ed_shape_red);
			tv_price_50.setTextColor(mContext.getResources().getColor(R.color.red));
		}
		if (id==R.id.id_tv_price_100){
			tv_price_100.setBackgroundResource(R.drawable.ed_shape_red);
			tv_price_100.setTextColor(mContext.getResources().getColor(R.color.red));
		}

	}
	private void  resetAllBg(){
		tv_price_30.setBackgroundResource(R.drawable.ed_shape_aw);
		tv_price_30.setTextColor(mContext.getResources().getColor(R.color.result_view));
		tv_price_50.setBackgroundResource(R.drawable.ed_shape_aw);
		tv_price_50.setTextColor(mContext.getResources().getColor(R.color.result_view));
		tv_price_100.setBackgroundResource(R.drawable.ed_shape_aw);
		tv_price_100.setTextColor(mContext.getResources().getColor(R.color.result_view));
	}

	int mstoneWt1,mstoneWt2;
	private String showProductUrl = "";
	private int key = 1;            //=>  1-销量 3-价格量 空-按最新发布排序
	private int order = 2;  //排序方式 1-升序 2-降序
	private int showPageData = 10;//默认每页显示10条数据
	// 默认显示第一页
	private int curpage = 1;
	boolean isPriceSelect=false;
	// 搜索关键字
	private String keyword = "";
	private String storeId;
	String type;
	String shape;
	private void selectPrice(int stoneWt1,int stoneWt2,int mstoneNum) {
		this.mstoneWt1=stoneWt1;
		this.mstoneWt2=stoneWt2;
		showProductUrl = AppURL.SEARCH_URL + "&category=" + 0
				+ "&keyword=" + URLtoUTF8Utils.toUtf8String(keyword)
				+ "&key=" + key + "&order=" + order + "&curpage=" + curpage
				+ "&page=" + showPageData + "&mstoreId=" + storeId+"&mstoneNum="+mstoneNum;
		if (mstoneWt2==0&&mstoneWt1==0){
			return;
		}else if (mstoneWt2==0){
			showProductUrl +="&mstoneWt1="+mstoneWt1;
			if (mstoneWt1==30){
				showProductUrl+="&mstoneType="+2;
			}else {
				showProductUrl+="&mstoneType="+1;
			}
		}else if (mstoneWt1!=0&&mstoneWt2!=0){
			showProductUrl+="&mstoneWt1="+mstoneWt1
					+"&mstoneWt2="+mstoneWt2+"&mstoneType="+1;
		}if (mstoneWt1==0){
			showProductUrl +="&mstoneWt2="+mstoneWt2
					+"&mstoneType="+1;
		}

	}


	private void selectPrice(int stoneWt1,int stoneWt2,int mstoneNum,String shape,String type){
		this.shape=shape;
		this.type=type;
		selectPrice(stoneWt1,stoneWt2,mstoneNum);
		if (shape!=null){
			showProductUrl+="&goodsCategory="+shape;
		}
		if (type!=null){
			showProductUrl+="&shapeType="+type;
		}
	}
	public interface OnDialogListener{
		public void filter();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reset_tv:
			break;

		case R.id.confirm_tv:
			if(listener!=null)
				listener.filter();
			break;

			case  R.id.id_tv_price_30:
				resetOnebg(R.id.id_tv_price_30);
				selectPrice(30,0,0,shape,type);
				//showProduct(showProductUrl);
			//	onSeachSuccess.onSeachSuccess(showProductUrl);
				break;
			case  R.id.id_tv_price_50:
				resetOnebg(R.id.id_tv_price_50);
				selectPrice(30,50,0,shape,type);
				//showProduct(showProductUrl);
			//	onSeachSuccess.onSeachSuccess(showProductUrl);
				break;
			case  R.id.id_tv_price_100:
				resetOnebg(R.id.id_tv_price_100);
				selectPrice(50,100,0,shape,type);
				//showProduct(showProductUrl);
			//	onSeachSuccess.onSeachSuccess(showProductUrl);
				break;
			case R.id.id_tv_price_seach:
				if (!isPriceSelect){
					isPriceSelect=true;
					tv_price_seach.setBackgroundResource(R.drawable.ed_shape_red);
					tv_price_seach.setTextColor(mContext.getResources().getColor(R.color.red));
					selectPrice(mstoneWt1,mstoneWt2,1,shape,type);
				}else {
					isPriceSelect=false;
					tv_price_seach.setBackgroundResource(R.drawable.ed_shape_aw);
					tv_price_seach.setTextColor(mContext.getResources().getColor(R.color.result_view));
					selectPrice(mstoneWt1,mstoneWt2,0,shape,type);
				}
				//showProduct(showProductUrl);
				//onSeachSuccess.onSeachSuccess(showProductUrl);
				break;
			case R.id.id_btn_seach:
				if(isShowing()){
					dismiss();
				}
				resetAllBg();
				String minstr=id_pirce_min.getText().toString();
				String maxstr=id_pirce_max.getText().toString();
				if (!minstr.equals("")&&!maxstr.equals("")){
					int min=Integer.valueOf(minstr);
					int max=Integer.valueOf(maxstr);
					if (min<max){
						selectPrice(min,max,0,shape,type);
					}
				}else if (!maxstr.equals("")){
					int max=Integer.valueOf(maxstr);
					selectPrice(0,max,0,shape,type);
				}else if (!minstr.equals("")){
					int min=Integer.valueOf(minstr);
					selectPrice(min,0,0,shape,type);
				}/*else {
					Toast.makeText(mContext,"请填写数据",Toast.LENGTH_LONG).show();
					return;
				}*/
				//showProduct(showProductUrl);
				Log.e("TAG",showProductUrl);
				onSeachSuccess.onSeachSuccess(showProductUrl);
				break;
		}

	}

	OnSeachSuccess onSeachSuccess;
	public interface OnSeachSuccess{
		void onSeachSuccess(String url);
	}

	public void setOnSeachSuccess(OnSeachSuccess onSeachSuccess) {
		this.onSeachSuccess = onSeachSuccess;
	}


	private List<ProductType> types;
	private List<String> shapes;
	private int[] checkedIds;//选中id数组
	LayoutInflater inflater;
	private int InitId = 0x1000;// 初始id


	private void LoadingGoodsLayout() {
		if (checkedIds==null){
			checkedIds=new int[shapes.size()];
		}
		if (inflater==null){
			inflater=LayoutInflater.from(mContext);
		}
		View inf_Rel=inflater.inflate(R.layout.goods_norms_menus,null);
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		ViewGroup.LayoutParams radioLp=new ViewGroup.LayoutParams(getWindowWidth()/5, DensityUtil.dip2px(mContext,35) );
		TextView normsName = (TextView) inf_Rel.findViewById(R.id.goods_norms_tv);
		normsName.setText("图形");
		MyRadioGroup normsGroup = (MyRadioGroup) inf_Rel.findViewById(R.id.goods_norms_group);
		normsGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
		for (int i=0;i<shapes.size();i++){
			RadioButton view=new RadioButton(mContext);
			view.setId(++InitId);
			view.setText(shapes.get(i));
			view.setTextColor(mContext.getResources().getColor(R.color.result_view));
			view.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
			view.setGravity(Gravity.CENTER);
			view.setPadding(50,50,50,50);
			view.setBackgroundResource(R.drawable.seach_dialog_radio_bg);
			view.setLayoutParams(radioLp);
			normsGroup.addView(view);
		}
		normsLay.addView(inf_Rel,layoutParams);
	}


	private void LoadingGoodsLayout1() {
		if (checkedIds==null){
			checkedIds=new int[types.size()];
		}
		if (inflater==null){
			inflater=LayoutInflater.from(mContext);
		}

		View inf_Rel=inflater.inflate(R.layout.goods_norms_menus,null);
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		ViewGroup.LayoutParams radioLp=new ViewGroup.LayoutParams(getWindowWidth()/5, DensityUtil.dip2px(mContext,35) );
		TextView normsName = (TextView) inf_Rel.findViewById(R.id.goods_norms_tv);
		normsName.setText("类型");
		MyRadioGroup normsGroup = (MyRadioGroup) inf_Rel.findViewById(R.id.goods_norms_group);
		normsGroup.setOnCheckedChangeListener(new MyOnCheckedTypeListener());
		for (int i=0;i<types.size();i++){
			RadioButton view=new RadioButton(mContext);
			view.setId(++InitId);
			view.setText(types.get(i).getGcName());
			view.setTextColor(mContext.getResources().getColor(R.color.result_view));
			view.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
			view.setGravity(Gravity.CENTER);
			view.setPadding(50,50,50,50);
			view.setBackgroundResource(R.drawable.seach_dialog_radio_bg);
			view.setLayoutParams(radioLp);
			normsGroup.addView(view);
		}
		normsLay1.addView(inf_Rel,layoutParams);
	}


	private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
			for (int i=0;i<group.getChildCount();i++){
				RadioButton radioButton1=(RadioButton)group.getChildAt(i);
				radioButton1.setTextColor(mContext.getResources().getColor(R.color.result_view));
			}
			shape=radioButton.getText().toString();
			selectPrice(mstoneWt1,mstoneWt2,1,shape,type);
			radioButton.setTextColor(mContext.getResources().getColor(R.color.red));
			Log.e("onCheckedChanged",  radioButton.getText().toString() + "  ");
		}
	}

	private class MyOnCheckedTypeListener implements RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
			for (int i=0;i<group.getChildCount();i++){
				RadioButton radioButton1=(RadioButton)group.getChildAt(i);
				radioButton1.setTextColor(mContext.getResources().getColor(R.color.result_view));
			}
			type=radioButton.getText().toString();
			selectPrice(mstoneWt1,mstoneWt2,1,shape,type);
			radioButton.setTextColor(mContext.getResources().getColor(R.color.red));
			Log.e("onCheckedChanged",  radioButton.getText().toString() + "  ");
		}
	}

}
