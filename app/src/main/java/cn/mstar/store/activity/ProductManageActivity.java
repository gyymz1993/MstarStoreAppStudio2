package cn.mstar.store.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.adapter.ManageProductAdapter;
import cn.mstar.store.adapter.ShopClassifyAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.entity.GoodsClassify;
import cn.mstar.store.entity.MyStoreProductEntity;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.interfaces.AdapterCallBack;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by 1 on 2016/1/6.
 */
public class ProductManageActivity extends BaseActivity implements PullToRefreshView.OnHeaderRefreshListener,
        PullToRefreshView.OnFooterRefreshListener, AdapterCallBack {


    private final String TAG="ProductManageActivity";
    private static final int PULL_REFRESH = 1;
    private static final int PULL_LOAD = 2;

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.shop_search_edt)
    EditText inputTxt;
    @Bind(R.id.title_message)
    ImageView categoryImg;
    @Bind(R.id.tabs)
    RadioGroup rg;
    @Bind(R.id.tab1)
    RadioButton rb1;
    @Bind(R.id.tab2)
    RadioButton rb2;
    @Bind(R.id.tab3)
    RadioButton rb3;
    @Bind(R.id.content)
    ListView list;
    @Bind(R.id.pull_container)
    PullToRefreshView container;

    private View contentView;
    private CheckBox checkAll;
    private TextView undercarriage;
    private TextView upcarriage;
    private TextView uploadProduct;
    private SlidingMenu slidingMenu;
    private ListView classifylist;
    private List<GoodsClassify> categroyList;
    private ShopClassifyAdapter shopClassifyAdapter;

    private List<MyStoreProductEntity> dataList;
    private ManageProductAdapter adapter;

    private String mstoreId;
    private int page;
    private int curPage;
    private int key; //1-销量 3-价格量 空-按最新发布排序
    private int order;//排序方式 1-升序 2-降序
    private String keyword;//搜索关键字
    private int st;//展示类型（1为在售商品，2为本店商品，3为平台商品）
    private String category;//产品分类id

    private String proId;
    private String goodsCommonId;

    private int listcount;

    private int lastTab;
    private int curTab;
    private int lastPage;
    private String lastCategory;
    private String lastKeyword;

    private int missionNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_manage_layout);
        ButterKnife.bind(this);
        initParams();
        initWidget();
        showDialog();
        missionNum++;
        getNetData();
    }

    private void initParams() {
        dataList = new ArrayList<>();
        adapter = new ManageProductAdapter(this, dataList);
        adapter.setOnAdapterCallBack(this);

        mstoreId = MyApplication.getInstance().storeId;
        page = 10;
        curPage = 1;
        keyword = "";
        lastKeyword = "";
        category = "0";
        lastCategory = "0";
        st = 1;
        lastTab = R.id.tab1;
        curTab = R.id.tab1;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initWidget() {
        contentView = getWindow().getDecorView().findViewById(android.R.id.content);
        contentView.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
        inputTxt.setVisibility(View.VISIBLE);
        inputTxt.setSingleLine();
        inputTxt.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        inputTxt.setOnKeyListener(keyListener);
        categoryImg.setVisibility(View.VISIBLE);
        categoryImg.setImageDrawable(getResources().getDrawable(R.drawable.shop_nav_icon_classification));
        categoryImg.setOnClickListener(clickListener);
        rg.setOnCheckedChangeListener(checkedChangeListener);
        rb1.setOnClickListener(clickListener);
        rb2.setOnClickListener(clickListener);
        rb3.setOnClickListener(clickListener);
        initContent();
        initSlidingMenu();
    }

    private void initContent() {
        View view = getLayoutInflater().inflate(R.layout.product_manage_header, null, true);
        list.addHeaderView(view);
        list.setAdapter(adapter);
        checkAll = (CheckBox) view.findViewById(R.id.select_all);
        undercarriage = (TextView) view.findViewById(R.id.header_btn_down);
        upcarriage = (TextView) view.findViewById(R.id.header_btn_up);
        uploadProduct = (TextView) view.findViewById(R.id.btn_upload_pruduct);
        checkAll.setOnClickListener(clickListener);
        undercarriage.setOnClickListener(clickListener);
        upcarriage.setOnClickListener(clickListener);
        uploadProduct.setOnClickListener(clickListener);
        container.setOnFooterRefreshListener(this);
        container.setOnHeaderRefreshListener(this);
    }

    private void initSlidingMenu() {
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.RIGHT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.storedetails_menu_item, null);
        slidingMenu.setMenu(view);
        classifylist = (ListView) view.findViewById(R.id.storedetails_listView);
        categroyList = new ArrayList<>();
        shopClassifyAdapter = new ShopClassifyAdapter(this, categroyList);
        classifylist.setAdapter(shopClassifyAdapter);
        classifylist.setOnItemClickListener(itemClickListener);
        showDialog();
        missionNum++;
        getshopclassify();
    }

    private void getNetData() {
        String url = AppURL.GOODS_MANAGEMENT + "&mstoreId=" + mstoreId + "&page=" + page
                + "&curpage=" + curPage + "&key=" + key + "&order=" + order + "&keyword=" + URLtoUTF8Utils.toUtf8String(keyword)
                + "&st=" + st + "&category=" + category;
        L.i("wcl-->" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    try {
                        if (pullState != PULL_LOAD) {
                            dataList.clear();
                            adapter.cancelAll();
                        }
                        JsonObject jObj = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                        listcount = Integer.parseInt(gson.fromJson(jObj, JsonObject.class).get("list_count").getAsString());
                        String onlinegoods = gson.fromJson(jObj, JsonObject.class).get("onlinegoods").getAsString();
                        String storegoods = gson.fromJson(jObj, JsonObject.class).get("storegoods").getAsString();
                        String platformgoods = gson.fromJson(jObj, JsonObject.class).get("platformgoods").getAsString();
                        JsonArray jArr = gson.fromJson(jObj, JsonObject.class).get("search").getAsJsonArray();
                        MyStoreProductEntity[] arr = gson.fromJson(jArr, MyStoreProductEntity[].class);
                        Collections.addAll(dataList, arr);
                        rb1.setText("在售商品\n(" + onlinegoods + ")");
                        rb2.setText("本店商品\n(" + storegoods + ")");
                        rb3.setText("平台商品\n(" + platformgoods + ")");
                        endNetRequest(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                        Toast.makeText(ProductManageActivity.this, message, Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        endNetRequest(false);
                    }
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(ProductManageActivity.this, message, Toast.LENGTH_SHORT).show();
                    endNetRequest(false);
                }
                if (contentView.getVisibility() != View.VISIBLE)
                    contentView.setVisibility(View.VISIBLE);
                if (--missionNum <= 0)
                    dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(ProductManageActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                endNetRequest(false);
                if (contentView.getVisibility() != View.VISIBLE)
                    finish();
                if (--missionNum <= 0)
                    dismissDialog();
            }
        });
    }

    private void endNetRequest(boolean isSuccessful) {
        checkAll.setChecked(adapter.isAllSelected());
        if (isSuccessful) {
            upcarriage.setVisibility(st == 1 ? View.GONE : View.VISIBLE);
            undercarriage.setVisibility(st == 1 ? View.VISIBLE : View.GONE);
            adapter.setType(st);
            adapter.notifyDataSetChanged();
            lastTab = curTab;
            lastCategory = category;
            lastKeyword = keyword;
            lastPage = curPage;
        } else if (pullState == PULL_LOAD) {
            curPage--;
        } else if (pullState == PULL_REFRESH) {
            curPage = lastPage;
        } else if (pullState == 0) {
            rg.check(lastTab);
            category = lastCategory;
            keyword = lastKeyword;
        }

        if (pullState == PULL_REFRESH) {
            container.onHeaderRefreshComplete();
        } else if (pullState == PULL_LOAD) {
            container.onFooterRefreshComplete();
        }
        pullState = 0;
    }

    private void getshopclassify() {
        String url = AppURL.BASE_URL + "act=store&op=get_category&shopId=" + MyApplication.getInstance().storeId;
        L.e("wcl-->" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    try {
                        JsonObject j = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                        JsonArray jArr = gson.fromJson(j, JsonObject.class).get("gcInfo").getAsJsonArray();
                        GoodsClassify[] arr = gson.fromJson(jArr, GoodsClassify[].class);
                        categroyList.clear();
                        Collections.addAll(categroyList, arr);
                        shopClassifyAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(ProductManageActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                if (--missionNum <= 0)
                    dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(ProductManageActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                if (--missionNum <= 0)
                    dismissDialog();
            }
        });
    }

    private View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                keyword = inputTxt.getText().toString();
                showDialog();
                getNetData();
                return true;
            }
            return false;
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            category = categroyList.get(position).getGcId();
            showDialog();
            getNetData();
            slidingMenu.showContent();
        }
    };

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.tab1: //1为在售商品
                    st = 1;
                    break;
                case R.id.tab2://2为本店商品
                    st = 2;
                    break;
                case R.id.tab3://3为平台商品
                    st = 3;
                    break;
            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            } else if (v == categoryImg) {
                slidingMenu.toggle();
            } else if (v == rb1 || v == rb2 || v == rb3) {
                curTab = v.getId();
                lastPage = curPage;
                category = "0";
                curPage = 1;
                MyApplication.requestQueue.cancelAll(this);
                showDialog();
                getNetData();
            } else if (v == uploadProduct) {
                Intent intent = new Intent(ProductManageActivity.this, UploadProductActivity.class);
                startActivityForResult(intent, 11);
            } else if (v == checkAll) {
                selectAll();
            } else if (v == undercarriage) {
                goodsCommonId = adapter.getCommonIds();
                String url = AppURL.GOODS_UP_DOWN_CARRIAGE + "&mstoreId=" + mstoreId + "&goodsCommonId=" + goodsCommonId
                        + "&st=" + st + "&state=" + 2;
                LogUtils.e(TAG+"全部下架"+url);
                operate(url);
            } else if (v == upcarriage) {
                goodsCommonId = adapter.getCommonIds();
                String url = AppURL.GOODS_UP_DOWN_CARRIAGE + "&mstoreId=" + mstoreId + "&goodsCommonId=" + goodsCommonId
                        + "&st=" + st + "&state=" + 1;
                LogUtils.e(TAG+"全部上架"+url);
                operate(url);
            }
        }
    };

    private void selectAll() {
        if (checkAll.isChecked()) {
            adapter.selectAll();
        } else {
            adapter.cancelAll();
        }
        adapter.notifyDataSetChanged();
    }

    private void operate(String url) {
        showDialog();
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    Gson gson = new Gson();
                    String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(ProductManageActivity.this, message, Toast.LENGTH_SHORT).show();
                    if ("0".equals(error)) {
                        lastPage = curPage;
                        curPage = 1;
                        getNetData();
                    } else {
                        dismissDialog();
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(ProductManageActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11 && resultCode == RESULT_OK || requestCode == 12) {
            showDialog();
            lastPage = curPage;
            curPage = 1;
            getNetData();
        }
    }

    int pullState;

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        pullState = PULL_LOAD;
        curPage++;
        if (dataList.size() < listcount) {
            getNetData();
        } else {
            Toast.makeText(ProductManageActivity.this, "没有跟多数据！", Toast.LENGTH_SHORT).show();
            endNetRequest(false);
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        pullState = PULL_REFRESH;
        lastPage = curPage;
        curPage = 1;
        getNetData();
    }

    @Override
    public void changeState(boolean isChecked) {
        checkAll.setChecked(isChecked);
    }

    @Override
    public void subClick(TextView txt, int position) {
        String tag = (String) txt.getTag();
        Intent intent = null;
        String url;
        switch (tag) {
            case "删除":
                goodsCommonId = dataList.get(position).goodsCommonid;
                url = AppURL.GOODS_DELETE + "&st=" + st + "&goodsCommonId=" + goodsCommonId + "&mstoreId=" + mstoreId;
                operate(url);
                break;
            case "下架":
                goodsCommonId = dataList.get(position).goodsCommonid;
                url = AppURL.GOODS_UP_DOWN_CARRIAGE + "&mstoreId=" + mstoreId + "&goodsCommonId=" + goodsCommonId
                        + "&st=" + st + "&state=" + 2;
                operate(url);
                break;
            case "上架":
                goodsCommonId = dataList.get(position).goodsCommonid;
                url = AppURL.GOODS_UP_DOWN_CARRIAGE + "&mstoreId=" + mstoreId + "&goodsCommonId=" + goodsCommonId
                        + "&st=" + st + "&state=" + 1;
                operate(url);
                break;
            case "拿货":
                proId = dataList.get(position).proId;
                intent = new Intent(this, ProductDetailsActivity.class);
                intent.putExtra("proId", Integer.parseInt(proId));
                intent.putExtra("ifWholesale", 1);
                intent.setAction(MyAction.fromProductManage);
                startActivityForResult(intent, 12);
                break;
            case "修改":
                intent = new Intent(this, UploadProductActivity.class);
                intent.putExtra("goodsCommonid", dataList.get(position).goodsCommonid);
                intent.putExtra("st", st);
                startActivityForResult(intent, 11);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing()) {
            slidingMenu.showContent();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
    }
}
