package cn.mstar.store.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.activity.AgentActivity;
import cn.mstar.store.activity.CouponActivity;
import cn.mstar.store.activity.CreateReceiverAddressActivity;
import cn.mstar.store.activity.GoodsManagementActivity;
import cn.mstar.store.activity.LoginActivity;
import cn.mstar.store.activity.MainActivity;
import cn.mstar.store.activity.MessageActivity;
import cn.mstar.store.activity.MyCollectionActivity;
import cn.mstar.store.activity.MyCommissionActivity;
import cn.mstar.store.activity.MyStoreActivity;
import cn.mstar.store.activity.MyTrainActivity;
import cn.mstar.store.activity.PopularizeActivity;
import cn.mstar.store.activity.RegisterActivity;
import cn.mstar.store.activity.ReturnShopActivity;
import cn.mstar.store.activity.SelfInformationActivity;
import cn.mstar.store.activity.ShareQcodeActivity;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.Constants;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CircleImageView;
import cn.mstar.store.customviews.OverscrollView2;
import cn.mstar.store.entity.UserSelfInfoEntity;
import cn.mstar.store.functionutils.LogUtils;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * 我的订单页面
 */
public class MySelfFragment extends Fragment {
    @Bind(R.id.tv_Integral)
    TextView tv_Integral;
    @Bind(R.id.scrollview)
    OverscrollView2 scrollView;
    private SystemBarTintManager mStatusBarManager;
    @Bind(R.id.title_name)
    TextView titlebar_title;
    @Bind(R.id.title_message)
    ImageView titlebar_message;
    private MyApplication app;
    @Bind(R.id.iv_head)
    CircleImageView iv_head;
    private Context mContext;
    private LayoutInflater inflater;
    private final String TAG="MyselfFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout lny_login_succed_layout;
    private int LIGHT_LINE, HEAVY_LINE;
    private View i_login, i_register,view;
    private boolean loggedState = true;
    private String[] ICON_BOTTOM_TEXT;
    private TextView tv_logged_username,change_info;
    private TextView[] tv_side_numer;
    private View[] iv_goods_tocheck_number;
    final Semaphore semp = new Semaphore(0);
    private LinearLayout lny_good_menus,lny_good_below_menus,lny_login_layout;
    private int[] goods_management = new int[]{R.drawable.icon_to_be_paid, R.drawable.icon_deliver_goods,
            R.drawable.icon_goods_receipt, R.drawable.icon_pick_up, R.drawable.icon_pick_up};
    private int[] goods_below_icons = new int[]{R.drawable.icon_order_form, R.drawable.icon_collection, R.drawable.icon_coupons
            , R.drawable.shopping_icon_shop, R.drawable.icon_document, R.drawable.iconfont_yongjin, R.drawable.icon_train,
            R.drawable.icon_case, R.drawable.icon_order, R.drawable.iconfont_lianjie, R.drawable.iconfont_erweima};
    public static MySelfFragment newInstance(boolean isLogged) {
        MySelfFragment frg = new MySelfFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_LOGGED_LINK, isLogged);
        frg.setArguments(args);
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        app = (MyApplication) getActivity().getApplication();
        MainActivity.curu_Tab = 2;
        Bundle args = getArguments();
        if (args != null) {
            loggedState = args.getBoolean(Constants.IS_LOGGED_LINK, false);
        }
        view = inflater.inflate(R.layout.fragment_me, null);
        ButterKnife.bind(this, view);
        initView();
        initValues();
        initOutViews(view);
        initTopView(loggedState);
        initListeners();
        initGoodsManagement();
        initGoodsBelowMenus();
        return view;
    }

    private void initTopView(boolean loggedState) {
        if (loggedState) {
            lny_login_layout.setVisibility(View.GONE);
            lny_login_succed_layout.setVisibility(View.VISIBLE);
        } else {
            lny_login_succed_layout.setVisibility(View.GONE);
            lny_login_layout.setVisibility(View.VISIBLE);
            // listen to login and succed buttons.
            i_register.setOnClickListener(new Login_RegisterOnclickListener());
            i_login.setOnClickListener(new Login_RegisterOnclickListener());
        }
        mStatusBarManager = new SystemBarTintManager(getActivity());
        mStatusBarManager.setStatusBarTintEnabled(true);
    }


    private void initView() {
        iv_goods_tocheck_number = new View[4];
        tv_side_numer = new TextView[4];
        titlebar_title.setText(getString(R.string.me));
        titlebar_message.setVisibility(View.VISIBLE);
        titlebar_message.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            }
        });
        scrollView = (OverscrollView2) view.findViewById(R.id.scrollview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        // 设置下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        // 设置卷内的颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.color_gold_nor,
                R.color.color_gold_nor, R.color.color_gold_nor, R.color.color_gold_nor);
    }

    private void initListeners() {
        change_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelfInformationActivity.class);
                startActivityForResult(intent, 3);
            }
        });
    }


    // 下拉刷新监听器
    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initGoodsManagement();
            swipeRefreshLayout.setRefreshing(false);
        }
    };


    /**
     * @action: 判断状态
     * @author: YangShao
     * @date: 2015/10/27 @time: 17:54
     *
     */
    private void isDistributions() {
        String link = AppURL.MYDISTRIBUTION + "&key=" + Utils.getTokenKey(app)/*+"&wxName="+""*/;
        LogUtils.e(TAG+"申请分销路径" + link);
        VolleyRequest.GetCookieRequest(getActivity(), link,
                new VolleyRequest.HttpStringRequsetCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        if (result != null) {
                            try {
                                Gson gson = new Gson();
                                JsonObject jsData = gson.fromJson(result, JsonObject.class).getAsJsonObject("data");
                                int symbol = jsData.get("ifmshop").getAsInt();
                                //addDistributionView(symbol);
                                addDistributionView(1);
                                int storeId = jsData.get("shopId").getAsInt();
                                app.pic = jsData.get("pic").getAsString();
                                try {
                                    app.points = jsData.get("points").getAsString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                app.username = jsData.get("userName").getAsString();
                                if (storeId != 0) {
                                    MyApplication.getInstance().storeId = storeId + "";
                                }
                                int returnNumber = jsData.get("returnNum").getAsInt();
                                tv_logged_username.setText(app.username + " , " + getString(R.string.welcome));
                                tv_Integral.setText(getString(R.string.points) + app.points);
                                ImageLoader.getInstance().displayImage(app.pic, iv_head, ImageLoadOptions.getOptions());
                                semp.release();
                                if (onReturnNumber!=null){
                                    onReturnNumber.onNumberChange(returnNumber);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFail(String error) {

                    }
                });
    }

    private void setLoggedState(boolean booleanExtra, String username) {
        loggedState = booleanExtra;
    }

    private void initValues() {
        LIGHT_LINE = Utils.convertPxtoDip(1, mContext);
        HEAVY_LINE = Utils.convertPxtoDip(15, mContext);
        ICON_BOTTOM_TEXT = getResources().getStringArray(R.array.goods_managment_1);
    }

    /**
     * 初始化菜单
     */
    List count1 = new ArrayList<>();
    private void initGoodsBelowMenus() {
        count1.add("所有订单");
        count1.add("我的收藏");
        count1.add("代金劵");
        String [] tvHint=new String[]{getString(R.string.allthegoods_hint),getString(R.string.my_store_hint),getString(R.string.my_coupon_hint)};
        String [] tvTitle=new String[]{getString(R.string.allthegoods),getString(R.string.favorites),getString(R.string.coupon)};
        for (int i = 0; i < count1.size(); i++) {
            View inf_rel = getView();
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = LIGHT_LINE;
            viewHoder.iv_menu_ic.setImageResource(goods_below_icons[i]);
            viewHoder.tv_openning_hint.setText(tvHint[i]);
            viewHoder.tv_title.setText(tvTitle[i]);
            viewHoder.inf_rel.setTag(i);
            viewHoder.inf_rel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    switch (Integer.valueOf(v.getTag().toString())) {
                        case 0:
                            intent = new Intent(getActivity(), cn.mstar.store.activity.GoodsManagementActivity.class);
                            break;
                        case 1:
                            intent = new Intent(getActivity(), MyCollectionActivity.class);
                            break;
                        case 2:
                            intent = new Intent(getActivity(), CouponActivity.class);
                            break;
                    }
                    if (intent != null) {
                        startActivity(intent);
                    }
                }
            });
            lny_good_below_menus.addView(inf_rel, params);
        }

    }


    /**
     * @action:添加分销
     * @author: YangShao
     * @date: 2015/10/27 @time: 17:32
     */
    public void addDistributionView(int symbol) {
        count1.clear();
        lny_good_below_menus.removeAllViews();
        initGoodsBelowMenus();
        if (symbol == 0||symbol == 2) {
            count1.add("加盟代理");
            count1.add("申请开店");
            for (int i = 3; i < count1.size(); i++) {
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                View inf_rel = getView();
                params.topMargin = LIGHT_LINE;
                viewHoder.iv_menu_ic.setImageResource(goods_below_icons[i]);
                viewHoder.tv_title.setText(count1.get(i).toString());
                viewHoder.tv_openning_hint.setText("");
                switch (i){
                    case 3:
                        viewHoder.inf_rel.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), AgentActivity.class);
                                startActivity(intent);
                            }
                        });
                        break;
                    case 4:
                        if (symbol == 0) {
                            viewHoder.inf_rel.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), CreateReceiverAddressActivity.class);
                                    intent.putExtra("code", 1);
                                    startActivity(intent);
                                }
                            });
                        }
                        if (symbol == 2) {
                            viewHoder.tv_title.setText("审核不通过");
                        }
                        if (symbol == 3) {
                            viewHoder.tv_title.setText("审核中");
                        }
                        break;
                }
                lny_good_below_menus.addView(inf_rel, params);
            }
        } if(symbol == 1){     //实体店
            count1.add("我的店铺");
            count1.add("我的培训");
            count1.add("案例分享");
            count1.add("活动订购");
            count1.add("推广二维码");
            count1.add("我的推广");
            String hint []=new String[]{getString(R.string.my_store_hint),getString(R.string.see_train_content),getString(R.string.see_case),
                    getString(R.string.see_train_content),getString(R.string.see_activity),getString(R.string.see_two_dimension_code)
            ,getString(R.string.see_extension_workers)};
            int []icons=new  int[]{R.drawable.shopping_icon_shop,R.drawable.icon_train,R.drawable.icon_case,R.drawable.icon_order,
                    R.drawable.iconfont_erweima,R.drawable.icon_document};
            for (int i = 3; i < count1.size(); i++) {
                {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
                   final View inf_rel = getView();
                    params.topMargin = LIGHT_LINE;
                    viewHoder.tv_title.setText(count1.get(i).toString());
                    viewHoder.tv_openning_hint.setText(hint[i-3]);
                    viewHoder.iv_menu_ic.setImageResource(icons[i - 3]);
                    inf_rel.setTag(i);
                    viewHoder.inf_rel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=null;
                            switch ( Integer.valueOf(String.valueOf(v.getTag()))) {
                                case 3:
                                    intent   = new Intent(getActivity(), MyStoreActivity.class);
                                    break;
                                case 4:
                                     intent = new Intent(getActivity(), MyTrainActivity.class);
                                    intent.putExtra("state", 1);
                                    break;
                                case 5:
                                     intent = new Intent(getActivity(), MyTrainActivity.class);
                                    intent.putExtra("state", 2);
                                    break;
                                case 6:
                                     intent = new Intent(getActivity(), MyTrainActivity.class);
                                    intent.putExtra("state", 3);
                                    break;
                                case 7:
                                    inf_rel.setVisibility(View.GONE);
                                    intent = new Intent(getActivity(), ShareQcodeActivity.class);
                                    intent.putExtra("code", 2);
                                    break;
                                case 8:
                                    intent = new Intent(getActivity(), PopularizeActivity.class);
                                    intent.putExtra(MyAction.isPropularze,1);      //实体店
                                    break;
                            }
                            startActivity(intent);
                        }
                    });

                    lny_good_below_menus.addView(inf_rel, params);
                }
            }

        }if (symbol==4){      //代理商
            count1.add("我的佣金");
            count1.add("推广链接");
            count1.add("我的推广");
            String hint []=new String[]{getString(R.string.see_and_get_commission),getString(R.string.see_extension_link),getString(R.string.see_extension_workers)};
            int []icons=new  int[]{R.drawable.iconfont_yongjin,R.drawable.iconfont_lianjie,R.drawable.icon_document};
            for (int i = 3; i < count1.size(); i++) {
                View inf_rel = getView();
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = LIGHT_LINE;
                viewHoder.tv_title.setText(count1.get(i).toString());
                viewHoder.tv_openning_hint.setText(hint[i-3]);
                viewHoder.iv_menu_ic.setImageResource(icons[i - 3]);
                inf_rel.setTag(i);
                inf_rel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=null;
                        switch (Integer.valueOf(String.valueOf(v.getTag()))) {
                            case 3:
                                //My commission  我的佣金页面
                                 intent = new Intent(getActivity(), MyCommissionActivity.class);
                                break;
                            case 4:
                                 intent = new Intent(getActivity(), ShareQcodeActivity.class);
                                intent.putExtra("code", 1);
                                break;
                            case 5:
                                 intent = new Intent(getActivity(), PopularizeActivity.class);
                                intent.putExtra(MyAction.isPropularze, 4);    //实体店 分销商
                                break;
                        }
                        startActivity(intent);

                    }
                });

                lny_good_below_menus.addView(inf_rel, params);
            }
        }
    }


    public View getView() {
        viewHoder = new ViewHoder();
        if (inflater == null)
            inflater = LayoutInflater.from(mContext);
        View inf_rel = inflater.inflate(R.layout.me_goods_below_menus, null);
        viewHoder.tv_title = (TextView) inf_rel.findViewById(R.id.tv_menu_title);
        viewHoder.tv_openning_hint = (TextView) inf_rel.findViewById(R.id.tv_expand_hint);
        viewHoder.iv_menu_ic = (ImageView) inf_rel.findViewById(R.id.iv_below_menu_ic);
        if (!loggedState) {
            viewHoder.tv_openning_hint.setVisibility(View.INVISIBLE);
        } else {
            viewHoder.tv_openning_hint.setVisibility(View.VISIBLE);
        }
        viewHoder.inf_rel = inf_rel;
        return inf_rel;
    }

    ViewHoder viewHoder;

    public class ViewHoder {
        View inf_rel;
        TextView tv_title;
        TextView tv_openning_hint;
        ImageView iv_menu_ic;
    }

    private void initOutViews(View view) {
        i_login = view.findViewById(R.id.btn_login);
        i_register = view.findViewById(R.id.btn_register);
        lny_good_menus = (LinearLayout) view.findViewById(R.id.lny_manage_goods);
        lny_good_below_menus = (LinearLayout) view.findViewById(R.id.lny_goods_below_menus);
        lny_login_layout = (LinearLayout) view.findViewById(R.id.login_layout);
        lny_login_succed_layout = (RelativeLayout) view.findViewById(R.id.login_successed_layout);
        tv_logged_username = (TextView) view.findViewById(R.id.tv_logged_username);
        change_info = (TextView) view.findViewById(R.id.change_info);
    }

    /**
     *   代付款 条形 按钮
     * @param
     */
    public void initGoodsManagement(){
        if (app == null)
            return;
        lny_good_menus.removeAllViews();
        String tokenKey = app.tokenKey;
        String[] links = new String[]{
                AppURL.GET_ORDER_LIST_WAITING_FOR_PAY + "&key=" + tokenKey,
                AppURL.GET_ORDER_LIST_WAITING_FOR_SEND + "&key=" + tokenKey,
                AppURL.GET_ORDER_LIST_WAITING_FOR_RECEIVE + "&key=" + tokenKey
        };
        LogUtils.e(TAG+"initGoodsManagement"+AppURL.GET_ORDER_RETURN_SHOP_COUNT + "&key="+10);
        for (int i = 0; i < 4; i++) {
            if (inflater == null){
                inflater = LayoutInflater.from(mContext);
            }
            View inf_rel = inflater.inflate(R.layout.me_goods_management_items, null);
            inf_rel.setTag((i + 1));
            iv_goods_tocheck_number[i] = inf_rel.findViewById(R.id.iv_goods_tocheck_number);
            tv_side_numer[i] = (TextView) inf_rel.findViewById(R.id.tv_number_tocheck);
            ImageView iv_top_img = (ImageView) inf_rel.findViewById(R.id.iv_menu_goods_top_icon);
            TextView tv_bottom = (TextView) inf_rel.findViewById(R.id.tv_goods_menu_option);
            // 获取每个列表一共有多少个订单 & 然后在旁边显示
            final int finalI = i;
            if (i==3){
                isDistributions();
                setOnReturnNumber(new OnReturnNumber() {
                    @Override
                    public void onNumberChange(int number) {
                        if (number != 0) {
                            iv_goods_tocheck_number[finalI].setVisibility(View.VISIBLE);
                            tv_side_numer[finalI].setText(number + "");
                        }
                    }
                });
            }else {
                VolleyRequest.GetCookieRequest(getActivity(), links[i], new VolleyRequest.HttpStringRequsetCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            Gson gson = new Gson();
                            JsonElement elm = gson.fromJson(result, JsonElement.class).getAsJsonObject().get("data");
                            int totalItem = elm.getAsJsonObject().get("list_count").getAsInt();
                            // make a list of what we need from here.
                            if (totalItem != 0) {
                                tv_side_numer[finalI].setText(totalItem + "");
                                iv_goods_tocheck_number[finalI].setVisibility(View.VISIBLE);
                            } else {
                                iv_goods_tocheck_number[finalI].setVisibility(View.INVISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String error) {
                    }
                });
            }

            iv_top_img.setImageResource(goods_management[i]);
            tv_bottom.setText(ICON_BOTTOM_TEXT[i]);
            LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_params.weight = 25;
            if (loggedState == true){
                inf_rel.setOnClickListener(new GoodManagmentTopOnclickListener(i + 1));
            }
            else{
                inf_rel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        loginToServer(GoodsManagementActivity.class);
                    }
                });
            }

            lny_good_menus.addView(inf_rel, layout_params);
        }
        // how to do with the dividers.
    }

    private class Login_RegisterOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == i_login) {
                loginToServer(null);
            } else if (v == i_register) {
                Intent i = new Intent(getActivity(), RegisterActivity.class);
                startActivity(i);
            }
        }
    }


    OnReturnNumber onReturnNumber;
    public void setOnReturnNumber(OnReturnNumber onReturnNumber) {
        this.onReturnNumber = onReturnNumber;
    }

    public interface OnReturnNumber{
        void onNumberChange(int number);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (app.isFrg_me_needUpdate) {
            initTopView("".equals(app.tokenKey) ? false : true);
        }
        if (loggedState) {
            initGoodsManagement();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && data != null && !"".equals(Utils.getTokenKey(app))) {
            String infoS = data.getStringExtra("message");
            UserSelfInfoEntity info = (new Gson()).fromJson(infoS, UserSelfInfoEntity.class);
            if (info == null) {
                setLoggedState(false, "");
            } else {
                updateUserInformations(info);
            }
        } else {
            if ("".equals(Utils.getTokenKey(app)))
                ((MainActivity) getActivity()).selectItem(MainActivity.TAB_HOME);
        }

    }

    private void updateUserInformations(final UserSelfInfoEntity info) {
        String link = AppURL.UPDATE_USER_INFO + "&key=" + Utils.getTokenKey((MyApplication) getActivity().getApplication());
        String tName = Utils.encodeChinese(info.tName);
        try {
            tName = URLEncoder.encode(info.tName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        link += "&tName=" + tName + "&email=" + info.email + "&sex=" + info.sex;
        VolleyRequest.GetCookieRequest(getActivity(), link, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                /* success */
                try {
                    Gson gson=new Gson();
                    JsonElement elm = gson.fromJson(result, JsonElement.class).getAsJsonObject().get("error");
                    String error = elm.getAsString();
                    if ("0".equals(error)) {
                        ((MainActivity) getActivity()).selectItem(MainActivity.TAB_ME);
                        app.username = info.tName;
                        app.points = info.points;
                        initTopView(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFail(String error) {
                /*failure 可能是网络的问题 */
                CustomToast.makeToast(getActivity(), getString(R.string.information_change_failure), Toast.LENGTH_SHORT);
            }
        });
    }

    public void loginToServer(Class<?> jumpTo) {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        if (jumpTo == null)
            getActivity().startActivityForResult(loginIntent, 2);
        else {
            loginIntent.putExtra(LoginActivity.JUMP_TO_ACTIVITY, jumpTo);
            getActivity().startActivity(loginIntent);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int i);
    }

    private class GoodManagmentTopOnclickListener implements View.OnClickListener {
        private int MENU_ID = -1;
        public GoodManagmentTopOnclickListener(int i) {
            MENU_ID = i;
        }
        @Override
        public void onClick(View v) {
            if (Integer.valueOf(String.valueOf(v.getTag()))==4){
                Intent intent=new Intent(getActivity(),ReturnShopActivity.class);
                startActivity(intent);
            }else {
                Intent i = new Intent(getActivity(), GoodsManagementActivity.class);
                i.putExtra(Constants.MENU_POSITION, MENU_ID);
                startActivity(i);
            }
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
    }
}