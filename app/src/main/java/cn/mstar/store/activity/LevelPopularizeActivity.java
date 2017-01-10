package cn.mstar.store.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.adapter.PopularizeAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.entity.PopEntity;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/*
 * 创建人：Yangshao
 * 创建时间：2016/3/2 15:43
 * @version     下下级推广
 *    
 */

public class LevelPopularizeActivity extends BaseActivity implements PullToRefreshView.OnFooterRefreshListener,
        PullToRefreshView.OnHeaderRefreshListener {

    private final String TAG="LevlePopularzieActivity";
    private static final int HEAD_REFRESH = 1;
    private static final int FOOTER_LOAD = 2;
    private List<PopEntity> data;
    private PopularizeAdapter adapter;
    private String url2;
    private String url;
    private LoadingDialog dialog;
    private boolean isFirstAccess = true;
    private PullToRefreshView refreshView;
    private ListView list;
    private TextView notify;
    private int curpage = 1;
    private int prepage;
    private int state = 0;
    private int page = 10;
    private String  memberId;
    private String userName;
    private int ifmshop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_levelpopu_main);
        userName = getIntent().getStringExtra("userName");
        memberId =  getIntent().getStringExtra("memberId");
        ifmshop = getIntent().getIntExtra("ifmshop", 0);
        url=AppURL.LOWER+memberId+"&ifmshop="+ifmshop;
        url2 = url + "&page=10&curpage=1";
        LogUtils.e(TAG+"下下级推广"+url2);
        data = new ArrayList<>();
        adapter = new PopularizeAdapter(this, data);
        initHeader();
        setViewData();
    }

    private TextView title;
    private void initHeader() {
        ImageView back = (ImageView) findViewById(R.id.title_back);
        title = (TextView) findViewById(R.id.title_name);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText(userName+"的推广");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void setViewData(){
        refreshView = (PullToRefreshView)findViewById(R.id.comment_refresh_view);
        refreshView.setOnFooterRefreshListener(this);
        refreshView.setOnHeaderRefreshListener(this);
        list = (ListView) findViewById(R.id.comment_list);
        list.setDivider(getResources().getDrawable(R.color.transparent));
        list.setDividerHeight(1);
        notify = (TextView)findViewById(R.id.comment_list_notify);
        notify.setText("暂无数据");
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        if (isFirstAccess) {
            isFirstAccess = false;
            showDialog();
            inflateData();
        }
    }


    private void inflateData() {
        VolleyRequest.GetCookieRequest(LevelPopularizeActivity.this, url2, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    Gson gson = new Gson();
                    int totalItem = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("list_count").getAsInt();
                    if (totalItem == 0) {
                        notify.setVisibility(View.VISIBLE);
                        return;
                    } else if (curpage <= (totalItem + page - 1) / page) {
                        JsonArray item = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject().get("tgInfo").getAsJsonArray();
                        Type type = new TypeToken<PopEntity[]>() {
                        }.getType();
                        PopEntity[] arr = gson.fromJson(item, type);
                        for (PopEntity p : arr) {
                            data.add(p);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        curpage--;
                        Toast.makeText(LevelPopularizeActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                    }
                    notify.setVisibility(View.GONE);
                } catch (Exception e) {
                    data.clear();
                    adapter.notifyDataSetChanged();
                    notify.setVisibility(View.VISIBLE);
                } finally {
                    if (state == HEAD_REFRESH) {
                        refreshView.onHeaderRefreshComplete();
                    } else if (state == FOOTER_LOAD) {
                        refreshView.onFooterRefreshComplete();
                    }
                    if (dialog.isShowing())
                        hideDialog();
                }
            }

            @Override
            public void onFail(String error) {
                if (state == FOOTER_LOAD)
                    curpage--;
                if (state == HEAD_REFRESH)
                    curpage = prepage;
                Toast.makeText(LevelPopularizeActivity.this, "网络错误，请检查网络！", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing())
                    hideDialog();
            }
        });
    }

    @Override
    protected void showDialog() {
        dialog = new LoadingDialog(LevelPopularizeActivity.this);
        dialog.show();
    }

    private void hideDialog() {
        dialog.dismiss();
    }

    private void changeURL() {
        url2 = url + "&page=" + page + "&curpage=" + curpage;
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        state = FOOTER_LOAD;
        curpage++;
        changeURL();
        inflateData();
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        state = HEAD_REFRESH;
        prepage = curpage;
        curpage = 1;
        changeURL();
        data.clear();
        inflateData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
    }
}