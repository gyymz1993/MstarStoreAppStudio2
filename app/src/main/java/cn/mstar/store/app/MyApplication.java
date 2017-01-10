package cn.mstar.store.app;

import android.app.Activity;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.entity.MstarGeoPoint;
import cn.mstar.store.functionutils.SpUtils;
import cn.mstar.store.interfaces.MstarLocationListener;
import cn.mstar.store.interfaces.PaySuccessCallback;
import cn.mstar.store.interfaces.WXAuthSuccessInterface;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store2.wxapi.Constants;


public class MyApplication extends Application {
    public static SpUtils spUtils;
    private static MyApplication mInstance = null;
    private static IWXAPI api;
    // 运用list来保存们每一个activity
    private List<Activity> mList = new LinkedList<Activity>();
    // volley请求队列
    public static RequestQueue requestQueue = null;
    public LocationClient mLocationClient;
    public MyBDLocationListener mMyLocationListener;
    private MstarLocationListener mstarLocationListener;
    private PaySuccessCallback mPaySuccessCallback;
    public WXAuthSuccessInterface mWXAuthSuccessInterface;
    private boolean wxPaySymbol;
    // 登录数据
    public String loginName, username, password, tokenKey, pic, points, storeId,wxId,memberId;
    // fragment 更行条件
    public boolean isFrg_me_needUpdate = true, frg_isFrg_shoppingcart_needUpdate = true;

    @Override
    public void onCreate() {
        super.onCreate();
        initParams();
    }

    private void initParams() {
        mInstance = this;
        L.isDebug = true;
        ActiveAndroid.initialize(this);
        spUtils = SpUtils.getInstace(this);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        api.registerApp(Constants.APP_ID);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        initBaiduLocClient();
        //初始化 volley请求队列
        requestQueue = Volley.newRequestQueue(this);
        //初始化ImageLoader
        initImageLoader(getApplicationContext());
    }

    public static  IWXAPI getIWXAPI(){
        return api;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    /**
     * 初始化百度定位sdk
     */
    private void initBaiduLocClient() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyBDLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);

    }

    public void setMstarLocationListener(MstarLocationListener mstarLocationListener) {
        this.mstarLocationListener = mstarLocationListener;
    }

    public void cleanLoginInfo() {
        points = pic = tokenKey = password = username = "";
        isFrg_me_needUpdate = frg_isFrg_shoppingcart_needUpdate = true;
    }

    public class MyBDLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtils.e("百度地图定位"+location.getAddrStr());
            MstarGeoPoint geoPoint = new MstarGeoPoint(location.getLatitude(), location.getLongitude());
            geoPoint.setAddress(getResources().getString(R.string.current_location) + location.getAddrStr());
            if (mstarLocationListener != null)
                mstarLocationListener.successed(geoPoint);
        }
    }

    // 关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null) {
                    L.e("MyApplication", "" + activity);
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }


    /**
     * 初始化ImageLoader
     */
    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                "MstarStore/Cache");// 获取到缓存的目录地址
        // 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLICATION里面，设置为全局的配置参数
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                // 线程池内加载的数量
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                        //.discCacheFileNameGenerator(new Md5FileNameGenerator())
                        // 将保存的时候的URI名称用MD5 加密
                        //.tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
                        // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置
    }

    // 杀进程
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public void setmPaySuccessCallback(PaySuccessCallback mPaySuccessCallback) {
        this.mPaySuccessCallback = mPaySuccessCallback;
    }

    public PaySuccessCallback getmPaySuccessCallback() {
        return mPaySuccessCallback;
    }

    public void setWxPaySymbol(boolean wxPaySymbol) {
        this.wxPaySymbol = wxPaySymbol;
    }

    public boolean getWxPaySymbol() {
        return wxPaySymbol;
    }


}
