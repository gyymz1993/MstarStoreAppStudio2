package cn.mstar.store.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.ProductCommentIndicatorView;
import cn.mstar.store.fragments.StoreIndentFragment;
import cn.mstar.store.interfaces.ShowDialogInterface;
import cn.mstar.store.utils.Screen;
import cn.mstar.store.view.BadgeView;
import cn.mstar.store.view.CheckedLinearlayout;

/**
 * Created by 1 on 2016/1/7.
 */
public class MyStoreOrdersActivity extends BaseActivity implements ShowDialogInterface,StoreIndentFragment.OnOderNumberChange {

    private final String TAG = "MyStoreOrdersActivity";
    private static int WIDTH;
    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.content)
    ViewPager viewPager;
    @Bind(R.id.tab_strip)
    ProductCommentIndicatorView tabStrip;
    @Bind(R.id.tab1)
    RadioButton rb1;
    @Bind(R.id.tab1_count)
    TextView tab1Count;
    @Bind(R.id.id_fl_tab1)
    FrameLayout idFlTab1;
    @Bind(R.id.tab2)
    RadioButton rb2;
    @Bind(R.id.tab2_count)
    TextView tab2Count;
    @Bind(R.id.id_fl_tab2)
    FrameLayout idFlTab2;
    @Bind(R.id.tab3)
    RadioButton rb3;
    @Bind(R.id.shoppingcart)
    CheckedLinearlayout shoppingcart;
    @Bind(R.id.tab3_count)
    TextView tab3Count;
    @Bind(R.id.id_fl_tab3)
    FrameLayout idFlTab3;
    @Bind(R.id.tab4)
    RadioButton rb4;
    @Bind(R.id.mycenter)
    CheckedLinearlayout mycenter;
    @Bind(R.id.tab4_count)
    TextView tab4Count;
    @Bind(R.id.id_fl_tab4)
    FrameLayout idFlTab4;
    @Bind(R.id.tab5)
    RadioButton rb5;
    @Bind(R.id.tab5_count)
    TextView tab5Count;
    @Bind(R.id.id_fl_tab5)
    FrameLayout idFlTab5;
    private RadioButton[] rbs;
    private List<StoreIndentFragment> fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_store_order_layout);
        ButterKnife.bind(this);
        initParams();
        getWidget();
    }

    private void initParams() {
        WIDTH = new Screen(this).getWidth();
        rbs = new RadioButton[]{rb1, rb2, rb3, rb4, rb5};
        fragments = new ArrayList<>();
        fragments.add(new StoreIndentFragment(10));
        fragments.add(new StoreIndentFragment(20));
        fragments.add(new StoreIndentFragment(30));
        fragments.add(new StoreIndentFragment(50));
        fragments.add(new StoreIndentFragment(40));
        for (StoreIndentFragment f : fragments) {
            f.setShowDialogInterface(this);
            f.setOnOderNumberChange(this);
        }
    }

    private void getWidget() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
        title.setText(getString(R.string.my_store_order));
        rb1.setChecked(true);
        rb1.setOnClickListener(clickListener);
        rb2.setOnClickListener(clickListener);
        rb3.setOnClickListener(clickListener);
        rb4.setOnClickListener(clickListener);
        rb5.setOnClickListener(clickListener);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetAllRadio();
            if (v == back) {
                finish();
            } else if (v == rb1) {
                viewPager.setCurrentItem(0, false);
            } else if (v == rb2) {
                viewPager.setCurrentItem(1, false);
            } else if (v == rb3) {
                viewPager.setCurrentItem(2, false);
            } else if (v == rb4) {
                viewPager.setCurrentItem(3, false);
            } else if (v == rb5) {
                viewPager.setCurrentItem(4, false);
            }
        }
    };

    private FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    };

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            tabStrip.setTranslationX((position + positionOffset) * WIDTH / fragments.size());
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onPageSelected(int position) {
            tabStrip.setTranslationX(position * WIDTH / fragments.size());
            resetAllRadio();
            rbs[position].setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void resetAllRadio(){
        for (int i=0;i<rbs.length;i++){
            rbs[i].setChecked(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
    }

    @Override
    public void iShowDialog() {
        showDialog();
    }

    @Override
    public void iDismissDialog() {
        dismissDialog();
    }


    @Override
    public void onPayNum(int payNum) {
        if (payNum==0){
            tab1Count.setVisibility(View.GONE);
            return;
        }
        tab1Count.setText(""+payNum);
        tab1Count.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeliverNum(final int deliverNum) {
        if (deliverNum == 0) {
            tab2Count.setVisibility(View.GONE);
            return;
        }
        tab2Count.setText(""+deliverNum);
        tab2Count.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReceiverNum(int receiverNum) {
        if (receiverNum==0){
            tab3Count.setVisibility(View.GONE);
            return;
        }
        tab3Count.setText(receiverNum+"");
        tab3Count.setVisibility(View.VISIBLE);
    }


    @Override
    public void onReturnNum(int returnNum) {
        if (returnNum==0){
            tab4Count.setVisibility(View.GONE);
            return;
        }
        tab4Count.setText(""+returnNum);
        tab4Count.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishNum(int finishNum) {
        if (finishNum==0){
            tab5Count.setVisibility(View.GONE);
            return;
        }
        tab5Count.setText(""+finishNum);
        tab5Count.setVisibility(View.VISIBLE);
    }

    //BadgeView   onPayNum  = new BadgeView(MyStoreOrdersActivity.this, tab1Count);// 创建一个BadgeView对象，view为你需要显示提醒的控件
    private void remind(BadgeView badge, boolean isVisible,int  count) {
        badge.setText(count + ""); // 需要显示的提醒类容
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);// 显示的位置.右上角,BadgeView.POSITION_BOTTOM_LEFT,下左，还有其他几个属性
        // 显示的位置.右上角,BadgeView.POSITION_BOTTOM_LEFT,下左，还有其他几个属性
        badge.setTextColor(Color.WHITE); // 文本颜色
        int hint = Color.rgb(200, 39, 73);
        badge.setBadgeBackgroundColor(hint); // 提醒信息的背景颜色，自己设置
        badge.setTextSize(10); // 文本大小
        badge.setBadgeMargin(3, 3); // 水平和竖直方向的间距
        badge.setBadgeMargin(5); //各边间隔
        if (isVisible) {
            badge.show();// 显示
        } else {
            badge.hide();//影藏
        }
    }

}
