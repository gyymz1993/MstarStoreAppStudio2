package cn.mstar.store.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.mstar.store.R;
import cn.mstar.store.interfaces.OnLableChange;
import cn.mstar.store.utils.LogUtils;
import info.hoang8f.android.segmented.SegmentedGroup;

public class ChooseRingView extends RelativeLayout implements OnLableChange,RadioGroup.OnCheckedChangeListener{


    final String TAG="ChooseRingView";
    TextView tv_describe;
    public ChooseRingView(Context context){
        this(context, null);
    }

    public ChooseRingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    SegmentedGroup segmentedGroup;
    public ChooseRingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {// 得到自定义属性
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ChooseRingView);
           // item_name = ta.getString(R.styleable.ChooseRingView_name);
            ta.recycle();
        }
        View rootView = View.inflate(getContext(), R.layout.item_choose_ring,this);// 将view添加。
        tv_describe = (TextView) rootView.findViewById(R.id.id_tv_itme_name);
        segmentedGroup = (SegmentedGroup) rootView.findViewById(R.id.id_segmented_group);

    }

    Map<String,Integer> map;
    List<String> str;
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        LogUtils.e(TAG+":"+i);
        if (onSelectRingListener!=null){
            onSelectRingListener.onCheckedId(mname,i);
            for (int j=0;j<radioGroup.getChildCount();j++){
                RadioButton radioButton= (RadioButton) radioGroup.getChildAt(j);
                if (radioButton.getId()==i){
                    onSelectRingListener.onCheckedValue(mname,radioButton.getText().toString());
                }
            }

        }
    }

    OnSelectRingListener onSelectRingListener;
    String mname;
    public void setOnSelectRingListener(String name,OnSelectRingListener onSelectRingListener) {
        this.mname=name;
        this.onSelectRingListener = onSelectRingListener;
    }

    public  interface  OnSelectRingListener{
        void onCheckedId(String name,int id);
        void onCheckedValue(String name,String value);
    }
    @Override
    public void onLable(Map<String, Integer> lables, String name, List<String> mstr) {
        if (lables!=null&&name!=null){
            map=lables;
            str=mstr;
            for (int i=0,n=lables.size();i<n;i++){
                RadioButton radioButton=(RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.radio_button_item, null);
                if(i==0){
                    radioButton.setChecked(true);
                }
                radioButton.setText(str.get(i));
                radioButton.setId(lables.get(str.get(i)));
                segmentedGroup.addView(radioButton);
            }
            segmentedGroup.updateBackground();
            segmentedGroup.setOnCheckedChangeListener(this);
            tv_describe.setText(name);
        }
    }
}
