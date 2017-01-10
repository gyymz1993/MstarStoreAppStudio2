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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.PullToRefreshView;
import cn.mstar.store.entity.MyTrainEntity;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by 1 on 2016/1/5.
 */
public class MyTrainActivity extends BaseActivity implements PullToRefreshView.OnHeaderRefreshListener,
        PullToRefreshView.OnFooterRefreshListener {

    private static final int PULL_REFRESH = 1;
    private static final int PULL_LOAD = 2;

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.content)
    ListView content;
    @Bind(R.id.refresh_content)
    PullToRefreshView refreshContent;

    private int state;
    private int page;
    private int curPage;
    private int listcount;
    private int titleResId;

    private List<MyTrainEntity> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_train_layout);
        ButterKnife.bind(this);
        getParams();
        getWidget();
        showDialog();
        getNetData();
    }

    private void getParams() {

        page = 10;
        curPage = 1;
        Intent intent = getIntent();
        state = intent.getIntExtra("state", 0);
        dataList = new ArrayList<>();

    }

    private void getWidget() {
        switch (state) {
            case 1:
                titleResId = R.string.my_train;
                break;
            case 2:
                titleResId = R.string.share_case;
                break;
            case 3:
                titleResId = R.string.order_activity;
                break;
        }
        title.setText(getString(titleResId));
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);

        content.setOnItemClickListener(itemClickListener);

        content.setAdapter(adapter);
        refreshContent.setOnHeaderRefreshListener(this);
        refreshContent.setOnFooterRefreshListener(this);
    }

    private void getNetData() {
        String url = AppURL.MY_TRAIN_URL + "&tokenKey=" + MyApplication.getInstance().tokenKey
                + "&page=" + page + "&state=" + state + "&curpage=" + curPage;
        L.i("wcl-->" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    JsonObject j = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                    listcount = gson.fromJson(j, JsonObject.class).get("listcount").getAsInt();
                    JsonArray jArr = gson.fromJson(j, JsonObject.class).get("mdata").getAsJsonArray();
                    MyTrainEntity[] arr = gson.fromJson(jArr, MyTrainEntity[].class);
                    if (refreshState != PULL_LOAD)
                        dataList.clear();
                    Collections.addAll(dataList, arr);
                    endNetRequest(true);
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(MyTrainActivity.this, message, Toast.LENGTH_SHORT).show();
                    endNetRequest(false);
                }
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MyTrainActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                endNetRequest(false);
                dismissDialog();
            }
        });
    }

    private void endNetRequest(boolean isSuccess) {
        if (isSuccess) {
            adapter.notifyDataSetChanged();
        } else if (refreshState == PULL_LOAD) {
            curPage--;
        }
        if (refreshState == PULL_LOAD) {
            refreshContent.onFooterRefreshComplete();
        } else if (refreshState == PULL_REFRESH) {
            refreshContent.onHeaderRefreshComplete();
        }
        refreshState = 0;
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
            Intent intent = new Intent(MyTrainActivity.this, ArticleDetailActivity.class);
            intent.putExtra("articleId", dataList.get(position).articleId);
            startActivity(intent);
        }
    };


    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public MyTrainEntity getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHoler vh = null;
            if (convertView == null) {
                vh = new ViewHoler();
                convertView = LayoutInflater.from(MyTrainActivity.this).inflate(R.layout.item_train_layout, parent, false);
                vh.img = (ImageView) convertView.findViewById(R.id.img);
                vh.time = (TextView) convertView.findViewById(R.id.time);
                vh.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(vh);
            } else {
                vh = (ViewHoler) convertView.getTag();
            }

            ImageLoader.getInstance().displayImage(getItem(position).pic, vh.img, ImageLoadOptions.getOptions());
            vh.title.setText(getItem(position).articleTitle);
            vh.time.setText(getItem(position).addtime);

            return convertView;
        }

        class ViewHoler {
            ImageView img;
            TextView title;
            TextView time;
        }
    };

    int refreshState;

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        refreshState = PULL_LOAD;
        curPage++;
        if (dataList.size() < listcount) {
            getNetData();
        } else {
            Toast.makeText(MyTrainActivity.this, "没有更多数据！", Toast.LENGTH_SHORT).show();
            endNetRequest(false);
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        refreshState = PULL_REFRESH;
        curPage = 1;
        getNetData();
    }
}
