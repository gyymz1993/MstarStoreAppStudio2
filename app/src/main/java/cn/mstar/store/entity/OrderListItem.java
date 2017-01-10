package cn.mstar.store.entity;

import java.util.Arrays;

/**
 * Created by UlrichAbiguime at Shenzhen.
 */
public class OrderListItem {

        public OrderInfo orderInfo;
        public ProInfo[] proInfo;


    public class OrderInfo {

        public Double totalPrice;
        public String createTime, orderId, statusTxt, address;
        public String count, status, updateTime, shippingCode;
        public int evaluation_state;
        public int refundState;
        public String oderType;

        @Override
        public String toString() {
            return "OrderInfo{" +
                    "totalPrice=" + totalPrice +
                    ", createTime='" + createTime + '\'' +
                    ", orderId='" + orderId + '\'' +
                    ", statusTxt='" + statusTxt + '\'' +
                    ", address='" + address + '\'' +
                    ", count='" + count + '\'' +
                    ", status='" + status + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    ", shippingCode='" + shippingCode + '\'' +
                    ", evaluation_state=" + evaluation_state +
                    ", refundState=" + refundState +
                    '}';
        }
    }

    public class ProInfo {

        public String pic, name, specialTitle;
        public String num, proSpecialId, proId, refund_state;
        public Double Price;
        public int evaluation_state;

        @Override
        public String toString() {
            return "ProInfo{" +
                    "pic='" + pic + '\'' +
                    ", name='" + name + '\'' +
                    ", specialTitle='" + specialTitle + '\'' +
                    ", num='" + num + '\'' +
                    ", proSpecialId='" + proSpecialId + '\'' +
                    ", proId='" + proId + '\'' +
                    ", refund_state='" + refund_state + '\'' +
                    ", evaluation_state='" + evaluation_state + '\'' +
                    ", Price=" + Price +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OrderListItem{" +
                "orderInfo=" + orderInfo +
                ", proInfo=" + Arrays.toString(proInfo) +
                '}';
    }
}
