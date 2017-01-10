package cn.mstar.store.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.mvp.model.JsonRingFramDB;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CustomGridView;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.customviews.VerticalViewPager;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/*
 * 创建人：Yangshao
 * 创建时间：2016/4/14 15:55
 * @version    选择戒托
 *
 */
public class ChooseRingFrameActivity extends Activity  implements View.OnClickListener,PullToRefreshView.OnHeaderRefreshListener,
        PullToRefreshView.OnFooterRefreshListener {

    CustomGridView  mCustomGridView;
    //Button id_btn_make_num;
    private int gridWidth;
    private final String TAG="ChooseRingFrameActivity";
    @Bind(R.id.pull_refresh_view)
    PullToRefreshView listContainer;
    LinearLayout ly_content;
    ImageView id_ig_view;
    RadioGroup ry_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ring_frame_main);
        ButterKnife.bind(this);
        proid = getIntent().getIntExtra("proid",0);
        initTitle();
        initView();
        initTypeLayout();
        loadNetData();

    }


    LinearLayout ly_loading;
    LinearLayout ly_noData;
    LinearLayout ly_netError;
    TextView tv_reset;
    private void initTypeLayout(){
        ly_loading = (LinearLayout) findViewById(R.id.lny_loading_layout);
        ly_noData = (LinearLayout) findViewById(R.id.lny_no_result);
        ly_netError = (LinearLayout) findViewById(R.id.lny_network_error_view);
        tv_reset = (TextView) findViewById(R.id.wifi_retry);
    }

    private void showLoading() {
        dismissAllView();
        ly_loading.setVisibility(View.VISIBLE);
    }

    private void showNoResult() {
        dismissAllView();
        ly_noData.setVisibility(View.VISIBLE);
    }

    private void dismissAllView() {
        ly_loading.setVisibility(View.GONE);
        ly_noData.setVisibility(View.GONE);
        ly_netError.setVisibility(View.GONE);
    }

    private void showNetError() {
        dismissAllView();
        ly_netError.setVisibility(View.VISIBLE);
    }


    private void initView() {
        id_ig_view = (ImageView) findViewById(R.id.id_ig_view);
        salesBtn = (RadioButton) findViewById(R.id.salesBtn);
        priceBtn = (RadioButton) findViewById(R.id.priceBtn);
        ry_group = (RadioGroup) findViewById(R.id.id_ry_group);
        ly_content = (LinearLayout) findViewById(R.id.id_ly_content);
        mCustomGridView = (CustomGridView)findViewById(R.id.recommend_for_you);
        mCustomGridView.setAdapter(recommendfyAdapter);
        salesBtn.setOnClickListener(this);
        priceBtn.setOnClickListener(this);
        //id_btn_make_num.setOnClickListener(this);
        mCustomGridView.post(new Runnable() {
            @Override
            public void run() {
                gridWidth = mCustomGridView.getMeasuredWidth();
            }
        });

        listContainer.setOnHeaderRefreshListener(this);
        listContainer.setOnFooterRefreshListener(this);
        mCustomGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int ringid = Integer.valueOf(mdata.get(i).getProId());
                Intent intent = new Intent(ChooseRingFrameActivity.this, ChooseRingFraDetails.class);
                int proid = Integer.valueOf(diamond_info.getProId());
                intent.putExtra("proid", proid);
                intent.putExtra("ringid", ringid);
                startActivity(intent);
            }
        });

    }

    ImageView titleBack;
    private void initTitle() {
        titleBack= (ImageView) findViewById(R.id.title_back);
        TextView titleName= (TextView) findViewById(R.id.title_name);
        titleName.setText("选择戒托");
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);
    }

    private List<JsonRingFramDB.DataEntity.RingInfoEntity> mdata = new ArrayList<>();

    private BaseAdapter recommendfyAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mdata.size();
        }

        @Override
        public JsonRingFramDB.DataEntity.RingInfoEntity getItem(int position) {
            return mdata.get(position);
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
                final View view = LayoutInflater.from(ChooseRingFrameActivity.this).inflate(R.layout.recommend_for_you_item_layout, parent, false);
                vh.ll = (LinearLayout) view.findViewById(R.id.img_container);
                vh.name = (TextView) view.findViewById(R.id.name);
                vh.price = (TextView) view.findViewById(R.id.price);
                vh.img = new ImageView(ChooseRingFrameActivity.this) {
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

            vh.name.setText(getItem(position).getName());
            vh.price.setText(getString(R.string.yuan_char) + getItem(position).getPrice());
            ImageLoader.getInstance().displayImage(getItem(position).getPic(), vh.img, ImageLoadOptions.getOptions());
            vh.img.setTag(position);
            //vh.img.setOnClickListener(clickListener);
            return convertView;
        }

        class ViewHolder {
            LinearLayout ll;
            ImageView img;
            TextView name;
            TextView price;
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

    JsonRingFramDB.DataEntity.DiamondInfoEntity diamond_info;
    private void loadNetData(){
        LogUtils.e(TAG+"URL"+changeGetURL());
        showLoading();
        VolleyRequest.GetCookieRequest(this, changeGetURL(), new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try{
                    dismissAllView();
                    JsonRingFramDB jsonRingFramDB = new Gson().fromJson(result, JsonRingFramDB.class);
                    diamond_info = jsonRingFramDB.getData().getDiamond_info();
                    List<JsonRingFramDB.DataEntity.RingInfoEntity> ring_info = jsonRingFramDB.getData().getRing_info();
                    list_count=Integer.valueOf(jsonRingFramDB.getData().getList_count());
                    if (list_count==0){
                        showNoResult();
                        return;
                    }
                    if (pullState != PULL_LOAD) {
                        mdata.clear();
                    }
                    ry_group.setVisibility(View.VISIBLE);
                    mdata.addAll(ring_info);
                    endNetRequest();
                    addTextVie(diamond_info);
                }catch (Exception e){

                }

            }

            @Override
            public void onFail(String error) {

                showNetError();
            }
        });
    }



    /*
   * 创建人：Yangshao
   * 创建时间：2016/3/23 8:45
   * @version    根据网络数据填充textView
   *
   */
    List<String> titleNames=new ArrayList<>();
    public void addTextVie(JsonRingFramDB.DataEntity.DiamondInfoEntity diamond_info)  {
        ly_content.removeAllViews();
        titleNames.clear();
        ImageLoader.getInstance().displayImage(diamond_info.getPic(), id_ig_view, ImageLoadOptions.getOptions());
        for (int i=0,n=diamond_info.getAttr().size();i<n;i++){
            titleNames.add(diamond_info.getAttr().get(i));
        }
        titleNames.add("price:" + diamond_info.getPrice());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(VerticalViewPager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        layoutParams.setMargins(15, 6, 1, 1);
        LinearLayout.LayoutParams linelayoutParams = new LinearLayout.LayoutParams(VerticalViewPager.LayoutParams.MATCH_PARENT, 1);
        for (int i = 0,n=titleNames.size() ;i < n; i++) {
            TextView textView = new TextView(this);
            textView.setText(titleNames.get(i));
            textView.setTextSize(11);
            textView.setTag(i + 1);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(Color.BLACK);
            View view = new View(this);
            view.setBackgroundColor(getResources().getColor(R.color.awesome_red));
            //layoutParams.height=dip2px(this,30);
            // layoutParams.leftMargin=dip2px(this,20);
            textView.setPadding(1, 1, 1, 1);

            ly_content.addView(textView, layoutParams);
            ly_content.addView(view, linelayoutParams);
            TextView button=null;
            if (i==(n-1)){
                button=new TextView(this);
                //  button.setBackgroundColor(getResources().getColor(R.color.color_btn));
                button.setBackgroundResource(R.drawable.shape_buttnon);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams2.setMargins(20, 10, 1, 1);
                button.setText(" 重选裸戒  ▶ ");
                button.setGravity(Gravity.CENTER_VERTICAL);
                button.setTextSize(13);
                button.setTextColor(Color.WHITE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
                ly_content.addView(button, layoutParams2);
            }

        }

    }


    RadioButton salesBtn,priceBtn;
    private int key = 1;            //=>  1-销量 3-价格量 空-按最新发布排序
    private int order = 2;  //排序方式 1-升序 2-降序
    int proid=0;   //裸戒Id
    int page;
    int curpage;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:// 返回
                finish();
                break;
            case R.id.salesBtn:
                key = 1;
                if (order == 1) {
                    L.e("销量降序排列啦");
                    mdata.clear();
                   curpage = 1;
                    order = 2;
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price_down);
                    salesBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                } else {
                    L.e("销量升序排列啦");
                    curpage = 1;
                    order = 1;
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price);
                    salesBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                }
                loadNetData();
                break;
            case R.id.priceBtn:
                key = 3;
                if (order == 1) {
                    L.e("价格降序排列啦");
                    curpage = 1;
                    order = 2;
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price_down);
                    priceBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                } else {
                    L.e("价格升序排列啦");
                    curpage = 1;
                    order = 1;
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price);
                    priceBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                }
                loadNetData();
                break;
            case R.id.newsProductBtn:
                break;
            case R.id.id_btn_make_num:
//                Intent intent = new Intent(ChooseRingFrameActivity.this, ChooseRingFraDetails.class);
//                startActivity(intent);
                break;
        }
        if (v==titleBack){
            finish();
        }

    }



    private String changeGetURL() {
        String url = AppURL.CHOOSE_RING_FRAG + "&proid=" + proid
                + "&page=" + page + "&curpage=" + curpage + "&key=" + key
                + "&order=" + order + "&tokenKey=" + MyApplication.getInstance().tokenKey;
        Log.e(TAG, TAG + "\nkey:" + key + "   order" + order + "\n" + "change:" + url + "");
        return url;
    }


    private static final int PULL_REFRESH = 1;
    private static final int PULL_LOAD = 2;
    private int tempCurpage = 1;
    private int pullState = 1;
    private int list_count;
    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (mdata.size() < list_count) {
            tempCurpage = curpage;
            curpage++;
            pullState = PULL_LOAD;
            loadNetData();
        } else {
            Toast.makeText(this, "没有更多数据", Toast.LENGTH_SHORT).show();
            view.onFooterRefreshComplete();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        tempCurpage = curpage;
        curpage = 1;
        pullState = PULL_REFRESH;
        loadNetData();
    }


    private void endNetRequest() {
        recommendfyAdapter.notifyDataSetChanged();
        tempCurpage = curpage;
        if (pullState == PULL_LOAD) {
            listContainer.onFooterRefreshComplete();
        } else if (pullState == PULL_REFRESH) {
            listContainer.onHeaderRefreshComplete();
        }
        pullState = 0;
    }


    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }
}

