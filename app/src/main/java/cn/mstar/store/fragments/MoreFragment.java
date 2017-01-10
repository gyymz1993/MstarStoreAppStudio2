package cn.mstar.store.fragments;

import cn.mstar.store.R;
import cn.mstar.store.activity.ConfirmIndentActivity;
import cn.mstar.store.activity.CreateReceiverAddressActivity;
import cn.mstar.store.activity.IndentDetailsActivity;
import cn.mstar.store.activity.LogisticsDetialsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MoreFragment extends Fragment implements OnClickListener{
	private TextView titleName;
	private LinearLayout setLayout,aboutLayout,useHelpLayout,shareLayout,feekbackLayout;//设置,关于我们,使用帮助，分享

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_more, null);
		initView(view);
		return view;
	}


	private void initView(View view) {
		setLayout=(LinearLayout) view.findViewById(R.id.set_layout);
		aboutLayout=(LinearLayout) view.findViewById(R.id.about_layout);
		useHelpLayout=(LinearLayout) view.findViewById(R.id.use_help_layout);
		shareLayout=(LinearLayout) view.findViewById(R.id.software_share_layout);
		feekbackLayout= (LinearLayout) view.findViewById(R.id.feekback_layout);
		titleName=(TextView) view.findViewById(R.id.title_name);
		titleName.setText(getString(R.string.set));
		setLayout.setOnClickListener(this);
		aboutLayout.setOnClickListener(this);
		useHelpLayout.setOnClickListener(this);
		shareLayout.setOnClickListener(this);
		feekbackLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent=null;
		switch (v.getId()) {
	/*	case R.id.set_layout://设置
			 intent=new Intent(getActivity(), CeshiDengluActivity.class);//测试登录
			break;*/
		case R.id.about_layout://关于我们
			 intent=new Intent(getActivity(),ConfirmIndentActivity.class);
			break;
		case R.id.use_help_layout://使用帮助
			intent=new Intent(getActivity(),LogisticsDetialsActivity.class);
			break;
		case R.id.software_share_layout://软件分享
			intent=new Intent(getActivity(),IndentDetailsActivity.class);
			break;
			case R.id.feekback_layout:
				intent=new Intent(getActivity(), CreateReceiverAddressActivity.class);
				break;
		}
		if(intent!=null)
			startActivity(intent);
	}


}
