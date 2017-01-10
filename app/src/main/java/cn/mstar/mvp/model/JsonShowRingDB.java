package cn.mstar.mvp.model;

import java.util.List;

import cn.mstar.store.entity.JsonResult;

/**
 * Created by Administrator on 2016/4/19.
 */
public class JsonShowRingDB extends JsonResult {

    /**
     * total_info : {"totalprice":3601,"yfprice":1080.3}
     * ring_info : {"ringId":"1024741","name":"戒托4（测试商品请勿购买） G18K白 10","price":"1101.00","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140439979746323_240.png","proNum":"","attr":["成色: G18K白","手寸: 10"]}
     * diamond_info : {"diamondId":"1024720","name":"裸钻6（测试商品请勿购买）","price":"2500.00","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05139592603749367_240.png","proNum":"","attr":["图形: 心形","主石重量: 0.4","钻石颜色: K-L","钻石净度: VS","钻石切工: EX"]}
     */

    private DataEntity data;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        /**
         * totalprice : 3601
         * yfprice : 1080.3
         */

        private TotalInfoEntity total_info;
        /**
         * ringId : 1024741
         * name : 戒托4（测试商品请勿购买） G18K白 10
         * price : 1101.00
         * pic : http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140439979746323_240.png
         * proNum :
         * attr : ["成色: G18K白","手寸: 10"]
         */

        private RingInfoEntity ring_info;
        /**
         * diamondId : 1024720
         * name : 裸钻6（测试商品请勿购买）
         * price : 2500.00
         * pic : http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05139592603749367_240.png
         * proNum :
         * attr : ["图形: 心形","主石重量: 0.4","钻石颜色: K-L","钻石净度: VS","钻石切工: EX"]
         */

        private DiamondInfoEntity diamond_info;

        public void setTotal_info(TotalInfoEntity total_info) {
            this.total_info = total_info;
        }

        public void setRing_info(RingInfoEntity ring_info) {
            this.ring_info = ring_info;
        }

        public void setDiamond_info(DiamondInfoEntity diamond_info) {
            this.diamond_info = diamond_info;
        }

        public TotalInfoEntity getTotal_info() {
            return total_info;
        }

        public RingInfoEntity getRing_info() {
            return ring_info;
        }

        public DiamondInfoEntity getDiamond_info() {
            return diamond_info;
        }

        public static class TotalInfoEntity {
            private int totalprice;
            private double yfprice;

            public void setTotalprice(int totalprice) {
                this.totalprice = totalprice;
            }

            public void setYfprice(double yfprice) {
                this.yfprice = yfprice;
            }

            public int getTotalprice() {
                return totalprice;
            }

            public double getYfprice() {
                return yfprice;
            }
        }

        public static class RingInfoEntity {
            private String ringId;
            private String name;
            private String price;
            private String pic;
            private String proNum;
            private List<String> attr;

            public void setRingId(String ringId) {
                this.ringId = ringId;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public void setProNum(String proNum) {
                this.proNum = proNum;
            }

            public void setAttr(List<String> attr) {
                this.attr = attr;
            }

            public String getRingId() {
                return ringId;
            }

            public String getName() {
                return name;
            }

            public String getPrice() {
                return price;
            }

            public String getPic() {
                return pic;
            }

            public String getProNum() {
                return proNum;
            }

            public List<String> getAttr() {
                return attr;
            }
        }

        public static class DiamondInfoEntity {
            private String diamondId;
            private String name;
            private String price;
            private String pic;
            private String proNum;
            private List<String> attr;

            public void setDiamondId(String diamondId) {
                this.diamondId = diamondId;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public void setProNum(String proNum) {
                this.proNum = proNum;
            }

            public void setAttr(List<String> attr) {
                this.attr = attr;
            }

            public String getDiamondId() {
                return diamondId;
            }

            public String getName() {
                return name;
            }

            public String getPrice() {
                return price;
            }

            public String getPic() {
                return pic;
            }

            public String getProNum() {
                return proNum;
            }

            public List<String> getAttr() {
                return attr;
            }
        }
    }
}
