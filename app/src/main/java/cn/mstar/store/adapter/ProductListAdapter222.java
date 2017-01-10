package cn.mstar.store.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.mstar.store.R;
import cn.mstar.store.customviews.SquareImageView;
import cn.mstar.store.entity.Product;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.customviews.ScaleImageView;

/**产品列表适配器
 * @author wenjundu 2015-7-13
 *
 */
public class ProductListAdapter222 extends BaseAdapter {

	private int SCREENWIDTH = -1;
	private Context context;
	private List<Product> productList;
	private LayoutInflater inflater;

	private static ViewGroup.LayoutParams parentParams;

	public ProductListAdapter222(Context context, List<Product> productList, int sw){
		this.context=context;
		this.productList=productList;
		inflater=LayoutInflater.from(context);
		SCREENWIDTH = sw;
	}
	@Override
	public int getCount() {
		return productList.size();
	}

	@Override
	public Object getItem(int position) {
		return productList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	ViewGroup.LayoutParams layoutParams;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		Product product=productList.get(position);
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_product_list2, null);
			viewHolder.productIV=(SquareImageView) convertView.findViewById(R.id.product_img);
			viewHolder.productName=(TextView) convertView.findViewById(R.id.product_name);
			viewHolder.price_r = (TextView) convertView.findViewById(R.id.product_price_r);
			viewHolder.price_b = (TextView) convertView.findViewById(R.id.product_price_b);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.productName.setText(/*"女装太便宜了"*/product.getName().trim());
		viewHolder.price_r.setText(product.getPrice() + "");
		viewHolder.price_b.setText(product.getMkprice() + "");
		if (product.getImageUrl().equals(viewHolder.productIV.getTag())) {
		} else {
			// 如果不相同，就加载。现在在这里来改变闪烁的情况
			ImageLoader.getInstance().displayImage(product.getImageUrl(), viewHolder.productIV, ImageLoadOptions.getOptions());
			viewHolder.productIV.setTag(product.getImageUrl());
		}
		return convertView;
	}

	private Map<Integer,Bitmap> cache = new HashMap<>();
	public void asynLoadBitmap(ImageView image,int position){
		Bitmap bit = cache.get(position);
		if(bit ==null){
			//需要设置为默认的图片来显示
			image.setImageResource(R.drawable.no_img_uploaded);
			Bitmap bits = ((BitmapDrawable)image.getDrawable()).getBitmap();
			setImageBitmap(image, bits, new Handler(),position);
			//使用Task或者是线程去加载图片
			new Thread(new LoadTask(image,new Handler(),position)).start();
		}else{
			image.setImageBitmap(bit);
		}
	}



	public class LoadTask  implements Runnable{
		ImageView view;
		Handler handler;
		int position;
		public LoadTask(ImageView view,Handler handler,int position){
			this.view = view;
			this.handler = handler;
			this.position = position;
		}
		public void run(){
			ImageView image=new ImageView(context);
			image.setImageResource(R.drawable.no_img_uploaded);
			Bitmap bits = ((BitmapDrawable)image.getDrawable()).getBitmap();
			setImageBitmap(view,bits,handler,position);
		}
	}

	private void setImageBitmap(final ImageView view,final Bitmap bit,Handler handler,int position){
		int tag=Integer.valueOf(view.getTag().toString());
		if(tag== position){
			handler.post(new Runnable(){
				public void run(){
					view.setImageBitmap(bit);
				}
			});
		}
	}

	public class ViewHolder{
		public SquareImageView productIV;//产品图片
		public TextView productName;
		public TextView price_r;
		public TextView price_b;
	}
}
