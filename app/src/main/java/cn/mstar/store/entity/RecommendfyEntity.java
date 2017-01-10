package cn.mstar.store.entity;

/**
 * Created by Shinelon on 2016/1/27.
 */
public class RecommendfyEntity {

    public String goodsId;
    public String goodsImage;
    public String goodsPrice;
    public String goodsName;

    @Override
    public String toString() {
        return "RecommendfyEntity{" +
                "goodsId='" + goodsId + '\'' +
                ", goodsImage='" + goodsImage + '\'' +
                ", goodsPrice='" + goodsPrice + '\'' +
                ", goodsName='" + goodsName + '\'' +
                '}';
    }
}
