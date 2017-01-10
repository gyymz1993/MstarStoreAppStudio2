package cn.mstar.store.entity;

import java.util.List;

/**
 * Created by Shinelon on 2016/1/26.
 */
public class RefoundEntity {

    public OrderInfo orderInfo;
    public List<ProInfo> proInfo;

    public class OrderInfo {
        public String orderId;
        public String refundAmount;
        public String buyerName;
        public String addtime;
        public String buyerMessage;
        public String refundState;
        public String sellerState;
        public String ifstate;
        public String ifship;
        public String eName;
        public String eCode;
        public String shippingCode;
        public String shipTime;
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
