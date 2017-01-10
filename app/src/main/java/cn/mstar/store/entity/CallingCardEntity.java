package cn.mstar.store.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shinelon on 2016/1/13.
 */
public class CallingCardEntity implements Parcelable {

    public String storeLogo;
    public String trueName;
    public String storeTel;
    public String storeQq;
    public String storeWxname;
    public String storeName;
    public String storeDescription;
    public String ewCode;

    protected CallingCardEntity(Parcel in) {
        storeLogo = in.readString();
        trueName = in.readString();
        storeTel = in.readString();
        storeQq = in.readString();
        storeWxname = in.readString();
        storeName = in.readString();
        storeDescription = in.readString();
        ewCode = in.readString();
    }

    public static final Creator<CallingCardEntity> CREATOR = new Creator<CallingCardEntity>() {
        @Override
        public CallingCardEntity createFromParcel(Parcel in) {
            return new CallingCardEntity(in);
        }

        @Override
        public CallingCardEntity[] newArray(int size) {
            return new CallingCardEntity[size];
        }
    };

    @Override
    public String toString() {
        return "CallingCardEntity{" +
                "storeLogo='" + storeLogo + '\'' +
                ", trueName='" + trueName + '\'' +
                ", storeTel='" + storeTel + '\'' +
                ", storeQq='" + storeQq + '\'' +
                ", storeWxname='" + storeWxname + '\'' +
                ", storeName='" + storeName + '\'' +
                ", description='" + storeDescription + '\'' +
                ", ewcode='" + ewCode + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(storeLogo);
        dest.writeString(trueName);
        dest.writeString(storeTel);
        dest.writeString(storeQq);
        dest.writeString(storeWxname);
        dest.writeString(storeName);
        dest.writeString(storeDescription);
        dest.writeString(ewCode);
    }
}
