package cn.mstar.store.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.activity.ExpressSelectActivity;
import cn.mstar.store.activity.LevelLowerBranchActivity;
import cn.mstar.store.adapter.LowerBranchAdapter;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.entity.Json_LowBra;
import cn.mstar.store.functionutils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/**
 *
 */
public class LowerBranchFragment extends Fragment implements PullToRefreshView.OnFooterRefreshListener,
        PullToRefreshView.OnHeaderRefreshListener {

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
    private int curpage = 1;
    private int prepage;
    private int state = 0;
    private int page = 10;
    private int ifmshop;

    public LowerBranchFragment(String url,int ifmshop) {
        this.url = url;
        this.ifmshop=ifmshop;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url2 = url + "&page=10&curpage=1";
        data = new ArrayList<>();
        adapter = new LowerBranchAdapter(getActivity(), data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_comment_list_fragment, container, false);
        refreshView = (PullToRefreshView) view.findViewById(R.id.comment_refresh_view);
        refreshView.setOnFooterRefreshListener(this);
        refreshView.setOnHeaderRefreshListener(this);
        list = (ListView) view.findViewById(R.id.comment_list);
        list.setDivider(getResources().getDrawable(R.color.transparent));
        list.setDividerHeight(1);
        notify = (TextView) view.findViewById(R.id.comment_list_notify);
        notify.setText("暂无数据");
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), LevelLowerBranchActivity.class);
                intent.putExtra("ifmshop", ifmshop);
                intent.putExtra("userName", data.get(i).getStoreName());
                intent.putExtra("memberId",data.get(i).getMemberId());
                startActivity(intent);
            }
        });
        if (isFirstAccess) {
            isFirstAccess = false;
            showDialog();
            inflateData();
        }
        return view;
    }

    private void inflateData() {
        VolleyRequest.GetCookieRequest(getActivity(), url2, new VolleyRequest.HttpStringRequsetCallBack() {
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
                    } else if (curpage <= (totalItem + page - 1) / page) {
                        List<Json_LowBra.DataEntity.StoreInfoEntity> storeInfoEntityList = infomatHome.getData().getStoreInfo();
                        for (Json_LowBra.DataEntity.StoreInfoEntity p : storeInfoEntityList) {
                            data.add(p);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        curpage--;
                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "网络错误，请检查网络！", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing())
                    hideDialog();
            }
        });
    }

    private void showDialog() {
        dialog = new LoadingDialog(getActivity());
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
}