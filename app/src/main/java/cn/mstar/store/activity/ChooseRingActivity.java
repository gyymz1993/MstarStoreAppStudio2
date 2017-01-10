package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.mstar.mvp.model.JsonRingDB;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;
import cn.mstar.store.view.ChooseRingView;

/*
 * 创建人：Yangshao
 * 创建时间：2016/4/18
 * @version
 *
 */
public class ChooseRingActivity extends ChooseRingBaseActivity {

    private final String TAG = "ChooseRingActivity";
    /**
     * 存放所有选择的id   首先存放默认值
     **/
    private Map<String, Integer> idMaps = new HashMap<>();

    /**
     * 存放所有选择的Value   首先存放默认值
     **/
    private Map<String, String> valueMaps = new HashMap<>();
    /**
     * 所有的类型
     **/
    private List<String> types = new ArrayList<>();
    /**
     * 类型中的键值对
     **/
    private List<Map<String, Integer>> maps = new ArrayList<>();
    /**
     * 存放所有的键
     **/
    private List<List<String>> map_key = new ArrayList<>();

    //图形单独存储
    private List<String> ringSrcList = new ArrayList();
    private Map<String, Integer> ringSrcMap = new HashMap<>();
    private EditText edPriceMin, edPriceMax;
    private static int IN_SLELCT_RING1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IN_SLELCT_RING1 = 1;
        if (getIntent().getAction() == null) return;
        if (getIntent().getAction().equals(MyAction.ringSelect)) {
            IN_SLELCT_RING1 = 2;
        }
    }


    public void loadNetData() {
        VolleyRequest.GetCookieRequest(this, AppURL.NAKED_DIAMOND, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                dismissAllView();
                LogUtils.e(TAG + "result" + result);
                String srcName = null;
                JsonRingDB jsonRingDB = new Gson().fromJson(result, JsonRingDB.class);
                JsonRingDB.DataEntity dataEntity = jsonRingDB.getData();
                if (jsonRingDB != null) {
                    List<JsonRingDB.DataEntity.ProInfoEntity> proInfos = dataEntity.getProInfo();
                    if (proInfos == null || proInfos.size() == 0) {
                        showNoResult();
                        return;
                    }
                    for (JsonRingDB.DataEntity.ProInfoEntity proInfo : proInfos) {
                        srcName = proInfo.getName();
                        types.add(srcName);  //添加文字类型
                        List<JsonRingDB.DataEntity.ProInfoEntity.ValueEntity> values = proInfo.getValue();
                        if (srcName.equals("图形")) {
                            //默认没有选中图形类型
                            idMaps.put(srcName, /*Integer.valueOf(values.get(0).getAttrValueId().toString())*/0);
                        }
                        if (values == null) return;
                        Map<String, Integer> allValue = new HashMap<>();
                        List<String> allName = new ArrayList();
                        for (JsonRingDB.DataEntity.ProInfoEntity.ValueEntity valueEntity : values) {
                            String attrValueId = valueEntity.getAttrValueId();  //所有ID
                            String attrValueName = valueEntity.getAttrValueName();  //所有value
                            if (srcName.equals("图形")) {
                                ringSrcList.add(attrValueName);
                                ringSrcMap.put(attrValueName, Integer.valueOf(attrValueId));
                            }
                            allValue.put(attrValueName, Integer.valueOf(attrValueId));
                            allName.add(attrValueName);
                        }
                        maps.add(allValue);
                        map_key.add(allName);
                    }
                    bottom_action.setVisibility(View.VISIBLE);
                    addLayoutView(srcName);
                }

            }

            @Override
            public void onFail(String error) {
                showNetError();
            }
        });
    }

    @Override
    public void setTitile() {
        titleName.setText("选择裸戒");
    }


    private void addLayoutView(String srcName) {
        LogUtils.e(TAG + types.size());
        for (int i = 0; i < types.size(); i++) {
            RelativeLayout relativeLayout = new RelativeLayout(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
            lp.setMargins(0, 50, 0, 0);
            if (i == types.size() - 1) {
                lp.setMargins(0, 50, 0, 50);
            }
            ChooseRingView chooseRingView = new ChooseRingView(this);
            Map<String, Integer> map = maps.get(i);
            List<String> list = map_key.get(i);
            srcName = types.get(i);
            if (srcName.equals("图形")) {
                continue;
            }
            idMaps.put(srcName, map.get(list.get(0))); //设置默认ID
            valueMaps.put(srcName, list.get(0));   //设置默认Value
            chooseRingView.setOnSelectRingListener(srcName, this);
            if (map != null && list != null) {
                chooseRingView.onLable(map, srcName, list);
            }
            relativeLayout.addView(chooseRingView, lp);
            linearLayout.addView(relativeLayout);
        }
        //质量View
        View view = LayoutInflater.from(this).inflate(R.layout.item_select_price, null);
        if (IN_SLELCT_RING1 == 2) {
            view.setVisibility(View.GONE);
        }
        edPriceMin = (EditText) view.findViewById(R.id.id_id_tv1);
        edPriceMax = (EditText) view.findViewById(R.id.id_id_tv2);
        linearLayout.addView(view);
        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                idMaps.put("图形", ringSrcMap.get(ringSrcList.get(i)));
            }
        });
        BaseAdapter recommendfyAdapter = new RecommendfyAdapter(ringSrcList);
        gridView.setAdapter(recommendfyAdapter);
    }


    public String getUrl() {
        String price_min = "";
        String price_max = "";
        if (!edPriceMin.getText().toString().equals("")) {
            price_min = edPriceMin.getText().toString();
        }
        if (!edPriceMax.getText().toString().equals("")) {
            price_max = edPriceMax.getText().toString();
        }
//        if (price_min.equals("")&&price_max.equals("")){
//            return "";
//        }else {
//            if (Integer.valueOf(price_min)>Integer.valueOf(price_max)){
//                Toast.makeText(this,"请检查参数是否有误",Toast.LENGTH_LONG).show();
//                return "";
//            }
//        }
        StringBuilder sb = new StringBuilder();
        sb.append("attr_id=");
        for (int i = 0; i < types.size(); i++) {
            int id;
            try {
                id = idMaps.get(types.get(i));
            } catch (Exception e) {
                continue;
            }
            LogUtils.e(TAG + types.get(i) + ":" + id);
            sb.append(id);
            if (i != (types.size() - 1)) {
                sb.append("|");
            }
        }
        sb.append("&price=");
        sb.append(price_min);
        sb.append(",");
        sb.append(price_max);
        LogUtils.e(TAG + "url" + sb.toString());
        return sb.toString();
    }


    @Override
    public void onCheckedId(String name, int id) {
        idMaps.put(name, id);
    }

    @Override
    public void onCheckedValue(String name, String value) {
        valueMaps.put(name, value);
    }


    public String getPorUrl() {
        String DWT = "主石重量";
        String DNEATNESS = "钻石净度";
        String DCUT = "钻石切工";
        String DSHAPE = "图形";
        String DCOLOR = "钻石颜色";
        String dwt = "";
        String dnea = "";
        String dcut = "";
        int dshape = 0;
        String dColor = "";
        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);

            if (type.equals(DWT)) {
                dwt = valueMaps.get(DWT);
            }
            if (type.equals(DNEATNESS)) {
                dnea = valueMaps.get(DNEATNESS);
            }
            if (type.equals(DCUT)) {
                dcut = valueMaps.get(DCUT);
            }
            if (type.equals(DSHAPE)) {
            }
            if (type.equals(DCOLOR)) {
                dColor = valueMaps.get(DCOLOR);
            }
        }
        try {
            dshape = idMaps.get("图形");
        } catch (Exception e) {
        }
        String url = AppURL.RING_NAKED_BARE + "&tokenKey=" + MyApplication.getInstance().tokenKey + "&dwt=" +
                dwt + "&dcolor=" + dColor + "&dneatness=" + dnea + "&dcut=" + dcut + "&dshape=" + dshape;
        Log.e(TAG, url);
        return url;
    }

    @Override
    public void onClick(View view) {
        if (view == commitbtn) {
            if (IN_SLELCT_RING1 == 2) {
                submit();
            } else {
                Intent intent = new Intent(ChooseRingActivity.this, ChooseRingListActivity.class);
                if (getUrl().equals("")) {
                    Toast.makeText(this, "请检查参数是否有误", Toast.LENGTH_LONG).show();
                    return;
                }
                intent.putExtra("url", getUrl());
                startActivity(intent);
            }
        }
        if (view == titleBack) {
            finish();
        }
    }


    public void submit() {
        VolleyRequest.GetCookieRequest(this, getPorUrl(), new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                try {
                    LogUtils.e(TAG + "result" + result);
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Log.e(TAG, TAG + ":" + message);
                    JsonObject jsData = gson.fromJson(result, JsonObject.class).getAsJsonObject("data");
                    int diamondId = jsData.get("diamondId").getAsInt();
                    Intent intent = new Intent(ChooseRingActivity.this, ChooseRingFrameActivity.class);
                    int proid = Integer.valueOf(diamondId);
                    intent.putExtra("proid", proid);
                    startActivity(intent);
                } catch (Exception e) {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Log.e(TAG, TAG + ":" + message);
                }
            }

            @Override
            public void onFail(String error) {
                showNetError();
            }
        });
    }

    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }
}
