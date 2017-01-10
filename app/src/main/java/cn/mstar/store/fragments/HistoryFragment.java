package cn.mstar.store.fragments;

import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.activity.ProductListActivity;
import cn.mstar.store.adapter.HistorySearchAdapter;
import cn.mstar.store.adapter.HistorySearchAdapter.OnDeleteListener;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.db.DBFinals;
import cn.mstar.store.db.DBTool;
import cn.mstar.store.utils.L;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryFragment extends Fragment {
    private ListView listView;
    private LinearLayout cleanView;
    private TextView noView;
    private HistorySearchAdapter adapter;
    //存放搜索数据的List
    private List<String> list;
    private Context context;
    private DBTool dbTool;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        dbTool = new DBTool(context);
        dbTool.open();
        initView();
        getDB();
    }


    private void initView() {
        // TODO Auto-generated method stub
        listView = (ListView) getView().findViewById(R.id.history_search_list);
        //清空历史记录布局
        View view = LayoutInflater.from(context).inflate(R.layout.history_listview_footer_layout, null);
        cleanView = (LinearLayout) view.findViewById(R.id.clean_history);
        noView = (TextView) view.findViewById(R.id.no_history);
        view.setTag("clear_all");
        listView.addFooterView(view);
        list = new ArrayList<String>();
        adapter = new HistorySearchAdapter(context, list);
        adapter.setOnDeleteListener(new OnDeleteListener() {

            @Override
            public void delete(int position) {
                // TODO Auto-generated method stub
                deleteDataToDB(list.remove(position));
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setOnInitListener(new HistorySearchAdapter.OnInitListener() {
            @Override
            public void onInit_n() {
                noView.setVisibility(View.VISIBLE);
                cleanView.setVisibility(View.GONE);
            }

            @Override
            public void onInit_c() {
                noView.setVisibility(View.GONE);
                cleanView.setVisibility(View.VISIBLE);
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if ("clear_all".equals(view.getTag())) {//清空历史记录
                    list.clear();
                    clearDataToDB();
                    adapter.notifyDataSetChanged();
                } else {
                    //跳转到产品列表页
                    Intent intent = new Intent(context, ProductListActivity.class);
                    intent.putExtra("key", list.get(position));
                    intent.setAction(MyAction.searchActivitryAction);
                    startActivity(intent);
                }
            }
        });
    }

    //删除数据库里的数据
    private void deleteDataToDB(String text) {

        // 删除数据库一条数据
        dbTool.executeSQL("delete  from "
                + DBFinals.DBSports.BATABASE_TABLE_HISTORY_SEARCH
                + " where text = '" + text + "'");
    }

    private void clearDataToDB() {

        dbTool.executeSQL("delete  from "
                + DBFinals.DBSports.BATABASE_TABLE_HISTORY_SEARCH);
    }

    //从数据库里面取出历史记录
    public void getDB() {
        // TODO Auto-generated method stub
        list.clear();
        Cursor curs = dbTool.getAll("SELECT * from " + DBFinals.DBSports.BATABASE_TABLE_HISTORY_SEARCH);

        if (!curs.moveToLast()) {
            return;
        }
        do {
            String text = curs.getString(curs.getColumnIndex("text"));
            L.e(text);
            list.add(text);
        } while (curs.moveToPrevious());

        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.frigment_history_search, null);
        return view;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (dbTool != null)
            dbTool.close();
        super.onDestroy();
    }

}
