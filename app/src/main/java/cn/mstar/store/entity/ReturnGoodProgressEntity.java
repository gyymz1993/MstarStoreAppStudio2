package cn.mstar.store.entity;

import java.util.Arrays;

/**
 * Created by UlrichAbiguime at Shenzhen.
 */
public class ReturnGoodProgressEntity {


    public TotalInfo totalInfo;
    public ReceiverInfo receiverInfo;
    public ProInfo[] proInfo;
    public String message;

    public class TotalInfo {

        public String states, orderNo;

        @Override
        public String toString() {
            return "TotalInfo{" +
                    "states='" + states + '\'' +
                    ", orderNo='" + orderNo + '\'' +
                    '}';
        }
    }

    public class ReceiverInfo {
        public String postName,
        mobile, address, shipping_code,
        e_name, add_time;

        @Override
        public String toString() {
            return "ReceiverInfo{" +
                    "postName='" + postName + '\'' +
                    ", mobile='" + mobile + '\'' +
                    ", address='" + address + '\'' +
                    ", shipping_code='" + shipping_code + '\'' +
                    ", e_name='" + e_name + '\'' +
                    ", add_time='" + add_time + '\'' +
                    '}';
        }
    }


    public class ProInfo {

       public String orderNo, pName, pic, title,
        specialTitle;

        @Override
        public String toString() {
            return "ProInfo{" +
                    "orderNo='" + orderNo + '\'' +
                    ", pName='" + pName + '\'' +
                    ", pic='" + pic + '\'' +
                    ", title='" + title + '\'' +
                    ", specialTitle='" + specialTitle + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ReturnGoodProgressEntity{" +
                "totalInfo=" + totalInfo +
                ", receiverInfo=" + receiverInfo +
                ", proInfo=" + Arrays.toString(proInfo) +
                ", message='" + message + '\'' +
                '}';
    }
}
