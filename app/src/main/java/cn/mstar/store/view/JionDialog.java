package cn.mstar.store.view;

import android.app.Activity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.mstar.store.R;
import cn.mstar.store.activity.AgentActivity;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.VerticalViewPager;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.utils.Utils;

/**
 * Created by Administrator on 2016/2/24.
 */
public class JionDialog implements AgentActivity.onURL {

    private PopupWindow mpopupWindow;
    private WindowManager.LayoutParams lp;
    private EditText phone;
    private EditText storeName;
    private Button btn_config;
    private TextView priceTxt;

    private CheckBox ck1, ck2, ck3;

    public void showDialog(final Activity activity, View view, final int state) {
        if (mpopupWindow != null && mpopupWindow.isShowing()) {
            mpopupWindow.dismiss();
        }
        View showConview = activity.getLayoutInflater().inflate(R.layout.view_dialog, null, false);
        ImageView igcolose = (ImageView) showConview.findViewById(R.id.id_ig_close);
        TextView titleName = (TextView) showConview.findViewById(R.id.id_dialog_title);
        btn_config = (Button) showConview.findViewById(R.id.id_btn_config);
        LinearLayout layOne = (LinearLayout) showConview.findViewById(R.id.id_lay_one);
        LinearLayout layTwo = (LinearLayout) showConview.findViewById(R.id.id_lay_two);
        LinearLayout layThree = (LinearLayout) showConview.findViewById(R.id.id_lay_three);
        ck1 = (CheckBox) showConview.findViewById(R.id.check1);
        ck2 = (CheckBox) showConview.findViewById(R.id.check2);
        ck3 = (CheckBox) showConview.findViewById(R.id.check3);
        phone = (EditText) showConview.findViewById(R.id.phone);
        storeName = (EditText) showConview.findViewById(R.id.store_name);
        priceTxt = (TextView) showConview.findViewById(R.id.price);
        igcolose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpopupWindow.dismiss();
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
            }
        });
        mpopupWindow = new PopupWindow(showConview, VerticalViewPager.LayoutParams.WRAP_CONTENT, VerticalViewPager.LayoutParams.WRAP_CONTENT, true);
        mpopupWindow.setTouchable(true);
        lp = activity.getWindow().getAttributes();
        lp.alpha = 0.3f;
        activity.getWindow().setAttributes(lp);
        mpopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
                return false;
            }
        });
        mpopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        if (state > 2) {
            mpopupWindow.dismiss();
            lp.alpha = 1f;
            activity.getWindow().setAttributes(lp);
        }
        switch (state) {
            case 0:
                titleName.setText("登记信息");
                btn_config.setText("确定提交");
                layOne.setVisibility(View.VISIBLE);
                layTwo.setVisibility(View.GONE);
                layThree.setVisibility(View.GONE);
                break;
            case 1:
                titleName.setText("支付金额");
                btn_config.setText("确定支付");
                layTwo.setVisibility(View.VISIBLE);
                layOne.setVisibility(View.GONE);
                layThree.setVisibility(View.GONE);
                break;
            case 2:
                titleName.setText("申请代理成功");
                btn_config.setVisibility(View.GONE);
                layThree.setVisibility(View.VISIBLE);
                layTwo.setVisibility(View.GONE);
                layOne.setVisibility(View.GONE);
                break;
        }
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onStateChange != null) {
                    onStateChange.onChange(state);
                }
            }
        });

    }

    OnStateChange onStateChange;

    public void setOnStateChange(OnStateChange onStateChange) {
        this.onStateChange = onStateChange;
    }

    @Override
    public String submitURL(/*int tjrid*/) {
        String url = AppURL.APPLY_FOR_AGENT + "&tokenKey=" + Utils.getTokenKey(MyApplication.getInstance()) + "&tel=" + phone.getText().toString()
                /*+ "&tjrid=" + tjrid */+ "&wxName=" + MyApplication.getInstance().wxId + "&mstoreName=" + URLtoUTF8Utils.toUtf8String(storeName.getText().toString());
        return url;
    }

    @Override
    public void disableBtn() {
        btn_config.setEnabled(false);
    }

    @Override
    public void enableBtn() {
        btn_config.setEnabled(true);
    }

    @Override
    public void setPrice(double price) {
        priceTxt.setText("¥" + price);
    }

    @Override
    public void setCheck(int state) {
        switch (state) {
            case 2:
                ck1.setChecked(true);
                break;
            case 3:
                ck1.setChecked(true);
                ck2.setChecked(true);
                ck3.setChecked(true);
                break;
        }
    }

    public interface OnStateChange {
        void onChange(int state);
    }

}
