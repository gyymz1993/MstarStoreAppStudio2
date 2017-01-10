package cn.mstar.store.entity;

/**
 * Created by Shinelon on 2016/1/15.
 */
public class MyStoreProductEntity {

    public String storeId;
    public String proId;
    public String name;
    public String pic;
    public String goodsCommonid;
    public String price;
    public String costPrice;
    public String marketPrice;
    public String goodsType;
    public String state;

    @Override
    public String toString() {
        return "MyStoreProductEntity{" +
                "storeId='" + storeId + '\'' +
                ", proId='" + proId + '\'' +
                ", name='" + name + '\'' +
                ", pic='" + pic + '\'' +
                ", goodsCommonid='" + goodsCommonid + '\'' +
                ", price='" + price + '\'' +
                ", costPrice='" + costPrice + '\'' +
                ", marketPrice='" + marketPrice + '\'' +
                ", goodsType='" + goodsType + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
