package cn.mstar.store.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.mstar.mvp.aview.ReturnShopView;
import cn.mstar.mvp.model.ReturnListBn;
import cn.mstar.mvp.presenter.ReturnShopPresenter;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.KeyString;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.entity.OrderListItem;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.LogUtils;

/*
 * 创建人：Yangshao
 * 创建时间：2016/3/23 16:22
 * @version    退换货列表数据
 *
 */
public class ReturnShopActivity extends Activity implements ReturnShopView, PullToRefreshView.OnHeaderRefreshListener,
        PullToRefreshView.OnFooterRefreshListener {

    private final String TAG = "ReturnShopActivity";
    @Bind(R.id.title_back)
    ImageView titleBack;
    @Bind(R.id.title_name)
    TextView titleName;
    @Bind(R.id.lny_loading_layout)
    LinearLayout ly_loading;
    @Bind(R.id.lny_no_result)
    LinearLayout ly_noData;
    @Bind(R.id.id_lv_return)
    ListView idLvReturn;
    @Bind(R.id.lny_network_error_view)
    LinearLayout ly_netError;
    @Bind(R.id.wifi_retry)
    TextView networkError_reset;

    @Bind(R.id.container)
    PullToRefreshView listContainer;

    private ListView lv;
    private ReturnShopPresenter returnShopPresenter;
    private RetrunShopAdpater adpater;
    private List<ReturnListBn.DataEntity.ReturnInfoEntity> returnListBns;

    private static final int PULL_REFRESH = 1;
    private static final int PULL_LOAD = 2;
    private int tempCurpage = 1;
    private int curpage = 1;
    private int pageCount = 10;
    private int pullState = 1;
    private int list_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_return_shop);
        ButterKnife.bind(this);
        returnShopPresenter = new ReturnShopPresenter(this);
        initViewParma();
        onGetNetData();
    }

    private void initViewParma() {
        titleName.setText("退换货");
        titleBack.setVisibility(View.VISIBLE);
        lv = (ListView) findViewById(R.id.id_lv_return);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                Intent intent = new Intent(ReturnShopActivity.this, ReturnShopDetailActivity.class);
                intent.putExtra(KeyString.ORDERNUMBER, viewHolder.returnInfoEntity.getRefundSn());
                startActivity(intent);
            }
        });
        returnListBns = new ArrayList<>();
        adpater = new RetrunShopAdpater();
        lv.setAdapter(adpater);
        listContainer.setOnHeaderRefreshListener(this);
        listContainer.setOnFooterRefreshListener(this);
        lv.addFooterView(onFoodView(), null, false);
        lv.setFooterDividersEnabled(false);
    }

    @Override
    public void getRturnListBn(List<ReturnListBn.DataEntity.ReturnInfoEntity> mreturnListBns) {
        if (returnListBns != null) {
            if (pullState != PULL_LOAD) {
                returnListBns.clear();
            }
            returnListBns.addAll(mreturnListBns);
            endNetRequest();
        }
    }

    @Override
    public void getListCount(int count) {
        list_count = count;
        if (returnListBns.size() < list_count) {
            setOnFoodView("上拉加载更多");
        } else {
            setOnFoodView("加载完成");
        }
    }

    private void endNetRequest() {
        adpater.notifyDataSetChanged();
        tempCurpage = curpage;
        if (pullState == PULL_LOAD) {
            listContainer.onFooterRefreshComplete();
        } else if (pullState == PULL_REFRESH) {
            listContainer.onHeaderRefreshComplete();
        }
        pullState = 0;
    }


    @Override
    public String setUrl() {
        LogUtils.e(TAG + "退货列表地址" + AppURL.RETURN_SHOP_lIST + " &tokenKey=" + MyApplication.getInstance().tokenKey + "&curpage=" + curpage + "&page=" + pageCount);
        return AppURL.RETURN_SHOP_lIST + "&tokenKey=" + MyApplication.getInstance().tokenKey + "&curpage=" + curpage + "&page=" + pageCount;
    }

    @Override
    public void showLoading() {
        dismissAllView();
        ly_loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoResult() {
        dismissAllView();
        curpage = tempCurpage;
        ly_noData.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissAllView() {
        ly_loading.setVisibility(View.GONE);
        ly_noData.setVisibility(View.GONE);
        ly_netError.setVisibility(View.GONE);
    }

    @OnClick(R.id.wifi_retry)
    public void onGetNetData(){
        returnShopPresenter.getNetData(ReturnShopActivity.this, setUrl());
    }


    @Override
    public void showNetError() {
        dismissAllView();
        curpage = tempCurpage;
        ly_netError.setVisibility(View.VISIBLE);
    }

    @Override
    public void showContent() {

    }

    RelativeLayout relativeLayoutText;
    TextView textView;

    public View onFoodView() {
        relativeLayoutText = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dip2px(30));
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //layoutParams.setMargins(0, 15, 0, 0);
        textView = new TextView(this);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        relativeLayoutText.addView(textView, layoutParams);
        //listText.setBackgroundColor(Color.WHITE);
        return relativeLayoutText;
    }


    public void setOnFoodView(String text) {
        textView.setText(text);
    }

    /**
     * dip转换px
     */
    public int dip2px(float dip) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }


    @Override
    public void dismissLoading() {
        ly_loading.setVisibility(View.GONE);
    }



    @OnClick(R.id.title_back)
    public void onClick() {
        this.finish();
    }


    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (returnListBns.size() < list_count) {
            tempCurpage = curpage;
            curpage++;
            pullState = PULL_LOAD;
            onGetNetData();
        } else {
            Toast.makeText(this, "没有更多数据", Toast.LENGTH_SHORT).show();
            view.onFooterRefreshComplete();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        tempCurpage = curpage;
        curpage = 1;
        pullState = PULL_REFRESH;
        onGetNetData();
    }


    private class RetrunShopAdpater extends BaseAdapter {

        @Override
        public int getCount() {
            return returnListBns == null ? 0 : returnListBns.size();
        }

        @Override
        public Object getItem(int i) {
            return returnListBns.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(ReturnShopActivity.this).inflate(R.layout.item_return_shop, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.returnInfoEntity = returnListBns.get(position);
            viewHolder.idTvPrice.setText("退款金额¥：" + viewHolder.returnInfoEntity.getAmount());
            viewHolder.idTvSpec.setText(viewHolder.returnInfoEntity.getSellerState());
            viewHolder.idTvShopName.setText(viewHolder.returnInfoEntity.getGoodsName());
            viewHolder.idTvDate.setText(viewHolder.returnInfoEntity.getAddtime());
            viewHolder.idTvState.setText(viewHolder.returnInfoEntity.getRefundType());
            ImageLoader.getInstance().displayImage(viewHolder.returnInfoEntity.getPic(), viewHolder.img, ImageLoadOptions.getOptions());
            return view;

        }


        private View inflateItem(final OrderListItem.ProInfo item) {
            View view = LayoutInflater.from(ReturnShopActivity.this).inflate(R.layout.item_return_shop_item, null);
            ViewHolder viewHolder = new ViewHolder(view);
            OrderListItem.ProInfo entity = item;
            ImageLoader.getInstance().displayImage(entity.pic, viewHolder.img, ImageLoadOptions.getOptions());
            viewHolder.idTvShopName.setText(entity.name);
            viewHolder.idTvPrice.setText(entity.pic);
            return view;
        }

    }

    class ViewHolder {

        @Bind(R.id.id_tv_state)
        TextView idTvState;
        @Bind(R.id.id_tv_date)
        TextView idTvDate;
        @Bind(R.id.img)
        public ImageView img;
        @Bind(R.id.id_tv_shop_name)
        public TextView idTvShopName;
        @Bind(R.id.id_tv_spec)
        public TextView idTvSpec;
        @Bind(R.id.id_tv_price)
        public TextView idTvPrice;
        @Bind(R.id.linear_container_view)
        LinearLayout linearContainerView;
        ReturnListBn.DataEntity.ReturnInfoEntity returnInfoEntity;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
    }
}
