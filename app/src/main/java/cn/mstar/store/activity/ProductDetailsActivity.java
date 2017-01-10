package cn.mstar.store.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.adapter.DetailsListAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.Constants;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CompatibilityScrollListView;
import cn.mstar.store.customviews.CustomWebview;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.customviews.MyRadioGroup;
import cn.mstar.store.customviews.SharePopup;
import cn.mstar.store.customviews.SharePopup.OnDialogListener;
import cn.mstar.store.db.entityToSave.ProAndSpecialIdz;
import cn.mstar.store.entity.BuyProductEntity;
import cn.mstar.store.entity.Product;
import cn.mstar.store.entity.ProductSpec;
import cn.mstar.store.entity.ProductSpecPrice;
import cn.mstar.store.functionutils.HtmlStyle;
import cn.mstar.store.functionutils.LogUtils;
import cn.mstar.store.functionutils.RequestUtils;
import cn.mstar.store.functionutils.WeiXinShare;
import cn.mstar.store.interfaces.HttpRequestCallBack;
import cn.mstar.store.interfaces.OnResultStatusListener;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.NetWorkUtil;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;
import cn.mstar.store.utils.VolleyRequest.HttpStringRequsetCallBack;
import cn.mstar.store.utils.VolleyRequest.LogonStatusLinstener;
import cn.mstar.store.view.ObservableScrollView;

/**
 * 产品详情界面
 *
 * @author Yangshao 2015-7-17
 */
public class ProductDetailsActivity extends AppCompatActivity implements
        OnDialogListener, OnClickListener, ObservableScrollView.Callbacks {


    private final String TAG="ProductDetailsActivity";
    public static final int SET_TO_FAVORITE_SUCCESS = 221;
    public static final int SET_TO_FAVORITE_FAILURE = 222;
    // 分享按钮
    private ImageView shareIV;
    // 分享窗口
    private SharePopup popup;
    private RelativeLayout layout_root;
    private Product product;
    private String productinfoURL;
    private ViewPager viewPager;
    private LayoutInflater inflater;
    private View item;
    //private ImageView image;
    private MyAdapter adapter;// viewpager适配器
    private ImageView titleBack;
    private TextView titleName;
    private TextView productName, productPrice;// 产品名称,价格
    private LinearLayout normsLayout;// 规格行
    private TextView normsTV;// 规格文字
    private LinearLayout btnAddcart, btnBuyNow;// 加入购物车 立即购买
    private String norm = "";// 规格字符串
    private int buyNum = 1;// 购买数量默认1
    private TextView indicatorTV;//展示图片指示器
    private int[] checkedIds;
    private ImageView iv_collect;

    private boolean hasContentChanged = false;
    private BuyProductEntity buyProductEntity;
    private MyApplication app;

    private CustomWebview webview;
    private CompatibilityScrollListView listView;
    private RadioGroup radioGroup;

    private Dialog mAlertDialog;

    private String shopid;
    private int proId;

    @Bind(R.id.share_btn)
    ImageView share;

    //主页图片
    String[] getPics;

    //进入商店和联系客服
    private LinearLayout go_comment_list;
    private LinearLayout go_store_button;
    private LinearLayout entrance_store_button;
    private LinearLayout contacet_service_button;
    //	@Bind(R.id.frame_product_details_viewpager) FrameLayout frame_product_details_viewpager;
    //新商品个数
    public int storeNewPropectCount = 10;
    public int storeGoodsPrCount = 10;
    private TextView tv_new_product_count, tv_cx_product_count;


    public void initScrollView() {
        stopView = (View) findViewById(R.id.stopView);
        scrollView = (ObservableScrollView) findViewById(R.id.sv_bottom);
        scrollView.setCallbacks(this);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onScrollChanged(scrollView.getScrollY());
                    }
                });
        // 滚动范围
        scrollView.scrollTo(0, 0);
        scrollView.smoothScrollTo(0, 0);//设置scrollView默认滚动到顶部

    }

    private View stopView;
    private ObservableScrollView scrollView;

    @SuppressLint("NewApi")
    @Override
    public void onScrollChanged(int scrollY) {
        ((LinearLayout) findViewById(R.id.stickyView))
                .setTranslationY(Math.max(stopView.getTop(), scrollY));

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent() {

    }


    public void loginToServer() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);
        app = (MyApplication) getApplication();
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        initView();
        initIntentData();
        initScrollView();


        initView1();
        // we will init it with the gotten data.
//		initBottomFragment ();
    }


    @Override
    protected void onResume() {
        super.onResume();
        i_dismissProgressDialog();
        String tempUrl;
        if ("".equals(((MyApplication) getApplication()).tokenKey)) {
            tempUrl = productinfoURL;
        } else {
            tempUrl = productinfoURL + "&key=" + ((MyApplication) getApplication()).tokenKey;
        }
        getproductInfo(tempUrl);
    }


    /**
     * 获取 Intent  传入数据
     */
    private void initIntentData() {
        Intent intent = getIntent();
        if (MyAction.productListActivityAction.equals(intent.getAction())) {
            proId = intent.getExtras().getInt("proId");
            productinfoURL = AppURL.PRODUCTDETAIL_URL + "&proid=" + proId;
        } else if (MyAction.fromProductManage.equals(intent.getAction())) {
            proId = intent.getExtras().getInt("proId");
            int ifWholesale = intent.getExtras().getInt("ifWholesale");
            productinfoURL = AppURL.PRODUCTDETAIL_URL + "&proid=" + proId + "&ifWholesale=" + ifWholesale;
        }
        LogUtils.e(TAG+"商品详情页面"+productinfoURL);
        //  标题
        titleName.setText(getString(R.string.product_details));
    }

    // 获得产品信息
    private void getproductInfo(String productinfoURL) {
        productinfoURL += "&size=" + (getScreenWidth() < 700 ? "500" : "800");
        LogUtils.e(TAG+"获取商品详情URL" + productinfoURL);
        if (NetWorkUtil.isNetworkConnected(this)) {
            i_showProgressDialog();
            VolleyRequest.GetCookieRequestPurePage(ProductDetailsActivity.this, productinfoURL, new HttpStringRequsetCallBack() {
                @Override
                public void onSuccess(String result) {
                    try {
                        L.i("wcl-->result:" + result);
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject dataJson = jsonObject.getJSONObject("data");
                        product = new Product();
                        product.setProId(dataJson.getInt("proId"));
                        product.setName(dataJson.getString("name"));
                        product.setDescURL(dataJson.getString("desc"));
                        product.setImageUrl(dataJson.getString("pic"));
                        product.setAttributes(dataJson.getString("attribute"));
                        product.setPrice(Double.parseDouble(dataJson.getString("price")));
                        product.setKindName(dataJson.getString("kindName"));
                        product.setWeight(dataJson.getString("weight"));
                        product.setBrandName(dataJson.getString("brandName"));
                        product.setArea(dataJson.getString("area"));
                        product.setHit(dataJson.getString("hit"));
                        product.setProNum(dataJson.getString("proNum"));
                        product.setHaveProSpecificationPrice(Boolean.parseBoolean(dataJson.getString("isHaveProSpecificationPrice")));
                        product.setStock(dataJson.optInt("stock"));

                        product.setStock(dataJson.optInt("stock"));
                        product.setIfWholesale(dataJson.optInt("ifWholesale"));
                        product.setIfmshop(dataJson.optInt("ifmshop"));
                        product.setMemberId(dataJson.optString("memberId"));
                        product.setTjrid(dataJson.optString("tjrid"));
                        storeNewPropectCount = dataJson.optInt("storeGoodsNew");
                        storeGoodsPrCount = dataJson.optInt("storeGoodsPr");
                        product.setHaveProFavorite(Boolean.parseBoolean(dataJson.getString("isHaveproFavorite")));
                        shopid = dataJson.getString("storeId");
//                        Product temp = product;
                        JSONArray picsJson = dataJson
                                .getJSONArray("pics");
                        getPics = new String[picsJson
                                .length()];
                        for (int i = 0; i < picsJson.length(); i++) {
                            getPics[i] = picsJson.optString(i);
                        }
                        Log.e(TAG,"数量："+getPics.length);
                        product.setPics(getPics);
                        // 往页面填充数据
                        L.d(" --- " + product.toString());
                        LoadingData(product);

                       initData1();
                    } catch (Exception e) {
                        CustomToast.makeToast(ProductDetailsActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
                        finish();
                    }
                    i_dismissProgressDialog();
                }

                @Override
                public void onFail(String error) {
                    CustomToast.makeToast(ProductDetailsActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
                    i_dismissProgressDialog();
                    finish();
                }
            });
        }
    }


    @SuppressLint("InflateParams")
    private void LoadingData(Product product) {

        tv_new_product_count.setText(storeNewPropectCount + " 个");
        tv_cx_product_count.setText(storeGoodsPrCount + " 个");
        final List<View> list = new ArrayList<View>();
        inflater = LayoutInflater.from(this);
        /**
         * 创建多个item （每一条viewPager都是一个item） 从服务器获取完数据（图片url地址） 后，再设置适配器
         */
        for (int i = 0; i < product.getPics().length; i++) {
            item = inflater.inflate(R.layout.item_product_viewpager, null);
            list.add(item);
        }
        // 创建适配器， 把组装完的组件传递进去
        adapter = new MyAdapter(list);
        L.e(viewPager.toString());
        viewPager.setAdapter(adapter);
        indicatorTV.setVisibility(View.VISIBLE);
        indicatorTV.setText("1" + "/" + list.size());
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                indicatorTV.setText(position + 1 + "/" + list.size());
                L.e("" + position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        if (product != null)
            L.d("pro::", product.toString());
        // 产品名称
        productName.setText(product.getName());
        // 产品价格
        productPrice.setText(Utils.formatedPrice(product.getPrice()));
        // 默认规格字
        if (product.isHaveProSpecificationPrice()) {
            normsTV.setText(getString(R.string.select_norms));
            normsLayout.setClickable(true);
//			iv_collect.setBackgroundResource(R.drawable.commoditydetails_icon_collection_down);
        } else {
            normsTV.setText(getString(R.string.no_norms));
            normsLayout.setClickable(false);
//			iv_collect.setBackgroundResource(R.drawable.commoditydetails_icon_collection_nor);
        }

        if (product.isHaveProFavorite()) {
            iv_collect.setImageResource(R.drawable.commoditydetails_icon_collection_down);
//            iv_collect.setBackgroundResource(R.drawable.commoditydetails_icon_collection_down);
        } else
            iv_collect.setImageResource(R.drawable.commoditydetails_icon_collection_nor);
//            iv_collect.setBackgroundResource(R.drawable.commoditydetails_icon_collection_nor);
        // loading the webview.
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
        webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        StorageUtils.getOwnCacheDirectory(ProductDetailsActivity.this,
                "MstarStore/Cache").getAbsolutePath();
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // remove a weird white line on the right size
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        String mime = "text/html";
        String encoding = "utf-8";
        webview.getSettings().setJavaScriptEnabled(true);
        L.i("wcl-->" + product.getDescURL());
        product.setDescURL(HtmlStyle.setHtmlStyle(this, product.getDescURL()));
        //product.setDescURL(HtmlUtils.parseHtml(product.getDescURL()));
        webview.loadDataWithBaseURL(null, product.getDescURL(), mime, encoding, null);
        webview.setVerticalScrollBarEnabled(false);

        Gson gson = new Gson();
        listData = gson.fromJson(product.getAttributes(), String[].class);
        listView.setAdapter(new DetailsListAdapter(ProductDetailsActivity.this, listData));
        listView.setVerticalScrollBarEnabled(false);
    }
    String[] listData;

    private int getScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }


    private int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


    LoadingDialog dialog;
    public void i_showProgressDialog() {
        dialog = new LoadingDialog(this);
        dialog.show();
    }

    public void i_dismissProgressDialog() {
        if (dialog != null) {
            dialog.cancel();
            dialog.dismiss();
            dialog = null;
        }
    }

    private void initView() {

        share.setVisibility(View.VISIBLE);
        share.setOnClickListener(this);

        tv_cx_product_count = (TextView) findViewById(R.id.id_tv_cx_product_count);
        tv_new_product_count = (TextView) findViewById(R.id.id_tv_new_product_count);
        webview = (CustomWebview) findViewById(R.id.webview);
        listView = (CompatibilityScrollListView) findViewById(R.id.listview);
        radioGroup = (RadioGroup) findViewById(R.id.bottom_view_radiogroup);
        shareIV = (ImageView) findViewById(R.id.iv_share);
        layout_root = (RelativeLayout) findViewById(R.id.layout_root);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        titleBack = (ImageView) findViewById(R.id.title_back);
        titleName = (TextView) findViewById(R.id.title_name);

        shareIV = (ImageView) findViewById(R.id.iv_share);
        layout_root = (RelativeLayout) findViewById(R.id.layout_root);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);
        productName = (TextView) findViewById(R.id.tv_product_name);
        productPrice = (TextView) findViewById(R.id.tv_price);

        btnAddcart = (LinearLayout) findViewById(R.id.btn_add_shopping_cart);
        btnBuyNow = (LinearLayout) findViewById(R.id.btn_buy_now_cart);

        indicatorTV = (TextView) findViewById(R.id.indicator_tv);

        btnAddcart.setOnClickListener(this);
        btnBuyNow.setOnClickListener(this);
        normsLayout = (LinearLayout) findViewById(R.id.norms_layout);
        normsTV = (TextView) findViewById(R.id.tv_norms);
        popup = new SharePopup(this, this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.detail_radiobutton) {
                    webview.setVisibility(View.VISIBLE);
                   // listView.setVisibility(View.GONE);
                } else {
                   // listView.setVisibility(View.VISIBLE);
                    webview.setVisibility(View.GONE);
                }
            }
        });
        radioGroup.check(R.id.detail_radiobutton);
        webview.setVisibility(View.VISIBLE);
       // listView.setVisibility(View.GONE);
        go_comment_list = (LinearLayout) findViewById(R.id.comment_layout);
        go_comment_list.setOnClickListener(this);
        go_store_button = (LinearLayout) findViewById(R.id.product_go_store);
        contacet_service_button = (LinearLayout) findViewById(R.id.contacet_service_button);
        entrance_store_button = (LinearLayout) findViewById(R.id.entrance_store_button);
        contacet_service_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog = new Dialog(ProductDetailsActivity.this);
                mAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mAlertDialog.setContentView(R.layout.product_contact_service);
                mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mAlertDialog.show();
                mAlertDialog.findViewById(R.id.product_detail_refuse).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                    }
                });
                mAlertDialog.findViewById(R.id.product_detail_accept).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getResources().getString(R.string.service_phone_number)));
                        startActivity(intent);
                        mAlertDialog.dismiss();
                    }
                });
            }
        });
        entrance_store_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this, StoreDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("shopid", shopid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        go_store_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this, StoreDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("shopid", shopid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        normsLayout.setOnClickListener(this);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);
        iv_collect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                L.d(" --- got object " + product.toString());
                RequestUtils.checkLogStatus(new LogonStatusLinstener() {
                    @Override
                    public void OK(String reason) {
                        if (product.isHaveProFavorite())
                            RequestUtils.deleteItemsFromFavorite(ProductDetailsActivity.this, String.valueOf(product.getProId()), Utils.getTokenKey((MyApplication) ProductDetailsActivity.this.getApplication()), new OnResultStatusListener() {
                                @Override
                                public void success(String result) {
                                    product.setHaveProFavorite(!product.isHaveProFavorite());
                                    hasContentChanged = !hasContentChanged;
                                    if (product.isHaveProFavorite()) {
//                                        iv_collect.setBackgroundResource(R.drawable.commoditydetails_icon_collection_down);
                                        iv_collect.setImageResource(R.drawable.commoditydetails_icon_collection_down);
                                        //										CustomToast.mToast(ProductDetailsActivity.this, "adding ok");
                                        CustomToast.mSystemToast(ProductDetailsActivity.this, getString(R.string.delete_favorite_error));
                                    } else {
//                                        iv_collect.setBackgroundResource(R.drawable.commoditydetails_icon_collection_nor);
                                        iv_collect.setImageResource(R.drawable.commoditydetails_icon_collection_nor);
                                        //										CustomToast.mToast(ProductDetailsActivity.this, "deleting ok");
                                        CustomToast.mSystemToast(ProductDetailsActivity.this, getString(R.string.delete_favorite_ok));
                                    }
                                }

                                @Override
                                public void failure(String error) {
                                    CustomToast.mSystemToast(ProductDetailsActivity.this, error);
                                }
                            });
                        else {
                            RequestUtils.addItemsFromFavorite(ProductDetailsActivity.this, String.valueOf(product.getProId()), Utils.getTokenKey((MyApplication) ProductDetailsActivity.this.getApplication()), new OnResultStatusListener() {

                                @Override
                                public void success(String result) {
                                    product.setHaveProFavorite(!product.isHaveProFavorite());
                                    hasContentChanged = !hasContentChanged;
                                    if (product.isHaveProFavorite()) {
//                                        iv_collect.setBackgroundResource(R.drawable.commoditydetails_icon_collection_down);
                                        iv_collect.setImageResource(R.drawable.commoditydetails_icon_collection_down);
                                        CustomToast.mSystemToast(ProductDetailsActivity.this, getString(R.string.adding_favorite_ok));
                                    } else {
//                                        iv_collect.setBackgroundResource(R.drawable.commoditydetails_icon_collection_nor);
                                        iv_collect.setImageResource(R.drawable.commoditydetails_icon_collection_nor);
                                        CustomToast.mSystemToast(ProductDetailsActivity.this, getString(R.string.adding_favorite_error));
                                    }
                                }
                                @Override
                                public void failure(String error) {
                                    CustomToast.mSystemToast(ProductDetailsActivity.this, error);
                                }
                            });
                        }
                    }

                    @Override
                    public void NO() {

                        // 需要首先登陆
                        loginToServer();
                    }
                });


            }
        });
    }

    @Override
    public void onShareWechat() {
    }

    @Override
    public void onShareFriends() {
    }

    @Override
    public void onShareSina() {
    }

    @Override
    public void onShareQQ() {
    }

    @Override
    public void onShareQQZone() {
    }

    /**
     * 适配器，负责装配 、销毁 数据 和 组件 。
     */
    private class MyAdapter extends PagerAdapter {
        private List<View> mList;

        public MyAdapter(List<View> list) {
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        /**
         * Remove a page for the given position. 滑动过后就销毁 ，销毁当前页的前一个的前一个的页！
         * instantiateItem(View container, int position) This method was
         * deprecated in API level . Use instantiateItem(ViewGroup, int)
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mList.get(position);
            ImageView image = ((ImageView) view.findViewById(R.id.image));
            if (image==null){
                Log.e(TAG,"image==null");
                return mList.get(position);
            }
            if (getPics==null) {
                Log.e(TAG,"getPics==null");
                return mList.get(position);
            }
            ImageLoader.getInstance().displayImage(getPics[position],
                    image, ImageLoadOptions.getOptions());
            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductDetailsActivity.this,
                            ImageBrowserActivity.class);
                    intent.putExtra("photos", getPics);
                    L.e("size:" + getPics.length);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
            container.removeView(mList.get(position));
            container.addView(mList.get(position));
            return mList.get(position);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                LogUtils.e(TAG+"onActivityResult"+requestCode);
                buyProductEntity = (BuyProductEntity) data.getSerializableExtra("buyProduct");
                if (buyProductEntity != null)
                product.setPrice(buyProductEntity.getProduct().getPrice());
                product.setStock(buyProductEntity.getProduct().getStock());
                product.setProSpecialID(buyProductEntity.getProduct().getProSpecialID());
                buyProductEntity.setProduct(product);
                productPrice.setText(Utils.formatedPrice(product.getPrice()));
                // 不让改变选择规格那部分二
//				normsTV.setText(buyProductEntity.getNorms());
                checkedIds = data.getIntArrayExtra("checkedIds");
                buyNum = buyProductEntity.getBuyNum();
                break;
        }
    }

    @Override
    public void finish() {
        Intent i = new Intent();
        i.putExtra(Constants.START_ACTIVITY_FOR_RESULT_KEY, hasContentChanged);
        this.setResult(2, i);
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private PopupWindow popView;
    WindowManager.LayoutParams lp;
    private String shareUrl;
    Bitmap bit;

    @Override
    public void onClick(View v) {
        shareUrl = "http://m.fanershop.com/index.php?m=QxWeb&a=goods&id=" + proId + "&code=001464f690bb83265732312739ce8b1y&state=STATE"
                + (product.getIfmshop() == 1 ? "&tjrid=" + product.getMemberId() : "");
        switch (v.getId()) {
            case R.id.title_back:// 返回
                finish();
                break;
            case R.id.btn_commodity_plus: // 添加数量
                if (goodsNum < goodsStock)
                    ++goodsNum;
                showGoodsNumTV.setText("" + goodsNum);
                break;
            case R.id.btn_commodity_minus:// 减少数量
                --goodsNum;
                if (goodsNum < 1)
                        goodsNum = 1;
                showGoodsNumTV.setText("" + goodsNum);
                break;
            case R.id.norms_layout:// 跳转到产品规格页
                addElementToShoppingCart();
                break;
            case R.id.btn_add_shopping_cart:// 加入购物车
                // add elements to shopping cart.
                if (product.getIfWholesale() == 1) {
                    showMessageDialog();
                } else {
                    addElementToShoppingCart();
                }
                break;
            case R.id.btn_buy_now_cart:// 立即购买
                if (product.getStock() == 0) {
                    CustomToast.makeToast(this, "库存不足", Toast.LENGTH_SHORT);
                    return;
                }

                if ("".equals(app.tokenKey)) {
                    loginToServer();
                    return;
                }

                if (!product.isHaveProSpecificationPrice()) {
//                    BuyProductEntity buyProductEntity = new BuyProductEntity();
//                    buyProductEntity.setProduct(product);
//                    buyProductEntity.setBuyNum(1);
//                    Intent intent = new Intent(this, ConfirmIndentActivity.class);
//                    intent.setAction(MyAction.productDetailsActivityAction);
//                    intent.putExtra("buyProduct", buyProductEntity);
//                    intent.putExtra("ifWholesale", product.getIfWholesale());
//                    startActivity(intent);
                   // return;


                    Intent intent1 = new Intent(this, ConfirmIndentActivity.class);
                    intent1.setAction(MyAction.selectCommodityActivityAction);
                    BuyProductEntity buyProductEntity1 = new BuyProductEntity();
                    buyProductEntity1.setBuyNum(goodsNum);//设置购买数量
                    buyProductEntity1.setProduct(product);//产品信息
                    buyProductEntity1.setNorms(Arrays.toString(norms));//规格参数
                    intent1.putExtra("buyProduct", buyProductEntity1);
                    startActivity(intent1);
                    return;
                }

                // 直接调到选择规格见面，然后
                Intent intent = new Intent(this, SelectCommodityActivity.class);
                intent.putExtra("proId", product.getProId());
                intent.putExtra("ifWholesale", product.getIfWholesale());
                intent.putExtra("listData",listData);
                intent.putExtra("descURL", product.getDescURL());
                intent.setAction(MyAction.directlyGetToPayAction);
                startActivity(intent);
                break;
            case R.id.comment_layout:
                Intent intent2 = new Intent(this, CommentListActivity.class);
                intent2.putExtra("proId", proId + "");
                startActivity(intent2);
                break;
            case R.id.id_lay_sharweixin:
                //修改
                bit = ImageLoader.getInstance().loadImageSync(getPics[0]);
                WeiXinShare.wechatShare(this, shareUrl, 0, bit);//分享到微信好友
                if (popView != null)
                    popView.dismiss();
                break;
            case R.id.id_lay_sharweixinfriend:
                WeiXinShare.wechatShare(this, shareUrl, 1, bit);//分享到微信好友
                if (popView != null)
                    popView.dismiss();
                break;
            case R.id.share_btn:
                LogUtils.e("showDialog");
                View convertView = getLayoutInflater().inflate(R.layout.share_layout, null, false);
                convertView.findViewById(R.id.id_lay_sharweixin).setOnClickListener(this);
                convertView.findViewById(R.id.id_lay_sharweixinfriend).setOnClickListener(this);
                popView = new PopupWindow(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popView.setTouchable(true);
                // 设置背景颜色变暗
                lp = getWindow().getAttributes();
                lp.alpha = 0.3f;
                getWindow().setAttributes(lp);
                popView.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        lp.alpha = 1f;
                        getWindow().setAttributes(lp);
                        return false;
                    }
                });
                popView.setAnimationStyle(R.style.share_anim_style);
                popView.setBackgroundDrawable(getResources().getDrawable(R.color.white));
                popView.showAtLocation(btnBuyNow, Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    private void showMessageDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(R.layout.message_dialog_layout);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        message.setText("处于拿货状态，请直接购买");
        dialog.findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addElementToShoppingCart() {
        if (app == null || "".equals(app.tokenKey)) {
            loginToServer();
            return;
        }
        if (product != null) {
            if (!product.isHaveProSpecificationPrice()) {
                if (product.getStock() == 0) {
                    CustomToast.makeToast(this, "库存不足", Toast.LENGTH_SHORT);
                    return;
                }
                i_showProgressDialog();
                ProAndSpecialIdz item = new ProAndSpecialIdz(product.getProId(), product.getProSpecialID(), 1);
                RequestUtils.addInShoppingCart( new ProAndSpecialIdz[]{item},
                        Utils.getTokenKey((MyApplication) ProductDetailsActivity.this.getApplication()), new OnResultStatusListener() {
                            @Override
                            public void success(String result) {
                                // 添加成功
                                CustomToast.makeToast(ProductDetailsActivity.this, getString(R.string.add_shopping_cart_success), Toast.LENGTH_SHORT);
                                i_dismissProgressDialog();
                            }
                            @Override
                            public void failure(String error) {
                                switch (error) {
                                    case "0":
                                        // 添加失败
                                        CustomToast.makeToast(ProductDetailsActivity.this, getString(R.string.add_shopping_cart_failure), Toast.LENGTH_SHORT);
                                        break;
                                    case "-1":
                                        // 网络失败
                                        CustomToast.makeToast(ProductDetailsActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
                                        break;
                                    default:
                                        CustomToast.makeToast(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT);
                                        break;
                                }
                                i_dismissProgressDialog();
                            }
                        });

                return;
            }
            Intent intent = new Intent(this, SelectCommodityActivity.class);
            if (checkedIds != null){
                intent.putExtra("checkedIds", checkedIds);
            }
            if (buyProductEntity != null){
                intent.putExtra("buyNums", buyProductEntity.getBuyNum());
            }else {
                intent.putExtra("buyNums", 1);
            }
            intent.putExtra("proId", product.getProId());
            intent.putExtra("ifWholesale", product.getIfWholesale());
            intent.putExtra("listData",listData);
            intent.putExtra("descURL", product.getDescURL());

            intent.setAction(MyAction.productDetailsActivityAction);
            startActivityForResult(intent, 1);
        }
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
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }









    private ImageView btnAddGoods, btnMinusGoods;// 添加减去商品数量按钮
    private ImageView goodsIconIV;// 商品图标
    private TextView goodsNameTV;// 商品名称
    private TextView goodsPriceTV;// 商品价格
    private int goodsStock;// 商品库存
    private TextView goodsStockTV;// 商品库存TV
    private int InitId = 0x1000;// 初始id
    private TextView showGoodsNumTV;// 购买数量
    String URL;
    int goodsNum=1;

    private void initView1() {
        goodsIconIV = (ImageView) findViewById(R.id.commodity_icon);
        goodsNameTV = (TextView) findViewById(R.id.commodity_name);
        goodsPriceTV = (TextView) findViewById(R.id.commodity_price);
        normsLayout = (LinearLayout) findViewById(R.id.goods_norms_layout);
        goodsStockTV = (TextView) findViewById(R.id.commodity_stock);
        showGoodsNumTV = (TextView) findViewById(R.id.btn_commodity_number_display);

        btnAddGoods = (ImageView) findViewById(R.id.btn_commodity_plus);
        btnMinusGoods = (ImageView) findViewById(R.id.btn_commodity_minus);
        btnAddGoods.setOnClickListener(this);
        btnMinusGoods.setOnClickListener(this);
    }


    //新页面
    private void initData1() {
        URL = AppURL.CUSTOM_MADE_URL + "&proid=" + proId;
        normsLayout.removeAllViews();
        getProductNorms(URL);
    }

    private List<ProductSpec> productSpecs;
    private List<ProductSpecPrice>  productSpecPrices;

    // 获取商品规格
    private void getProductNorms(String URL) {
        productSpecs = new ArrayList<>();
        if (NetWorkUtil.isNetworkConnected(this)) {
            VolleyRequest.GetRequest(this, URL, new HttpRequestCallBack() {
                @Override
                public void onSuccess(JSONObject jsonObj) {
                    if (jsonObj != null) {
                        try {
                            JSONObject productJson = (jsonObj.getJSONObject("data")).getJSONObject("product");
                            JSONArray productSpecListJson = (jsonObj.getJSONObject("data")).optJSONArray("productSpecList");
                            JSONArray productSpecPriceJson = (jsonObj.getJSONObject("data")).optJSONArray("productSpecPrice");
                            product = new Product();
                            product.setProId(productJson.getInt("proId"));
                            product.setName(productJson.getString("name"));
                            product.setImageUrl(productJson.getString("pic"));
                            product.setPrice(productJson.getDouble("price"));
                            product.setProSpecialID(productJson.getInt("proSpecialID"));
                            product.setStock(productJson.optInt("stock"));
                            product.setIfWholesale(productJson.optInt("ifWholesale"));
                            if (productSpecListJson != null) {
                                for (int i = 0; i < productSpecListJson.length(); i++) {
                                    ProductSpec pSpec = new ProductSpec();
                                    pSpec.setId(productSpecListJson.getJSONObject(i).getString("id"));
                                    pSpec.setTitle(productSpecListJson.getJSONObject(i).getString("title"));
                                    pSpec.setAttrName(productSpecListJson.getJSONObject(i).getString("attrName"));
                                    int normsLength = productSpecListJson.getJSONObject(i).getJSONArray("specSubTitle").length();
                                    String[] specSubTitle = new String[normsLength];
                                    for (int j = 0; j < normsLength; j++) {
                                        specSubTitle[j] = productSpecListJson.getJSONObject(i).getJSONArray("specSubTitle").getString(j);
                                    }
                                    pSpec.setSpecSubTitle(specSubTitle);
                                    productSpecs.clear();
                                    productSpecs.add(pSpec);
                                }
                            }
                            productSpecPrices = new ArrayList<>();
                            if (productSpecPriceJson != null) {
                                for (int i = 0; i < productSpecPriceJson.length(); i++) {
                                    ProductSpecPrice pSpecPrice = new ProductSpecPrice();
                                    pSpecPrice.setId(productSpecPriceJson.getJSONObject(i).getInt("id"));
                                    pSpecPrice.setAttr1(productSpecPriceJson.getJSONObject(i).getString("attrs1"));
                                    pSpecPrice.setAttr2(productSpecPriceJson.getJSONObject(i).getString("attrs2"));
                                    pSpecPrice.setAttr3(productSpecPriceJson.getJSONObject(i).getString("attrs3"));
                                    pSpecPrice.setPrice(productSpecPriceJson.getJSONObject(i).getDouble("price"));
                                    pSpecPrice.setStock(productSpecPriceJson.getJSONObject(i).optInt("stock"));
                                    productSpecPrices.add(pSpecPrice);
                                }
                            }
                            LoadingData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(String failresult) {
                }
            });
        }
    }

    private void LoadingData() {
        ImageLoader.getInstance().displayImage(product.getImageUrl(), goodsIconIV, ImageLoadOptions.getOptions());
        goodsNameTV.setText(product.getName());
        goodsPriceTV.setText(getString(R.string.renminbi) + product.getPrice());
        goodsStock = product.getStock();
        goodsStockTV.setText(getString(R.string.stock) + product.getStock() + getString(R.string.piece));
        showGoodsNumTV.setText("" + goodsNum);
        // 动态加载商品规格布局
        LoadingGoodsLayout();
    }


    private RadioGroup[] radioGroups;// 存储
    private String[] query;// 从productSpecPrices查询价格查询语句字符串数组
    private String[] norms;// 规格字符数组
    boolean isflag = false;
    @SuppressLint("NewApi")
    private void LoadingGoodsLayout() {
        radioGroups = new RadioGroup[productSpecs.size()];
        query = new String[productSpecs.size()];
        norms = new String[productSpecs.size()];
        if (checkedIds == null) {
            isflag = true;
            checkedIds = new int[productSpecs.size()];
        }
        for (int i = 0; i < productSpecs.size(); i++) {
            if (inflater == null)
                inflater = LayoutInflater.from(this);
            View inf_rel = inflater.inflate(R.layout.goods_norms_menus, null);
            LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ViewGroup.LayoutParams radiolp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView normsName = (TextView) inf_rel.findViewById(R.id.goods_norms_tv);
            normsName.setText(productSpecs.get(i).getTitle());
            MyRadioGroup normsGroup = (MyRadioGroup) inf_rel.findViewById(R.id.goods_norms_group);
            normsGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener(i));
            for (int j = 0; j < productSpecs.get(i).getSpecSubTitle().length; j++) {
                RadioButton view = new RadioButton(this);
                if (j == 0) {
                    view.setTag(i);
                }
                view.setId(++InitId);
                view.setText(productSpecs.get(i).getSpecSubTitle()[j]);
                view.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                view.setGravity(Gravity.CENTER);
                view.setPadding(20, 20, 20, 20);
                view.setBackgroundResource(R.drawable.commodity_category_select);
                view.setLayoutParams(radiolp);
                normsGroup.addView(view);
                if (view.getId() == checkedIds[i]) {
                    L.e("radioButton0:" + view.getId());
                    view.setChecked(true);
                }
            }
            radioGroups[i] = normsGroup;
            normsLayout.addView(inf_rel, llparams);
            if (!isflag)
                initRadioButton(i, checkedIds[i]);
        }
        if (radioGroups != null && radioGroups.length > 0 && isflag) {
            for (int i = 0; i < radioGroups.length; i++) {
                RadioGroup rg = radioGroups[i];
                RadioButton rb = (RadioButton) rg.findViewWithTag(i);
                if (rb != null) {
                    rb.setChecked(true);
                    initRadioButton(i, rb.getId());
                }
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initRadioButton(int position, int viewId) {
        Class clazz = RadioGroup.class;
        MyOnCheckedChangeListener listener = null;
        try {
            Field f = clazz.getDeclaredField("mOnCheckedChangeListener");
            f.setAccessible(true);
            listener = (MyOnCheckedChangeListener) f.get(radioGroups[position]);
            listener.onCheckedChanged(radioGroups[position], viewId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            L.e("wcl-->" + e.toString());
        }
    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        private int i;// 分辨哪个RadioGroup

        public MyOnCheckedChangeListener(int i) {
            this.i = i;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
            L.e("radioButton:" + checkedId);
            norms[i] = productSpecs.get(i).getTitle() + " "
                    + radioButton.getText().toString() + "  ";
            checkedIds[i] = radioButton.getId();
            query[i] = radioButton.getText().toString();
            productName.setText( query[i]);
            L.e("query[i]:" + query[i]);
            String querySQL = "";
            for (String qu : query) {
                if (qu != null)
                    querySQL += qu;
            }
            // 根据选择规格查询价格
            queryPrice(querySQL);
        }
    }

    private void queryPrice(String querySQL) {
        for (ProductSpecPrice pSpecPrice : productSpecPrices) {
            if (querySQL.equals(pSpecPrice.toString())) {
                L.e("price:" + pSpecPrice.getPrice());
                // 设置价格
                goodsPriceTV.setText(getString(R.string.renminbi)
                        + pSpecPrice.getPrice());
                goodsStock = pSpecPrice.getStock();
                goodsStockTV.setText(getString(R.string.stock) + pSpecPrice.getStock()
                        + getString(R.string.piece));
                product.setPrice(pSpecPrice.getPrice());
                product.setStock(pSpecPrice.getStock());
                product.setProSpecialID(pSpecPrice.getId());
                productPrice.setText(""+pSpecPrice.getPrice());
            }
        }
    }

}
