package cn.mstar.store.activity;

import cn.mstar.store.R;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.db.DBFinals;
import cn.mstar.store.db.DBTool;
import cn.mstar.store.fragments.HistoryFragment;
import cn.mstar.store.fragments.HotFragment;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.L;
import cn.mstar.store.customviews.EditTextWithDelete;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索页
 *
 * @author duwenjun 2015-7-14
 */
public class SearchActivity extends FragmentActivity implements OnClickListener,HotFragment.OnHotClickListener {

    // 搜索框
    private EditTextWithDelete keyET;
    // 返回按钮
    private ImageView titleBack;
    // 搜索按钮
    private TextView searchBtn;

    private RadioGroup RG;
    private final int TAB_HISTORY = 1;// 历史搜索
    private final int TAB_HOT = 0;// 热门搜索
    private FragmentManager fragmentManager;
    // Fragment资源
    private Fragment historyFagment, hotFragment;
    private DBTool dbTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        MyApplication.getInstance().addActivity(this);
        dbTool = new DBTool(this);
        dbTool.open();
        // 设置为竖屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initView();
        inintListener();

    }

    private void inintListener() {
        titleBack.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        RG.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.hot_search:
                        selectItem(TAB_HOT);
                        break;

                    case R.id.history_search:
                        selectItem(TAB_HISTORY);
                        break;
                }
            }
        });
    }

    private void initView() {
        fragmentManager = getSupportFragmentManager();
        // 默认选中HotFragment
        selectItem(TAB_HOT);
        keyET = (EditTextWithDelete) findViewById(R.id.et_key);
        titleBack = (ImageView) findViewById(R.id.title_back);
        searchBtn = (TextView) findViewById(R.id.search_btn);
        RG = (RadioGroup) findViewById(R.id.radio_group);

        getDB();
        ArrayAdapter  arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,arr);
        keyET.setThreshold(1);
        keyET.setAdapter(arrayAdapter);
    }

   List<String>  arr;
    //从数据库里面取出历史记录
    public void getDB() {
        arr=new ArrayList<>();
        Cursor curs = dbTool.getAll("SELECT * from " + DBFinals.DBSports.BATABASE_TABLE_HISTORY_SEARCH);
        if (!curs.moveToLast()) {
            return;
        }
        do {
            String text = curs.getString(curs.getColumnIndex("text"));
            L.e(text);
            arr.add(text);
        } while (curs.moveToPrevious());
    }

    // 显示选定片段视图导航抽屉列表项
    private void selectItem(int position) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.cu_push_right_in,
                R.anim.cu_push_left_out);
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (position) {
            case TAB_HOT:
                if (hotFragment == null) {
                    hotFragment = new HotFragment();
                    transaction.add(R.id.content_frame, hotFragment);
                } else
                    transaction.show(hotFragment);
                break;

            case TAB_HISTORY:
                if (historyFagment == null) {
                    historyFagment = new HistoryFragment();
                    transaction.add(R.id.content_frame, historyFagment);
                } else
                    transaction.show(historyFagment);
                break;
        }
        transaction.commit();

    }

    private void hideFragments(FragmentTransaction transaction) {
        if (historyFagment != null)
            transaction.hide(historyFagment);
        if (hotFragment != null)
            transaction.hide(hotFragment);

    }

    // 选择adapter 热门搜索和历史搜索
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                if (Utils.isFastClick()) {
                    String key = keyET.getText().toString().trim();
                    if (!"".equals(key) && key != null) {
                        saveDB(key);
                    }
                    keyET.setText("");
                    //跳转到产品列表页
                    Intent intent = new Intent(this, ProductListActivity.class);
                    intent.setAction(MyAction.searchActivitryAction);
                    intent.putExtra("key", key);
                    startActivity(intent);

                }
                break;

            case R.id.title_back:
                finish();
                break;
        }
    }

    // 将搜索记录保存到数据库
    private void saveDB(String key) {
        // 先看数据有没有相同的搜索，有就删除，后再添加
        dbTool.executeSQL("delete  from "
                + DBFinals.DBSports.BATABASE_TABLE_HISTORY_SEARCH
                + " where text = '" + key + "'");
        ContentValues values = new ContentValues();
        values.put("text", key);
        dbTool.insert(DBFinals.DBSports.BATABASE_TABLE_HISTORY_SEARCH, values);
        if (historyFagment != null)
            ((HistoryFragment) historyFagment).getDB();
    }

    @Override
    protected void onDestroy() {
        if (dbTool != null)
            dbTool.close();
        super.onDestroy();
    }

    @Override
    public void onSkip(String key) {
        saveDB(key);
        Intent intent=new Intent(this,ProductListActivity.class);
        intent.setAction(MyAction.searchActivitryAction);
        intent.putExtra("key", key);
        startActivity(intent);
    }
}
