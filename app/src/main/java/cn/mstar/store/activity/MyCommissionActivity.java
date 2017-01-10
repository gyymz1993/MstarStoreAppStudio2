package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CustomListView;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.entity.CommissionDetailEntity;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Administrator on 2015/10/29.
 */
public class MyCommissionActivity extends BaseActivity implements View.OnClickListener ,PullToRefreshView.OnFooterRefreshListener,
        PullToRefreshView.OnHeaderRefreshListener{

    private TextView commission_text;
    private TextView allcommission_text;
    private TextView recentlycommission_text, titleName;
    private Button applyforcommission_btn;
    private ImageView titleBack;
    private ScrollView scroll;
    private CustomListView detail;
    private CommissionAdapter adapter;
    private List<CommissionDetailEntity> data;
    private int page;
    private int curpage;  //当前页
    private int tempage; //记录当前索引
    private static final int HEAD_REFRESH = 1;
    private static final int FOOTER_LOAD = 2;
    private PullToRefreshView refreshView;
    private int state = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycommission);
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        initParams();
        initView();
        initListener();
        initData();
    }

    private void initParams() {
        adapter = new CommissionAdapter();
        data = new ArrayList<>();
        page = 3;
        curpage = 1;
        url = AppURL.MYCOMMISSION + "&tokenKey=" + Utils.getTokenKey((MyApplication) this.getApplication())
                + "&page=" + page + "&curpage=" + curpage;
    }

    private void initData() {
        getInfo();
    }

    private void getInfo() {
        Log.e("ymz", "佣金UTL:" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.e("ymz", "佣金:" + result);
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    JsonObject jdata = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                    String restcomm = gson.fromJson(jdata, JsonObject.class).get("restcomm").getAsString();
                    String hiscomm = gson.fromJson(jdata, JsonObject.class).get("hiscomm").getAsString();
                    String newcomm = gson.fromJson(jdata, JsonObject.class).get("newcomm").getAsString();
                    commission_text.setText(restcomm);
                    allcommission_text.setText(hiscomm);
                    recentlycommission_text.setText(newcomm);
                    String totalItems = gson.fromJson(jdata, JsonObject.class).get("list_count").getAsString();
                    int totaldata = Integer.valueOf(totalItems);
                    if (totaldata == 0) {
                        refreshData();
                        return;
                    } else if (curpage <= (totaldata + page - 1) / page) {
                        List<CommissionDetailEntity> data1 = new ArrayList<CommissionDetailEntity>();
                        JsonArray jArr = gson.fromJson(jdata, JsonObject.class).get("orderInfo").getAsJsonArray();
                        CommissionDetailEntity[] arr = gson.fromJson(jArr, CommissionDetailEntity[].class);
                        Collections.addAll(data1, arr);
                        data.addAll(data1);
                        adapter.notifyDataSetChanged();
                    } else {
                        curpage--;
                        Toast.makeText(MyCommissionActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(MyCommissionActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                Log.e("ymz", "刷新回调");
                 refreshData();
            }

            @Override
            public void onFail(String error) {
                if (state == FOOTER_LOAD)
                    curpage--;
                if (state == HEAD_REFRESH)
                    curpage = tempage;
                Toast.makeText(MyCommissionActivity.this, "网络错误，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshData() {
        if (state == HEAD_REFRESH) {
            refreshView.onHeaderRefreshComplete();
        } else if (state == FOOTER_LOAD) {
            refreshView.onFooterRefreshComplete();
        }

    }

    private void initListener() {
        applyforcommission_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCommissionActivity.this, ApplyForWithdrawActivity.class);
                startActivity(intent);
            }
        });
        titleBack.setOnClickListener(this);
        refreshView.setOnFooterRefreshListener(this);
        refreshView.setOnHeaderRefreshListener(this);

    }

    private void initView() {
        commission_text = (TextView) findViewById(R.id.commission_text);
        titleBack = (ImageView) findViewById(R.id.title_back);
        titleName = (TextView) findViewById(R.id.title_name);
        allcommission_text = (TextView) findViewById(R.id.allcommission_text);
        recentlycommission_text = (TextView) findViewById(R.id.recentlycommission_text);
        applyforcommission_btn = (Button) findViewById(R.id.applyforcommission_btn);
        scroll = (ScrollView) findViewById(R.id.scroll);
        detail = (CustomListView) findViewById(R.id.detail);
        detail.setAdapter(adapter);
        titleBack.setVisibility(View.VISIBLE);
        titleName.setText("我的佣金");
        refreshView = (PullToRefreshView)findViewById(R.id.comment_refresh_view);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;

        }
    }


    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        state = FOOTER_LOAD;
        curpage++;
        changeURL();
        initData();
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        state = HEAD_REFRESH;
        tempage = curpage;
        curpage = 1;
        changeURL();
        data.clear();
        initData();
    }

    public class CommissionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public CommissionDetailEntity getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(MyCommissionActivity.this).inflate(R.layout.commisssion_detail_item_layout, parent, false);
                vh.sn = (TextView) convertView.findViewById(R.id.sn);
                vh.name = (TextView) convertView.findViewById(R.id.name);
                vh.num = (TextView) convertView.findViewById(R.id.num);
                vh.time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            vh.sn.setText("订单号：" + getItem(position).orderSn);
            vh.name.setText("姓名：" + getItem(position).buyName);
            vh.num.setText("数量：" + getItem(position).amount);
            vh.time.setText("时间：" + getItem(position).addtime);
            return convertView;
        }

        public final class ViewHolder {
            TextView sn;
            TextView name;
            TextView num;
            TextView time;
        }
    }

    String url;
    private String  changeURL(){
        url = AppURL.MYCOMMISSION + "&tokenKey=" + Utils.getTokenKey((MyApplication) this.getApplication())
                + "&page=" + page + "&curpage=" + curpage;
        return url;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
    }
}
