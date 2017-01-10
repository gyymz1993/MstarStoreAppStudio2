package cn.mstar.store.entity;

/**
 * Created by Shinelon on 2016/1/14.
 */
public class MyTrainEntity {

    public String articleId;
    public String articleTitle;
    public String addtime;
    public String pic;

    @Override
    public String toString() {
        return "MyTrainEntity{" +
                "articleId='" + articleId + '\'' +
                ", articleTitle='" + articleTitle + '\'' +
                ", addtime='" + addtime + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }
}
