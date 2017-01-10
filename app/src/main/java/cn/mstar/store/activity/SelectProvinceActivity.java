package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.adapter.AreaAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.entity.AreaEntity;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**选择省份
 * @author wenjundu
 *
 */
public class SelectProvinceActivity extends BaseActivity {
	
	private ListView provinceList;
	private ImageView titleBack;
	private TextView titleName;
	private List<AreaEntity> list;
	private AreaAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_province);
		Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
		Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
		MyApplication.getInstance().addActivity(this);
		init();
		initData();
	}

	private void initData() {
		list=new ArrayList<AreaEntity>();
		adapter=new AreaAdapter(this, list);
		provinceList.setAdapter(adapter);
		provinceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent(SelectProvinceActivity.this,SelectCityActivity.class);
				intent.putExtra("tempaddress", list.get(position).getName());//传递临时地址
				intent.putExtra("AreaEntity", list.get(position));
				intent.setAction(MyAction.selectProvinceActivityAction);
				startActivityForResult(intent, 0);
			}
		});
		loadProvince();
	}
	//加载省份
	private void loadProvince() {
		final LoadingDialog loadingDialog=new LoadingDialog(this, getString(R.string.pull_to_refresh_footer_refreshing_label));
		loadingDialog.show();
		VolleyRequest.GetCookieRequest(this, AppURL.SELECT_PROVINCE_URL, new VolleyRequest.HttpStringRequsetCallBack() {

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub

				if (loadingDialog != null)
					loadingDialog.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(result);

					//(new Gson()).fromJson(result,AppURL.getConstants2().getUrlTyeMap().get(Constants.SELECT_PROVINCE_URL));

					JSONArray jsonArray = jsonObject.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {


						String id = jsonArray.getJSONObject(i).getString("id");
						String parentId = jsonArray.getJSONObject(i).getString("parentId");
						String name = jsonArray.getJSONObject(i).getString("name");
						AreaEntity areaEntity = new AreaEntity(id, parentId, name);

						list.add(areaEntity);
					}

					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(String error) {
				if (loadingDialog != null)
					loadingDialog.dismiss();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0 && resultCode==RESULT_OK){
			setResult(RESULT_OK,data);
			finish();
		}
	}

	private void init() {
		provinceList=(ListView) findViewById(R.id.province_list);
		titleBack=(ImageView) findViewById(R.id.title_back);
		titleBack.setVisibility(View.VISIBLE);
		titleName=(TextView) findViewById(R.id.title_name);
		titleName.setText(getString(R.string.select_address));
		titleBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		MyApplication.requestQueue.cancelAll(this);
		super.onDestroy();
	}

}
