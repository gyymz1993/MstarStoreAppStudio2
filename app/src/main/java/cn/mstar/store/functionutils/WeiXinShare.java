package cn.mstar.store.functionutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

import cn.mstar.store.R;
import cn.mstar.store.app.MyApplication;

/**
 * Created by Administrator on 2015/11/11.
 */
public class WeiXinShare {

    public static  void wechatShare(Context c,String link,int flag,Bitmap thumb) {

     //   final SendAuth.Req req = new SendAuth.Req();
        if (!MyApplication.getIWXAPI().isWXAppInstalled()) {
            Toast.makeText(c, "请先安装微信", Toast.LENGTH_SHORT).show();
            return;
        } else if (!MyApplication.getIWXAPI().isWXAppSupportAPI()) {
            Toast.makeText(c, "请先更新微信应用", Toast.LENGTH_SHORT).show();
            return;
        }
       // IWXAPI wxApi;
        //实例化
        //wxApi = WXAPIFactory.createWXAPI(c, Constants.APP_ID);
      // MyApplication.api.registerApp(Constants.APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = link == null ?"":link;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "千禧之星刷";
        msg.description = "范儿珠宝 奢而不贵 品质保证 工厂直销 向暴利说不 售后无忧  是您购买珠宝的最佳选择";
        if (thumb == null){
            thumb = BitmapFactory.decodeResource(c.getResources(), R.drawable.signin_logo);
        }
        Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, 120, 120, true);
        msg.setThumbImage(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        MyApplication.getIWXAPI().sendReq(req);
    }
}
