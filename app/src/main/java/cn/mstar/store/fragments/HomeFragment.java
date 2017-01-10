package cn.mstar.store.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.OnClick;
import cn.mstar.store.R;
import cn.mstar.store.activity.ChooseRingActivity;
import cn.mstar.store.activity.CompanyProfile;
import cn.mstar.store.activity.LoginActivity;
import cn.mstar.store.activity.MainActivity;
import cn.mstar.store.activity.MyCollectionActivity;
import cn.mstar.store.activity.NearStoreActivity;
import cn.mstar.store.activity.ProductDetailsActivity;
import cn.mstar.store.activity.ProductListActivity;
import cn.mstar.store.activity.ScanCodeActivity;
import cn.mstar.store.activity.ScanHistoryActivity;
import cn.mstar.store.activity.SearchActivity;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CustomGridView;
import cn.mstar.store.customviews.JazzyViewPager;
import cn.mstar.store.customviews.OutlineContainer;
import cn.mstar.store.entity.FlashSaleEntity;
import cn.mstar.store.entity.RecommendNewEntity;
import cn.mstar.store.entity.RecommendfyEntity;
import cn.mstar.store.entity.RolateImageEntity;
import cn.mstar.store.functionutils.SpUtils;
import cn.mstar.store.interfaces.WXAuthSuccessInterface;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.NetWorkUtil;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

public class HomeFragment extends Fragment implements OnClickListener {

    private final String TAG="HomeFragment";
    private Context mContext;
    private TextView currentPrice;
    private JazzyViewPager mViewPager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter BannerAdapter = new MyAdapter();
    private Handler mHandler;
    private static final int MSG_CHANGE_PHOTO = 1;
    private boolean isFirstSetAdapter = true;

    private TextView hourTxt;
    private TextView minuteTxt;
    private TextView secondTxt;

    private ImageView flashSaleImg;
    private ImageView flashSaleImg2;
    private ImageView flashSaleImg3;
    private ImageView flashSaleImg4;

    private ImageView recommendImg;
    private ImageView recommendImg2;
    private ImageView recommendImg3;

    private ImageView superAffImg;
    private ImageView superAffImg2;
    private ImageView superAffImg3;

    private CustomGridView mCustomGridView;

    /**
     * 图片自动切换时间
     */
    private static final int PHOTO_CHANGE_TIME = 3000;
    private LinearLayout mIndicator;
    /**
     * 装指引的ImageView数组
     */
    private ImageView[] mIndicators;
    /**
     * 装ViewPager中ImageView的数组
     */
    private ImageView[] mImageViews;
    private List<String> mImageUrls = new ArrayList<String>();
    private List<RolateImageEntity> rolates = new ArrayList<>();
    private List<FlashSaleEntity> flashSales = new ArrayList<>();
    private List<RecommendNewEntity> recommends = new ArrayList<>();
    private List<RecommendNewEntity> superaffords = new ArrayList<>();
    private List<RecommendfyEntity> recommendfy = new ArrayList<>();

    private ImageButton locationBtn;
    private View rootview = null;
    private MyApplication app;
    private TextView input;
    private View favorite, logistics, history, profile;

    private int gridWidth;

    private Timer timer;
    private TimerTask task;

    public WXAuthSuccessInterface mWXAuthSuccessInterface = new WXAuthSuccessInterface() {
        @Override
        public void agree() {
            initData();
        }

        @Override
        public void refuse() {
            initData();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getActivity().getApplication();
        app.mWXAuthSuccessInterface = mWXAuthSuccessInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_home1, container, false);
        mContext = rootview.getContext();
        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        mCustomGridView.post(new Runnable() {
            @Override
            public void run() {
                gridWidth = mCustomGridView.getMeasuredWidth();
            }
        });
        if (MyApplication.getInstance().tokenKey != null && !MyApplication.getInstance().tokenKey.equals("")) {
            initData();
        }
    }


    // 通过网络加载数据
    @OnClick(R.id.wifi_retry)
    public void initData() {
        String url = AppURL.HOME_PAGE_URL + (MyApplication.getInstance().wxId == null || MyApplication.getInstance().wxId.equals("") ?
                "" : "&wxName=" + MyApplication.getInstance().wxId);
        LogUtils.e(TAG+"主页路径"+url);
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            VolleyRequest.GetCookieRequest(mContext, url/*AppURL.HOME_PAGE_URL*/, new VolleyRequest.HttpStringRequsetCallBack() {
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                    if ("0".equals(error)) {
                        JsonObject jData = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                        JsonArray rolateArr = gson.fromJson(jData, JsonObject.class).get("rotatePic").getAsJsonArray();
                        String tokenKey = gson.fromJson(jData, JsonObject.class).get("tokenKey").getAsString();
                        String storeId = gson.fromJson(jData, JsonObject.class).get("storeId").getAsString();
                        MyApplication.getInstance().memberId = gson.fromJson(jData, JsonObject.class).get("memberId").getAsString();
                        MyApplication.getInstance().storeId = storeId;
                        if (!(app.wxId == null || app.wxId.equals(""))) {
                            app.tokenKey = tokenKey;
                            app.spUtils.saveString(SpUtils.key_tokenKey, tokenKey);
                        }

                        RolateImageEntity[] ris = gson.fromJson(rolateArr, RolateImageEntity[].class);
                        mImageUrls.clear();
                        rolates.clear();
                        for (RolateImageEntity r : ris) {
                            rolates.add(r);
                            mImageUrls.add(r.pic);
                        }
                        setBannerAdapter(rolates);
                        if (mImageUrls != null && mImageUrls.size() > 0) {
                            reflushView();
                        }

                        String goldprice = gson.fromJson(jData, JsonObject.class).get("goldprice").getAsJsonObject().get("value").getAsString();
                        String platinumprice = gson.fromJson(jData, JsonObject.class).get("platinumprice").getAsJsonObject().get("value").getAsString();
                        initCurrentPrice(goldprice, platinumprice);

//                        JsonArray flashSaleArr = gson.fromJson(jData, JsonObject.class).get("xianshi_list").getAsJsonArray();
//                        FlashSaleEntity[] fss = gson.fromJson(flashSaleArr, FlashSaleEntity[].class);
//                        flashSales.clear();
//                        Collections.addAll(flashSales, fss);
//                        if (flashSales.size() > 0)
//                            initFlashSale();

                        JsonArray recommendArr = gson.fromJson(jData, JsonObject.class).get("new_list").getAsJsonArray();
                        RecommendNewEntity[] rns = gson.fromJson(recommendArr, RecommendNewEntity[].class);
                        recommends.clear();
                        Collections.addAll(recommends, rns);
                        if (recommends.size() > 0)
                            initNewRecommend();

                        JsonArray superArr = gson.fromJson(jData, JsonObject.class).get("chaozhi_list").getAsJsonArray();
                        RecommendNewEntity[] sas = gson.fromJson(superArr, RecommendNewEntity[].class);
                        superaffords.clear();
                        Collections.addAll(superaffords, sas);
                        if (superaffords.size() > 0)
                            initSuperAffordable();

                        JsonArray recommendfyArr = gson.fromJson(jData, JsonObject.class).get("recommend_list").getAsJsonArray();
                        RecommendfyEntity[] rfy = gson.fromJson(recommendfyArr, RecommendfyEntity[].class);
                        recommendfy.clear();
                        Collections.addAll(recommendfy, rfy);
                        initRecommendForYou();
                    } else {
                        String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFail(String error) {
                    Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }


    // 刷新页面
    private void reflushView() {
        mIndicators = new ImageView[mImageUrls.size()];
        mIndicator.removeAllViews();
        if (mImageUrls.size() <= 1) {
            mIndicator.setVisibility(View.GONE);
        } else {
            mIndicator.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < mIndicators.length; i++) {
            ImageView imageView = new ImageView(mContext);
            LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
            if (i != 0) {
                params.leftMargin = 5;
            }
            imageView.setLayoutParams(params);
            mIndicators[i] = imageView;
            if (i == 0) {
                mIndicators[i].setBackgroundResource(R.drawable.homepage_dot_gold);
            } else {
                mIndicators[i].setBackgroundResource(R.drawable.homepage_dot_grey);
            }
            mIndicator.addView(imageView);
        }
        mImageViews = new ImageView[mImageUrls.size()];

        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ScaleType.FIT_XY);
            mImageViews[i] = imageView;
        }
        if (isFirstSetAdapter) {
            mViewPager.setAdapter(BannerAdapter);
            isFirstSetAdapter = false;
        } else {
            BannerAdapter.notifyDataSetChanged();
        }
        mViewPager.setCurrentItem(0);

    }

    // 把数据适配到Banner
    private void setBannerAdapter(final List<RolateImageEntity> myBannerList) {
        if (mContext == null)
            return;
        if (myBannerList != null) {

            // ======= 初始化ViewPager ========
            mIndicators = new ImageView[myBannerList.size()];
            if (myBannerList.size() <= 1) {
                mIndicator.setVisibility(View.GONE);
            }

            for (int i = 0; i < mIndicators.length; i++) {
                ImageView imageView = new ImageView(mContext);
                LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
                if (i != 0) {
                    params.leftMargin = 10;
                }
                imageView.setLayoutParams(params);
                mIndicators[i] = imageView;
                if (i == 0) {
                    mIndicators[i].setBackgroundResource(R.drawable.homepage_dot_gold);
                } else {
                    mIndicators[i].setBackgroundResource(R.drawable.homepage_dot_grey);
                }
                mIndicator.addView(imageView);
            }

            mImageViews = new ImageView[myBannerList.size()];

            for (int i = 0; i < mImageViews.length; i++) {
                ImageView imageView = new ImageView(mContext);
                imageView.setScaleType(ScaleType.FIT_XY);
                mImageViews[i] = imageView;
            }
            mViewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.Standard);
            mViewPager.setCurrentItem(0);
            mHandler.removeMessages(MSG_CHANGE_PHOTO);
            mHandler.sendEmptyMessageDelayed(MSG_CHANGE_PHOTO, PHOTO_CHANGE_TIME);

            mViewPager.setAdapter(BannerAdapter);
            mViewPager.setOnPageChangeListener(new MyPageChangeListener());
            mViewPager.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        swipeRefreshLayout.setEnabled(false);
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        swipeRefreshLayout.setEnabled(false);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        swipeRefreshLayout.setEnabled(true);
                    }
                    if (myBannerList.size() == 0 || myBannerList.size() == 1)
                        return true;
                    else
                        return false;

                }
            });

            BannerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 当ViewPager中页面的状态发生改变时调用
     *
     * @author Administrator
     */
    private class MyPageChangeListener implements OnPageChangeListener {

        /**
         * This method will be invoked when a new page becomes selected.
         * position: Position index of the new selected page.
         */
        public void onPageSelected(int position) {
            setImageBackground(position);

        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    /**
     * 设置选中的tip的背景
     *
     * @param selectItemsIndex
     */
    private void setImageBackground(int selectItemsIndex) {
        for (int i = 0; i < mIndicators.length; i++) {
            if (i == selectItemsIndex) {
                mIndicators[i]
                        .setBackgroundResource(R.drawable.homepage_dot_gold);
            } else {
                mIndicators[i].setBackgroundResource(R.drawable.homepage_dot_grey);
            }
        }
    }

    private void initCurrentPrice(String gold, String pt) {
        currentPrice.setText("今日金价：黄金" + gold + "/元/克 铂金" + pt + "/元/克");
    }


    int hour;
    int minute;
    int second;

    private void initFlashSale() {
        hour = Integer.parseInt(flashSales.get(0).endHour);
        minute = Integer.parseInt(flashSales.get(0).endMinute);
        second = Integer.parseInt(flashSales.get(0).endSeconds);
        initTimer();
        ImageLoader.getInstance().displayImage(flashSales.get(0).pic, flashSaleImg, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(flashSales.get(1).pic, flashSaleImg2, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(flashSales.get(2).pic, flashSaleImg3, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(flashSales.get(3).pic, flashSaleImg4, ImageLoadOptions.getOptions());
        giveClick(flashSaleImg, flashSales.get(0).categoryId, flashSales.get(0).title);
        giveClick(flashSaleImg2, flashSales.get(1).categoryId, flashSales.get(1).title);
        giveClick(flashSaleImg3, flashSales.get(2).categoryId, flashSales.get(2).title);
        giveClick(flashSaleImg4, flashSales.get(3).categoryId, flashSales.get(3).title);
        if (timer != null) {
            timer.cancel();
        }
        task = new TimerTask() {
            @Override
            public void run() {
                if (second == 0) {
                    changeMinute();
                } else {
                    second--;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initTimer();
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(task, 1000, 1000);
    }

    private void initTimer() {
        hourTxt.setText(String.format("%02d", hour));
        minuteTxt.setText(String.format("%02d", minute));
        secondTxt.setText(String.format("%02d", second));
    }

    private void changeMinute() {
        if (minute == 0) {
            changeHour();
        } else {
            second = 59;
            minute--;
        }

    }

    private void changeHour() {
        if (hour == 0) {
            timer.cancel();
            timer = null;
        } else {
            second = 59;
            minute=59;
            hour--;
        }
    }

    private void initNewRecommend() {
        ImageLoader.getInstance().displayImage(recommends.get(0).pic, recommendImg, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(recommends.get(1).pic, recommendImg2, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(recommends.get(2).pic, recommendImg3, ImageLoadOptions.getOptions());
        giveClick(recommendImg, recommends.get(0).keywords, recommends.get(0).title);
        giveClick(recommendImg2, recommends.get(1).keywords, recommends.get(1).title);
        giveClick(recommendImg3, recommends.get(2).keywords, recommends.get(2).title);
    }

    private void initSuperAffordable() {
        ImageLoader.getInstance().displayImage(superaffords.get(0).pic, superAffImg, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(superaffords.get(1).pic, superAffImg2, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(superaffords.get(2).pic, superAffImg3, ImageLoadOptions.getOptions());
        giveClick(superAffImg, superaffords.get(0).keywords, superaffords.get(0).title);
        giveClick(superAffImg2, superaffords.get(1).keywords, superaffords.get(1).title);
        giveClick(superAffImg3, superaffords.get(2).keywords, superaffords.get(2).title);
    }

    private void giveClick(ImageView img, final String keywords, final String categoryName) {
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                intent.setAction(MyAction.classifyActivityAction);
                intent.putExtra("categoryId", keywords);
                intent.putExtra("categoryName", categoryName);
                startActivity(intent);
            }
        });
    }

    private void initRecommendForYou() {
        recommendfyAdapter.notifyDataSetChanged();
    }


    // 提供给banner的viewpager适配器
    public class MyAdapter extends PagerAdapter {

        ArrayList<RolateImageEntity> myBannerList = new ArrayList<>();

        @Override
        public int getCount() {
            return mImageUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            if (view instanceof OutlineContainer) {
                return ((OutlineContainer) view).getChildAt(0) == obj;
            } else {
                return view == obj;
            }
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mViewPager.findViewFromObject(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ImageLoader.getInstance().displayImage(rolates.get(position).pic, mImageViews[position], ImageLoadOptions.getOptions());
            ((ViewPager) container).addView(mImageViews[position], 0);
            mViewPager.setObjectForPosition(mImageViews[position], position);
            mImageViews[position].setOnClickListener(viewPagerOnClick);
            mImageViews[position].setTag(position);
            return mImageViews[position];
        }

        public void bindData(ArrayList<RolateImageEntity> myBannerList) {
            this.myBannerList = myBannerList;
        }

        OnClickListener viewPagerOnClick = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int vID = (Integer) v.getTag();
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                intent.setAction(MyAction.classifyActivityAction);
                intent.putExtra("categoryId", rolates.get(vID).keywords);
                intent.putExtra("categoryName", rolates.get(vID).title);
                startActivity(intent);
            }
        };

    }

    private BaseAdapter recommendfyAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return recommendfy.size();
        }

        @Override
        public RecommendfyEntity getItem(int position) {
            return recommendfy.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                vh = new ViewHolder();
                final View view = LayoutInflater.from(getActivity()).inflate(R.layout.recommend_for_you_item_layout, parent, false);
                vh.ll = (LinearLayout) view.findViewById(R.id.img_container);
                vh.name = (TextView) view.findViewById(R.id.name);
                vh.price = (TextView) view.findViewById(R.id.price);
                vh.img = new ImageView(getActivity()) {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int left = view.getPaddingLeft();
                        int right = view.getPaddingRight();
                        int containerWidth = (gridWidth - getHorizontalSpacing(mCustomGridView)) / 2;
                        int imgWith = containerWidth - left - right;
                        setMeasuredDimension(imgWith, imgWith);
                    }
                };
                vh.ll.addView(vh.img);
                convertView = view;
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            vh.name.setText(getItem(position).goodsName);
            vh.price.setText(getString(R.string.yuan_char) + getItem(position).goodsPrice);
            ImageLoader.getInstance().displayImage(getItem(position).goodsImage, vh.img, ImageLoadOptions.getOptions());
            vh.img.setTag(position);
            vh.img.setOnClickListener(clickListener);
            return convertView;
        }

        class ViewHolder {
            LinearLayout ll;
            ImageView img;
            TextView name;
            TextView price;
        }
    };

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
            intent.setAction(MyAction.productListActivityAction);
            intent.putExtra("proId", Integer.parseInt(recommendfy.get(position).goodsId));
            startActivity(intent);
        }
    };


    private int getHorizontalSpacing(CustomGridView view) {
        try {
            Class clazz = view.getClass();
            Field f = clazz.getDeclaredField("mHorizontalSpacing");
            f.setAccessible(true);
            return f.getInt(view);
        } catch (NoSuchFieldException ignored) {

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        hourTxt = (TextView) getView().findViewById(R.id.hour);
        minuteTxt = (TextView) getView().findViewById(R.id.minute);
        secondTxt = (TextView) getView().findViewById(R.id.second);

        flashSaleImg = (ImageView) getView().findViewById(R.id.img1);
        flashSaleImg2 = (ImageView) getView().findViewById(R.id.img12);
        flashSaleImg3 = (ImageView) getView().findViewById(R.id.img13);
        flashSaleImg4 = (ImageView) getView().findViewById(R.id.img14);

        recommendImg = (ImageView) getView().findViewById(R.id.img2);
        recommendImg2 = (ImageView) getView().findViewById(R.id.img22);
        recommendImg3 = (ImageView) getView().findViewById(R.id.img23);

        superAffImg = (ImageView) getView().findViewById(R.id.img3);
        superAffImg2 = (ImageView) getView().findViewById(R.id.img32);
        superAffImg3 = (ImageView) getView().findViewById(R.id.img33);

        mCustomGridView = (CustomGridView) getView().findViewById(R.id.recommend_for_you);

        currentPrice = (TextView) getView().findViewById(R.id.current_price);
        mViewPager = (JazzyViewPager) getView().findViewById(R.id.index_product_images_container);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        mIndicator = (LinearLayout) getView().findViewById(R.id.index_product_images_indicator);
        input = (TextView) getView().findViewById(R.id.index_search_button);
        locationBtn = (ImageButton) getView().findViewById(R.id.index_location_button);
        favorite = getView().findViewById(R.id.home_collection);
        logistics = getView().findViewById(R.id.home_logistics_information);
        history = getView().findViewById(R.id.home_history);
        profile = getView().findViewById(R.id.home_company_profile);
        input.setOnClickListener(this);
        locationBtn.setOnClickListener(this);
        favorite.setOnClickListener(this);
        logistics.setOnClickListener(this);
        history.setOnClickListener(this);
        profile.setOnClickListener(this);

        // 设置下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        // 设置卷内的颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.color_gold_nor,
                R.color.color_gold_nor, R.color.color_gold_nor, R.color.color_gold_nor);

        // 使用Handler，使viewpage自动播放
        mHandler = new Handler(mContext.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_CHANGE_PHOTO:
                        int index = mViewPager.getCurrentItem();
                        if (index == rolates.size() - 1) {
                            index = -1;
                        }
                        mViewPager.setCurrentItem(index + 1);
                        mHandler.sendEmptyMessageDelayed(MSG_CHANGE_PHOTO, PHOTO_CHANGE_TIME);
                }
            }

        };
        mCustomGridView.setAdapter(recommendfyAdapter);
    }

    // 下拉刷新监听器
    OnRefreshListener onRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            initData();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.index_search_button://搜索
                intent = new Intent(mContext, SearchActivity.class);
                break;

//			case R.id.index_location_button://定位
//				intent=new Intent(mContext,LogisticsDetialsActivity.class);
//
//				break;
            case R.id.index_location_button://定位
                intent = new Intent(mContext, NearStoreActivity.class);
                break;
            case R.id.index_camer_button://扫描二维码
                intent = new Intent(mContext, ScanCodeActivity.class);
                break;
            case R.id.home_collection:
                if ("".equals(Utils.getTokenKey(app))) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    loginIntent.putExtra(LoginActivity.JUMP_TO_ACTIVITY, MyCollectionActivity.class);
                    startActivity(loginIntent);
//                    startActivityForResult(loginIntent, 94);
                } else {
                    Intent collecionIntent = new Intent(getActivity(), MyCollectionActivity.class);
                    startActivity(collecionIntent);
                }
                break;
            case R.id.home_logistics_information:
//                if ("".equals(Utils.getTokenKey(app))) {
//                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
//                    loginIntent.putExtra(LoginActivity.JUMP_TO_ACTIVITY, LogisticsActivity.class);
//                    startActivityForResult(loginIntent, 94);
//                } else {
//                    Intent logisticsIntent = new Intent(getActivity(), LogisticsActivity.class);
//                    startActivity(logisticsIntent);
//                }

                if ("".equals(Utils.getTokenKey(app))) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    loginIntent.putExtra(LoginActivity.JUMP_TO_ACTIVITY, ChooseRingActivity.class);
                    startActivityForResult(loginIntent, 94);
                } else {
                    Intent logisticsIntent = new Intent(getActivity(), ChooseRingActivity.class);
                    startActivity(logisticsIntent);
                }
                break;
            case R.id.home_history:
                if ("".equals(Utils.getTokenKey(app))) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    loginIntent.putExtra(LoginActivity.JUMP_TO_ACTIVITY, ScanHistoryActivity.class);
                    startActivityForResult(loginIntent, 94);
                } else {
                    Intent historyIntent = new Intent(getActivity(), ScanHistoryActivity.class);
                    startActivity(historyIntent);
                }
                break;
            case R.id.home_company_profile:
                Intent profileIntent = new Intent(getActivity(), CompanyProfile.class);
                startActivity(profileIntent);
                break;
        }
        if (intent != null)
            startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && data != null) {
            ((MainActivity) mContext).selectItem(MainActivity.TAB_SHOPPING_CART);
            ((MainActivity) mContext).mySelfFragmentNeedUpdate(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
