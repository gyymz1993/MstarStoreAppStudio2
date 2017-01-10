package cn.mstar.store.utils;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by Shinelon on 2016/1/12.
 */
public class ContentUtils {

    /**
     *  查询图片路径
     * @param context
     * @param uri
     * @return
     */
    public static String getRealPath(Context context, Uri uri) {
        if (uri == null) return null;
        String data = null;
        String scheme = uri.getScheme();
        if (scheme == null){
            data = uri.getPath();
        }else if(ContentResolver.SCHEME_FILE.equals(scheme)){
            data = uri.getPath();
        }else if(ContentResolver.SCHEME_CONTENT.equals(scheme)){
            Cursor cursor = context.getContentResolver().query(uri,new String[]{MediaStore.Images.ImageColumns.DATA},null,null,null);
            if (cursor != null && cursor.moveToFirst()){
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if (index > -1){
                    data = cursor.getString(index);
                }
            }
            cursor.close();
        }
        return data;
    }
}
