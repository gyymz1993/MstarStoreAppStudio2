package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.mstar.store.R;
import cn.mstar.store.adapter.GoodsManagementViewPagerAdapter;
import cn.mstar.store.app.Constants;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.SlidingTabLayout;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.Utils;

/**
 * 所有订单页面
 */
public class GoodsManagementActivity extends FragmentActivity {

    // 管理一下货的情况~ 代发货、待收货、等等。。。
    // 往里面放歌viewpager,再在viewpager里面嵌入其他fragment
    // views
    @Bind(R.id.viewpager_goodsmanagement)
    ViewPager mViewpager;
    @Bind(R.id.tabs)
    SlidingTabLayout tabs;
    @Bind(R.id.title_name)
    TextView tv_middle;
    @Bind(R.id.title_back)
    ImageView iv_title_back;
    @Bind(R.id.the_title_layout)
    RelativeLayout titleLayout;


    // variables
    int launch_position; // here
    private GoodsManagementViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_management);
        ButterKnife.bind(this);
        // get position
        initValuez(getIntent());
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        viewPagerAdapter = new GoodsManagementViewPagerAdapter(getSupportFragmentManager(), (MyApplication) GoodsManagementActivity.this.getApplication());
        initAdapter();
        iv_title_back.setVisibility(View.VISIBLE);
        // 设置这个之后、他就不会变titlelayout的背景
        // i need a reload function.

    }

    @OnClick(R.id.title_back)
    public void back() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initValuez(Intent launcher) {
        launch_position = launcher.getIntExtra(Constants.MENU_POSITION, 0);
        tv_middle.setText(getString(R.string.goods_managment));
        tv_middle.setVisibility(View.VISIBLE);
    }

    private void initAdapter() {

        // init 那个viewpager的adapter
        mViewpager.setAdapter(viewPagerAdapter);
        // 反正里面只要放那些fragment就可以了~
        // Specify that tabs should be displayed in the action bar.
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true,
        // This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.color_gold_nor);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(mViewpager);
        tabs.setAnimationCacheEnabled(false);
        mViewpager.setCurrentItem(launch_position);
        mViewpager.setOffscreenPageLimit(3);
        CustomToast.mToast(this, "selected tab " + launch_position);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        // clean the tab
        viewPagerAdapter.clear();
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewpager != null && mViewpager.getChildCount() != 0) {
            viewPagerAdapter.updateAll();
//			CustomToast.makeToast(this, "Update --- activity", Toast.LENGTH_SHORT);
        }

    }
}
