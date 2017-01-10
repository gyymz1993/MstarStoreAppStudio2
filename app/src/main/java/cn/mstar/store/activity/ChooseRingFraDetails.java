package cn.mstar.store.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.mvp.model.JsonRingDetail;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CustomWebview;
import cn.mstar.store.functionutils.HtmlStyle;
import cn.mstar.store.utils.DialogUtils;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Administrator on 2016/4/15.
 */
public class ChooseRingFraDetails extends Activity implements View.OnClickListener,OnChangeSelectRingClick{

    private final String TAG = "ChooseRingFraDetails";
    private ImageView ig_show;

    @Bind(R.id.rb_select_1)
    RadioButton rbSelect1;
    @Bind(R.id.rb_select_2)
    RadioButton rbSelect2;
    @Bind(R.id.id_ly_ring_content1)
    LinearLayout idRyRingContent1;
    @Bind(R.id.id_ly_ring_content2)
    LinearLayout idRyRingContent2;
    @Bind(R.id.tv_show_go)
    TextView tvShowGo;
    private ImageView id_ig_right,id_ig_left;
    private CustomWebview webview;
    private DialogUtils dialogUtils1;
    private DialogUtils dialogUtils2;
    private  int proid,ringid;
    private LinearLayout lymenus;
    private TextView tv_price;
    private ScrollView slShowData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ring_fram_dt_main);
        ButterKnife.bind(this);
        proid = getIntent().getIntExtra("proid", 0);
        ringid = getIntent().getIntExtra("ringid", 0);
        intiTitle();
        initView();
        initTypeLayout();
        showLoading();
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
        slShowData.setVisibility(View.GONE);
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

    private void showDataing() {
        dismissAllView();
        slShowData.setVisibility(View.VISIBLE);
    }



    private void showWeb(String html){
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
        webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        StorageUtils.getOwnCacheDirectory(ChooseRingFraDetails.this,
                "MstarStore/Cache").getAbsolutePath();
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        String mime = "text/html";
        String encoding = "utf-8";
        webview.getSettings().setJavaScriptEnabled(true);
        String showHTML=HtmlStyle.setHtmlStyle(this, html);
        LogUtils.e(TAG+"HTMN\n"+showHTML);
        webview.loadDataWithBaseURL(null, showHTML, mime, encoding, null);
        webview.setVerticalScrollBarEnabled(false);

    }

    private String getUrl() {
        String url = AppURL.RING_FRAG_DETATIL + "&diamondId=" + proid + "&ringId=" + ringid + "&tokenKey=" + MyApplication.getInstance().tokenKey + "&picsize=8004.3";
        return url;
    }


    private List<String> pics;
    private List<String> specSubTitle;  //成色
    private List<String> specSize;  //尺寸
    private  Map<String,JsonRingDetail.DataEntity.ProductSpecPriceEntity> mapprice;  //计算价格
    private JsonRingDetail.DataEntity.ProductEntity product;
    private void loadNetData() {
        LogUtils.e(TAG + "loadNetData URL\n" + getUrl());
        VolleyRequest.GetCookieRequestPurePage(this, getUrl(), new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                showDataing();
                mapprice = new HashMap<>();
                LogUtils.e(TAG + "result" + result);
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if (error.equals("0")) {
                    JsonRingDetail jsonRingDetail = gson.fromJson(result, JsonRingDetail.class);
                    product = jsonRingDetail.getData().getProduct();
                    pics = jsonRingDetail.getData().getProduct().getPics();
                    List<JsonRingDetail.DataEntity.ProductSpecListEntity> productSpecList = jsonRingDetail.getData().getProductSpecList();
                    specSubTitle = productSpecList.get(0).getSpecSubTitle();
                    specSize = productSpecList.get(1).getSpecSubTitle();
                    int count = jsonRingDetail.getData().getProductSpecPrice().size();
                    for (int i = 0; i < count; i++) {
                        JsonRingDetail.DataEntity.ProductSpecPriceEntity bean = jsonRingDetail.getData().getProductSpecPrice().get(i);
                        String str = bean.getAttrs1() + bean.getAttrs2();
                        mapprice.put(str, bean);
                    }
                    layoutBtns();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject dataJson = jsonObject.getJSONObject("data");
                        JSONObject jsonObject1 = dataJson.getJSONObject("product");
                        String desc = jsonObject1.getString("desc");
                        showWeb(desc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(ChooseRingFraDetails.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
    }



    private void initView() {
        tv_price = (TextView) findViewById(R.id.id_tv_price);
        lymenus = (LinearLayout) findViewById(R.id.id_menus);
        ig_show = (ImageView) findViewById(R.id.id_ig_show);
        webview = (CustomWebview) findViewById(R.id.webview);
        id_ig_right = (ImageView) findViewById(R.id.id_ig_right);
        id_ig_left = (ImageView) findViewById(R.id.id_ig_left);
        slShowData= (ScrollView)findViewById(R.id.id_sl_show_data);
        dialogUtils1 = new DialogUtils();
        dialogUtils2 = new DialogUtils();
        tvShowGo.setOnClickListener(this);
        id_ig_left.setOnClickListener(this);
        id_ig_right.setOnClickListener(this);
        rbSelect1.setOnClickListener(this);
        rbSelect2.setOnClickListener(this);
    }

    ImageView titleBack;

    private void intiTitle() {
        titleBack = (ImageView) findViewById(R.id.title_back);
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("选择戒托详情");
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);
    }

    List<LinearLayout> linearLayouts = new ArrayList<>();
    int count = 0;

    private void layoutBtns() {
        ImageLoader.getInstance().displayImage(pics.get(0), ig_show, ImageLoadOptions.getOptions());
        for (int i = 0; i < pics.size(); i++) {
            ImageView img = new ImageView(this);
            img.setBackgroundResource(R.drawable.slecet_while_item);
            if (i == 0) {
                img.setBackgroundResource(R.drawable.selector_gridview_item);
            }
            ImageLoader.getInstance().displayImage(pics.get(i), img, ImageLoadOptions.getOptions());
            final LinearLayout ll = new LinearLayout(this);
            ll.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.addView(img, lp);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(172, 190);
            ll.setTag(i);
            lp1.setMargins(10, 0, 0, 0);
            linearLayouts.add(ll);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resteAll();
                    LinearLayout linearLayout = (LinearLayout) view;
                    current_show=Integer.valueOf(linearLayout.getTag().toString());
                    linearLayout.getChildAt(0).setBackgroundResource(R.drawable.selector_gridview_item);
                    ImageLoader.getInstance().displayImage(pics.get(current_show), ig_show, ImageLoadOptions.getOptions());
                }
            });
            lymenus.addView(ll, lp1);
        }
    }



    private void resteAll() {
        if (linearLayouts.size() != 0) {
            for (int i = 0; i < linearLayouts.size(); i++) {
                ImageView imageView = (ImageView) linearLayouts.get(i).getChildAt(0);
                imageView.setBackgroundResource(R.drawable.slecet_while_item);
            }
        }
    }


    //当前选择的图片
   private int current_show=0;
    @Override
    public void onClick(View view) {
        if (view == titleBack) {
            finish();
        }
        if (view==tvShowGo){
            Intent intent = new Intent(ChooseRingFraDetails.this, ChooseRingPreview.class);
            LogUtils.e(TAG + "proid\n" + proid + "ringid\n" + ringid);
            int proids = Integer.valueOf(product.getDiamondId());
            int ringids=Integer.valueOf(product.getRingId());
            intent.putExtra("proid", proids);
            intent.putExtra("ringid", ringids);
            startActivity(intent);
        }
        if(view==id_ig_right){
            if (current_show<pics.size()-1){
                current_show++;
                ImageLoader.getInstance().displayImage(pics.get(current_show), ig_show, ImageLoadOptions.getOptions());
                resteAll();
                LinearLayout linearLayout =  linearLayouts.get(current_show);
                linearLayout.getChildAt(0).setBackgroundResource(R.drawable.selector_gridview_item);
            }

        }
        if (view==id_ig_left){
            if (current_show>0){
                current_show--;
                resteAll();
                LinearLayout linearLayout =  linearLayouts.get(current_show);
                linearLayout.getChildAt(0).setBackgroundResource(R.drawable.selector_gridview_item);
                ImageLoader.getInstance().displayImage(pics.get(current_show), ig_show, ImageLoadOptions.getOptions());
            }

        }
        if (view==rbSelect1){
            showPopupWindow(1);
        }
        if (view==rbSelect2){
            showPopupWindow(2);
        }
    }

    //点击的选项
    private View layoutLeft;
    private ListView menulistLeft;
    private String one=null;
    private String two=null;
    public void showPopupWindow(int type) {
        layoutLeft = getLayoutInflater().inflate(
                R.layout.pop_menulist, null);
        menulistLeft = (ListView) layoutLeft
                .findViewById(R.id.menulist);
        if (type==1){
            if (specSubTitle != null && specSubTitle.size() != 0) {
                PopAdapter baseAdapter = new PopAdapter(specSubTitle);
                menulistLeft.setAdapter(baseAdapter);
                // 点击listview中item的处理
                menulistLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0,
                                            View arg1, int i, long arg3) {
                        if (dialogUtils1.isShow()) {
                            dialogUtils1.dismiss();
                        }
                        one=specSubTitle.get(i).toString();
                        rbSelect1.setText(one);
                        onChang(one, two);
                    }
                });
            }   dialogUtils1.setDialogList(layoutLeft,rbSelect1,idRyRingContent1);
        }else{
            if (specSize != null && specSize.size() != 0) {
                PopAdapter baseAdapter = new PopAdapter(specSize);
                menulistLeft.setAdapter(baseAdapter);
                // 点击listview中item的处理
                menulistLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0,
                                            View arg1, int i, long arg3) {
                        if (dialogUtils1.isShow()) {
                            dialogUtils1.dismiss();
                        }
                        two=specSize.get(i).toString();
                        rbSelect2.setText(two);
                        onChang(one, two);

                    }
                });
            }   dialogUtils2.setDialogList(layoutLeft,rbSelect2,idRyRingContent2);
        }
    }

    @Override
    public void onChang(String one, String two) {
        if (one!=null&&two!=null){
            LogUtils.e("onew"+"teo");
            String prices=one+two;
            JsonRingDetail.DataEntity.ProductSpecPriceEntity productSpecPriceEntity = mapprice.get(prices);
            LogUtils.e(TAG+"PRICE\n"+productSpecPriceEntity.getPrice());
            tv_price.setVisibility(View.VISIBLE);
            tv_price.setText(getResources().getString(R.string.yuan_char)+":"+productSpecPriceEntity.getPrice());
        }
    }



    class PopAdapter extends BaseAdapter {
        // 菜单数据项
        private List<String> listLeft;

        public PopAdapter(List<String> listLeft) {
            this.listLeft = listLeft;
        }

        @Override
        public int getCount() {
            return listLeft.size();
        }

        @Override
        public Object getItem(int i) {
            return listLeft.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(ChooseRingFraDetails.this).inflate(R.layout.pop_menuitem, null);
            }
            TextView textView = (TextView) view.findViewById(R.id.menuitem);
            textView.setText(listLeft.get(i));
            return view;
        }
    }

    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }

}




