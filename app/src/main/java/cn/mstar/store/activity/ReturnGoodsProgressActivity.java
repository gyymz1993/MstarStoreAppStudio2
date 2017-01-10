package cn.mstar.store.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.entity.ReturnGoodProgressEntity;
import cn.mstar.store.functionutils.LogUtils;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

public class ReturnGoodsProgressActivity extends AppCompatActivity {


    // 进度条
    @Bind(R.id.progress_1) RelativeLayout rel_progress_1;
    @Bind(R.id.progress_2) RelativeLayout rel_progress_2;
    @Bind(R.id.progress_3) RelativeLayout rel_progress_3;

    // 进度按钮
    @Bind(R.id.icon_progress_1)  ImageView iv_icon_progress_1;
    @Bind(R.id.icon_progress_2)  ImageView iv_icon_progress_2;
    @Bind(R.id.icon_progress_3)  ImageView iv_icon_progress_3;
    @Bind(R.id.icon_progress_4)  ImageView iv_icon_progress_4;

    public void setMyTitle(String title) {
        tv_title.setText(title);
    }

    int proId = -1;
    String orderId = "";

    public static final String PROID = "PROID", ORDERID = "ORDERID";
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_goods_progress);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        // inflate the data with the content of the data.
        ButterKnife.bind(this);
        setMyTitle(getString(R.string.request_sendback));
        setAllGray();
        gson = new Gson();
        iv_back.setVisibility(View.VISIBLE);
        proId = getIntent().getIntExtra(PROID, 1);
        orderId = getIntent().getStringExtra(ORDERID);
        loadInfo(proId, orderId);
    }

    private void loadInfo(int proId, String orderId) {

        i_showProgressDialog();
        String link = AppURL.RETURN_PRODUCT_PROGRESS+"&key="+ Utils.getTokenKey((MyApplication) getApplication())+"&orderNo="+orderId+"&proId="+proId;
        L.d("progress:::", link);
        VolleyRequest.GetCookieRequest(this, link, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                L.d("progress:::", result);
                try {
                    JsonElement elm = gson.fromJson(result, JsonElement.class).getAsJsonObject().get("error");
                    if ("0".equals(elm.getAsString())) {
                        makeToast("可以解析");
                        ReturnGoodProgressEntity entity = gson.fromJson(gson.fromJson(result, JsonElement.class).getAsJsonObject().get("data").getAsJsonObject(), ReturnGoodProgressEntity.class);
                        entity.message = entity.message.replaceAll("'$|^'", "");
                        L.d("progress:::", entity.toString());
                        inflateData(entity);
                    } else {
                        makeToast("请求有错误");
                        finish();
                    }
//                    orderDetailsEntity = gson.fromJson(elm, OrderDetailsEntity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast("请求有错误");
                    finish();
                } finally {
//                    i_dismissProgressDialog();
                }
            }

            @Override
            public void onFail(String error) {
                makeToast(getString(R.string.network_error));
                i_dismissProgressDialog();
                finish();
            }
        });
    }


    // view
    @Bind(R.id.tv_order_id)  TextView tv_order_id;
    @Bind(R.id.tv_date) TextView tv_date;

    // 自动添加
    @Bind(R.id.lny_logistic_details) LinearLayout lny_logistic_details;
    @Bind(R.id.lny_about_product) LinearLayout lny_about_product;
    @Bind(R.id.lny_problem_description) LinearLayout lny_problem_description;



    private void inflateData(ReturnGoodProgressEntity entity) {

        setState(Integer.valueOf(entity.totalInfo.states));
        tv_date.setText(entity.receiverInfo.add_time);
        tv_order_id.setText(entity.totalInfo.orderNo);

        // 物流信息
        TextView tv_postName = new TextView(this);
        tv_postName.setText(getString(R.string.postname)+entity.receiverInfo.postName); // 收货人
        TextView tv_address = new TextView(this);
        tv_address.setText(getString(R.string.adress)+entity.receiverInfo.address); // 地址
        TextView tv_eship_name = new TextView(this);
        tv_eship_name.setText(getString(R.string.e_ship_company) + entity.receiverInfo.e_name); // 快递公司
        TextView tv_eship_code = new TextView(this);
        tv_eship_code.setText(getString(R.string.e_ship_code)); // 运单号码
        TextView tv_eship_resume = new TextView(this);
        tv_eship_resume.setText(getString(R.string.tv_eship_resume) + ""); // 运单号码
        lny_logistic_details.addView(tv_postName);
        lny_logistic_details.addView(tv_address);
        lny_logistic_details.addView(tv_eship_name);
        lny_logistic_details.addView(tv_eship_code);
        lny_logistic_details.addView(tv_eship_resume);

        if (entity.proInfo.length!=0){
            LogUtils.e("商品详情"+entity.proInfo[0].toString());
            // 关联产品
            TextView tv_product_name = new TextView(this);
            tv_product_name.setText(getString(R.string.product_name) + entity.proInfo[0].title); // 产品名称
            TextView tv_product_specials = new TextView(this);
            tv_product_specials.setText(entity.proInfo[0].specialTitle==null?getString(R.string.product_specials)+"无规格":getString(R.string.product_specials)+entity.proInfo[0].specialTitle); // 产品规格
            TextView tv_product_idnumber = new TextView(this);
            tv_product_idnumber.setText(getString(R.string.product_idnumber) + entity.proInfo[0].orderNo); // 产品单号
            lny_about_product.addView(tv_product_name);
            lny_about_product.addView(tv_product_specials);
            lny_about_product.addView(tv_product_idnumber);
        }



        // 问题描述
        TextView tv_message = new TextView(this);
        tv_message.setText(getString(R.string.problem_description1)+entity.message); // 产品单号
        lny_problem_description.addView(tv_message);
        i_dismissProgressDialog();
    }

    private void makeToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_return_goods_progress, menu);
        return true;
    }

    LoadingDialog dialog;
    public void i_showProgressDialog() {
        if(dialog==null){
            dialog = new LoadingDialog(this);
            dialog.show();
        }
    }

    public void i_dismissProgressDialog () {
        if (dialog != null) {
            dialog.cancel();
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setState (int i) {

        // 一共有四个状态、 1.提交 2. 审核中 3. 退款中 4. 完成

        // 把全进度条都变成灰色的
        setAllGray ();
        //
        String uri_1 = "drawable://"+R.drawable.progress_down;
        String uri_0 = "drawable://"+R.drawable.progress_norm;

        switch (i) {
            case 0:
                ImageLoader.getInstance().displayImage(uri_1, iv_icon_progress_1, ImageLoadOptions.getOptions());
                break;
            case 1:
                rel_progress_1.setBackgroundResource(R.color.green);
                ImageLoader.getInstance().displayImage(uri_0, iv_icon_progress_1, ImageLoadOptions.getOptions());
                ImageLoader.getInstance().displayImage(uri_1, iv_icon_progress_2, ImageLoadOptions.getOptions());
                break;
            case 2:
                rel_progress_1.setBackgroundResource(R.color.green);
                rel_progress_2.setBackgroundResource(R.color.green);
                ImageLoader.getInstance().displayImage(uri_0, iv_icon_progress_1, ImageLoadOptions.getOptions());
                ImageLoader.getInstance().displayImage(uri_0, iv_icon_progress_2, ImageLoadOptions.getOptions());
                ImageLoader.getInstance().displayImage(uri_1, iv_icon_progress_3, ImageLoadOptions.getOptions());
                break;
            case 3:
                rel_progress_1.setBackgroundResource(R.color.green);
                rel_progress_2.setBackgroundResource(R.color.green);
                rel_progress_3.setBackgroundResource(R.color.green);
                ImageLoader.getInstance().displayImage(uri_0, iv_icon_progress_1, ImageLoadOptions.getOptions());
                ImageLoader.getInstance().displayImage(uri_0, iv_icon_progress_2, ImageLoadOptions.getOptions());
                ImageLoader.getInstance().displayImage(uri_0, iv_icon_progress_3, ImageLoadOptions.getOptions());
                ImageLoader.getInstance().displayImage(uri_1, iv_icon_progress_4, ImageLoadOptions.getOptions());
                break;
        }

    }

    private void setAllGray() {

/* @Bind(R.id.progress_1) RelativeLayout rel_progress_1;
    @Bind(R.id.progress_2) RelativeLayout rel_progress_2;
    @Bind(R.id.progress_3) RelativeLayout rel_progress_3;

    // 进度按钮
    @Bind(R.id.icon_progress_1)  ImageView iv_icon_progress_1;
    @Bind(R.id.icon_progress_2)  ImageView iv_icon_progress_2;
    @Bind(R.id.icon_progress_3)  ImageView iv_icon_progress_3;
    @Bind(R.id.icon_progress_4)  ImageView iv_icon_progress_4;
*/
        rel_progress_1.setBackgroundResource(R.color.page_background);
        rel_progress_2.setBackgroundResource(R.color.page_background);
        rel_progress_3.setBackgroundResource(R.color.page_background);

        String uri = "drawable://"+R.drawable.icon_graycircle;
        ImageLoader.getInstance().displayImage(uri, iv_icon_progress_1, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(uri, iv_icon_progress_2, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(uri, iv_icon_progress_3, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(uri, iv_icon_progress_4, ImageLoadOptions.getOptions());
        return;
    }

    //actionbar
    @OnClick(R.id.title_back)
    public void back () {
        finish ();
    }
    @Bind(R.id.title_back) ImageView iv_back;
    @Bind(R.id.title_name) TextView tv_title;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}
