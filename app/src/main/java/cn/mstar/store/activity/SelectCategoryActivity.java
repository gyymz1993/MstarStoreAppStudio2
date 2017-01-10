package cn.mstar.store.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.entity.CategoryEntity;
import cn.mstar.store.utils.DensityUtil;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Shinelon on 2016/1/11.
 */
public class SelectCategoryActivity extends BaseActivity {

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.list)
    ListView list;

    private View contentView;

    private List<CategoryEntity> data;
    private CategoryAdapter adapter;

    private String url;
    private int state = 0;

    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_select_layout);
        ButterKnife.bind(this);
        initParams();
        getWidget();
        showDialog();
        getNetData();
    }

    private void initParams() {

        data = new ArrayList<>();
        adapter = new CategoryAdapter();
        url = AppURL.CATEGORY_SELECT + "&storeId=" + MyApplication.getInstance().storeId;
    }

    private void getWidget() {

        contentView = getWindow().getDecorView().findViewById(android.R.id.content);
        contentView.setVisibility(View.INVISIBLE);

        title.setText("选择分类");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);

        list.setAdapter(adapter);
        list.setOnItemClickListener(itemClickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            }
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (state == 1) {
                name = data.get(position).gcName;
            } else {
                name += "/" + data.get(position).gcName;
            }

            if (state != 3) {
                url = AppURL.CATEGORY_SELECT + "&parentId=" + data.get(position).gcId + "&storeId=" + MyApplication.getInstance().storeId;
                showDialog();
                getNetData();
            } else {
                Intent intent = new Intent();
                intent.putExtra("name", name);
                intent.putExtra("gcId", data.get(position).gcId);
                intent.putExtra("spId", data.get(position).typeId);
                setResult(11, intent);
                finish();
            }

            L.i("wcl-->" + "gcId" + data.get(position).gcId + ",gcName" + data.get(position).gcName + ",typeId" + data.get(position).typeId);
        }
    };

    private void getNetData() {
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                data.clear();
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    JsonArray jArr = gson.fromJson(result, JsonObject.class).get("data").getAsJsonArray();
                    CategoryEntity[] arr = gson.fromJson(jArr, CategoryEntity[].class);
                    Collections.addAll(data, arr);
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(SelectCategoryActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                dismissDialog();
                endNetRequest();
            }

            @Override
            public void onFail(String error) {
                dismissDialog();
                Toast.makeText(SelectCategoryActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                if (contentView.getVisibility() != View.VISIBLE) {
                    finish();
                }
            }
        });
    }

    private void endNetRequest() {
        Log.e("endNetRequest", "endNetRequest");
        if (contentView == null) return;
        if (contentView.getVisibility() != View.VISIBLE) {
            contentView.setVisibility(View.VISIBLE);
        }
        state++;
        adapter.notifyDataSetChanged();
    }

    class CategoryAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public CategoryEntity getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(SelectCategoryActivity.this);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
//            txt.setLayoutParams(lp);
            txt.setBackgroundColor(Color.WHITE);
            int padding = DensityUtil.dip2px(MyApplication.getInstance(), 10);
            txt.setPadding(padding, padding, padding, padding);
            txt.setText(getItem(position).gcName);
            Log.e("xxxxxxx", "getView" + getItem(position).gcName);
            return txt;
        }
    }

}
