package cn.mstar.store.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CircleImageView;
import cn.mstar.store.entity.CallingCardEntity;
import cn.mstar.store.functionutils.BimpUtils;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.utils.ContentUtils;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.L;

/**
 * Created by Shinelon on 2016/1/22.
 *
 *         修改店铺
 */
public class ModifyCallingCard extends BaseActivity {

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.iv_circle_profile_image)
    CircleImageView img;
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.phone)
    EditText phone;
    @Bind(R.id.qq)
    EditText qq;
    @Bind(R.id.weixin)
    EditText weixin;
    @Bind(R.id.store_name)
    EditText storeName;
    @Bind(R.id.store_description)
    EditText storeDes;

    private PopupWindow pop;

    private String temp_img_dir;
    private String submitPath;

    private CallingCardEntity entity;

    private String tokenKey;
    private String mstoreId;
    private String mstoreName;
    private String mstoreTel;
    private String mstoreQq;
    private String mstoreWx;
    private String description;
    private String attachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_callingcard_layout);
        ButterKnife.bind(this);
        initParams();
        getWidgets();
    }

    private void initParams() {
        temp_img_dir = Environment.getExternalStorageDirectory() + File.separator + "tempImage.jpg";
        entity = getIntent().getParcelableExtra("entity");
    }

    private void getWidgets() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
        title.setText("修改资料");
        img.setOnClickListener(clickListener);

        initView();

        initPopupwindow();
    }

    private void initView() {
        ImageLoader.getInstance().displayImage(entity.storeLogo, img, ImageLoadOptions.getOptions());
        name.setText(entity.trueName);
        phone.setText(entity.storeTel);
        qq.setText(entity.storeQq);
        weixin.setText(entity.storeWxname);
        storeName.setText(entity.storeName);
        storeDes.setText(entity.storeDescription);
    }

    private void initPopupwindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.native_upload_layout, null, false);
        pop = new PopupWindow(view, -1, -1, true);
        pop.setBackgroundDrawable(new ColorDrawable(0x993b3b3b));
        View camera = view.findViewById(R.id.camera);
        View photos = view.findViewById(R.id.photos);
        View cancel = view.findViewById(R.id.cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(temp_img_dir)));
                startActivityForResult(intent, 11);
                pop.dismiss();
            }
        });

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 12);
                pop.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (pop != null && pop.isShowing()) {
                    pop.dismiss();
                }
                return false;
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            } else if (v == img) {
                pop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 11 && resultCode == RESULT_OK) {
            returnImage(temp_img_dir);
        } else if (requestCode == 12 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = ContentUtils.getRealPath(this, uri);
            returnImage(path);
        }
    }

    private void returnImage(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                submitPath = BimpUtils.getInstace().getCompressImagePaht(path);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshImg();
                    }
                });
            }
        }).start();
    }

    private void refreshImg() {
        Bitmap bmp = BimpUtils.getInstace().compressBitmapFromSrc(submitPath, img.getMeasuredWidth());
        img.setImageBitmap(bmp);
    }

    private void getSubmitParams() {

        tokenKey = MyApplication.getInstance().tokenKey;
        mstoreId = "1";
        mstoreName = storeName.getText().toString();
        mstoreTel = phone.getText().toString();
        mstoreQq = qq.getText().toString();
        mstoreWx = weixin.getText().toString();
        description = storeDes.getText().toString();
        attachment = submitPath == null ? "" : "attachment";
    }

    public void submit(View view) {
        getSubmitParams();
        String url = AppURL.MODIFY_CALLING_CARD + "&tokenKey=" + tokenKey
                + "&mstoreName=" + URLtoUTF8Utils.toUtf8String(mstoreName)
                + "&mstoreId=" + mstoreId + "&mstoreTel=" + mstoreTel + "&mstoreQq=" + mstoreQq
                + "&mstoreWx=" + mstoreWx + "&description=" + URLtoUTF8Utils.toUtf8String(description)
                + "&attachment=" + attachment;
        L.i("wcl-->" + url);
        upToServer(url);
    }

    private void upToServer(String url) {
        HttpUtils http = new HttpUtils();
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        RequestParams params = new RequestParams();
        if (!"".equals(attachment))
            params.addBodyParameter("attachment", new File(submitPath), "multipart/form-data");
        showDialog();
        http.send(method, url, params, new RequestCallBack<String>() {

            @Override
            public void onLoading(long total, long current, boolean isUploading) {

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Gson gson = new Gson();
                String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                Toast.makeText(ModifyCallingCard.this, message, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                dismissDialog();
                finish();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(ModifyCallingCard.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
        File file = new File(temp_img_dir);
        if (file.exists()) {
            file.delete();
        }
        if (submitPath != null && (file = new File(submitPath)).exists()) {
            file.delete();
        }
    }
}
