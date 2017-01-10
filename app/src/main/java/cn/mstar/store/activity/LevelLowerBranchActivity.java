package cn.mstar.store.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.adapter.LowerBranchAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.entity.Json_LowBra;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/*
 * 创建人：Yangshao
 * 创建时间：2016/3/2 16:19
 * @version    下下级分店
 *    
 */
public class LevelLowerBranchActivity  extends BaseActivity implements PullToRefreshView.OnFooterRefreshListener,
        PullToRefreshView.OnHeaderRefreshListener {

    private final String TAG="LevelLowerBranchActivity";
    private static final int HEAD_REFRESH = 1;
    private static final int FOOTER_LOAD = 2;
    private List<Json_LowBra.DataEntity.StoreInfoEntity> data;
    protected LowerBranchAdapter adapter;
    private String url;
    private String url2;
    private LoadingDialog dialog;
    private boolean isFirstAccess = true;
    private PullToRefreshView refreshView;
    private ListView list;
    private TextView notify;
    private int curpage = 1;  //第几页
    private int prepage;  //记录索引
    private int state = 0;
    private int page = 10; //每页条数
    private String  memberId;
    private String userName;
    private int ifmshop;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_levelpopu_main);
        ifmshop = getIntent().getIntExtra("ifmshop",0);
        userName = getIntent().getStringExtra("userName");
        memberId =  getIntent().getStringExtra("memberId");
        url= AppURL.AGENT+memberId+"&ifmshop="+ifmshop;
        url2 = url + "&page=10&curpage=1";
        LogUtils.e(TAG + "下下级分店" + url2);
        data = new ArrayList<>();
        adapter = new LowerBranchAdapter(this, data);
        initHeader();
        setViewData();
    }

    private TextView title;
    private void initHeader() {
        ImageView back = (ImageView) findViewById(R.id.title_back);
        title = (TextView) findViewById(R.id.title_name);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText(userName + "的分店");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void setViewData() {
        refreshView = (PullToRefreshView) findViewById(R.id.comment_refresh_view);
        refreshView.setOnFooterRefreshListener(this);
        refreshView.setOnHeaderRefreshListener(this);
        list = (ListView) findViewById(R.id.comment_list);
        list.setDivider(getResources().getDrawable(R.color.transparent));
        list.setDividerHeight(1);
        notify = (TextView) findViewById(R.id.comment_list_notify);
        notify.setText("暂无数据");
        list.setAdapter(adapter);

        if (isFirstAccess) {
            isFirstAccess = false;
            showDialog();
            inflateData();
        }
    }


    private void inflateData() {
        VolleyRequest.GetCookieRequest(this, url2, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (result == null) return;
                    Json_LowBra infomatHome = new Gson().fromJson(result, Json_LowBra.class);
                    if (!infomatHome.getError().equals("0")) return;
                    String totalItems = infomatHome.getData().getList_count();
                    int totalItem = Integer.valueOf(totalItems);
                    if (totalItem == 0) {
                        notify.setVisibility(View.VISIBLE);
                        return;
                    } else if (curpage <= (totalItem + page - 1) / page/*总页数*/) {
                        List<Json_LowBra.DataEntity.StoreInfoEntity> storeInfoEntityList = infomatHome.getData().getStoreInfo();
                        for (Json_LowBra.DataEntity.StoreInfoEntity p : storeInfoEntityList) {
                            data.add(p);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        curpage--;
                        Toast.makeText(LevelLowerBranchActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LevelLowerBranchActivity.this, "网络错误，请检查网络！", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing())
                    hideDialog();
            }
        });
    }

    protected void showDialog() {
        dialog = new LoadingDialog(LevelLowerBranchActivity.this);
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