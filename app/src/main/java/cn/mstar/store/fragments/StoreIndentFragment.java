package cn.mstar.store.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.activity.MyStoreOrderDetail;
import cn.mstar.store.adapter.StoreIndentAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.entity.MyStoreOrdersEntity;
import cn.mstar.store.interfaces.ShowDialogInterface;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Shinelon on 2016/1/11.
 */
public class StoreIndentFragment extends Fragment implements PullToRefreshView.OnHeaderRefreshListener,
        PullToRefreshView.OnFooterRefreshListener {

    private final String TAG="StoreIndentFragment";
    private static final int PULL_REFRESH = 1;
    private static final int PULL_LOAD = 2;

    private PullToRefreshView listContainer;
    private ListView listView;

    private String tokenKey;
    private int indentState;
    private int page;
    private int tempCurpage;
    private int curpage;
    private int list_count;

    private StoreIndentAdapter adapter;
    private List<MyStoreOrdersEntity> data;

    private int pullState;

    private boolean isflag;

    public StoreIndentFragment(int indentState) {
        this.indentState = indentState;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParams();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.store_indent_layout, container, false);
        listContainer = (PullToRefreshView) view.findViewById(R.id.container);
        listContainer.setOnHeaderRefreshListener(this);
        listContainer.setOnFooterRefreshListener(this);
        listView = (ListView) view.findViewById(R.id.content);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
        return view;
    }

    private void getParams() {
        tokenKey = MyApplication.getInstance().tokenKey;
        page = 10;
        tempCurpage = 1;
        curpage = 1;
        data = new ArrayList<>();
        adapter = new StoreIndentAdapter(getActivity(), data);
        adapter.setState(indentState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint() && !isflag) {
            LogUtils.e(TAG + "调用onActivityCreated" + indentState);
            curpage = 1;
            showDialog();
            getNetData();
        }
        isflag = true; //仅第一次调用
    }


    private void getNetData() {
        String url = AppURL.MY_STORE_INDENT + "&tokenKey=" + tokenKey + "&state=" + indentState + "&page=" + page + "&curpage=" + curpage;
        LogUtils.e(TAG+"本店订单"+url);
        VolleyRequest.GetCookieRequest(getActivity(), url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                L.i("wcl-->" + result + indentState);
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    try {
                        JsonObject j = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                        JsonArray jArr = gson.fromJson(j, JsonObject.class).get("OrderList").getAsJsonArray();
                        list_count = gson.fromJson(j, JsonObject.class).get("list_count").getAsInt();
                        int payNum = gson.fromJson(j, JsonObject.class).get("payNum").getAsInt();
                        int deliverNum = gson.fromJson(j, JsonObject.class).get("deliverNum").getAsInt();
                        int receiverNum = gson.fromJson(j, JsonObject.class).get("receiverNum").getAsInt();
                        int returnNum = gson.fromJson(j, JsonObject.class).get("returnNum").getAsInt();
                        int finishNum = gson.fromJson(j, JsonObject.class).get("finishNum").getAsInt();
                        if (onOderNumberChange!=null){
                            onOderNumberChange.onPayNum(payNum);
                            onOderNumberChange.onDeliverNum(deliverNum);
                            onOderNumberChange.onReceiverNum(receiverNum);
                            onOderNumberChange.onReturnNum(returnNum);
                            onOderNumberChange.onFinishNum(finishNum);
                        }
                        MyStoreOrdersEntity[] arr = gson.fromJson(jArr, MyStoreOrdersEntity[].class);
                        if (pullState != PULL_LOAD)
                            data.clear();
                        Collections.addAll(data, arr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        data.clear();
                        String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                    endNetRequest(true);
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    endNetRequest(false);
                }
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                endNetRequest(false);
                dismissDialog();
            }
        });
    }

    OnOderNumberChange onOderNumberChange;
    public interface OnOderNumberChange{
        void  onPayNum(int payNum);
        void  onDeliverNum(int deliverNum);
        void  onReceiverNum(int receiverNum);
        void  onReturnNum(int returnNum);
        void  onFinishNum(int finishNum);
    }

    public void setOnOderNumberChange(OnOderNumberChange onOderNumberChange) {
        this.onOderNumberChange = onOderNumberChange;
    }

    private void endNetRequest(boolean isSuccess) {
        if (isSuccess) {
            adapter.notifyDataSetChanged();
            tempCurpage = curpage;
        } else {
            curpage = tempCurpage;
        }
        if (pullState == PULL_LOAD) {
            listContainer.onFooterRefreshComplete();
        } else if (pullState == PULL_REFRESH) {
            listContainer.onHeaderRefreshComplete();
        }
        pullState = 0;
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), MyStoreOrderDetail.class);
            intent.putExtra("OrderNum", data.get(position).orderInfo.orderId);
            intent.putExtra("status", indentState);
            startActivityForResult(intent, 11);
        }
    };

    /**
     *  当前用户看到一个fragment时可以执行一下代码
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isflag) {
            LogUtils.e(TAG+"setUserVisibleHint是否在显示" + indentState);
            curpage = 1;
            showDialog();
            getNetData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.e(TAG+"跳转回调OnActivityResult"+"requestCode");   //跳转回调
        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            curpage = 1;
            showDialog();
            getNetData();
        }
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (data.size() < list_count) {
            tempCurpage = curpage;
            curpage++;
            pullState = PULL_LOAD;
            getNetData();
        } else {
            Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
            view.onFooterRefreshComplete();
        }

    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        tempCurpage = curpage;
        curpage = 1;
        pullState = PULL_REFRESH;
        getNetData();
    }

    private void showDialog() {
        mShowDialogInterface.iShowDialog();
    }

    private void dismissDialog() {
        mShowDialogInterface.iDismissDialog();
    }

    private ShowDialogInterface mShowDialogInterface;

    public void setShowDialogInterface(ShowDialogInterface mShowDialogInterface) {
        this.mShowDialogInterface = mShowDialogInterface;
    }

}
