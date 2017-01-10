package cn.mstar.store.adapter;

import java.util.List;

import cn.mstar.store.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HistorySearchAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private LayoutInflater inflater;
    private OnDeleteListener deleteListener;
    private OnInitListener initListener;

    public HistorySearchAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (list.size() == 0){
            initListener.onInit_n();
        }
        else {
            initListener.onInit_c();
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_history_search_listview, null);
            viewHolder.searchTV = (TextView) convertView.findViewById(R.id.search_tv);
            viewHolder.deleteIV = (ImageView) convertView.findViewById(R.id.delete_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.searchTV.setText(list.get(position));
        viewHolder.deleteIV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (deleteListener != null)
                    deleteListener.delete(position);
            }
        });
        return convertView;
    }

    public void setOnDeleteListener(OnDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setOnInitListener(OnInitListener initListener){
        this.initListener = initListener;
    }

    public interface OnDeleteListener {
        void delete(int position);
    }

    public interface OnInitListener{
        void onInit_n();
        void onInit_c();
    }

    public class ViewHolder {
        TextView searchTV;
        ImageView deleteIV;
    }


}
