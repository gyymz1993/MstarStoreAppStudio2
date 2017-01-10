package cn.mstar.store.entity;

/**
 * 支付方式 Administrator on 2015/8/10.
 */
public class PayType {

    private int typeId;
    private String typeName;

    public PayType() {
    }

    public PayType(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public boolean isNull() {

        if (typeId == 0)
            return true;
        if ("".equals(typeId))
            return true;
        return false;
    }
}
