package cn.mstar.store.customviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.activity.IndentDetailsActivity;
import cn.mstar.store.adapter.GoodsManagement_InnerAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.KeyString;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.entity.OrderListItem;
import cn.mstar.store.functionutils.HttpUtils;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * @action: 订单详情的入口  查看所所有订单
 * @author: YangShao
 * @date: 2015/10/20 @time: 9:11
 */
public class RefreshableMockFragment extends Fragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    private final String TAG="RefreshableMockFragment";
    // views
    protected LinearLayout lny_loading_layout, lny_network_error_layout, lny_noresult;
    protected ListView lv_list;
    private View rootView,footerview;
    private View lny_loading,loadingCompletet;
    private int preLast = 0;
    private TextView wifi_retry;
    private String BASE_LINK,tokenKey;
    private GoodsManagement_InnerAdapter adapter;
    private boolean finalFooterSet = false;
    private boolean allowToAppend = false;
    private int maxPage = -1, totalItem = -1,pageCount = 10, curpage = 1;
    public static RefreshableMockFragment getInstance(String link, String tk) {
        RefreshableMockFragment frg = new RefreshableMockFragment();
        Bundle argz = new Bundle();
        argz.putString(KeyString.LINK, link);
        argz.putString(KeyString.TK, tk);
        frg.setArguments(argz);
        return frg;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_refreshable_mock, container, false);
        ButterKnife.bind(rootView);
        initViewParams();
        initViews(rootView);
        loadData();
        showLoadView();
        initOnclick();
        return rootView;
    }

    protected void initViews(View view) {
        lny_loading_layout = (LinearLayout) view.findViewById(R.id.lny_loading_layout);
        lny_network_error_layout = (LinearLayout) view.findViewById(R.id.lny_network_error_view);
        lny_noresult = (LinearLayout) view.findViewById(R.id.lny_no_result);

        lv_list = (ListView) rootView.findViewById(R.id.listview);
        footerview = LayoutInflater.from(getActivity()).inflate(R.layout.product_list_footer_view, null);
        lny_loading = footerview.findViewById(R.id.lny_loading_layout);
        loadingCompletet = footerview.findViewById(R.id.tv_loadingcomplete);
    }



    private void initOnclick() {
        lv_list.setOnScrollListener(this);
        lv_list.setOnItemClickListener(this);
        wifi_retry = (TextView) rootView.findViewById(R.id.wifi_retry);
        wifi_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });
    }

    private void initViewParams() {
        BASE_LINK = getArguments().getString(KeyString.LINK);
        tokenKey = getArguments().getString(KeyString.TK);
        BASE_LINK = BASE_LINK + "&key=" + tokenKey;
        curpage = 1;
        adapter = null;
        allowToAppend = true;
    }

    public void reload() {
        maxPage = -1;
        curpage = 1;
        finalFooterSet = false;
        adapter = null;
        lv_list.removeFooterView(footerview);
        lny_loading.setVisibility(View.GONE);
        loadingCompletet.setVisibility(View.GONE);
        lv_list.setOnScrollListener(this);
        lv_list.setOnItemClickListener(this);
        // 再次进入页面的时候：
        hide_all();
        loadData();
        allowToAppend = true;
    }

    private void appendData() {
        if (allowToAppend) {
            if (maxPage == -1 || curpage < maxPage) {
                curpage++;
                loadData();
            } else {
                noResultFound();
            }
        }
    }


    private void loadData() {
        String baseLink = BASE_LINK + "&curpage=" + curpage + "&page=" + pageCount;
        LogUtils.e(TAG+"loadData  URL:"+baseLink);
        VolleyRequest.GetCookieRequest(getActivity(), baseLink, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    Gson gson = new Gson();
                    JsonElement elm = gson.fromJson(result, JsonElement.class).getAsJsonObject().get("data");
                    if (maxPage == -1) {
                        // get the max item count.
                        try {
                            totalItem = elm.getAsJsonObject().get("list_count").getAsInt();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // each page needs
                        maxPage = totalItem / pageCount;
                        if (!(totalItem % pageCount == 0)) {
                            maxPage++;
                        }
                    }
                    OrderListItem[] tmp = gson.fromJson(elm.getAsJsonObject().get("OrderList"), OrderListItem[].class);
                    if (tmp == null && tmp.length == 0) {
                        noResultFound();
                        return;
                    }
                    for (OrderListItem item : tmp) {
                        OrderListItem.OrderInfo d = item.orderInfo;
                        HttpUtils.getWuLiuState(getActivity(), d);
                    }
                    setUpAdapter(tmp);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    noResultFound();
                } catch (Exception e) {
                    e.printStackTrace();
                    noResultFound();
                } finally {
                    noResultFound();
                }
            }

            @Override
            public void onFail(String error) {
                networkExceptionError();
            }
        });

    }

    private void setUpAdapter(OrderListItem[] order_items) {
        final List<OrderListItem> itemz = fromArrayToList(order_items);
        if (adapter == null) {
            adapter = new GoodsManagement_InnerAdapter(getActivity(), itemz);
            if (lv_list != null) {
                lv_list.addFooterView(footerview);
                lv_list.setAdapter(adapter);
                hide_all();
                lv_list.setVisibility(View.VISIBLE);
            }
        } else {
            if (curpage == 1)
                adapter = new GoodsManagement_InnerAdapter(getActivity(), itemz);
            else
                adapter.addAll(itemz);
        }

        if (itemz.size() < pageCount) {
            setFinalFooter();
        }
        setFinalFooter();
    }

    private List<OrderListItem> fromArrayToList(OrderListItem[] order_items) {
        List<OrderListItem> list = new ArrayList<>();
        for (OrderListItem it : order_items) {
            list.add(it);
        }
        return list;
    }



    /**
     * @action: 每次都需要设置ListVie
     * @author: YangShao
     * @date: 2015/10/21 @time: 8:27
     */
    private void setFinalFooter() {
//        if (listState!=null){
//            lv_list.onRestoreInstanceState(listState);
//        }

        //LogUtils.e("记忆中ListView位置"+listPos);
//                if (listPos!=0){
//                    LogUtils.e("设置记录位置"+listPos);
//                    lv_list.setSelection(listPos);
//                    //设置记录位置
//                }

        if ((!finalFooterSet && totalItem == adapter.getCount()) || (lny_loading.getVisibility() == View.VISIBLE && totalItem == adapter.getCount())) {
            lny_loading.setVisibility(View.GONE);
            loadingCompletet.setVisibility(View.VISIBLE);
            if (lny_loading.getVisibility() == View.GONE && loadingCompletet.getVisibility() == View.VISIBLE) {
                finalFooterSet = true;
                allowToAppend = false;
            }
        }
    }


    protected void showLoadView(){
        hide_all();
        lny_loading_layout.setVisibility(View.VISIBLE);
    }

    protected void hide_all() {
        lny_loading_layout.setVisibility(View.GONE);
        lny_network_error_layout.setVisibility(View.GONE);
        lny_noresult.setVisibility(View.GONE);
        lv_list.setVisibility(View.GONE);
    }


    protected void networkExceptionError() {
        hide_all();
        lny_network_error_layout.setVisibility(View.VISIBLE);
    }


    protected void noResultFound() {
        if (adapter == null) {
            hide_all();
            lny_noresult.setVisibility(View.VISIBLE);
        } else {
            setFinalFooter();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        reload();
    }

    public static Parcelable listState;
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //ListPos记录当前可见的List顶端的一行的位置
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            // listPos=lv_list.getFirstVisiblePosition();
            //  LogUtils.e("开始记忆当前值"+listPos);
           // listState = lv_list.onSaveInstanceState();
        }
        if (adapter != null && adapter.getCount() >= totalItem) {
            setFinalFooter();
        }
    }

    @Override
    public void onScroll(AbsListView lw, final int firstVisibleItem,
                         final int visibleItemCount, final int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItemCount;
        if (lastItem >= totalItemCount - 1) {
            if (preLast != lastItem) {
                preLast = lastItem;
                appendData();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < adapter.getCount()) {
            final OrderListItem orderItem = (OrderListItem) adapter.getItem(position);
            //代发货
            if (Integer.valueOf(orderItem.orderInfo.status) == 20) {
            }
            final LoadingDialog dialog = new LoadingDialog(getActivity());
            dialog.show();
            final String link = AppURL.GOPAY_FOR_ORDER + "&key=" + Utils.getTokenKey((MyApplication) getActivity().getApplication()) + "&OrderNum=" + orderItem.orderInfo.orderId;
            VolleyRequest.GetCookieRequest(getActivity(), link, new VolleyRequest.HttpStringRequsetCallBack() {
                @Override
                public void onSuccess(String result) {
                    if (!"".equals(result)) {
                        Intent intent = new Intent(getActivity(), IndentDetailsActivity.class);
                        intent.setAction(MyAction.goPayAction);
                        L.e(orderItem.orderInfo.orderId);
                        intent.putExtra("data", result);
                        intent.putExtra("link", link);
                        intent.putExtra("status", Integer.valueOf(orderItem.orderInfo.status));
                        if (dialog != null) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                        startActivityForResult(intent, Activity.RESULT_OK);
                    } else {
                        CustomToast.makeToast(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT);
                    }
                    if (dialog != null) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFail(String error) {
                    CustomToast.makeToast(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT);
                    if (dialog != null) {
                        dialog.cancel();
                    }
                }
            });
        }
    }
}
