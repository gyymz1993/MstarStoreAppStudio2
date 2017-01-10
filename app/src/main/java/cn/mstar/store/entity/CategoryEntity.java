package cn.mstar.store.entity;

/**
 * Created by Shinelon on 2016/1/11.
 */
public class CategoryEntity {

    public String gcId;
    public String gcName;
    public String typeId;

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "gcId='" + gcId + '\'' +
                ", gcName='" + gcName + '\'' +
                ", typeId='" + typeId + '\'' +
                '}';
    }
}
