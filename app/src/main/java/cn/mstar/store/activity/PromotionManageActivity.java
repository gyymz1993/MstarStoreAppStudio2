package cn.mstar.store.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
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
import com.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.adapter.PromotionContentAdapter;
import cn.mstar.store.adapter.ShopClassifyAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.entity.GoodsClassify;
import cn.mstar.store.entity.PromotionProduct;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.interfaces.AdapterCallBack;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by 1 on 2016/1/6.
 */
public class PromotionManageActivity extends BaseActivity implements PullToRefreshView.OnFooterRefreshListener,
        PullToRefreshView.OnHeaderRefreshListener, AdapterCallBack {

    private static final int PULL_REFRESH = 1;
    private static final int PULL_LOAD = 2;

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.pull_container)
    PullToRefreshView container;
    @Bind(R.id.content)
    ListView list;
    @Bind(R.id.tabs)
    RadioGroup rg;
    @Bind(R.id.tab1)
    RadioButton rb1;
    @Bind(R.id.tab2)
    RadioButton rb2;
    @Bind(R.id.tab3)
    RadioButton rb3;
    @Bind(R.id.shop_search_edt)
    EditText inputTxt;
    @Bind(R.id.title_message)
    ImageView categoryImg;

    private View contentView;
    private CheckBox selectBtn;
    private TextView headerBtn;
    private TextView uploadProduct;

    private List<PromotionProduct> dataList;
    private PromotionContentAdapter adapter;
    private String url;
    private String mstoreId;
    private String tokenKey;
    private String keyword;//搜索关键字
    private int page;
    private int curPage;
    private int type;
    private String proId;

    private int count_num;
    private int lastTab;
    private int curTab;
    private int lastPage;
    private int missionNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_manage_layout);
        contentView = getWindow().getDecorView().findViewById(android.R.id.content);
        contentView.setVisibility(View.INVISIBLE);
        ButterKnife.bind(this);
        missionNum++;
        initParams();
        initWidget();
        showDialog();
        requestNet();
    }

    private void initParams() {

        dataList = new ArrayList<>();
        adapter = new PromotionContentAdapter(this, dataList);
        adapter.setOnAdapterCallBack(this);
        tokenKey = MyApplication.getInstance().tokenKey;
        mstoreId = MyApplication.getInstance().storeId;
        page = 10;
        curPage = 1;
        lastPage = 1;
        type = 0;/*1为上新 2为促销，其他或不填为在售*/

        lastTab = R.id.tab1;
        curTab = R.id.tab1;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initWidget() {
        title.setText("商品促销");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
        rg.setOnCheckedChangeListener(checkedChangeListener);
        rb1.setOnClickListener(clickListener);
        rb2.setOnClickListener(clickListener);
        rb3.setOnClickListener(clickListener);

        inputTxt.setVisibility(View.VISIBLE);
        inputTxt.setSingleLine();
        inputTxt.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        inputTxt.setOnKeyListener(keyListener);

        categoryImg.setVisibility(View.VISIBLE);
        categoryImg.setImageDrawable(getResources().getDrawable(R.drawable.shop_nav_icon_classification));
        categoryImg.setOnClickListener(clickListener);
        initContent();
        initSlidingMenu();
    }
    private SlidingMenu slidingMenu;
    private ListView classifylist;
    private List<GoodsClassify> categroyList;
    private ShopClassifyAdapter shopClassifyAdapter;
    private String category;//产品分类id
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
                   // Toast.makeText(ProductManageActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                if (--missionNum <= 0)
                    dismissDialog();
            }

            @Override
            public void onFail(String error) {
               // Toast.makeText(ProductManageActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                if (--missionNum <= 0)
                    dismissDialog();
            }
        });
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            category = categroyList.get(position).getGcId();
            showDialog();
            requestNet();
            slidingMenu.showContent();
        }
    };

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

    private void initContent() {

        container.setOnFooterRefreshListener(this);
        container.setOnHeaderRefreshListener(this);
        View view = getLayoutInflater().inflate(R.layout.product_manage_header, null, true);
        list.addHeaderView(view);
        list.setAdapter(adapter);

        selectBtn = (CheckBox) view.findViewById(R.id.select_all);
        selectBtn.setOnClickListener(clickListener);

        headerBtn = (TextView) view.findViewById(R.id.header_btn_down);
        headerBtn.setText("全部促销");
        headerBtn.setOnClickListener(clickListener);

        uploadProduct = (TextView) view.findViewById(R.id.btn_upload_pruduct);
        uploadProduct.setText("全部上新");
        uploadProduct.setOnClickListener(clickListener);

    }


    private void getNetData() {
        url = AppURL.PROMOTION_MANAGE + "&mstoreId=" + mstoreId + "&tokenKey=" + tokenKey
                + "&page=" + page + "&curpage=" + curPage + "&type=" + type+"&keyword="+
        URLtoUTF8Utils.toUtf8String(keyword)+"&category="+0;
        L.e("wcl-->" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {

                String onsale, newg, prom;
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    onsale = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("totalInfo").getAsJsonObject().get("onsale").getAsString();
                    newg = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("totalInfo").getAsJsonObject().get("new").getAsString();
                    prom = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("totalInfo").getAsJsonObject().get("prom").getAsString();
                    count_num = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("totalInfo").getAsJsonObject().get("list_count").getAsInt();
                    PromotionProduct[] arr = new PromotionProduct[0];
                    try {
                        JsonArray jArr = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("mdata").getAsJsonArray();
                        arr = gson.fromJson(jArr, PromotionProduct[].class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (state != PULL_LOAD) {
                        dataList.clear();
                        adapter.cancelAll();
                    }
                    Collections.addAll(dataList, arr);
                    if (dataList.size() == 0)
                        container.setVisibility(View.GONE);
                    else
                        container.setVisibility(View.VISIBLE);
                    rb1.setText("在售(" + onsale + ")");
                    rb2.setText("促销(" + prom + ")");
                    rb3.setText("上新(" + newg + ")");
                    adapter.setType(type);
                    adapter.notifyDataSetChanged();
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(PromotionManageActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                endNetRequest(true);
                if (contentView.getVisibility() != View.VISIBLE)
                    contentView.setVisibility(View.VISIBLE);
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(PromotionManageActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                endNetRequest(false);
                if (contentView.getVisibility() != View.VISIBLE)
                    finish();
                dismissDialog();
            }
        });
    }


    /**
     * access net
     */
    private void requestNet() {
        url = AppURL.PROMOTION_MANAGE + "&mstoreId=" + mstoreId + "&tokenKey=" + tokenKey
                + "&page=" + page + "&curpage=" + curPage + "&type=" + type;
        L.e("wcl-->" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {

                String onsale, newg, prom;
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    onsale = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("totalInfo").getAsJsonObject().get("onsale").getAsString();
                    newg = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("totalInfo").getAsJsonObject().get("new").getAsString();
                    prom = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("totalInfo").getAsJsonObject().get("prom").getAsString();
                    count_num = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("totalInfo").getAsJsonObject().get("list_count").getAsInt();
                    PromotionProduct[] arr = new PromotionProduct[0];
                    try {
                        JsonArray jArr = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("mdata").getAsJsonArray();
                        arr = gson.fromJson(jArr, PromotionProduct[].class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (state != PULL_LOAD) {
                        dataList.clear();
                        adapter.cancelAll();
                    }
                    Collections.addAll(dataList, arr);
                    if (dataList.size() == 0)
                        container.setVisibility(View.GONE);
                    else
                        container.setVisibility(View.VISIBLE);
                    rb1.setText("在售(" + onsale + ")");
                    rb2.setText("促销(" + prom + ")");
                    rb3.setText("上新(" + newg + ")");
                    adapter.setType(type);
                    adapter.notifyDataSetChanged();
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(PromotionManageActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                endNetRequest(true);
                if (contentView.getVisibility() != View.VISIBLE)
                    contentView.setVisibility(View.VISIBLE);
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(PromotionManageActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                endNetRequest(false);
                if (contentView.getVisibility() != View.VISIBLE)
                    finish();
                dismissDialog();
            }
        });
    }

    private void endNetRequest(boolean netState) {
        selectBtn.setChecked(adapter.isAllSelected());
        if (netState) {
            switch (type) {
                case 0: //在售
                    headerBtn.setVisibility(View.VISIBLE);
                    uploadProduct.setVisibility(View.VISIBLE);
                    headerBtn.setText("全部促销");
                    uploadProduct.setText("全部上新");
                    break;
                case 1: //上新
                    headerBtn.setVisibility(View.GONE);
                    uploadProduct.setVisibility(View.VISIBLE);
                    uploadProduct.setText("全部取消");
                    break;
                case 2: //促销
                    headerBtn.setVisibility(View.VISIBLE);
                    uploadProduct.setVisibility(View.GONE);
                    headerBtn.setText("全部取消");
                    break;
            }
        } else if (state == PULL_LOAD) {
            curPage--;
        } else if (state == PULL_REFRESH) {
            curPage = lastPage;
        } else if (state == 0) {
            rg.check(lastTab);
            curPage = lastPage;
        }

        if (state == PULL_REFRESH) {
            container.onHeaderRefreshComplete();
        } else if (state == PULL_LOAD) {
            container.onFooterRefreshComplete();
        }
        state = 0;
    }

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.tab1: //在售
                    type = 0;
                    break;
                case R.id.tab2://促销
                    type = 2;
                    break;
                case R.id.tab3://上新
                    type = 1;
                    break;
            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            } else if (v == selectBtn) {
                clickSelectBtn();
            } else if (v == headerBtn) {
                proId = adapter.getIds();
                type0 = 2;
                if (type == 0)
                    operateSubmit();
                else
                    operateCancel();
            } else if (v == uploadProduct) {
                proId = adapter.getIds();
                type0 = 1;
                if (type == 0)
                    operateSubmit();
                else
                    operateCancel();
            } else if (v == rb1 || v == rb2 || v == rb3) {
                curTab = v.getId();
                lastPage = curPage;
                curPage = 1;
                MyApplication.requestQueue.cancelAll(this);
                showDialog();
                requestNet();
            }else if (v == categoryImg) {
                slidingMenu.toggle();
            }
        }
    };

    private void clickSelectBtn() {
        if (selectBtn.isChecked()) {
            adapter.selectAll();
        } else {
            adapter.cancelAll();
        }
        adapter.notifyDataSetChanged();
    }

    private void operateSubmit() {
        String url = AppURL.PROMOTION_SUBMIT + "&mstoreId=" + mstoreId + "&tokenKey=" + tokenKey
                + "&type=" + type0 + "&proId=" + proId;
        showDialog();
        submitRequest(url);
    }

    private void operateCancel() {
        String url = AppURL.PROMOTION_CANCEL + "&mstoreId=" + mstoreId + "&tokenKey=" + tokenKey
                + "&type=" + type0 + "&proId=" + proId;
        showDialog();
        submitRequest(url);
    }

    private void submitRequest(String url) {
        L.i("wcl-->" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                Toast.makeText(PromotionManageActivity.this, message, Toast.LENGTH_SHORT).show();
                lastPage = curPage;
                curPage = 1;
                MyApplication.requestQueue.cancelAll(this);
                requestNet();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(PromotionManageActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    int state;

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (dataList.size() < count_num) {
            state = PULL_LOAD;
            curPage++;
            requestNet();
        } else {
            container.onFooterRefreshComplete();
            Toast.makeText(PromotionManageActivity.this, "没有更多数据！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        state = PULL_REFRESH;
        lastPage = curPage;
        curPage = 1;
        requestNet();
    }

    @Override
    public void changeState(boolean isChecked) {
        selectBtn.setChecked(isChecked);
    }

    int type0;

    @Override
    public void subClick(TextView txt, int position) {
        String tag = (String) txt.getTag();
        proId = dataList.get(position).proId;
        switch (tag) {
            case "取消上新":
                type0 = 1;
                operateCancel();
                break;
            case "取消促销":
                type0 = 2;
                operateCancel();
                break;
            case "上新":
                type0 = 1;
                operateSubmit();
                break;
            case "促销":
                type0 = 2;
                operateSubmit();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
    }
}
