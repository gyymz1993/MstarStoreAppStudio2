package cn.mstar.store.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;

/**
 * Created by 1 on 2016/1/5.
 */
public class OrderActivity extends BaseActivity {

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_layout);
        ButterKnife.bind(this);
        getWidget();
    }

    private void getWidget() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
        title.setText(getString(R.string.order_activity));
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            }
        }
    };
}
