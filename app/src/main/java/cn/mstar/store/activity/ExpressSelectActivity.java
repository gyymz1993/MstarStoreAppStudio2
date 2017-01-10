package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
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
import cn.mstar.store.utils.VolleyRequest;

/*
 * 创建人：Yangshao
 * 创建时间：2016/1/25 11:00
 * @version
 *
 */
public class ExpressSelectActivity extends BaseActivity {

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.id_lv_express)
    ListView idLvExpress;

    private ExpressAdapter expressAdapter;
    private List<Express> expresses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.express_main);
        ButterKnife.bind(this);
        initView();
        showDialog();
        getNetData();
    }

    public void initView() {
        title.setText("物流公司");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
        expressAdapter = new ExpressAdapter();
        idLvExpress.setAdapter(expressAdapter);
        idLvExpress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                expressAdapter.selectOne(i);
                Intent intent = new Intent();
                intent.putExtra("eName", expresses.get(i).eName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void getNetData() {
        String url = AppURL.LOGISTICS_COMPANY + "&tokenKey=" + MyApplication.getInstance().tokenKey;
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    JsonObject j = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                    JsonArray jArr = gson.fromJson(j, JsonObject.class).get("mdata").getAsJsonArray();
                    Express[] arr = gson.fromJson(jArr, Express[].class);
                    expresses.clear();
                    Collections.addAll(expresses, arr);
                    expressAdapter.notifyDataSetChanged();
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(ExpressSelectActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(ExpressSelectActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            }
        }
    };


    public class ExpressAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return expresses == null ? 0 : expresses.size();
        }

        @Override
        public Object getItem(int i) {
            return expresses.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void selectOne(int id) {
            Express express = expresses.get(id);

            for (Express e : expresses) {
                if (e.eName.equals(express.eName)) {
                    e.setIsChecke(!e.isChecke());
                } else {
                    e.setIsChecke(false);
                }

            }
            notifyDataSetChanged();
        }


        public View getViewByPosition(int pos, ListView listView) {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
            if (pos < firstListItemPosition || pos > lastListItemPosition) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHoleder viewHoleder;
            if (view == null) {
                viewHoleder = new ViewHoleder();
                view = LayoutInflater.from(ExpressSelectActivity.this).inflate(R.layout.item_express, null);
                viewHoleder.radioButton = (RadioButton) view.findViewById(R.id.id_rb_select_express);
                viewHoleder.imageView = (ImageView) view.findViewById(R.id.id_ig_exicon);
                viewHoleder.textView = (TextView) view.findViewById(R.id.id_ex_tv);
                view.setTag(viewHoleder);
            } else {
                viewHoleder = (ViewHoleder) view.getTag();
            }

            viewHoleder.express = expresses.get(i);
            viewHoleder.radioButton.setChecked(viewHoleder.express.isChecke());
            viewHoleder.textView.setText(viewHoleder.express.eName);
            return view;
        }

        class ViewHoleder {
            RadioButton radioButton;
            ImageView imageView;
            TextView textView;
            Express express;
        }
    }

    static class Express {
        private boolean isChecke;
        public String Id;
        public String eName;
        public String eCode;
        private int expressIcon;

        public boolean isChecke() {
            return isChecke;
        }

        public void setIsChecke(boolean isChecke) {
            this.isChecke = isChecke;
        }


        public int getExpressIcon() {
            return expressIcon;
        }

        public void setExpressIcon(int expressIcon) {
            this.expressIcon = expressIcon;
        }
    }

}
