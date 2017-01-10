package cn.mstar.store.entity;

/**
 * Created by Shinelon on 2016/1/20.
 */
public class RolateImageEntity {

    public String pic;
    public String keywords;
    public String title;

    @Override
    public String toString() {
        return "RolateImageEntity{" +
                "pic='" + pic + '\'' +
                ", categoryId='" + keywords + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
