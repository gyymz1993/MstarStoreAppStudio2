package cn.mstar.store.interfaces;

import android.widget.TextView;

/**
 * Created by Shinelon on 2016/1/8.
 */
public interface AdapterCallBack {

    void changeState(boolean isChecked);
    void subClick(TextView txt,int position);
}
