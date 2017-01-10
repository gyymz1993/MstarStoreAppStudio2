package cn.mstar.store.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.adapter.HotSearchAdapter;

public class HotFragment extends Fragment {

	
	private ListView listView;
	private HotSearchAdapter adapter;
	//存放搜索数据的List
	private List<String> list;
	private Context context;

	OnHotClickListener hotListener;

	public interface OnHotClickListener{
		void onSkip(String key);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		hotListener = (OnHotClickListener) activity;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context=getActivity();
		initView();
	}

	private void initView() {
		listView=(ListView) getView().findViewById(R.id.hot_search_list);
		list=new ArrayList<String>();
		list.add("黄金");
		list.add("珠宝");
		adapter=new HotSearchAdapter(getActivity(), list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				hotListener.onSkip(list.get(position));
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_hot_search, null);
		return view;
	}
}
