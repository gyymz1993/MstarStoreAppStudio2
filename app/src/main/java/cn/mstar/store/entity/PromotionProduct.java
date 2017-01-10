package cn.mstar.store.entity;

/**
 * Created by Shinelon on 2016/1/7.
 */
public class PromotionProduct {

    public String proId;
    public String name;
    public String pic;
    public String price;
    public int salecomm;
    public int fxcomm;
    public String sales;
    public int ifNew;
    public int ifPro;

    @Override
    public String toString() {
        return "PromotionProduct{" +
                "proId='" + proId + '\'' +
                ", name='" + name + '\'' +
                ", pic='" + pic + '\'' +
                ", price='" + price + '\'' +
                ", salecomm=" + salecomm +
                ", fxcomm=" + fxcomm +
                ", sales='" + sales + '\'' +
                ", ifNew=" + ifNew +
                ", ifPro=" + ifPro +
                '}';
    }
}
