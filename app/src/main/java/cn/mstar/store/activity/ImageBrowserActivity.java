package cn.mstar.store.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import cn.mstar.store.R;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.CustomViewPager;
import cn.mstar.store.utils.Utils;
import uk.co.senab.photoview.PhotoView;

/**
 * 图片浏览
 * @author duwenjun
 * @date 2015-7-20
 */
public class ImageBrowserActivity extends BaseActivity implements OnPageChangeListener{

	private CustomViewPager mSvpPager;
	private ImageBrowserAdapter mAdapter;
	LinearLayout layout_image;
	private int mPosition;
	
	private String[] mPhotos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showpicture);
		MyApplication.getInstance().addActivity(this);
		getIntentData();
		initViews();
		Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
		Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
	}

	private void getIntentData() {
		mPhotos = getIntent().getExtras().getStringArray("photos");
		mPosition = getIntent().getIntExtra("position", 0);
	}
	
	protected void initViews() {
		mSvpPager = (CustomViewPager) findViewById(R.id.pagerview);
		mAdapter = new ImageBrowserAdapter(this);
		mSvpPager.setAdapter(mAdapter);
		mSvpPager.setCurrentItem(mPosition, false);
		mSvpPager.setOnPageChangeListener(this);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		mPosition = arg0;
	}

	private class ImageBrowserAdapter extends PagerAdapter{
		
		private LayoutInflater inflater;
		
		public ImageBrowserAdapter (Context context){
			this.inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return mPhotos.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			
			View imageLayout = inflater.inflate(R.layout.item_show_picture,
	                container, false);
	        final PhotoView photoView = (PhotoView) imageLayout
	                .findViewById(R.id.photoview);
	        final ProgressBar progress = (ProgressBar)imageLayout.findViewById(R.id.progress);
	        final String imgUrl = mPhotos[position];
	        ImageLoader.getInstance().displayImage(imgUrl, photoView,new SimpleImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					progress.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					progress.setVisibility(View.GONE);
					
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					progress.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					progress.setVisibility(View.GONE);
					
				}
			});
	        
	        container.addView(imageLayout, 0);
	        return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
	}
	
}
