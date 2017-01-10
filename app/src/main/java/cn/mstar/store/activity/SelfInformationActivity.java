package cn.mstar.store.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.Constants;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CircleImageView;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.entity.JsonPc;
import cn.mstar.store.entity.UserSelfInfoEntity;
import cn.mstar.store.functionutils.BimpUtils;
import cn.mstar.store.functionutils.LogUtils;
import cn.mstar.store.functionutils.RequestUtils;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

public class SelfInformationActivity extends FragmentActivity {

    LinearLayout lny_activity_selfinformation;
    private LayoutInflater inflater;
    private int LIGHT_LINE, HEAVY_LINE;
    private ImageView title_back;
    private TextView tv_topbar_title;
    private String[] hints, blue_links;
    @Bind(R.id.iv_circle_profile_image)
    CircleImageView iv_head;
    private Gson gson;
    private UserSelfInfoEntity info;
    @Bind(R.id.pb_progressbar)
    LinearLayout loading;
    @Bind(R.id.scrollView)
    ScrollView scrollview;
    UserSelfInfoEntity initialInfo;
    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private AlertDialog dialog;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_information);
        ButterKnife.bind(this);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        captureImageInitialization();
        initViews();
        loadData();
        // add the title
        tv_topbar_title.setText(getString(R.string.modify_self_data));
        iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    /**
     * 服务器数据下载
     */
    LoadingDialog loadingdialog;
    private void loadData() {
        String link = AppURL.REQUEST_USER_INFO + "&key=" + Utils.getTokenKey(MyApplication.getInstance());
        L.i("wcl-->selfinformation:" + link);
        VolleyRequest.GetCookieRequest(this, link, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                dismissDialog_();
                gson = new Gson();
                JsonElement elm = gson.fromJson(result, JsonElement.class).getAsJsonObject();
                try {
                    JsonElement elm1 = gson.fromJson(result, JsonElement.class).getAsJsonObject().get("data");
                    info = gson.fromJson(elm1.getAsJsonObject(), UserSelfInfoEntity.class);
                    initialInfo = new UserSelfInfoEntity(info);
//					CustomToast.makeToast(SelfInformationActivity.this, info.toString(), Toast.LENGTH_SHORT);
                    inflateData(info);
                } catch (Exception e) {
                    e.printStackTrace();
                    CustomToast.makeToast(SelfInformationActivity.this, elm.getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT);
                    tv_topbar_title.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 500);
                }
            }

            @Override
            public void onFail(String error) {
                dismissDialog_();
                CustomToast.makeToast(SelfInformationActivity.this, error, Toast.LENGTH_SHORT);
                tv_topbar_title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);
            }
        });
    }


    private void inflateData(UserSelfInfoEntity info) {
        L.d("pic:::", "user " + info.pic);
        ImageLoader.getInstance().displayImage(info.pic, iv_head, ImageLoadOptions.getOptions());
        initValues(this);
        initListeners();
        addBottomViews(this);
        loading.setVisibility(View.GONE);
        scrollview.setVisibility(View.VISIBLE);
    }

    private void dismissDialog_() {
        if (loadingdialog != null) {
            loadingdialog.dismiss();
            dialog.cancel();
            dialog = null;
        }
    }

    private void showDialog() {
        loadingdialog = new LoadingDialog(this);
        loadingdialog.show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initListeners() {
        title_back.setVisibility(View.VISIBLE);
        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initValues(Context mContext) {
        LIGHT_LINE = Utils.convertPxtoDip(1, mContext);
        HEAVY_LINE = Utils.convertPxtoDip(15, mContext);
        hints = getResources().getStringArray(R.array.selfinfo);
        blue_links = getResources().getStringArray(R.array.self_menu_blue_link);
    }


    private void addBottomViews(Context mContext) {
        // add bottom views
        for (int i = 2; i < 9; i++) {
            // inflate it first
            if (inflater == null)
                inflater = LayoutInflater.from(mContext);
            View inf_rel = inflater.inflate(R.layout.activity_self_information_item, null);
            //            View inf_rel = inflater.inflate(R.layout.me_goods_below_menus, null);
            // get the views.
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final TextView tv_title = (TextView) inf_rel.findViewById(R.id.tv_menu_title);
            final TextView tv_openning_hint = (TextView) inf_rel.findViewById(R.id.tv_expand_hint);
            TextView tv_blue_link = (TextView) inf_rel.findViewById(R.id.tv_bluelink);
            ImageView iv = (ImageView) inf_rel.findViewById(R.id.iv_btn_expand_pressed);
            tv_openning_hint.setVisibility(View.VISIBLE);
            //            iv.setVisibility(View.GONE);
            //            tv_title.setVisibility(View.GONE);h
            tv_blue_link.setText(blue_links[i]);
            switch (i) {
                case 0:
                    params.topMargin = LIGHT_LINE;
                    tv_title.setText(getString(R.string.client_rank));
                    tv_openning_hint.setText(R.string.diamond_client);
                    iv.setVisibility(View.GONE);
                    tv_blue_link.setText(blue_links[i]);
                    break;
                case 1:
                    params.topMargin = LIGHT_LINE;
                    tv_title.setText(getString(R.string.client_point));
                    tv_openning_hint.setText(MyApplication.getInstance().points + "");
                    iv.setVisibility(View.GONE);
                    break;
                case 2:
                    params.topMargin = HEAVY_LINE;
                    tv_title.setText(getString(R.string.client_name));
                    tv_openning_hint.setText(info.userName);
                    iv.setVisibility(View.GONE);

                    break;
                case 3:
                    params.topMargin = LIGHT_LINE;
                    tv_title.setText(getString(R.string.client_nickname));
                    tv_openning_hint.setText(info.tName);
                    inf_rel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buildResponsiveDialog(tv_openning_hint, hints[1]).show();
                        }
                    });
                    break;
                case 4:
                    params.topMargin = LIGHT_LINE;
                    tv_title.setText(getString(R.string.client_mailadress));
                    tv_openning_hint.setText(info.email);
                    inf_rel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buildResponsiveDialog(tv_openning_hint, hints[2]).show();
                        }
                    });
                    break;
                case 5:
                    params.topMargin = LIGHT_LINE;
                    tv_title.setText(getString(R.string.client_gender));
                    tv_openning_hint.setText(info.sex.equals("0") ? getString(R.string.sex_secret) : (info.sex.equals("1") ? getString(R.string.male) : getString(R.string.female)));
                    inf_rel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] choices = new String[]{"保密", "男", "女"};
                            buildResponsiveDialog(tv_openning_hint, tv_title.getText().toString(), hints[2], choices).show();
                        }
                    });
                    break;
                case 6:
                    params.topMargin = HEAVY_LINE;
                    tv_title.setText(getString(R.string.client_adress_manager));
                    tv_openning_hint.setVisibility(View.GONE);
                    inf_rel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SelfInformationActivity.this, ShippingAddressActivity.class);
                            startActivity(intent);
                        }
                    });
                    break;
                case 7:
                    params.topMargin = LIGHT_LINE;
                    params.bottomMargin = HEAVY_LINE;
                    tv_title.setText(getString(R.string.change_password));
                    inf_rel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SelfInformationActivity.this, ChangePasswordActivity.class);
                            startActivity(intent);
                        }
                    });
                    tv_openning_hint.setVisibility(View.GONE);
                    break;
                case 8:
                    params.topMargin = LIGHT_LINE;
                    tv_blue_link.setVisibility(View.VISIBLE);
                    params.bottomMargin = HEAVY_LINE;
                    tv_title.setText(getString(R.string.aboutus));
                    inf_rel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SelfInformationActivity.this, AboutUsActivity.class);
                            startActivity(intent);
                        }
                    });
                    tv_openning_hint.setVisibility(View.GONE);
                    break;
            }
            // i want to put them inside a linearlayout.
            lny_activity_selfinformation.addView(inf_rel, params);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
    }


    public android.support.v7.app.AlertDialog.Builder buildResponsiveDialog(final TextView tv_openning_hint, String title, String hint, final String[] choices) {

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // set  a layout.
        View lny = getLayoutInflater().inflate(R.layout.select_sex_layout, null);
        final RadioGroup rd = (RadioGroup) lny.findViewById(R.id.radio_group);
        for (String choice : choices) {
            RadioButton bt = new RadioButton(SelfInformationActivity.this);
            bt.setText(choice);
            bt.setTag(choice);
            rd.addView(bt);
        }
        builder.setView(lny)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RadioButton rb = (RadioButton) rd.findViewById(rd.getCheckedRadioButtonId());
                        int i = 0;
                        for (String choice : choices) {
                            if (rb.getTag().equals(choice)) {
                                tv_openning_hint.setText(choice);
                                info.sex = "" + i;
                            }
                            i++;
                        }
                        updateUserInfo();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setTitle(getString(R.string.enter_new) + title);
        return builder;
    }

    private void initViews() {

        lny_activity_selfinformation = (LinearLayout) findViewById(R.id.lny_activity_selfinformation);
        title_back = (ImageView) findViewById(R.id.title_back);
        tv_topbar_title = (TextView) findViewById(R.id.title_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_self_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // logOut
    public void logOut(View view) {
        // clean the rep
        Utils.cleanSharedPref(SelfInformationActivity.this);
        MyApplication.spUtils.cleanSharedPref();
        if (MainActivity.mainActivity != null) {
            MainActivity.mainActivity.setMycartCount(0);//cartCount
        }
        // show the dialog...
        loadingdialog = new LoadingDialog(this, getString(R.string.pull_to_refresh_footer_refreshing_label));
        loadingdialog.show();
        // log out.
        RequestUtils.Logout(new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                // anyway, the mainactivty has to know that im no more dealing.
                dismissDialog_();
                logoutByForce();
                finish();
            }

            @Override
            public void onFail(String error) {
                dismissDialog_();
                logoutByForce();
                finish();
            }
        });
        // finish ()
        // hide the dialog.
        MainActivity.curu_Tab = MainActivity.TAB_HOME;
        finish();
    }

    private void logoutByForce() {
        /*Intent data = new Intent ();
		setResult(666, data);*/
    }

    public android.support.v7.app.AlertDialog.Builder buildResponsiveDialog(final TextView tv, final String title) {

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // set  a layout.
        View innerView = getLayoutInflater().inflate(R.layout.custom_field_layout, null);
        final EditText ed = (EditText) innerView.findViewById(R.id.ed);
        builder.setView(innerView)

                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tmp = ed.getText().toString();
                        tv.setText(tmp);
                        // change it inside the userinfo.
                        switch (title) {
                            case "用户名":
                                info.userName = tmp;
                                break;
                            case "昵称":
                                info.tName = tmp;
                                break;
                            case "邮箱":
                                // check if email is correct.
                                info.email = tmp;
                                break;
                        }
                        updateUserInfo();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setTitle(getString(R.string.enter_new) + title);
        return builder;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (info != null && initialInfo != null && MyApplication.getInstance() != null)
            updateUserInfo();
    }

    private void updateUserInfo() {
        if (!info.toString().equals(initialInfo.toString()) || MyApplication.getInstance().isFrg_me_needUpdate) {
            Intent intent = new Intent();
            info.pic += " ";
            intent.putExtra("message", (new Gson()).toJson(info));
            setResult(3, intent);
            MyApplication.getInstance().isFrg_me_needUpdate = false;
        }
    }


    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }

    /*
    * 照片选择器
    * */
    private void captureImageInitialization() {
		/*"Take from camera"*/
        final String[] items = new String[]{"现在拍照",
                "相册"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择头像");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { // pick from
                // camera
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mImageCaptureUri = Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), "tmp_avatar_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg"));
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                            mImageCaptureUri);
                    try {
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "选择头像"), PICK_FROM_FILE);
                }
            }
        });

        dialog = builder.create();
    }

    public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
            super(context, R.layout.crop_selector, options);
            mOptions = options;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.crop_selector, null);
            CropOption item = mOptions.get(position);
            if (item != null) {
                ((ImageView) convertView.findViewById(R.id.iv_icon))
                        .setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.tv_name))
                        .setText(item.title);

                return convertView;
            }

            return null;
        }
    }

    public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                /**
                 * After taking a picture, do the crop
                 */
                doCrop();

                break;

            case PICK_FROM_FILE:
                /**
                 * After selecting image from files, save the selected path
                 */
                mImageCaptureUri = data.getData();

                doCrop();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();
                /**
                 * After cropping the image, get the bitmap of the cropped image and
                 * display it on imageview.
                 */
                Bitmap photo = null;
                if (extras != null) {
                    photo = extras.getParcelable("data");
                    iv_head.setImageBitmap(photo);
                }

//				L.d("file:::", "1 "+mImageCaptureUri.getPath());
                File f = new File(getRealPathFromURI(mImageCaptureUri));
//				L.d("file:::", "2 "+f.getAbsolutePath() +" file exist ? "+f.exists());
//				L.d("file:::", "3 "+getRealPathFromURI(mImageCaptureUri));

                /**
                 * Delete the temporary image
                 */
                // delete it after uploading to server.
                if (f.exists() && photo != null)
                    uploadFileToServer(photo);
                else
                    Toast.makeText(SelfInformationActivity.this, "the file doesnt exist", Toast.LENGTH_SHORT).show();
				/*
				if (f.exists())
					f.delete();*/
                break;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void uploadFileToServer(final Bitmap bitmap) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                final File file = new File(BimpUtils.getInstace().savebitmap(bitmap));
                L.d("file:::", file.getAbsolutePath() + " exist is " + file.exists());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendImgToServer(file.getAbsolutePath(), bitmap);
                    }
                });
            }
        }.start();

    }


    public void sendImgToServer(final String filePath, final Bitmap bitmap) {
        {
            HttpUtils http = new HttpUtils();
            HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;
            RequestParams params = new RequestParams();
            params.addBodyParameter("key", Utils.getTokenKey((MyApplication) getApplication()));
            params.addBodyParameter("attachment", new File(filePath));//, "multipart/form-data"
            http.send(HttpRequest.HttpMethod.POST,  Constants.FILE_UPLOAD_URL, params, new RequestCallBack<String>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    String text = "upload: " + current + "/" + total;
                    LogUtils.i("上传中" + text);
                }

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    com.lidroid.xutils.util.LogUtils.i(responseInfo.result);
                    Gson gson = new Gson();
                    try {
                        JsonPc jsonResult = null;
                        jsonResult = gson.fromJson(responseInfo.result, JsonPc.class);
                        //maketoast("功能吗"+jsonResult.getError());
                        if (jsonResult.getError().equals("0")) {
                            // maketoast(getString(R.string.send_comment_success));
                            BimpUtils.getInstace().savebitmap(bitmap);
                            //finish();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            iv_head.setImageBitmap(bitmap);
                            MyApplication.getInstance().pic = jsonResult.getData().getPic();
                            com.lidroid.xutils.util.LogUtils.i("请求成功");
                            com.lidroid.xutils.util.LogUtils.i("请求成功" + jsonResult.getData().getPic());
                        } else {
                            com.lidroid.xutils.util.LogUtils.i("请求失败" + jsonResult.getMessage());
                            // maketoast("请求失败" + jsonResult.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        com.lidroid.xutils.util.LogUtils.i("返回数据问题");
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {

                }
            });
        }
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(
                intent, 0);

        int size = list.size();

        /**
         * If there is no image cropper app, display warning message
         */
        if (size == 0) {

            Toast.makeText(this, "Can not find image crop app",
                    Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName,
                        res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                /**
                 * If there are several app exist, create a custom chooser to
                 * let user selects the app.
                 */
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(
                            res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(
                            res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent
                            .setComponent(new ComponentName(
                                    res.activityInfo.packageName,
                                    res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(
                        getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                startActivityForResult(
                                        cropOptions.get(item).appIntent,
                                        CROP_FROM_CAMERA);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null,
                                    null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

}
