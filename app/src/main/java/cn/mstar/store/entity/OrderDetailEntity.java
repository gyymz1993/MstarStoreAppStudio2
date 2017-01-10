package cn.mstar.store.entity;

/**
 * Created by Shinelon on 2016/1/26.
 */
public class OrderDetailEntity {

    public class Order {
        public String orderId;
        public String postName;
        public String mobile;
        public String status;
        public String store_name;
        public String address;
        public String orderTotalPrice;
        public String statusTxt;
        public String addtime;
        public String invoice_info;
        public String voucher_price;
        public String shipping_fee;
        public String eName;
        public String eCode;
        public String shippingCode;
        public String fxcomm;
    }

    public class OrderItem {
        public String title;
        public String proSpecialId;
        public String price;
        public String pay_price;
        public String totalPrice;
        public String num;
        public String specialTitle;
        public String pic;
        public String evaluation_state;
        public String promotionInfo;
    }
}
