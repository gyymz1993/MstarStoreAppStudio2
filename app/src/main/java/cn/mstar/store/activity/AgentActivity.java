package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.interfaces.HttpRequestCallBack;
import cn.mstar.store.interfaces.PaySuccessCallback;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.VolleyRequest;
import cn.mstar.store.view.JionDialog;

/*
 * 创建人：Yangshao
 * 创建时间：2016/2/24 9:13
 * @version    加盟代理界面
 *
 */
public class AgentActivity extends BaseActivity {

    private WebView mWebView;
    private TextView title;
    private ImageView back;


    private Button mbut;
    private final int completeState_one = 0;
    private final int completeState_tow = 1;
    private JionDialog jionDialog;
    private int currentState = 0;
    private int tjrid;
    private PaySuccessCallback mPaySuccessCallback = new PaySuccessCallback() {

        @Override
        public void doChange() {
            jionDialog.showDialog(AgentActivity.this, mWebView, currentState + 1);
            urlInterface.setCheck(3);
            mbut.setEnabled(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agent_main);
        initParams();
        title = (TextView) findViewById(R.id.title_name);
        title.setText("范儿网络申请代理商");
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mWebView = (WebView) findViewById(R.id.id_webshow);
        mWebView.loadUrl(AppURL.WEBURL);

        mbut = (Button) findViewById(R.id.id_btjoin);

        jionDialog = new JionDialog();
        urlInterface = jionDialog;
        mbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jionDialog.showDialog(AgentActivity.this, mWebView, completeState_one);
            }
        });

        jionDialog.setOnStateChange(new JionDialog.OnStateChange() {
            @Override
            public void onChange(final int state) {
                getNetData(state);
            }
        });
    }

    private void initParams() {
        tjrid = 5;
    }

    //订单号
    private String out_trade_no;
    private double totalPrices;

    public void getNetData(final int state) {
        final String url;
        switch (state) {
            case completeState_one:
                url = urlInterface.submitURL(/*tjrid*/);
                urlInterface.disableBtn();
                L.i("wcl-->" + url);
                VolleyRequest.GetRequest(this, url, new HttpRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        try {
                            String error = jsonObject.getString("error");
                            if (error.equals("0")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                currentState = state + 1;
                                out_trade_no = data.getString("paySn");
                                totalPrices = data.getDouble("amount");
                                Log.e("ymz", out_trade_no);
                                jionDialog.showDialog(AgentActivity.this, mWebView, currentState);
                                urlInterface.setCheck(2);
                                urlInterface.setPrice(totalPrices);
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(AgentActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {

                        } finally {
                            urlInterface.enableBtn();
                        }
                    }

                    @Override
                    public void onFailure(String fail) {
                        Toast.makeText(AgentActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        urlInterface.enableBtn();
                    }
                });

                break;
            case completeState_tow:
                ((MyApplication) getApplication()).setmPaySuccessCallback(mPaySuccessCallback);
                Intent intent = new Intent(AgentActivity.this, PayActivity.class);
                intent.setAction(MyAction.fromApplyForAgent);
                Log.e("ymz", "totalPrices" + totalPrices + "out_trade_no" + out_trade_no);
                intent.putExtra("pname", "范儿网络代理加盟");
                intent.putExtra("pdesc", "范儿网络代理加盟");
                intent.putExtra("totalPrices", totalPrices);
                intent.putExtra("out_trade_no", out_trade_no);
                startActivity(intent);
                break;
            case 2:
                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
    }

    public interface onURL {
        String submitURL(/*int tjrid*/);

        void disableBtn();

        void enableBtn();

        void setPrice(double price);

        void setCheck(int state);
    }

    private onURL urlInterface;

}
