package cn.mstar.store.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.functionutils.HtmlStyle;
import cn.mstar.store.utils.HtmlUtils;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Shinelon on 2016/1/14.
 */
public class ArticleDetailActivity extends BaseActivity {

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.title)
    TextView articleTitle;
    @Bind(R.id.time)
    TextView time;
    @Bind(R.id.content)
    WebView content;
    private String articleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_content_layout);
        ButterKnife.bind(this);
        getParams();
        getWidget();
        showDialog();
        getNetData();
    }

    private void getParams() {
        Intent intent = getIntent();
        articleId = intent.getStringExtra("articleId");
    }

    private void getWidget() {
        title.setText("详细内容");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
        initWebView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initWebView() {
        WebSettings settings = content.getSettings();
        settings.setLoadsImagesAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);
        content.setVerticalScrollbarOverlay(false);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            }
        }
    };

    private void getNetData() {
        String url = AppURL.CONTENT_DETAIL + "&articleId=" + articleId;
        L.i("wcl-->" + url);
        VolleyRequest.GetCookieRequestPurePage(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                L.i("wcl-->" + result);
                try {
                    JSONObject j = new JSONObject(result);
                    String error = j.optString("error");
                    if ("0".equals(error)) {
                        JSONObject data = j.optJSONObject("data");
                        String title = data.optString("articleTitle");
                        String addTime = data.optString("addtime");
                        String body = data.optString("content");
                        L.i("wcl-->" + body);
                        endNetRequest(title, addTime, body);
                    } else {
                        String message = j.optString("message");
                        Toast.makeText(ArticleDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    dismissDialog();
                }
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(ArticleDetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void endNetRequest(String title, String time, String body) {
        articleTitle.setText(title);
        this.time.setText(time);
        String html = "<html><head></head><body>" + body + "</body></html>";
        content.loadDataWithBaseURL(null, HtmlStyle.parseHtml(html), "text/html", "utf-8", null);
    }
}
