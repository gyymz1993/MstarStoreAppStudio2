package cn.mstar.store.customviews;

import cn.mstar.store.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class AppFragment extends Fragment {


	protected LinearLayout lny_loading_layout, lny_network_error_layout, lny_noresult;
	protected FrameLayout framelayout_main;
	private View actionbar;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.appfragment, container, false);
		// create the views here
		initViews (view);
		//1- inflate the view network failure view...
		//2- the no result view...
		return view;
	}

	protected void hideActionBar () {
		if (actionbar != null)
			actionbar.setVisibility(View.GONE);
	}

	protected void hide_all (View view) {
		getLny_loading_layout(view).setVisibility(View.GONE);
		getLny_network_error_layout(view).setVisibility(View.GONE);
		getLny_noresult(view).setVisibility(View.GONE);
		getFramelayout_main(view).setVisibility(View.GONE);
	}

	protected FrameLayout getFramelayout_main(View v) {
		if (framelayout_main == null)
			framelayout_main = (FrameLayout) v.findViewById(R.id.framelayout_main);
		return this.framelayout_main;
	}


	protected void setFramelayout_main(FrameLayout framelayout_main) {
		this.framelayout_main = framelayout_main;
	}


	protected void initViews (View view) {
		lny_loading_layout = (LinearLayout) view.findViewById(R.id.lny_loading_layout);
		lny_network_error_layout = (LinearLayout) view.findViewById(R.id.lny_network_error_view);
		lny_noresult = (LinearLayout) view.findViewById(R.id.lny_no_result);
		framelayout_main = (FrameLayout) view.findViewById(R.id.framelayout_main);
		actionbar = view.findViewById(R.id.the_title_layout);
	}


	protected LinearLayout getLny_loading_layout(View v) {
		if (lny_loading_layout == null)
			lny_loading_layout = (LinearLayout) v.findViewById(R.id.lny_loading_layout);
		return lny_loading_layout;
	}

	protected void setLny_loading_layout(LinearLayout lny_loading_layout) {
		this.lny_loading_layout = lny_loading_layout;
	}

	protected LinearLayout getLny_network_error_layout(View v) {
		if (lny_network_error_layout == null)
			lny_network_error_layout = (LinearLayout) v.findViewById(R.id.lny_network_error_view);
		return lny_network_error_layout;
	}

	protected void setLny_network_error_layout(LinearLayout lny_network_error_layout) {
		this.lny_network_error_layout = lny_network_error_layout;
	}

	protected LinearLayout getLny_noresult(View v) {
		if (lny_noresult == null)
			lny_noresult = (LinearLayout) v.findViewById(R.id.lny_no_result);
		return lny_noresult;
	}

	protected void setLny_noresult(LinearLayout lny_noresult) {
		this.lny_noresult = lny_noresult;
	}


	// the use can then do whatever he wants in the onviewcreated ...


}
