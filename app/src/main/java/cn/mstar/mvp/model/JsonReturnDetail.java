package cn.mstar.mvp.model;

import cn.mstar.store.entity.JsonResult;

/**
 * Created by Administrator on 2016/3/22.
 */
public class JsonReturnDetail extends JsonResult{


    /**
     * refundSn : 533101160321100856
     * refundType : 退货
     * addtime : 2016-03-21 10:08:56
     * goodsName : 钻石女戒 G18K白 14号 4分
     * amount : 2259.00
     * num : 1
     * buyerMessage : asdf
     * sellerState : 同意
     * sellerMessage : sadf
     * ifdelivery : 1
     */

    private DataEntity data;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        private String refundSn;
        private String refundType;
        private String addtime;
        private String goodsName;
        private String amount;
        private String num;
        private String buyerMessage;
        private String sellerState;
        private String sellerMessage;
        private int ifdelivery;

        public void setRefundSn(String refundSn) {
            this.refundSn = refundSn;
        }

        public void setRefundType(String refundType) {
            this.refundType = refundType;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public void setBuyerMessage(String buyerMessage) {
            this.buyerMessage = buyerMessage;
        }

        public void setSellerState(String sellerState) {
            this.sellerState = sellerState;
        }

        public void setSellerMessage(String sellerMessage) {
            this.sellerMessage = sellerMessage;
        }

        public void setIfdelivery(int ifdelivery) {
            this.ifdelivery = ifdelivery;
        }

        public String getRefundSn() {
            return refundSn;
        }

        public String getRefundType() {
            return refundType;
        }

        public String getAddtime() {
            return addtime;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public String getAmount() {
            return amount;
        }

        public String getNum() {
            return num;
        }

        public String getBuyerMessage() {
            return buyerMessage;
        }

        public String getSellerState() {
            return sellerState;
        }

        public String getSellerMessage() {
            return sellerMessage;
        }

        public int getIfdelivery() {
            return ifdelivery;
        }
    }
}
