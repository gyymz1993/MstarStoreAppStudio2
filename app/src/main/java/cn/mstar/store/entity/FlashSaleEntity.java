package cn.mstar.store.entity;

/**
 * Created by Shinelon on 2016/1/22.
 */
public class FlashSaleEntity {

    public String pic;
    public String categoryId;
    public String title;
    public String endHour;
    public String endMinute;
    public String endSeconds;

    @Override
    public String toString() {
        return "FlashSaleEntity{" +
                "pic='" + pic + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", title='" + title + '\'' +
                ", endHour='" + endHour + '\'' +
                ", endMinute='" + endMinute + '\'' +
                ", endSeconds='" + endSeconds + '\'' +
                '}';
    }
}
