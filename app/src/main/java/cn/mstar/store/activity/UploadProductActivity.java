package cn.mstar.store.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CustomGridView;
import cn.mstar.store.customviews.MyLinearLayout;
import cn.mstar.store.entity.SpecificationEntity;
import cn.mstar.store.functionutils.BimpUtils;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.utils.ContentUtils;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by 1 on 2016/1/6.
 */
public class UploadProductActivity extends BaseActivity {

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.add_specifiction_btn)
    TextView addSpecification;
    @Bind(R.id.category)
    LinearLayout category;
    @Bind(R.id.category_name)
    TextView categoryName;
    @Bind(R.id.specifiction_content)
    MyLinearLayout spContent;
    @Bind(R.id.if_upload_to_platform)
    LinearLayout isUploadToPlatformView;
    @Bind(R.id.if_open)
    TextView ifOpenTxt;
    @Bind(R.id.goodsName)
    EditText goodsNameTxt;
    @Bind(R.id.goodsNo)
    EditText goodsNoTxt;
    @Bind(R.id.native_upload)
    TextView nativeUpload;
    @Bind(R.id.img_container)
    CustomGridView imgContainer;


    private String spId, gcId, storeId, goodsName, goodsSerial, ifOpen, specInfo, attachment0;

    private Dialog dialog;
    private List<EditText> valList;
    private TextView dialogDelete, dialogConfirm;
    private PopupWindow pop;

    private int state = 0;
    private Map<Integer, SpecificationEntity> data;

    private String temp_img_dir;
    private List<String> imgFiles;

    private int imgContainerWidth;

    private Map<String, Bitmap> cache;

    private int st;
    private String goodsCommonid;

    private int currentSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);*/
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        setContentView(R.layout.upload_product_layout);
        ButterKnife.bind(this);
        initParams();
        getWidget();
        initDialog();
        if (goodsCommonid != null) {
            title.setText("修改商品");
            initContent();
        }
    }

    private void initParams() {
        storeId = MyApplication.getInstance().storeId;
        ifOpen = "1";

        valList = new ArrayList<>();
        data = new HashMap<>();

        temp_img_dir = Environment.getExternalStorageDirectory() + File.separator + "tempImage.jpg";
        imgFiles = new ArrayList<>();

        cache = new HashMap<>();
        st = getIntent().getIntExtra("st", 0);
        goodsCommonid = getIntent().getStringExtra("goodsCommonid");
    }

    private void getWidget() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
        title.setText(getString(R.string.upload_product));

        category.setOnClickListener(clickListener);
        addSpecification.setOnClickListener(clickListener);
        spContent.post(new Runnable() {
            @Override
            public void run() {
                MyLinearLayout.HORIZONTAL_SPACE = spContent.getMeasuredWidth() - spContent.getPaddingLeft() - spContent.getPaddingRight();
                imgContainerWidth = imgContainer.getMeasuredWidth();
            }
        });

        isUploadToPlatformView.setOnClickListener(clickListener);
        nativeUpload.setOnClickListener(clickListener);

        imgContainer.setAdapter(adapter);
    }

    private void initDialog() {
        dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(R.layout.add_specification_layout);
        dialogDelete = (TextView) dialog.findViewById(R.id.delete);
        dialogConfirm = (TextView) dialog.findViewById(R.id.confirm);
        dialogDelete.setOnClickListener(clickListener);
        dialogConfirm.setOnClickListener(clickListener);
        initValView();
    }

    private void initValView() {
        valList.add((EditText) dialog.findViewById(R.id.spVal));
        valList.add((EditText) dialog.findViewById(R.id.spNo));
        valList.add((EditText) dialog.findViewById(R.id.stack));
        valList.add((EditText) dialog.findViewById(R.id.price));
        valList.add((EditText) dialog.findViewById(R.id.market_price));
        valList.add((EditText) dialog.findViewById(R.id.actual_price));
        valList.add((EditText) dialog.findViewById(R.id.weight));
        valList.add((EditText) dialog.findViewById(R.id.process_cost));
    }

    private void initContent() {
        category.setEnabled(false);
        goodsNameTxt.setEnabled(false);
        goodsNoTxt.setEnabled(false);
        isUploadToPlatformView.setEnabled(false);
        addSpecification.setVisibility(View.GONE);
        nativeUpload.setVisibility(View.GONE);
        for (EditText et : valList) {
            if (et.getId() != R.id.actual_price)
                et.setEnabled(false);
        }
        showDialog();
        requestNet();
    }

    private void requestNet() {
        String url = AppURL.MODIFICATION_GOODS + "&tokenKey=" + MyApplication.getInstance().tokenKey
                + "&goodsCommonid=" + goodsCommonid + "&st=" + st;
        L.i("wcl-->" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    JsonObject j = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                    JsonObject totalInfo = gson.fromJson(j, JsonObject.class).get("total_info").getAsJsonObject();
                    String gcName = totalInfo.get("gcName").getAsString();
                    String goodsName = totalInfo.get("goodsName").getAsString();
                    String goodsSerial = totalInfo.get("goodsSerial").getAsString();
                    String ifOpen = totalInfo.get("ifOpen").getAsString();
                    categoryName.setText(gcName);
                    goodsNameTxt.setText(goodsName);
                    goodsNoTxt.setText(goodsSerial);
                    UploadProductActivity.this.ifOpen = ifOpen;
                    ifOpenTxt.setText("1".equals(ifOpen) ? "是" : "否");
                    JsonArray imgArr = totalInfo.get("goodsImage").getAsJsonArray();
                    imgFiles.clear();
                    for (int i = 0; i < imgArr.size(); i++) {
                        imgFiles.add(imgArr.get(i).getAsString());
                    }
                    refreshImg();

                    JsonArray ja = gson.fromJson(j, JsonObject.class).get("goods_info").getAsJsonArray();
                    SpecificationEntity[] specials = gson.fromJson(ja, SpecificationEntity[].class);
                    for (SpecificationEntity s : specials) {
                        confirmAddSp(s);
                    }
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(UploadProductActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(UploadProductActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            } else if (v == addSpecification) {
                addSpec();
            } else if (v == category) {
                Intent intent = new Intent(UploadProductActivity.this, SelectCategoryActivity.class);
                startActivityForResult(intent, 11);
            } else if (v == dialogDelete) {
                dialog.dismiss();
            } else if (v == dialogConfirm) {
                if (goodsCommonid == null && isAddSpec) {
                    confirmAddSp(getVal());
                } else {
                    confirmModifycation();
                }
            } else if (v == isUploadToPlatformView) {
                isUploadtoPlatform();
            } else if (v == nativeUpload) {
                nativeupload();
            }
        }
    };

    boolean isAddSpec;

    private void addSpec() {
        if (gcId == null) {
            final Dialog dialog = new Dialog(UploadProductActivity.this, R.style.dialog);
            dialog.setContentView(R.layout.message_dialog_layout);
            TextView message = (TextView) dialog.findViewById(R.id.message);
            message.setText("请先选择商品分类");
            dialog.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            isAddSpec = true;
            valList.get(1).setText(spId);
            valList.get(1).setEnabled(false);
            dialog.show();
        }
    }

    TextView currentClickItem;

    private void confirmAddSp(SpecificationEntity entity) {
        String content = entity.getPrice() + " " + entity.getNetWeight() + " " + entity.getStocks()/*+"¥2012.00 785g 325件"*/;
        data.put(state, entity);
        TextView txt = new TextView(UploadProductActivity.this);
        txt.setTag(state);
        state++;
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAddSpec = false;
                currentClickItem = (TextView) v;
                currentSelectedItem = (Integer) v.getTag();
                SpecificationEntity entity = data.get(currentSelectedItem);
                valList.get(0).setText(entity.speValue);
                valList.get(1).setText(entity.spId);
                valList.get(2).setText(entity.stocks);
                valList.get(3).setText(entity.costPrice);
                valList.get(4).setText(entity.marketPrice);
                valList.get(5).setText(entity.price);
                valList.get(6).setText(entity.netWeight);
                valList.get(7).setText(entity.basicCost);
                dialog.show();
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.leftMargin = 20;
        lp.topMargin = 10;
        txt.setLayoutParams(lp);
        txt.setPadding(8, 8, 8, 8);
        txt.setText(content);
        txt.setBackgroundResource(R.drawable.txt_border);
        spContent.addNewView(txt);
        dialog.dismiss();
    }

    private void confirmModifycation() {
        SpecificationEntity entity = getVal();
        entity.goodsId = data.get(currentSelectedItem).goodsId;
        data.put(currentSelectedItem, entity);
        String content = entity.getPrice() + " " + entity.getNetWeight() + " " + entity.getStocks();
        currentClickItem.setText(content);
        dialog.dismiss();
    }

    private SpecificationEntity getVal() {
        List<String> params = new ArrayList<>();
        for (EditText et : valList) {
            params.add(et.getText().toString());
        }
        return new SpecificationEntity(params);
    }

    private void isUploadtoPlatform() {
        final Dialog dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(R.layout.is_upload_platform_dialog_layout);
        final RadioButton r1 = (RadioButton) dialog.findViewById(R.id.rb1);
        RadioButton r2 = (RadioButton) dialog.findViewById(R.id.rb2);
        r1.setText("是");
        r2.setText("否");
        if ("1".equals(ifOpen)) r1.setChecked(true);
        else r2.setChecked(true);
        TextView confirm = (TextView) dialog.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (r1.isChecked()) {
                    ifOpenTxt.setText("是");
                    ifOpen = "1";
                } else {
                    ifOpenTxt.setText("否");
                    ifOpen = "0";
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void nativeupload() {
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
        pop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11 && resultCode == 11) {
            categoryName.setText(data.getStringExtra("name"));
            gcId = data.getStringExtra("gcId");
            spId = data.getStringExtra("spId");
        } else if (requestCode == 11 && resultCode == RESULT_OK) {
            returnImage(temp_img_dir);
        } else if (requestCode == 12 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = ContentUtils.getRealPath(this, uri);
            returnImage(path);
        }
    }

    private void returnImage(final String path) {
        nativeUpload.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                imgFiles.add(BimpUtils.getInstace().getCompressImagePaht(path));
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
        adapter.notifyDataSetChanged();
        if (imgFiles.size() != 0)
            imgContainer.setPadding(0, 0, 0, 36);
        else
            imgContainer.setPadding(0, 0, 0, 0);
        if (imgFiles.size() != 5) {
            nativeUpload.setEnabled(true);
        }
    }

    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return imgFiles.size();
        }

        @Override
        public String getItem(int position) {
            return imgFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        int width;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                int horizontalSpace = imgContainer.getHorizontalSpacing();
                int colums = imgContainer.getNumColumns();
                width = (imgContainerWidth - colums * horizontalSpace) / colums;
                ImageView img = new ImageView(UploadProductActivity.this) {
                    @Override
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        setMeasuredDimension(width, width);
                    }
                };
                convertView = img;
            }
            String imgPath = getItem(position);
            if (goodsCommonid == null) {
                Bitmap bmp = null;
                if (cache.get(imgPath) == null) {
                    bmp = BimpUtils.getInstace().compressBitmapFromSrc(imgPath, width);
                    cache.put(getItem(position), bmp);
                } else {
                    bmp = cache.get(imgPath);
                }
                ((ImageView) convertView).setImageBitmap(bmp);
            } else {
                ImageLoader.getInstance().displayImage(imgPath, (ImageView) convertView, ImageLoadOptions.getOptions());
            }
            convertView.setDrawingCacheEnabled(false);
            return convertView;
        }
    };

    private void confirmParams() {
        goodsName = goodsNameTxt.getText().toString();
        goodsSerial = goodsNoTxt.getText().toString();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Integer key : data.keySet()) {
            if (i++ != 0) {
                sb.append("|");
            }
            sb.append(data.get(key).toString());
        }
        specInfo = sb.toString();
        sb = null;
        sb = new StringBuilder();
        for (int j = 0; j < imgFiles.size(); j++) {
            if (j != 0) sb.append(",");
            sb.append("attachment").append(j);
        }
        attachment0 = sb.toString();
    }

    public void submit(View view) {
        String url = null;
        if (goodsCommonid != null) {
            String specInfo = "";
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Integer key : data.keySet()) {
                if (i++ != 0) sb.append("|");
                sb.append(data.get(key).getString());
            }
            specInfo = sb.toString();
            url = AppURL.SUBMIT_MODIFYCATION + "&tokenKey=" + MyApplication.getInstance().tokenKey
                    + "&goodsCommonid=" + goodsCommonid + "&specInfo=" + specInfo + "&st=" + st;
            showDialog();
            submitModifycation(url);
        } else {
            confirmParams();
            url = AppURL.UPLOAD_GOODS + "&gcId=" + gcId + "&storeId=" + storeId
                    /*+ "&specInfo=" + URLtoUTF8Utils.toUtf8String(specInfo)*/
                    + "&goodsName=" + URLtoUTF8Utils.toUtf8String(goodsName)
                    + "&goodsSerial=" + URLtoUTF8Utils.toUtf8String(goodsSerial)
                    + "&ifOpen=" + ifOpen + "&attachment0=" + attachment0;
            Log.i("wcl", "url-->" + url);
            Log.i("wcl", "files -->" + imgFiles.toString());
            submitToServer(cache.keySet(), url);
        }
    }

    private void submitToServer(Set<String> files, String url) {
        HttpUtils http = new HttpUtils();
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
        RequestParams params = new RequestParams();
        params.addBodyParameter("specInfo", specInfo);
        int count = 0;
        File file = null;
        if (files != null && files.size() > 0)
            for (String f : files) {
                file = null;
                file = new File(f);
                params.addBodyParameter("attachment" + count, file, "multipart/form-data");
                count++;
            }
        showDialog();
        http.send(method, url, params, new RequestCallBack<String>() {

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                Log.i("wcl", "current process -->" + current + "/" + total);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i("wcl", "callback-->" + result);
                Gson gson = new Gson();
                try {
                    String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                    String message = null;
                    if ("0".equals(error)) {
                        JsonObject j = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                        message = gson.fromJson(j, JsonObject.class).get("message").getAsString();
                        Toast.makeText(UploadProductActivity.this, message, Toast.LENGTH_SHORT).show();
                        dismissDialog();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                        Toast.makeText(UploadProductActivity.this, message, Toast.LENGTH_SHORT).show();
                        dismissDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissDialog();
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(UploadProductActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void submitModifycation(String url) {
        L.i("wcl-->" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                String message;
                if ("0".equals(error)) {
                    JsonObject j = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                    message = gson.fromJson(j, JsonObject.class).get("message").getAsString();
                    Toast.makeText(UploadProductActivity.this, message, Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(UploadProductActivity.this, message, Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(UploadProductActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
        File f = null;
        for (String s : cache.keySet()) {
            cache.get(s).recycle();
            f = new File(s);
            f.delete();
        }
        f = new File(temp_img_dir);
        if (f.exists())
            f.delete();
    }
}
