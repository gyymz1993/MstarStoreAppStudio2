package cn.mstar.store.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.adapter.ProductListAdapter222;
import cn.mstar.store.adapter.ProductListAdapterWcl;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.FilterPopup;
import cn.mstar.store.customviews.GridViewWithHeaderAndFooter;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.customviews.PullToRefreshView.OnFooterRefreshListener;
import cn.mstar.store.customviews.PullToRefreshView.OnHeaderRefreshListener;
import cn.mstar.store.customviews.SquareTextView;
import cn.mstar.store.entity.Product;
import cn.mstar.store.entity.ProductType;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.NetWorkUtil;
import cn.mstar.store.utils.Screen;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;
import cn.mstar.store.utils.VolleyRequest.HttpStringRequsetCallBack;

/**
 * 产品列表界面
 *
 * @author wenjundu 2015-7-9
 */
public class ProductListActivity extends BaseActivity implements
        OnClickListener, CompoundButton.OnCheckedChangeListener, OnHeaderRefreshListener, OnFooterRefreshListener ,FilterPopup.OnSeachSuccess {


    private final String TAG="ProductListActivity";
    // 返回键
    private ImageView titleBackBtn;
    // 标题
    private TextView titleName;
    private CheckBox change_layout;
    private RadioGroup radioGroup;
    private RadioButton salesBtn, priceBtn, newsProductBtn,rbFilterBtn;
    // 显示产品列表
    private GridViewWithHeaderAndFooter product_list;
    // 产品列表适配器
    private ProductListAdapter222 productListAdapter;
    private ProductListAdapterWcl productListAdapter_wcl;
    // 显示产品的url
    private String showProductUrl = "";
    // 存储产品List
    private List<Product> productList;
    // 获取从分类里传递过来的categoryId
    private String categoryId;
    private String categoryName;
    // 默认显示第一页
    private int curpage = 1;
    // 搜索关键字
    private String keyword = "";
    // 刷新
    private PullToRefreshView mRefreshView;
    private int Refresh; // 上、下拉刷新的标识
    private boolean PriceIsAsc;//价格升序
    //排序方式
    private String sortord;
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
    private String storeId;

    private LoadingDialog progressDialog;

    TextView tv_price_30,tv_price_50,tv_price_100,tv_price_seach;
    Button id_btn_seach;
    EditText id_pirce_min,id_pirce_max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mctontext=this;
        setContentView(R.layout.activity_productlist);
        MyApplication.getInstance().addActivity(this);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        storeId = MyApplication.getInstance().storeId;


        init();
        initListener();
        initData();
    }

    // 添加控件监听
    private void initListener() {
        salesBtn.setOnClickListener(this);
        priceBtn.setOnClickListener(this);
        newsProductBtn.setOnClickListener(this);
        rbFilterBtn.setOnClickListener(this);
        titleBackBtn.setOnClickListener(this);
        product_list.setOnItemClickListener(listItemClick);
        mRefreshView.setOnHeaderRefreshListener(this);
        mRefreshView.setOnFooterRefreshListener(this);
        filterBtn.setOnClickListener(this);
        change_layout.setOnCheckedChangeListener(this);
//        tv_price_100.setOnClickListener(this);
//        tv_price_30.setOnClickListener(this);
//        tv_price_50.setOnClickListener(this);
//        tv_price_seach.setOnClickListener(this);
//        id_btn_seach.setOnClickListener(this);
    }

    private void initData() {
        productList = new ArrayList<Product>();
        productListAdapter = new ProductListAdapter222(this, productList, getScreenWidth());
        productListAdapter_wcl = new ProductListAdapterWcl(this, productList);
        product_list.setAdapter(productListAdapter);
        // 判断是从分类页跳转过来的吗
        Intent intent = getIntent();
        if (MyAction.classifyActivityAction.equals(intent.getAction()))//从分类页跳转过来
        {
            keyword = intent.getExtras().getString("categoryId");
            categoryName = intent.getExtras().getString("categoryName");
            L.e(TAG+"categoryId:" + categoryId);
            titleName.setText(categoryName);
        } else if (MyAction.searchActivitryAction.equals(intent.getAction())) {//从搜索页跳转过来
            keyword = intent.getExtras().getString("key");
            titleName.setText(keyword);
        }
        showProductUrl = AppURL.SEARCH_URL + "&category=" + categoryId
                + "&keyword=" + URLtoUTF8Utils.toUtf8String(keyword)
                + "&key=" + key + "&order=" + order + "&curpage=" + curpage
                + "&page=" + showPageData + "&mstoreId=" + storeId;
        showProduct(showProductUrl);
    }

    private int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


    private void changeURL() {
        showProductUrl = AppURL.SEARCH_URL + "&category=" + categoryId
                + "&keyword=" + URLtoUTF8Utils.toUtf8String(keyword)
                + "&key=" + key + "&order=" + order + "&curpage=" + curpage
                + "&page=" + showPageData + "&mstoreId=" + storeId;
        Log.e(TAG,TAG+"\nkey:"+key+"   order"+order+"\n"+"change:"+showProductUrl+"");
    }

    int mstoneWt1,mstoneWt2;
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
        Log.e(TAG,"url: \n"+showProductUrl+"");
    }


    Context mctontext;

    private List<ProductType> productTypes=new ArrayList<>();
    private List<String> shapes=new ArrayList<>();
    private void showProduct(String url) {
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
            progressDialog = new LoadingDialog(ProductListActivity.this,ProductListActivity.this
                    .getString(R.string.pull_to_refresh_footer_refreshing_label));
            progressDialog.show();
            //  发送获取json的请求
            L.i("wcl-->" + url);
            VolleyRequest.GetCookieRequest(ProductListActivity.this, url, new HttpStringRequsetCallBack() {
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
                                JSONArray goodsShape = data.getJSONArray("goodsShape");
                                shapes.clear();
                                for (int i=0;i<goodsShape.length();i++){
                                    //图形
                                    shapes.add(goodsShape.get(i)+"");
                                }
                                JSONArray goodsCategory = data.getJSONArray("goodsCategory");
                                productTypes.clear();
                                for (int i=0;i<goodsCategory.length();i++){
                                    ProductType type=new ProductType();
                                    type.setGcId(goodsCategory.getJSONObject(i).getString("gcId"));
                                    type.setGcName(goodsCategory.getJSONObject(i).getString("gcName"));
                                    productTypes.add(type);
                                }

                                popup=new FilterPopup(mctontext,(new Screen(mctontext)).getHeight() - getStatusBarHeight(),shapes,productTypes);
                                Log.e(TAG, TAG+"popup:"+popup);
                                //添加pop窗口关闭事件
                                popup.setOnDismissListener(new poponDismissListener());

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
                                        product.setProId(productArray
                                                .getJSONObject(i)
                                                .getInt("proId"));
                                        product.setCategoryId(productArray
                                                .getJSONObject(i).getString(
                                                        "categoryId"));
                                        product.setName(productArray.getJSONObject(
                                                i).getString("name"));
                                        product.setPrice(Double
                                                .parseDouble(productArray
                                                        .getJSONObject(i)
                                                        .getString("price")));
                                        product.setMkprice(Double.parseDouble(productArray.getJSONObject(i).getString("mkprice")));
                                        product.setSales_way(productArray
                                                .getJSONObject(i).getString(
                                                        "sales_way"));
                                        product.setImageUrl(productArray
                                                .getJSONObject(i).getString("pic"));
                                        productList.add(product);
                                    }
                                    productListAdapter.notifyDataSetChanged();
                                    productListAdapter_wcl.notifyDataSetChanged();
                                } else if (1 == curpage) {
                                    mRefreshView.setVisibility(View.GONE);
                                } else {
                                    // 所有页面加载完成
                                    CustomToast.makeToast(
                                            ProductListActivity.this,
                                            getResources().getString(
                                                    R.string.data_loaded),
                                            Toast.LENGTH_SHORT);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                progressDialog.dismiss();
                            }
                            if (Refresh == 2)
                                mRefreshView.onFooterRefreshComplete();
                            if (Refresh == 1)
                                mRefreshView.onHeaderRefreshComplete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                }

                @Override
                public void onFail(String error) {
                    progressDialog.dismiss();
                    Toast.makeText(ProductListActivity.this, "网络错误，请检查网络！", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // 无网络连接处理方法
            Toast.makeText(ProductListActivity.this, "网络错误，请检查网络！", Toast.LENGTH_SHORT).show();
        }
    }


    private OnItemClickListener listItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            switch (parent.getId()) {
                case R.id.product_list:
                    // 跳转到产品详情
                    Product product = productList.get(position);
                    Intent intent = new Intent(ProductListActivity.this,
                            ProductDetailsActivity.class);
                    intent.putExtra("proId", product.getProId());
                    intent.setAction(MyAction.productListActivityAction);
                    startActivity(intent);
                    break;

            }
        }

    };

    private void init() {
        titleBackBtn = (ImageView) findViewById(R.id.title_back);
        titleBackBtn.setVisibility(View.VISIBLE);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setVisibility(View.VISIBLE);
        change_layout = (CheckBox) findViewById(R.id.change_layout);
        change_layout.setVisibility(View.VISIBLE);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
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
        filterBtn = (SquareTextView) findViewById(R.id.tv_filter);
        filterBtn.setVisibility(View.GONE);
        salesBtn = (RadioButton) findViewById(R.id.salesBtn);
        priceBtn = (RadioButton) findViewById(R.id.priceBtn);
        newsProductBtn = (RadioButton) findViewById(R.id.newsProductBtn);
        rbFilterBtn = (RadioButton) findViewById(R.id.id_rb_filter);
        product_list = (GridViewWithHeaderAndFooter) findViewById(R.id.product_list);
        mRefreshView = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

//        popup = new FilterPopup(this,this, (new Screen(this)).getHeight() - getStatusBarHeight());
//        //添加pop窗口关闭事件
//        popup.setOnDismissListener(new poponDismissListener());
//        popup.setOnSeachSuccess(this);
        layout_root = (LinearLayout) findViewById(R.id.layout_root);


//        tv_price_30= (TextView) findViewById(R.id.id_tv_price_30);
//        tv_price_50= (TextView) findViewById(R.id.id_tv_price_50);
//        tv_price_100= (TextView) findViewById(R.id.id_tv_price_100);
//        tv_price_seach=(TextView)findViewById(R.id.id_tv_price_seach);
//        id_btn_seach= (Button) findViewById(R.id.id_btn_seach);
//
//        id_pirce_min= (EditText) findViewById(R.id.id_pirce_min);
//        id_pirce_max= (EditText) findViewById(R.id.id_pirce_max);
    }

    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }

    boolean isPriceSelect=false;
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
                    L.e("销量降序排列啦");
                    productList.clear();
                    curpage = 1;
                    order = 2;
                    changeURL();
                    L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_pull_down);
                    salesBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);
                } else {
                    L.e("销量升序排列啦");
                    productList.clear();
                    curpage = 1;
                    order = 1;
                    changeURL();
                    L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_go_up);
                    salesBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);
                }

                break;
            case R.id.priceBtn:
                key = 3;
                if (order == 1) {
                    L.e("价格降序排列啦");
                    productList.clear();
                    curpage = 1;
                    order = 2;
                    changeURL();
                    L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price_down);
                    priceBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);

                } else {
                    L.e("价格升序排列啦");
                    productList.clear();
                    curpage = 1;
                    //price_down:价格降序
                    order = 1;
                    changeURL();
                    L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_price);
                    priceBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);
                }
                break;
            case R.id.newsProductBtn:
                key = 0;
                if (order == 1) {
                    L.e("新品降序排列啦");
                    productList.clear();
                    curpage = 1;
                    order = 2;
                    changeURL();
                    L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_pull_down);
                    newsProductBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);

                } else {
                    L.e("新品升序排列啦");
                    productList.clear();
                    curpage = 1;
                    order = 1;
                    changeURL();
                    L.e(showProductUrl);
                    Drawable myImage = getResources().getDrawable(R.drawable.homepage_list_go_up);
                    newsProductBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                    showProduct(showProductUrl);
                }
                break;
            case R.id.id_rb_filter://筛选按钮
                if (popup==null){
                    return;
                }
                popup.setOnSeachSuccess(this);
            	popup.showAtLocation(layout_root, Gravity.RIGHT, 0, getStatusBarHeight());
                backgroundAlpha(0.7f);
                break;

            case  R.id.id_tv_price_30:
                productList.clear();
                resetOnebg(R.id.id_tv_price_30);
                selectPrice(30,0,0);
                showProduct(showProductUrl);
                break;
            case  R.id.id_tv_price_50:
                productList.clear();
                resetOnebg(R.id.id_tv_price_50);
                selectPrice(30,50,0);
                showProduct(showProductUrl);
                break;
            case  R.id.id_tv_price_100:
                productList.clear();
                resetOnebg(R.id.id_tv_price_100);
                selectPrice(50,100,0);
                showProduct(showProductUrl);
                break;
            case R.id.id_tv_price_seach:
                if (!isPriceSelect){
                    isPriceSelect=true;
                    tv_price_seach.setBackgroundResource(R.drawable.ed_shape_red);
                    tv_price_seach.setTextColor(getResources().getColor(R.color.red));
                    selectPrice(mstoneWt1,mstoneWt2,1);
                }else {
                    isPriceSelect=false;
                    tv_price_seach.setBackgroundResource(R.drawable.ed_shape_aw);
                    tv_price_seach.setTextColor(getResources().getColor(R.color.result_view));
                    selectPrice(mstoneWt1,mstoneWt2,0);
                }
                productList.clear();
                showProduct(showProductUrl);
                break;
            case R.id.id_btn_seach:
                productList.clear();
                resetAllBg();
                String minstr=id_pirce_min.getText().toString();
                String maxstr=id_pirce_max.getText().toString();
                if (!minstr.equals("")&&!maxstr.equals("")){
                    int min=Integer.valueOf(minstr);
                    int max=Integer.valueOf(maxstr);
                    if (min<max){
                        selectPrice(min,max,0);
                    }
                }else if (!maxstr.equals("")){
                    int max=Integer.valueOf(maxstr);
                    selectPrice(0,max,0);
                }else if (!minstr.equals("")){
                    int min=Integer.valueOf(minstr);
                    selectPrice(min,0,0);
                }else {
                    Toast.makeText(this,"请填写数据",Toast.LENGTH_LONG).show();
                    return;
                }
                showProduct(showProductUrl);
                break;
        }
    }

    private void cleanTextView(){
        id_pirce_min.getText().clear();
        id_pirce_max.getText().clear();
    }
    private void  resetOnebg(int id){
        cleanTextView();
        resetAllBg();
        if (id==R.id.id_tv_price_30){
            tv_price_30.setBackgroundResource(R.drawable.ed_shape_red);
            tv_price_30.setTextColor(getResources().getColor(R.color.red));
        }
        if (id==R.id.id_tv_price_50){
            tv_price_50.setBackgroundResource(R.drawable.ed_shape_red);
            tv_price_50.setTextColor(getResources().getColor(R.color.red));
        }
        if (id==R.id.id_tv_price_100){
            tv_price_100.setBackgroundResource(R.drawable.ed_shape_red);
            tv_price_100.setTextColor(getResources().getColor(R.color.red));
        }

    }
    private void  resetAllBg(){
        tv_price_30.setBackgroundResource(R.drawable.ed_shape_aw);
        tv_price_30.setTextColor(getResources().getColor(R.color.result_view));
        tv_price_50.setBackgroundResource(R.drawable.ed_shape_aw);
        tv_price_50.setTextColor(getResources().getColor(R.color.result_view));
        tv_price_100.setBackgroundResource(R.drawable.ed_shape_aw);
        tv_price_100.setTextColor(getResources().getColor(R.color.result_view));
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
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

    @Override
    public void onSeachSuccess(String url) {
        showProduct(url);
        Log.e(TAG,url);

    }
}
