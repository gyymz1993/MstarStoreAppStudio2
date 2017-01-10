package cn.mstar.store.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import cn.mstar.store.R;
import cn.mstar.store.activity.IndentDetailsActivity;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.Constants;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.db.entityToSave.ProAndSpecialIdz;
import cn.mstar.store.entity.RegisterOb;
import cn.mstar.store.entity.ShoppingCartItem;
import cn.mstar.store.interfaces.HttpRequestCallBack;
import cn.mstar.store.interfaces.OnResultStatusListener;

/**
 * volley get post请求
 * @author wenjundu 2015-07-03
 */
public class VolleyRequest {


	public static void cleanCookie () {
		CookieStringtRequest.cookie = "";
		CookieStringtRequest.isTruncate = true;
	}


	//！！！！！！！！get请求为了保证cookie一致  后来不要使用该方法！！！！！！！！！！
	public static void GetRequest(Context context,String url,final HttpRequestCallBack callback){

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( Request.Method.GET, url, null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						if(callback!=null)
							callback.onSuccess(response);
					}
				}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(callback!=null)
					callback.onFailure(error.toString());
			}

		});

		MyApplication.requestQueue.add(jsonObjectRequest);
	}


	//volley get操作 保证cookie一致
	public static void GetCookieRequestPurePage (Context context, String url,final HttpStringRequsetCallBack callBack){

		CookieStringtRequest jsonObjectRequest=new CookieStringtRequest(Method.GET, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				if(callBack!=null)
					callBack.onSuccess(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if(callBack!=null){
					callBack.onFail(error.toString());
				}
			}
		});
		/*jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
		jsonObjectRequest.setCookie();
		MyApplication.requestQueue.add(jsonObjectRequest);
	}

	public static void GetCookieRequest (Context context, String url,final HttpStringRequsetCallBack callBack){

		CookieStringtRequest jsonObjectRequest=new CookieStringtRequest(Method.POST, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				if(callBack!=null)
					callBack.onSuccess(android.text.Html.fromHtml(response).toString());
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if(callBack!=null){
					callBack.onFail(error.toString());
				}
			}
		});
//		jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjectRequest.setTag(context);
		jsonObjectRequest.setCookie();
		MyApplication.requestQueue.add(jsonObjectRequest);
	}


	//新加接口 ，保证cookie一致的volley网络请求回调这个接口
	public  interface HttpStringRequsetCallBack{
		void onSuccess(String result);
		void onFail(String error);
	}


	public interface LogonStatusLinstener{
		void OK(String token);//已经登录返回token
		void NO();
	}



}
