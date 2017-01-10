package cn.mstar.store.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.view.ChooseRingView;

/**
 * Created by Administrator on 2016/7/12.
 */
public abstract class ChooseRingBaseActivity extends Activity implements ChooseRingView.OnSelectRingListener,View.OnClickListener {

    public final String TAG="ChooseRingActivity";
    public LinearLayout linearLayout;
    public GridView gridView;
    public Button commitbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ring_main);
        intiTitle();
        initView();
        initTypeLayout();
        showLoading();
        loadNetData();
        setTitile();
    }
    public abstract void loadNetData();
    public abstract void setTitile();
    public TextView titleName;
   public ImageView titleBack;
    public  RelativeLayout bottom_action;
    public void intiTitle() {
        titleBack = (ImageView) findViewById(R.id.title_back);
        titleName= (TextView) findViewById(R.id.title_name);
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);
    }

    public void initView() {
        commitbtn = (Button) findViewById(R.id.bt_commit);
        linearLayout= (LinearLayout) findViewById(R.id.id_ry_content);
        bottom_action = (RelativeLayout) findViewById(R.id.rel_shopping_car_bottom_action);
        commitbtn.setOnClickListener(this);
    }

    LinearLayout ly_loading;
    LinearLayout ly_noData;
    LinearLayout ly_netError;
    TextView tv_reset;
    public void initTypeLayout(){
        ly_loading = (LinearLayout) findViewById(R.id.lny_loading_layout);
        ly_noData = (LinearLayout) findViewById(R.id.lny_no_result);
        ly_netError = (LinearLayout) findViewById(R.id.lny_network_error_view);
        tv_reset = (TextView) findViewById(R.id.wifi_retry);
    }

    public void showLoading() {
        dismissAllView();
        ly_loading.setVisibility(View.VISIBLE);
    }

    public void showNoResult() {
        dismissAllView();
        ly_noData.setVisibility(View.VISIBLE);
    }

    public void dismissAllView() {
        ly_loading.setVisibility(View.GONE);
        ly_noData.setVisibility(View.GONE);
        ly_netError.setVisibility(View.GONE);
    }

    public void showNetError() {
        dismissAllView();
        ly_netError.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {

    }



    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }


    public class RecommendfyAdapter extends BaseAdapter{

        private List<String> ringSrcList;

        RecommendfyAdapter(List<String> ringSrcList){
            this.ringSrcList=ringSrcList;
        }
        @Override
        public int getCount() {
            return ringSrcList.size();
        }

        @Override
        public String getItem(int position) {
            return ringSrcList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView= LayoutInflater.from(ChooseRingBaseActivity.this).inflate(R.layout.item_gridview,parent, false);
                vh.img = (ImageView) convertView.findViewById(R.id.id_gv_ig);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            // vh.img.setImageResource(msrc.get(position));
            ImageLoader.getInstance().displayImage(ringSrcList.get(position), vh.img, ImageLoadOptions.getOptions());
            return convertView;
        }

        class ViewHolder {
            ImageView img;
        }
    }

}
