package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.RoundedImageView;
import cn.mstar.store.entity.CallingCardEntity;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Shinelon on 2016/1/13.
 */
public class MyCallingCardActivity extends BaseActivity {

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.main_img)
    RoundedImageView logo1;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.phone)
    TextView phone;
    @Bind(R.id.qq)
    TextView qq;
    @Bind(R.id.winxin)
    TextView winxin;
    @Bind(R.id.store_name)
    TextView storeName;
    @Bind(R.id.store_description)
    TextView storeDescription;
    @Bind(R.id.two_dimension_code)
    ImageView ewcode;
    @Bind(R.id.logo)
    ImageView logo2;
    @Bind(R.id.storeWxname)
    TextView storeWxName;
    @Bind(R.id.title_message)
    ImageView editbtn;

    private String storeId;

    private CallingCardEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_calling_card_layout);
        ButterKnife.bind(this);
        getParams();
        getWidget();
        showDialog();
        getNetData();
    }

    private void getParams() {
        storeId = MyApplication.getInstance().storeId;
    }

    private void getWidget() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
        title.setText(getString(R.string.my_calling_card));
        editbtn.setImageResource(R.drawable.icon_edit);
        editbtn.setVisibility(View.VISIBLE);
        editbtn.setOnClickListener(clickListener);
    }

    public void getNetData() {
        String url = AppURL.MY_CALLING_CARD + "&storeId=" + storeId + "&tokenKey=" + MyApplication.getInstance().tokenKey;
        Log.i("wcl", "url-->" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    JsonObject j = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                    entity = gson.fromJson(j, CallingCardEntity.class);
                    endNetRequest(entity);
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(MyCallingCardActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MyCallingCardActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
                finish();
            }
        });
    }

    private void endNetRequest(CallingCardEntity entity) {
        ImageLoader.getInstance().displayImage(entity.storeLogo, logo1, ImageLoadOptions.getOptions());
        name.setText(entity.trueName);
        phone.setText(entity.storeTel);
        qq.setText(entity.storeQq);
        winxin.setText(entity.storeWxname);
        storeName.setText(entity.storeName);
        storeDescription.setText(entity.storeDescription);
        ImageLoader.getInstance().displayImage(entity.ewCode, ewcode, ImageLoadOptions.getOptions());
        ImageLoader.getInstance().displayImage(entity.storeLogo, logo2, ImageLoadOptions.getOptions());
        storeWxName.setText(entity.storeName);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            } else if (v == editbtn) {
                Intent intent = new Intent(MyCallingCardActivity.this, ModifyCallingCard.class);
                intent.putExtra("entity", entity);
                startActivityForResult(intent, 11);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11 && resultCode == RESULT_OK) {
            getNetData();
        }
    }
}
