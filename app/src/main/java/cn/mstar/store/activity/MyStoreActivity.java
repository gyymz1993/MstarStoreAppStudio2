package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.Utils;

/**
 * Created by 1 on 2016/1/5.
 */
public class MyStoreActivity extends BaseActivity {


    private final String TAG="MystoreActivity";
    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.content)
    LinearLayout content;

    private LayoutInflater inflater;
    private int[] images = new int[]{R.drawable.icon_shops, R.drawable.icon_commoditymanagement,
            R.drawable.icon_promotion, R.drawable.icon_order2, R.drawable.icon_branch,
            R.drawable.icon_businesscard,R.drawable.iconfont_erweima};
    private String[] titles = new String[]{"我的店铺", "商品管理", "促销管理",
            "本店订单", "店铺推广", "我的名片","推广二维码"};
    private String[] titles_hint = new String[]{"查看我的店铺", "查看商品详情", "查看促销上新商品",
            "查看本店订单详情", "查看店铺推广详情", "查看我的名片","二维码推广"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_store_layout);
        ButterKnife.bind(this);
        getWidget();
    }

    private void getWidget() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
        title.setText(getString(R.string.my_store));
        initContent();
    }

    private static int LIGHT_MARGIN = Utils.convertPxtoDip(1, MyApplication.getInstance());

    private void initContent() {
        content.removeAllViews();
        for (int i = 0; i < titles.length; i++) {
            getView();
            viewHoder.iv_menu_ic.setImageResource(images[i]);
            viewHoder.tv_title.setText(titles[i]);
            viewHoder.tv_openning_hint.setText(titles_hint[i]);
            viewHoder.inf_rel.setTag(titles[i]);
            viewHoder.inf_rel.setOnClickListener(clickListener);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            if (i != 0) {
                lp.topMargin = LIGHT_MARGIN;
            }
            content.addView(viewHoder.inf_rel, lp);
        }
    }

    public View getView() {
        viewHoder = new ViewHoder();
        if (inflater == null)
            inflater = getLayoutInflater();
        View inf_rel = inflater.inflate(R.layout.me_goods_below_menus, null);
        viewHoder.tv_title = (TextView) inf_rel.findViewById(R.id.tv_menu_title);
        viewHoder.tv_openning_hint = (TextView) inf_rel.findViewById(R.id.tv_expand_hint);
        viewHoder.iv_menu_ic = (ImageView) inf_rel.findViewById(R.id.iv_below_menu_ic);
        viewHoder.inf_rel = inf_rel;
        return inf_rel;
    }

    private ViewHoder viewHoder;

    public class ViewHoder {
        View inf_rel;
        TextView tv_title;
        TextView tv_openning_hint;
        ImageView iv_menu_ic;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            } else {
                String tag = v.getTag().toString();
                Intent intent = new Intent();
                switch (tag) {
                    case "我的店铺":
                        intent.setClass(MyStoreActivity.this, StoreDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        LogUtils.e(TAG + "我的店铺StoreDetailsActivity" + MyApplication.getInstance().storeId);
                        bundle.putString("shopid", MyApplication.getInstance().storeId);
                        intent.putExtras(bundle);
                        break;
                    case "商品管理":
                        intent.setClass(MyStoreActivity.this, ProductManageActivity.class);
                        LogUtils.e(TAG + "商品管理ProductManageActivity");
                        break;
                    case "促销管理":
                        intent.setClass(MyStoreActivity.this, PromotionManageActivity.class);
                        LogUtils.e(TAG + "促销管理PromotionManageActivitys" );
                        break;
                    case "本店订单":
                        intent.setClass(MyStoreActivity.this, MyStoreOrdersActivity.class);
                        LogUtils.e(TAG + "本店订单MyStoreOrdersActivity" );
                        break;
                    case "店铺推广":
                        intent.setClass(MyStoreActivity.this, PopularizeActivity.class);
                       // intent.setAction("downstair_store");
                        LogUtils.e(TAG + "下级推广PopularizeActivity" );
                        break;
                    case "我的名片":
                        intent.setClass(MyStoreActivity.this, MyCallingCardActivity.class);
                        LogUtils.e(TAG + "我的名片MyCallingCardActivity");
                        break;
                    case "推广二维码":
                        intent.setClass(MyStoreActivity.this, ShareQcodeActivity.class);
                        LogUtils.e(TAG + "推广二维码");
                        intent.putExtra("code", 2);
                        break;
                }
                startActivity(intent);
            }
        }
    };
}
