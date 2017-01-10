package cn.mstar.mvp.model;

import java.util.List;

import cn.mstar.store.entity.JsonResult;

/**
 * Created by Administrator on 2016/3/22.
 */
public class ReturnListBn extends JsonResult {


    /**
     * return_info : [{"refundSn":"246208160321093639","refundType":"退货","goodsName":"范儿珠宝正品 白k金钻石女戒 邂逅(12号（金750 16分）)","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_56ea4a62ef112_240.jpg","amount":"0.00","num":"1","sellerState":"卖家处理状态：同意","addtime":"2016-03-21 09:36:39"},{"refundSn":"727208160318171741","refundType":"退货","goodsName":"钻石耳环","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/7/7_05112951163627578_240.jpg","amount":"0.00","num":"1","sellerState":"卖家处理状态：待审核","addtime":"2016-03-18 17:17:41"},{"refundSn":"418208160318163423","refundType":"退货","goodsName":"范儿珠宝正品 白k金钻石女戒 邂逅(12号（金750 16分）)","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_56ea4a62ef112_240.jpg","amount":"0.00","num":"1","sellerState":"卖家处理状态：同意","addtime":"2016-03-18 16:34:23"}]
     * list_count : 3
     */

    private DataEntity data;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        private String list_count;
        /**
         * refundSn : 246208160321093639
         * refundType : 退货
         * goodsName : 范儿珠宝正品 白k金钻石女戒 邂逅(12号（金750 16分）)
         * pic : http://qx.fanershop.com/data/upload/shop/store/goods/1/1_56ea4a62ef112_240.jpg
         * amount : 0.00
         * num : 1
         * sellerState : 卖家处理状态：同意
         * addtime : 2016-03-21 09:36:39
         */

        private List<ReturnInfoEntity> return_info;

        public void setList_count(String list_count) {
            this.list_count = list_count;
        }

        public void setReturn_info(List<ReturnInfoEntity> return_info) {
            this.return_info = return_info;
        }

        public String getList_count() {
            return list_count;
        }

        public List<ReturnInfoEntity> getReturn_info() {
            return return_info;
        }

        public static class ReturnInfoEntity {
            private String refundSn;
            private String refundType;
            private String goodsName;
            private String pic;
            private String amount;
            private String num;
            private String sellerState;
            private String addtime;

            public void setRefundSn(String refundSn) {
                this.refundSn = refundSn;
            }

            public void setRefundType(String refundType) {
                this.refundType = refundType;
            }

            public void setGoodsName(String goodsName) {
                this.goodsName = goodsName;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public void setNum(String num) {
                this.num = num;
            }

            public void setSellerState(String sellerState) {
                this.sellerState = sellerState;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public String getRefundSn() {
                return refundSn;
            }

            public String getRefundType() {
                return refundType;
            }

            public String getGoodsName() {
                return goodsName;
            }

            public String getPic() {
                return pic;
            }

            public String getAmount() {
                return amount;
            }

            public String getNum() {
                return num;
            }

            public String getSellerState() {
                return sellerState;
            }

            public String getAddtime() {
                return addtime;
            }
        }
    }
}
