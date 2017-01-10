package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;

/**
 * Created by Shinelon on 2016/1/27.
 */
public class OrderNoActivity extends BaseActivity {

    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.order_txt)
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_no_layout);
        ButterKnife.bind(this);
        getWidgets();
    }

    private void getWidgets() {
        title.setText("订单单号");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public void confirm(View view) {
        String shippingCode = input.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("shippingCode", shippingCode);
        setResult(RESULT_OK, intent);
        finish();
    }
}
