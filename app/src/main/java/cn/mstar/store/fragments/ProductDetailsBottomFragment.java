package cn.mstar.store.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.customviews.CustomWebview;
import cn.mstar.store.customviews.SlidingTabLayout;


public class ProductDetailsBottomFragment extends Fragment {


    private static final String WD = "screenwidth";
    private int screenHeight;
//    private GoodsManagementViewPagerAdapter viewPagerAdapter;

    public static ProductDetailsBottomFragment getInstance(String descURL, String attributes, int screenHeight) {

        ProductDetailsBottomFragment frg = new ProductDetailsBottomFragment();
        Bundle args = new Bundle();
        args.putString(DESC_URL, descURL);
        args.putString(ATTRIBUTES, attributes);
        args.putInt(WD, screenHeight);
        frg.setArguments(args);
        return frg;
    }


    private static final String ATTRIBUTES = "attributes";
    private ProductDetailsViewPagerAdapter viewPagerAdapter;
    private static final String DESC_URL = "desc_url";
    View rootView;
    @Bind(R.id.viewpager_details)
    ViewPager mViewpager;
    @Bind(R.id.tabs)
    SlidingTabLayout tabs;
    @Bind(R.id.rel)
    RelativeLayout rel;

    // variables
    public String attributes, descUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_product_details_bottom, container, false);

        // get values
        attributes = getArguments().getString(ATTRIBUTES);
        descUrl = getArguments().getString(DESC_URL);
        screenHeight = getArguments().getInt(WD) /*- Utils.convertPxtoDip(150, getActivity())*/;
        ButterKnife.bind(this, rootView);

        ViewGroup.LayoutParams layoutParams = mViewpager.getLayoutParams();
        layoutParams.height = screenHeight;
        mViewpager.setLayoutParams(layoutParams);
//        mViewpager.setLayoutParams(layoutParams);

        viewPagerAdapter = new ProductDetailsViewPagerAdapter(getChildFragmentManager(), getActivity());

//        viewPagerAdapter = new GoodsManagementViewPagerAdapter(getChildFragmentManager(), (MyApplication) getActivity().getApplication());
        mViewpager.setAdapter(viewPagerAdapter);

        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true,
        // This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.little_red);
            }
        });
        tabs.getChildAt(0).setBackgroundResource(R.color.page_background);
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(mViewpager);
        tabs.setAnimationCacheEnabled(false);
        mViewpager.setCurrentItem(0);
        mViewpager.setOffscreenPageLimit(2);
        return rootView;
    }

    private class ProductDetailsViewPagerAdapter extends FragmentPagerAdapter {

        private   String[] page_title;
        Context mctx;
        Map<String, Fragment> frg_db;


        public ProductDetailsViewPagerAdapter(FragmentManager childFragmentManager, Context ctx) {
            super(childFragmentManager);
            mctx = ctx;
            page_title = mctx.getResources().getStringArray(R.array.product_details_tab_menu);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment frg = null;
            if (frg_db == null)
                frg_db = new HashMap<>();

            if (position == 0) {
                // webview
                if (frg_db.get("p"+position) == null)
                    frg_db.put("p"+position, MyWebViewFragment.getInstance (descUrl));
                frg = frg_db.get("p"+position);
            } else if (position == 1) {
                // details list view
                if (frg_db.get("p"+position) == null)
                    frg_db.put("p"+position, DetailsFragment.getInstance (attributes));
                frg = frg_db.get("p" + position);
            }
            return frg;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (page_title == null) {
                return "";
            }
            return page_title[position];
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

    public static class MyWebViewFragment extends Fragment {

        private View root;

        public static Fragment getInstance(String descURL) {
            Fragment fragment = new MyWebViewFragment();
            Bundle args = new Bundle();
            args.putString("desc", descURL);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            root = View.inflate(getActivity(), R.layout.fragment_webview_layout, null);
            String descUrl = getArguments().getString("desc");
//            L.d("desc:::", descUrl);
            CustomWebview webView = (CustomWebview) root.findViewById(R.id.webview);
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);

            webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
//            webView.getSettings().setAppCachePath("/data/data/com.your.package.appname/cache"‌​);
            StorageUtils.getOwnCacheDirectory(getActivity(),
                    "MstarStore/Cache").getAbsolutePath();
            webView.getSettings().setAppCacheEnabled(true);

            // remove a weird white line on the right size
            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            String mime = "text/html";
            String encoding = "utf-8";
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadDataWithBaseURL(null, descUrl, mime, encoding, null);
            return root;
        }
    }

    public static class DetailsFragment extends Fragment{


        private View root;
        private LayoutInflater inf;

        public static Fragment getInstance(String attr) {
            Fragment fragment = new DetailsFragment();
            Bundle args = new Bundle();
            args.putString("attr", attr);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            root = View.inflate(getActivity(), R.layout.fragment_details_layout, null);
            ListView lv = (ListView) root.findViewById(R.id.list_content);
            lv.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            inf = LayoutInflater.from(root.getContext());
            String attr = getArguments().getString("attr");
            Gson gson = new Gson();
            String[] data = gson.fromJson(attr, String[].class);
            lv.setAdapter(new DetailsListAdapter(getActivity(), data));
        /*    for (String s: data
                    ) {
                L.d("data:::", s);
            }*/
/*lny.removeAllViews();
            for (String s: data
                 ) {
                    lny.addView(inflateView(s));
            }*/
            return root;
        }

        private View inflateView(String s) {

            View view = inf.inflate(R.layout.textview_item, null);
            ViewHolder vh = new ViewHolder(view);
            String[] splitted = s.split(":");

            vh.tv1.setText(splitted[0]);
            vh.tv2.setText(splitted[1]);
            return view;
        }

        public class ViewHolder {

            TextView tv1, tv2;

            public ViewHolder (View v) {
                tv1 = (TextView) v.findViewById(R.id.tv1);
                tv2 = (TextView) v.findViewById(R.id.tv2);
            }
        }

        private class DetailsListAdapter extends BaseAdapter {

            Context ctx;
            String[] data;

            public DetailsListAdapter (Context context, String[] d) {
                ctx = context;
                data = d;
            }

            @Override
            public int getCount() {
                return data.length;
            }

            @Override
            public Object getItem(int position) {
                return data[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String s = (String) getItem(position);
                View view = inf.inflate(R.layout.textview_item, null);
                ViewHolder vh = new ViewHolder(view);
                String[] splitted = s.split(":");
                vh.tv1.setText(splitted[0]);
                vh.tv2.setText(splitted[1]);
                return view;
            }
        }

    }


}
