package cn.mstar.store.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.mvp.model.JsonRingListDB;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.customviews.VerticalViewPager;
import cn.mstar.store.utils.DensityUtil;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/*
 * 创建人：Yangshao
 * 创建时间：2016/4/14 15:55
 * @version    裸戒列表
 *
 */
public class ChooseRingListActivity extends Activity implements View.OnClickListener,PullToRefreshView.OnHeaderRefreshListener,
        PullToRefreshView.OnFooterRefreshListener {

    private List<JsonRingListDB.DataEntity.ProInfoEntity> mdata =new ArrayList<>();
    private final String TAG="ChooseRingListActivity";
    @Bind(R.id.container)
    PullToRefreshView listContainer;
    RadioGroup ry_group;

    private RadioButton salesBtn,priceBtn;
    private int key = 2;            //=>  1-销量 3-价格量 空-按最新发布排序
    private int order = 2;  //排序方式 1-升序 2-降序
    private int page=10;
    private int curpage=1;
    private  GridView gridView;
    private  String url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ring_list_main);
        ButterKnife.bind(this);
        url=getIntent().getStringExtra("url");
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



    private String changeGetURL(){
       String urls= AppURL.RINGG_LIST+url+"&page="+page+"&curpage="+curpage+"&key="+key+"&order="+order+"&tokenKey="+ MyApplication.getInstance().tokenKey;
        LogUtils.e(TAG + "Url\n" + urls);
       return  urls;
    }
    private void initView() {
        gridView= (GridView) findViewById(R.id.id_gv_slelect_ring);
        salesBtn = (RadioButton) findViewById(R.id.salesBtn);
        priceBtn = (RadioButton) findViewById(R.id.priceBtn);
        ry_group = (RadioGroup) findViewById(R.id.id_ry_group);

        gridView.setAdapter(recommendfyAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ChooseRingListActivity.this, ChooseRingFrameActivity.class);
                int proid=Integer.valueOf(mdata.get(i).getProId());
                intent.putExtra("proid",proid);
                startActivity(intent);
            }
        });
        priceBtn.setOnClickListener(this);
        salesBtn.setOnClickListener(this);
        listContainer.setOnHeaderRefreshListener(this);
        listContainer.setOnFooterRefreshListener(this);

    }


    private void loadNetData(){
        showLoading();
        VolleyRequest.GetCookieRequest(this, changeGetURL(), new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                dismissAllView();
                JsonRingListDB jsonRingListDB = new Gson().fromJson(result, JsonRingListDB.class);
                JsonRingListDB.DataEntity dataEntity = jsonRingListDB.getData();
                List<JsonRingListDB.DataEntity.ProInfoEntity> info = dataEntity.getProInfo();
                list_count = Integer.valueOf(dataEntity.getList_count());
                if (list_count == 0) {
                    showNoResult();
                    return;
                }
                if (pullState != PULL_LOAD) {
                    mdata.clear();
                }
                ry_group.setVisibility(View.VISIBLE);
                mdata.addAll(info);
                endNetRequest();
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
    private List<String> titleNames=new ArrayList<>();
    public void addTextVie(LinearLayout linearLayout,JsonRingListDB.DataEntity.ProInfoEntity proInfoEntity)  {
        linearLayout.removeAllViews();
        titleNames.clear();
        for (int i=0,n=proInfoEntity.getAttr().size();i<n;i++){
            titleNames.add(proInfoEntity.getAttr().get(i));
        }
        titleNames.add("price:" + proInfoEntity.getPrice());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(VerticalViewPager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER;
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
            textView.setPadding(1, 1, 1, 1);
            linearLayout.addView(textView, layoutParams);
            linearLayout.addView(view, linelayoutParams);
            if (i==(n-1)){
                TextView button=new TextView(this);
              //  button.setBackgroundColor(getResources().getColor(R.color.color_btn));
                button.setBackgroundResource(R.drawable.shape_buttnon);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams2.setMargins(20, 10, 1, 1);
                button.setText(" 选择戒托 ▶ ");
                button.setGravity(Gravity.CENTER_VERTICAL);
                button.setTextSize(13);
                button.setTextColor(Color.WHITE);
                linearLayout.addView(button, layoutParams2);
            }

        }

    }



    private BaseAdapter recommendfyAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mdata.size();
        }

        @Override
        public JsonRingListDB.DataEntity.ProInfoEntity getItem(int position) {
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
                convertView= LayoutInflater.from(ChooseRingListActivity.this).inflate(R.layout.item_ring_list,parent, false);
                vh.id_ig_view = (ImageView) convertView.findViewById(R.id.id_ig_view);
                vh.ly_content = (LinearLayout) convertView.findViewById(R.id.id_ly_content);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            // vh.img.setImageResource(msrc.get(position));
            ImageLoader.getInstance().displayImage(mdata.get(position).getPic(), vh.id_ig_view, ImageLoadOptions.getOptions());
            addTextVie(vh.ly_content, mdata.get(position));
            return convertView;
        }

        class ViewHolder {
            ImageView id_ig_view;
            LinearLayout ly_content;
        }
    };


    ImageView titleBack;
    private void initTitle() {
        ImageView titleBack= (ImageView) findViewById(R.id.title_back);
        TextView titleName= (TextView) findViewById(R.id.title_name);
        titleName.setText("裸戒列表");
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);
    }


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
                    loadNetData();
                } else {
                    L.e("销量升序排列啦");
                    mdata.clear();
                    curpage = 1;
                    order = 1;
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price);
                    salesBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    loadNetData();
                }
                break;
            case R.id.priceBtn:
                key = 3;
                if (order == 1) {
                    L.e("价格降序排列啦");
                    mdata.clear();
                    curpage = 1;
                    order = 2;
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price_down);
                    priceBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    loadNetData();
                } else {
                    L.e("价格升序排列啦");
                    mdata.clear();
                    curpage = 1;
                    order = 1;
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price);
                    priceBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    loadNetData();
                }
                break;
            case R.id.newsProductBtn:
                break;
        }
        if (v==titleBack){
            finish();
        }

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
