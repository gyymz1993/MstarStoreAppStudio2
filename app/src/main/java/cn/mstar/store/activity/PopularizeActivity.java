package cn.mstar.store.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.adapter.CommentListPagerAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.fragments.LowerBranchFragment;
import cn.mstar.store.fragments.PopularizeFragment;
import cn.mstar.store.utils.Utils;

/**
 * Created by UlrichAbiguime at Shenzhen.
 */
public class PopularizeActivity extends FragmentActivity implements ViewPager.OnPageChangeListener,
                RadioGroup.OnCheckedChangeListener {

            private TextView title;
            private RadioGroup poprg;
            private View popindicator;
            private ViewPager popcontent;
            private CommentListPagerAdapter adapter;
            private int ifmshop; //（1代表实体店；4代表分销商）
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
                setContentView(R.layout.popularize_layout);
                initParam();
                initHeader();
                initView();
            }

    private void initParam() {
        ifmshop = getIntent().getIntExtra(MyAction.isPropularze,0);
    }

    private void initView() {

            popcontent = (ViewPager) findViewById(R.id.pop_content);
            popindicator = findViewById(R.id.pop_indicator);
            poprg = (RadioGroup) findViewById(R.id.pop_rg);
            List<Fragment> fragments = new ArrayList<>();
            String url1 = AppURL.BASE_URL + "act=branch_mstore&op=tgmember" + "&tokenKey=" + ((MyApplication) getApplication()).tokenKey+"&ifmshop="+ifmshop;
            String url2 = AppURL.BASE_URL + "act=branch_mstore&op=branchlist" + "&tokenKey=" + ((MyApplication) getApplication()).tokenKey+"&ifmshop="+ifmshop;
            PopularizeFragment fragment1 = new PopularizeFragment(url1,ifmshop);
            LowerBranchFragment fragment2 = new LowerBranchFragment(url2,ifmshop);
            fragments.add(fragment1);
            fragments.add(fragment2);
            adapter = new CommentListPagerAdapter(getSupportFragmentManager(), fragments);
            popcontent.setAdapter(adapter);
            poprg.check(R.id.pop_rb1);
            popcontent.setOnPageChangeListener(this);
            poprg.setOnCheckedChangeListener(this);
            if ("downstair_store".equals(getIntent().getAction())) {
                popcontent.post(new Runnable() {
                    @Override
                    public void run() {
                        popcontent.setCurrentItem(1);
                    }
                });
        }
    }

    private void initHeader() {
        ImageView back = (ImageView) findViewById(R.id.title_back);
        title = (TextView) findViewById(R.id.title_name);
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("我的推广");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        popindicator.setX(getScreenWidth() / 2 * (position + positionOffset));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onPageSelected(int position) {
        popindicator.setX(getScreenWidth() / 2 * position);
        switch (position) {
            case 0:
                poprg.check(R.id.pop_rb1);
                title.setText("我的推广");
                break;
            case 1:
                poprg.check(R.id.pop_rb2);
                title.setText("下级分店");
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.pop_rb1:
                popcontent.setCurrentItem(0);
                break;
            case R.id.pop_rb2:
                popcontent.setCurrentItem(1);
                break;
        }
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
    }
}
