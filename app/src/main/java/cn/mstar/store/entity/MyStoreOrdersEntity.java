package cn.mstar.store.entity;

import java.util.List;

/**
 * Created by Shinelon on 2016/1/25.
 */
public class MyStoreOrdersEntity {

    public OrderInfo orderInfo;
    public List<ProInfo> proInfo;

    public class OrderInfo {
        public String totalPrice;
        public String orderId;
        public String count;
        public String status;
        public String statusTxt;
        public String fxcomm;
        public String eCode;
        public String eName;
        public String shippingCode;
    }

    public class ProInfo {
        public String pic;
        public String name;
        public String specialTitle;
        public String num;
        public String Price;
        public String proId;
    }
}
