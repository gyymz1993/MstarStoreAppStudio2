package cn.mstar.store.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.adapter.DetailsListAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CompatibilityScrollListView;
import cn.mstar.store.customviews.CustomWebview;
import cn.mstar.store.customviews.ErrorHintView;
import cn.mstar.store.customviews.ErrorHintView.OperateListener;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.customviews.MyRadioGroup;
import cn.mstar.store.db.entityToSave.ProAndSpecialIdz;
import cn.mstar.store.entity.BuyProductEntity;
import cn.mstar.store.entity.Product;
import cn.mstar.store.entity.ProductSpec;
import cn.mstar.store.entity.ProductSpecPrice;
import cn.mstar.store.functionutils.RequestUtils;
import cn.mstar.store.interfaces.HttpRequestCallBack;
import cn.mstar.store.interfaces.OnResultStatusListener;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.NetWorkUtil;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * 选择商品页
 *
 * @author wenjundu 2015-7-20
 */
public class SelectCommodityActivity extends BaseActivity implements
        OnClickListener {

    private ImageView titleBack; // 返回按钮
    private TextView titleName;// 标题
    private int InitId = 0x1000;// 初始id
    private List<ProductSpec> productSpecs;
    private List<ProductSpecPrice> productSpecPrices;
    private LayoutInflater inflater;
    private LinearLayout normsLayout;// 规格Layout
    private ImageView goodsIconIV;// 商品图标
    private TextView goodsNameTV;// 商品名称
    private TextView goodsPriceTV;// 商品价格
    private Product product;// 商品类
    private ImageView btnAddGoods, btnMinusGoods;// 添加减去商品数量按钮
    private TextView showGoodsNumTV;// 购买数量
    private int goodsNum = 1;// 默认购买产品数量
    private RadioGroup[] radioGroups;// 存储
    private String[] query;// 从productSpecPrices查询价格查询语句字符串数组
    private Button btnAddCart, btnBuyNow;// 加入购物车,立即购买
    private TextView goodsStockTV;// 商品库存TV
    private int goodsStock;// 商品库存
    private String[] norms;// 规格字符数组
    private int[] checkedIds;//选中id数组
    private String URL;
    private ErrorHintView hintview;
    private CompatibilityScrollListView listView;
    private CustomWebview webview;
    private Button btnSelectRing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_commodity);
        MyApplication.getInstance().addActivity(this);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        initView();
        initData();
        initWeb();
    }

    private void initWeb() {
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
        webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        StorageUtils.getOwnCacheDirectory(this,
                "MstarStore/Cache").getAbsolutePath();
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // remove a weird white line on the right size
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        String mime = "text/html";
        String encoding = "utf-8";
        webview.getSettings().setJavaScriptEnabled(true);
        //L.i("wcl-->" + product.getDescURL());
        //  product.setDescURL(HtmlStyle.setHtmlStyle(this, descURL));
        //product.setDescURL(HtmlUtils.parseHtml(product.getDescURL()));
        webview.loadDataWithBaseURL(null, descURL, mime, encoding, null);
        webview.setVerticalScrollBarEnabled(false);
    }

    private void initData() {
        Intent intent = getIntent();
        // 从商品详情页跳转过来
        if (MyAction.productDetailsActivityAction.equals(intent.getAction())) {
            int proId = intent.getExtras().getInt("proId");
            checkedIds = intent.getIntArrayExtra("checkedIds");
            goodsNum = intent.getExtras().getInt("buyNums");

            if (goodsNum == 0)
                goodsNum = 1;
            URL = AppURL.CUSTOM_MADE_URL + "&proid=" + proId;
            btnBuyNow.setVisibility(View.GONE);
            L.e("URL:" + URL);
        } else if (MyAction.directlyGetToPayAction.equals(intent.getAction())) {
            int proId = intent.getExtras().getInt("proId");
            checkedIds = intent.getIntArrayExtra("checkedIds");
            goodsNum = intent.getExtras().getInt("buyNums");
            if (goodsNum == 0)
                goodsNum = 1;
            URL = AppURL.CUSTOM_MADE_URL + "&proid=" + proId;
            btnAddCart.setVisibility(View.GONE);
        }
        showGoodsNumTV.setText("" + goodsNum);
        productSpecs = new ArrayList<>();
        // 获取商品规格
        int ifWholesale = intent.getExtras().getInt("ifWholesale");
        listData = intent.getExtras().getStringArray("listData");
        descURL = intent.getExtras().getString("descURL");
        URL = URL + "&ifWholesale=" + ifWholesale;
        L.i("wcl-->" + URL);
        getProductNorms(URL);

        listView.setAdapter(new DetailsListAdapter(this, listData));
        listView.setVerticalScrollBarEnabled(false);
    }

    String descURL;
    LoadingDialog dialog;
    String[] listData;

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


    // 获取商品规格
    private void getProductNorms(String URL) {
        if (NetWorkUtil.isNetworkConnected(this)) {
            showLoading(1);
            VolleyRequest.GetRequest(this, URL, new HttpRequestCallBack() {

                @Override
                public void onSuccess(JSONObject jsonObj) {
                    hintview.close();
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
                            CustomToast.makeToast(SelectCommodityActivity.this, getString(R.string.came_accross_error), Toast.LENGTH_SHORT);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(String failresult) {
                    hintview.close();
                }
            });
        } else {
            showLoading(2);
        }
    }

    private void LoadingData() {
        ImageLoader.getInstance().displayImage(product.getImageUrl(), goodsIconIV, ImageLoadOptions.getOptions());
        goodsNameTV.setText(product.getName());
        goodsPriceTV.setText(getString(R.string.renminbi) + product.getPrice());
        goodsStock = product.getStock();
        goodsStockTV.setText(getString(R.string.stock) + product.getStock() + getString(R.string.piece));
        // 动态加载商品规格布局
        LoadingGoodsLayout();
    }

    @SuppressLint("NewApi")
    private void LoadingGoodsLayout() {
        radioGroups = new RadioGroup[productSpecs.size()];
        query = new String[productSpecs.size()];
        norms = new String[productSpecs.size()];
        boolean isflag = false;
        if (checkedIds == null) {
            isflag = true;
            checkedIds = new int[productSpecs.size()];
        }
        for (int i = 0; i < productSpecs.size(); i++) {
            if (inflater == null)
                inflater = LayoutInflater.from(this);
            View inf_rel = inflater.inflate(R.layout.goods_norms_menus, null);
            LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            LayoutParams radiolp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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

    private class MyOnCheckedChangeListener implements OnCheckedChangeListener {
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
            }
        }
    }


    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }

    private void initView() {
        inflater = LayoutInflater.from(this);
        titleBack = (ImageView) findViewById(R.id.title_back);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText(getString(R.string.select_commodity));
        goodsIconIV = (ImageView) findViewById(R.id.commodity_icon);
        goodsNameTV = (TextView) findViewById(R.id.commodity_name);
        goodsPriceTV = (TextView) findViewById(R.id.commodity_price);
        normsLayout = (LinearLayout) findViewById(R.id.goods_norms_layout);
        btnAddGoods = (ImageView) findViewById(R.id.btn_commodity_plus);
        btnMinusGoods = (ImageView) findViewById(R.id.btn_commodity_minus);
        showGoodsNumTV = (TextView) findViewById(R.id.btn_commodity_number_display);
        btnBuyNow = (Button) findViewById(R.id.btn_buy_now_cart);
        btnAddCart = (Button) findViewById(R.id.btn_add_shopping_cart);
        goodsStockTV = (TextView) findViewById(R.id.commodity_stock);

        hintview = (ErrorHintView) findViewById(R.id.hintview);

        listView = (CompatibilityScrollListView) findViewById(R.id.listview);
        webview = (CustomWebview) findViewById(R.id.webview);
        btnSelectRing = (Button) findViewById(R.id.id_btn_slectring);
        btnSelectRing.setOnClickListener(this);
        btnBuyNow.setOnClickListener(this);
        btnAddCart.setOnClickListener(this);
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);
        btnAddGoods.setOnClickListener(this);
        btnMinusGoods.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            Boolean isbackNorms = true;//是否返回规格,全部选择了才返回规格
            if (norms == null)
                return super.onKeyDown(keyCode, event);
            for (String no : norms) {
                if (no == null)
                    isbackNorms = false;
            }
            Intent intent = new Intent();
            BuyProductEntity buyProductEntity = new BuyProductEntity();
            buyProductEntity.setProduct(product);
            if (isbackNorms)
                buyProductEntity.setNorms(Arrays.toString(norms));
            buyProductEntity.setBuyNum(goodsNum);
            if (checkedIds != null) {
                intent.putExtra("checkedIds", checkedIds);
            }
            intent.putExtra("buyProduct", buyProductEntity);
            setResult(1, intent);// 从productDetails传递过来的请求码为1
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showLoading(int i) {
        hintview.setVisibility(View.GONE);
        switch (i) {
            case 1:
                hintview.loadingData();
                break;

            case 2:
                hintview.netError(new OperateListener() {

                    @Override
                    public void operate() {
                        showLoading(1);
                        // 获取商品规格
                        getProductNorms(URL);
                    }
                });
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                Boolean isbackNorms = true;//是否返回规格,全部选择了才返回规格
                if (norms == null) {
                    finish();
                }

                try {
                    for (String no : norms) {
                        if (no == null)
                            isbackNorms = false;
                    }
                } catch (Exception e) {
                    isbackNorms = false;
                }

                Intent intent = new Intent();
                BuyProductEntity buyProductEntity = new BuyProductEntity();
                buyProductEntity.setProduct(product);
                if (isbackNorms) {
                    buyProductEntity.setNorms(Arrays.toString(norms));
                }
                buyProductEntity.setBuyNum(goodsNum);
                if (checkedIds != null) {
                    intent.putExtra("checkedIds", checkedIds);
                }
                intent.putExtra("buyProduct", buyProductEntity);
                setResult(1, intent);// 从productDetails传递过来的请求码为1
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
            case R.id.btn_buy_now_cart:// 立即购买
                if (goodsStock == 0) {
                    CustomToast.makeToast(this, "库存不足", Toast.LENGTH_SHORT);
                    return;
                }
                for (RadioGroup radioGroup : radioGroups) {
                    if (radioGroup.getCheckedRadioButtonId() == -1) {// 有radiogroup未被选中
                        CustomToast.makeToast(this, getString(R.string.please_select_norms), Toast.LENGTH_SHORT);
                        return;
                    }
                }

                RequestUtils.checkLogStatus(new VolleyRequest.LogonStatusLinstener() {
                    @Override
                    public void OK(String token) {
                        Intent intent1 = new Intent(SelectCommodityActivity.this, ConfirmIndentActivity.class);
                        intent1.setAction(MyAction.selectCommodityActivityAction);
                        BuyProductEntity buyProductEntity1 = new BuyProductEntity();
                        buyProductEntity1.setBuyNum(goodsNum);//设置购买数量
                        buyProductEntity1.setProduct(product);//产品信息
                        buyProductEntity1.setNorms(Arrays.toString(norms));//规格参数
                        intent1.putExtra("buyProduct", buyProductEntity1);
                        startActivity(intent1);
                    }

                    @Override
                    public void NO() {
                        loginToServer();
                    }
                });
                // verifications before sending the request.

                break;
            case R.id.btn_add_shopping_cart:// 加入购物车

                if (product.getIfWholesale() == 1) {
                    showMessageDialog();
                    return;
                }

                if (goodsStock == 0) {
                    CustomToast.makeToast(this, "库存不足", Toast.LENGTH_SHORT);
                    return;
                }

                for (RadioGroup radioGroup : radioGroups) {
                    if (radioGroup.getCheckedRadioButtonId() == -1) {// 有radiogroup未被选中
                        CustomToast.makeToast(this, getString(R.string.please_select_norms), Toast.LENGTH_SHORT);
                        return;
                    }
                }
                addElementToCart(goodsNum);
                break;
            case R.id.id_btn_slectring:
                Intent intent1=new Intent(this,ChooseRingActivity.class);
                intent1.setAction(MyAction.ringSelect);
                startActivity(intent1);
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


    private void addElementToCart(final int buyNum) {
        RequestUtils.checkLogStatus(new VolleyRequest.LogonStatusLinstener() {
            @Override
            public void OK(String reason) {
                // 登录~可以发出去请求
                L.d("登录后~  spec " + product.getProSpecialID() + " proId " + product.getProId() + " count = " + buyNum);
                ProAndSpecialIdz item = new ProAndSpecialIdz(product.getProId(), product.getProSpecialID(), buyNum);
                RequestUtils.addInShoppingCart(new ProAndSpecialIdz[]{item},
                        Utils.getTokenKey((MyApplication) SelectCommodityActivity.this.getApplication()), new OnResultStatusListener() {

                            @Override
                            public void success(String result) {
                                // 添加成功
                                CustomToast.makeToast(SelectCommodityActivity.this, getString(R.string.add_shopping_cart_success), Toast.LENGTH_SHORT);
                                i_dismissProgressDialog();
                                finish();
                            }

                            @Override
                            public void failure(String error) {
                                switch (error) {
                                    case "0":
                                        // 添加失败
                                        CustomToast.makeToast(SelectCommodityActivity.this, getString(R.string.add_shopping_cart_failure), Toast.LENGTH_SHORT);
                                        break;
                                    case "-1":
                                        // 网络失败
                                        CustomToast.makeToast(SelectCommodityActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
                                        break;
                                    default:
                                        CustomToast.makeToast(SelectCommodityActivity.this, error, Toast.LENGTH_SHORT);
                                        break;
                                }
                                i_dismissProgressDialog();
                            }
                        });

            }

            @Override
            public void NO() {
                loginToServer();
            }
        });
    }

    private void loginToServer() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setAction(MyAction.logForShoppingCart);
        startActivityForResult(loginIntent, 11);
    }


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        finish();
        overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
