package cn.mstar.store.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.adapter.ProductListAdapter222;
import cn.mstar.store.adapter.ProductListAdapterWcl;
import cn.mstar.store.adapter.ShopClassifyAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.FilterPopup;
import cn.mstar.store.customviews.GridViewWithHeaderAndFooter;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.customviews.SquareTextView;
import cn.mstar.store.entity.GoodsClassify;
import cn.mstar.store.entity.Product;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.NetWorkUtil;
import cn.mstar.store.utils.Screen;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Administrator on 2015/9/24.
 */
public class ClassifyStoreShopActivity extends BaseActivity implements
        View.OnClickListener, PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    private ShopClassifyAdapter shopClassifyAdapter;
    private ImageView title_message;
    // 返回键
    private ImageView titleBackBtn;
    // 标题
    private TextView titleName;
    private CheckBox changeLayout;
    private RadioGroup radioGroup;
    private RadioButton salesBtn, priceBtn, newsProductBtn;
    // 显示产品列表
    private GridViewWithHeaderAndFooter product_list;
    // 产品列表适配器
    private ProductListAdapter222 productListAdapter;
    private ProductListAdapterWcl productListAdapter_wcl;
    // 显示产品的url
    private String showProductUrl;
    // 存储产品List
    private List<Product> productList;
    // 获取从分类里传递过来的categoryId
    // 默认显示第一页
    private int curpage = 1;
    // 搜索关键字
    private String keyword = "";
    // 刷新
    private PullToRefreshView mRefreshView;
    private int Refresh; // 上、下拉刷新的标识
    //排序方式
    //筛选按钮
    private SquareTextView filterBtn;
    private FilterPopup popup;
    private LinearLayout layout_root;
    public static final int JSON_OBTAIN_SUCCESS = 12, JSON_OBTAIN_FAILURE = 13;
    private int key = 1;            //=>  1-销量 3-价格量 空-按最新发布排序
    private int order = 2;  //排序方式 1-升序 2-降序
    private int showPageData = 10;//默认每页显示10条数据
    private int countData;//总共多少条数据
    private int maxpage;//多少页
    private String category;
    private String shopid;
    private List<GoodsClassify> list;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // handle message sent from the other side.
            switch (msg.what) {
                case JSON_OBTAIN_FAILURE:

                    break;
                case JSON_OBTAIN_SUCCESS:

                    break;
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        ;
    };
    private LoadingDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);
        MyApplication.getInstance().addActivity(this);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        init();
        initListener();
        initData();
    }


    // 添加控件监听
    private void initListener() {
        salesBtn.setOnClickListener(this);
        priceBtn.setOnClickListener(this);
        newsProductBtn.setOnClickListener(this);
        titleBackBtn.setOnClickListener(this);
        product_list.setOnItemClickListener(listItemClick);
        mRefreshView.setOnHeaderRefreshListener(this);
        mRefreshView.setOnFooterRefreshListener(this);
        filterBtn.setOnClickListener(this);
        title_message.setOnClickListener(this);
        changeLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    product_list.setNumColumns(1);
                    product_list.setAdapter(productListAdapter_wcl);
                } else {
                    product_list.setNumColumns(2);
                    product_list.setAdapter(productListAdapter);
                }
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                order = 1;
                if (checkedId != R.id.salesBtn) {
                    salesBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
                if (checkedId != R.id.newsProductBtn) {
                    newsProductBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
        });
    }

    private void initData() {
        productList = new ArrayList<Product>();
        productListAdapter = new ProductListAdapter222(this, productList, getScreenWidth());
        productListAdapter_wcl = new ProductListAdapterWcl(this, productList);
        product_list.setAdapter(productListAdapter);
        // 判断是从分类页跳转过来的吗
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        shopid = bundle.getString("shopid");
        category = bundle.getString("category");
        String categoryName = intent.getStringExtra("categoryName");
        titleName.setText(categoryName);
        list = (List<GoodsClassify>) intent.getSerializableExtra("classifylist");
        shopClassifyAdapter = new ShopClassifyAdapter(this, list);
        showProductUrl = AppURL.BASE_URL + "act=shop_goods&op=index" + "&shopId=" + shopid + "&key=" + key + "&order=" + order + "&curpage=" + curpage + "&page=" + showPageData + "&category=" + category;

        showProduct(showProductUrl);

    }

    private int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


    private void changeURL() {//+"&category="+categoryId
        showProductUrl = AppURL.BASE_URL + "act=shop_goods&op=index" + "&shopId=" + shopid + "&key=" + key + "&order=" + order + "&curpage=" + curpage + "&page=" + showPageData + "&category=" + category;
    }

    private void showProduct(String url) {

        // get phone data...
        int screenWidth = getScreenWidth();
        String add;
        if (screenWidth < 700) {
            add = "&size=60";
        } /*else if (screenWidth < 1200) {
            add="&size=240";
		}*/ else {
            add = "&size=360";
        }
        url += add;

        // 是否有网络连接
        if (NetWorkUtil.isNetworkConnected(this)) {
            progressDialog = new LoadingDialog(
                    ClassifyStoreShopActivity.this,
                    ClassifyStoreShopActivity.this
                            .getString(R.string.pull_to_refresh_footer_refreshing_label));
            progressDialog.show();
            //  发送获取json的请求
            VolleyRequest.GetCookieRequest(ClassifyStoreShopActivity.this, url, new VolleyRequest.HttpStringRequsetCallBack() {

                @Override
                public void onSuccess(String result) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        if (jsonObject != null) {
                            try {
                                JSONObject data = jsonObject.getJSONObject("data");
                                countData = data.getInt("list_count");
                                if (countData % showPageData == 0)
                                    maxpage = countData / showPageData;
                                else
                                    maxpage = countData / showPageData + 1;
                                JSONArray productArray = data
                                        .getJSONArray("search");
                                if (productArray.length() > 0) {
                                    mRefreshView.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < productArray.length(); i++) {
                                        Product product = new Product();
                                        product.setProId(productArray.getJSONObject(i).getInt("proId"));
                                        product.setCategoryId(productArray.getJSONObject(i).getString("categoryId"));
                                        product.setName(productArray.getJSONObject(i).getString("name"));
                                        product.setPrice(Double.parseDouble(productArray.getJSONObject(i).getString("price")));
                                        product.setMkprice(Double.parseDouble(productArray.getJSONObject(i).getString("mkprice")));
                                        product.setImageUrl(productArray.getJSONObject(i).getString("pic"));
                                        productList.add(product);
                                    }
                                    productListAdapter.notifyDataSetChanged();
                                    productListAdapter_wcl.notifyDataSetChanged();
                                } else if (1 == curpage) {
                                    mRefreshView.setVisibility(View.GONE);
                                } else {
                                    // 所有页面加载完成
                                    CustomToast.makeToast(ClassifyStoreShopActivity.this,getResources().getString(
                                                    R.string.data_loaded),Toast.LENGTH_SHORT);
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if (Refresh == 2)
                                mRefreshView.onFooterRefreshComplete();
                            if (Refresh == 1)
                                mRefreshView.onHeaderRefreshComplete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

                @Override
                public void onFail(String error) {
                    Toast.makeText(ClassifyStoreShopActivity.this, "网络连接错误，请检查网络！", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

        } else {
            // 无网络连接处理方法
            Toast.makeText(ClassifyStoreShopActivity.this, "未连接网络", Toast.LENGTH_SHORT).show();
        }
    }


    private AdapterView.OnItemClickListener listItemClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            switch (parent.getId()) {
                case R.id.product_list:
                    // 跳转到产品详情
                    Product product = productList.get(position);
                    Intent intent = new Intent(ClassifyStoreShopActivity.this,
                            ProductDetailsActivity.class);
                    intent.putExtra("proId", product.getProId());
                    intent.setAction(MyAction.productListActivityAction);
                    startActivity(intent);
                    break;

            }
        }

    };

    private void init() {
        // TODO Auto-generated method stub
        /*slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.RIGHT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.storedetails_menu_item, null);
        classifylist = (ListView) view.findViewById(R.id.storedetails_listView);
        slidingMenu.setMenu(view);*/
        titleBackBtn = (ImageView) findViewById(R.id.title_back);
        titleBackBtn.setVisibility(View.VISIBLE);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setVisibility(View.VISIBLE);
        changeLayout = (CheckBox) findViewById(R.id.change_layout);
        changeLayout.setVisibility(View.VISIBLE);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

        filterBtn = (SquareTextView) findViewById(R.id.tv_filter);
        filterBtn.setVisibility(View.GONE);
        salesBtn = (RadioButton) findViewById(R.id.salesBtn);
        priceBtn = (RadioButton) findViewById(R.id.priceBtn);
        newsProductBtn = (RadioButton) findViewById(R.id.newsProductBtn);

        product_list = (GridViewWithHeaderAndFooter) findViewById(R.id.product_list);

        mRefreshView = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        popup = new FilterPopup(this, (new Screen(this)).getHeight() - getStatusBarHeight());
        //添加pop窗口关闭事件
        popup.setOnDismissListener(new poponDismissListener());
        layout_root = (LinearLayout) findViewById(R.id.layout_root);

        title_message = (ImageView) findViewById(R.id.title_message);
//        title_message.setVisibility(View.VISIBLE);
        title_message.setImageDrawable(getResources().getDrawable(R.drawable.shop_nav_icon_classification));

    }

    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:// 返回
                finish();
                break;
            case R.id.salesBtn:
                Log.d("wcl", "click");
                key = 1;
                if (order == 1) {
                    com.nostra13.universalimageloader.utils.L.e("销量升序排列啦");
                    productList.clear();
                    curpage = 1;
                    order = 2;
                    changeURL();
                    com.nostra13.universalimageloader.utils.L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_pull_down);
                    salesBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);
                } else {
                    com.nostra13.universalimageloader.utils.L.e("销量降序排列啦");
                    productList.clear();
                    curpage = 1;
                    order = 1;
                    changeURL();
                    com.nostra13.universalimageloader.utils.L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_go_up);
                    salesBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);
                }

                break;
            case R.id.priceBtn:
                key = 3;
                if (order == 1) {
                    com.nostra13.universalimageloader.utils.L.e("价格升序排列啦");
                    productList.clear();
                    curpage = 1;
                    order = 2;
                    changeURL();
                    com.nostra13.universalimageloader.utils.L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price_down);
                    priceBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);

                } else {
                    com.nostra13.universalimageloader.utils.L.e("价格降序排列啦");
                    productList.clear();
                    curpage = 1;
                    //price_down:价格降序
                    order = 1;
                    changeURL();
                    com.nostra13.universalimageloader.utils.L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price);
                    priceBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);
                }
                break;
            case R.id.newsProductBtn:
                key = 0;
                if (order == 1) {
                    com.nostra13.universalimageloader.utils.L.e("新品升序排列啦");
                    productList.clear();
                    curpage = 1;
                    order = 2;
                    changeURL();
                    com.nostra13.universalimageloader.utils.L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_pull_down);
                    newsProductBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);

                } else {
                    com.nostra13.universalimageloader.utils.L.e("新品降序排列啦");
                    productList.clear();
                    curpage = 1;
                    order = 1;
                    changeURL();
                    com.nostra13.universalimageloader.utils.L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_go_up);
                    newsProductBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);
                }
                break;
            case R.id.tv_filter://筛选按钮
            /*	popup.showAtLocation(layout_root, Gravity.RIGHT, 0, getStatusBarHeight());
                backgroundAlpha(0.7f);*/
                break;
        }
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        // TODO Auto-generated method stub
        Refresh = 2;
        curpage++;
        L.e("maxpage:" + maxpage);
        if (maxpage >= curpage) {
            changeURL();
            L.e(showProductUrl);

            showProduct(showProductUrl);

        } else {
            CustomToast.makeToast(this, "没有更多数据", Toast.LENGTH_SHORT);
            mRefreshView.onFooterRefreshComplete();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        // TODO Auto-generated method stub
        productList.clear();
        Refresh = 1;
        curpage = 1;
        changeURL();
        L.e(showProductUrl);
        showProduct(showProductUrl);
    }

    //获取状态栏高度
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
    }

}