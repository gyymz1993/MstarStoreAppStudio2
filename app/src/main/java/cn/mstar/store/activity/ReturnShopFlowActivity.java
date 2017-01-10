package cn.mstar.store.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.common.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.mvp.model.JsonRetuenFlowDB;
import cn.mstar.mvp.model.JsonReturnDetail;
import cn.mstar.mvp.model.ReturnFlowDB;


import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.KeyString;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/*
 * 创建人：Yangshao
 * 创建时间：2016/3/22 13:13
 * @version    退款流程页面
 *
 */
public class ReturnShopFlowActivity extends Activity {
    @Bind(R.id.title_back)
    ImageView titleBack;
    @Bind(R.id.title_name)
    TextView titleName;
    @Bind(R.id.id_lv_flow)
    ListView idLvFlow;
    ReturnShoopFlowAdapter adapter;
    private List<ReturnFlowDB> returnFlowDBss;
    private String refundSn;
    private final String TAG="ReturnShopFlowActivity";

    private List<String> msgArray;
    private List<String> dataArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_returnshop_flow);
        ButterKnife.bind(this);
        refundSn = getIntent().getStringExtra(KeyString.ORDERNUMBER);
        initView();
        loadNetData();
    }

    private void initView() {
        titleName.setText("退货流程");
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void loadNetData() {
        if (getUrl()==null){
            Toast.makeText(this, "订单编号有误", Toast.LENGTH_LONG).show();
            return;
        }
        LogUtils.e(TAG + "getUrl" + getUrl());
        VolleyRequest.GetCookieRequest(this, getUrl(), new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                JsonRetuenFlowDB jsonRetuenFlowDB = new Gson().fromJson(result, JsonRetuenFlowDB.class);
                if (jsonRetuenFlowDB.getError().equals("0")) {
                    JsonRetuenFlowDB.DataEntity jsonRetuenFlowDBData = jsonRetuenFlowDB.getData();
                    initData(jsonRetuenFlowDBData);
                    setViewData();
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    private void setViewData() {
        returnFlowDBss=new ArrayList<>();
        for (int i = 0; i < msgArray.size(); i++) {
            ReturnFlowDB entity = new ReturnFlowDB();
            entity.setDate(dataArray.get(i));
            if (i % 2 == 0) {
                entity.setIsComMeg(false);// 收到的消息
            } else {
                entity.setIsComMeg(true);// 自己发送的消息
            }
            entity.setMessage(msgArray.get(i));
            returnFlowDBss.add(entity);
        }

        adapter = new ReturnShoopFlowAdapter();
        idLvFlow.setAdapter(adapter);
    }

    private String getUrl() {
        if (refundSn==null||refundSn.equals("")){
            return null;
        }
        return AppURL.RETUTN_SHOP_FLOW + "&refundSn=" + refundSn;
    }


    /**
     *
     */
    public void initData(JsonRetuenFlowDB.DataEntity jsonRetuenFlowDBData) {
        msgArray=new ArrayList<>();
        dataArray=new ArrayList<>();
        if (jsonRetuenFlowDBData.getOneStep().equals("")){
            return;
        }if (jsonRetuenFlowDBData.getTwoStep().equals("")){
            msgArray.add(jsonRetuenFlowDBData.getOneStep());
            dataArray.add(jsonRetuenFlowDBData.getOneStepTime());
            return;
        }
        if (jsonRetuenFlowDBData.getThreeStep().equals("")){
            msgArray.add(jsonRetuenFlowDBData.getOneStep());
            dataArray.add(jsonRetuenFlowDBData.getOneStepTime());
            msgArray.add(jsonRetuenFlowDBData.getTwoStep());
            dataArray.add(jsonRetuenFlowDBData.getTwoStepTime());
            return;
        }
        if (jsonRetuenFlowDBData.getFourStep().equals("")){
            msgArray.add(jsonRetuenFlowDBData.getOneStep());
            dataArray.add(jsonRetuenFlowDBData.getOneStepTime());
            msgArray.add(jsonRetuenFlowDBData.getTwoStep());
            dataArray.add(jsonRetuenFlowDBData.getTwoStepTime());
            msgArray.add(jsonRetuenFlowDBData.getThreeStep());
            dataArray.add(jsonRetuenFlowDBData.getThreeStepTime());
            return;
        }
        if (jsonRetuenFlowDBData.getFineStep().equals("")){
            msgArray.add(jsonRetuenFlowDBData.getOneStep());
            dataArray.add(jsonRetuenFlowDBData.getOneStepTime());
            msgArray.add(jsonRetuenFlowDBData.getTwoStep());
            dataArray.add(jsonRetuenFlowDBData.getTwoStepTime());
            msgArray.add(jsonRetuenFlowDBData.getThreeStep());
            dataArray.add(jsonRetuenFlowDBData.getThreeStepTime());
            msgArray.add(jsonRetuenFlowDBData.getFourStep());
            dataArray.add(jsonRetuenFlowDBData.getFourStepTime());
            return;
        }else {
            msgArray.add(jsonRetuenFlowDBData.getOneStep());
            dataArray.add(jsonRetuenFlowDBData.getOneStepTime());
            msgArray.add(jsonRetuenFlowDBData.getTwoStep());
            dataArray.add(jsonRetuenFlowDBData.getTwoStepTime());
            msgArray.add(jsonRetuenFlowDBData.getThreeStep());
            dataArray.add(jsonRetuenFlowDBData.getThreeStepTime());
            msgArray.add(jsonRetuenFlowDBData.getFourStep());
            dataArray.add(jsonRetuenFlowDBData.getFourStepTime());
            msgArray.add(jsonRetuenFlowDBData.getFineStep());
            dataArray.add(jsonRetuenFlowDBData.getFineStepTime());
        }


    }

    private static interface IMsgViewType {
        int IMVT_COM_MSG = 0; //收到的消息
        int IMVT_TO_MSG = 1;
    }


    public class ReturnShoopFlowAdapter extends BaseAdapter {

        private static final int TIMECOUNT = 2;

        @Override
        public int getCount() {
            return returnFlowDBss == null ? 0 : returnFlowDBss.size();
        }

        @Override
        public Object getItem(int i) {
            return returnFlowDBss.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * 得到Item的类型，是对方发过来的消息，还是自己发送出去的
         */
        public int getItemViewType(int position) {
            ReturnFlowDB entity = returnFlowDBss.get(position);
            if (entity.isComMeg()) {//收到的消息
                return IMsgViewType.IMVT_COM_MSG;
            } else {//自己发送的消息
                return IMsgViewType.IMVT_TO_MSG;
            }
        }

        public int getViewTypeCount() {
            return TIMECOUNT;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ReturnFlowDB returnFlowDB = returnFlowDBss.get(i);
            boolean isComMsg = returnFlowDB.isComMeg();
            ViewHolder viewHolder;
            if (view == null) {
                if (isComMsg) {
                    view = LayoutInflater.from(ReturnShopFlowActivity.this).inflate(R.layout.chatting_item_msg_text_left, null);
                } else {
                    view = LayoutInflater.from(ReturnShopFlowActivity.this).inflate(R.layout.chatting_item_msg_text_right, null);
                }
                viewHolder=new ViewHolder(view);
                view.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) view.getTag();
            }
            viewHolder.tvSendtime.setText(returnFlowDB.getDate());
            viewHolder.tvChatcontent.setText(returnFlowDB.getMessage());
            return view;
        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_sendtime)
        TextView tvSendtime;
        @Bind(R.id.tv_chatcontent)
        TextView tvChatcontent;

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
