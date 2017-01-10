package cn.mstar.store.entity;

/**
 * Created by Shinelon on 2016/1/22.
 */
public class RecommendNewEntity {

    public String pic;
    public String keywords;
    public String title;

    @Override
    public String toString() {
        return "RecommendNewEntity{" +
                "pic='" + pic + '\'' +
                ", categoryId='" + keywords + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
