package cn.mstar.store.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.mstar.store.R;
import cn.mstar.store.interfaces.ActivityFragment_Relation_Interface;
import cn.mstar.store.utils.Utils;

public class MockActivity extends FragmentActivity implements ActivityFragment_Relation_Interface {


	private static final String SHOPPING_CART = "MockActivity";
	@Bind(R.id.lny_loading_layout) protected LinearLayout lny_loading_layout;
	@Bind(R.id.lny_network_error_view) protected LinearLayout lny_network_error_layout;
	@Bind(R.id.lny_no_result) protected LinearLayout lny_noresult;
	@Bind(R.id.framelayout_main) protected FrameLayout framelayout_main;
	@Bind(R.id.title_back) ImageView iv_back;
	@Bind(R.id.title_name) TextView tv_title;


	public static final String GET_TO = "GET_TO";
	public static final String EXCHANGE_POINTS_ACTIVITY = "EXCHANGE_POINTS_ACTIVITY";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mock);
		ButterKnife.bind(this);
		initVarz();
		Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
		Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
		// when we get it, it is loading till the moment the inner fragment tells him to stop loading.
	}

	//actionbar
	@OnClick (R.id.title_back)
	public void back () {
		finish ();
	}


	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	private void initVarz () {
		iv_back.setVisibility(View.VISIBLE);tv_title.setVisibility(View.VISIBLE); tv_title.setText(getString(R.string.exchangepoints_activity));
	}


	public void all_invisible() {
		lny_loading_layout.setVisibility(View.GONE);
		lny_network_error_layout.setVisibility(View.GONE);
		lny_noresult.setVisibility(View.GONE);
		framelayout_main.setVisibility(View.GONE);
	}

	@Override
	public void setLoading(boolean state) {
		all_invisible ();
		if (state) {
			lny_loading_layout.setVisibility(View.VISIBLE);
		} else {
			lny_loading_layout.setVisibility(View.GONE);
		}
	}

	@Override
	public void setNetworkError(boolean state) {
		all_invisible ();
		if (state) {
			lny_network_error_layout.setVisibility(View.VISIBLE);
		} else {
			lny_network_error_layout.setVisibility(View.GONE);
		}
	}


	public static int getScreenHeigth (Context context) {

		DisplayMetrics metrics = new DisplayMetrics();
		((MockActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	public static int getScreenWidth (Context context) {

		DisplayMetrics metrics = new DisplayMetrics();
		((MockActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}


	@Override
	public void setNoResult(boolean state) {
		all_invisible();
		if (state) {
			lny_noresult.setVisibility(View.VISIBLE);
		} else {
			lny_noresult.setVisibility(View.GONE);
		}
	}

	public void setMyTitle(String title) {
		tv_title.setText(title);
	}
}
