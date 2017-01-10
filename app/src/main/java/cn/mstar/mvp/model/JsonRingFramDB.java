package cn.mstar.mvp.model;

import java.util.List;

import cn.mstar.store.entity.JsonResult;

/**
 * Created by Administrator on 2016/4/18.
 */
public class JsonRingFramDB extends JsonResult{


    /**
     * diamond_info : {"proId":"1024720","price":"2500.00","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05139592603749367_240.png","attr":["图形: 心形","主石重量: 0.4","钻石颜色: K-L","钻石净度: VS","钻石切工: EX"]}
     * ring_info : [{"proId":"1024721","price":"1001.00","name":"戒托1（测试商品请勿购买） G18K白 9","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437521932211_240.png"},{"proId":"1024737","price":"1501.00","name":"戒托3（测试商品请勿购买） PT950 10","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140439289240797_240.png"},{"proId":"1024733","price":"1201.00","name":"戒托2（测试商品请勿购买） G18K白 9","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140438576824100_240.png"},{"proId":"1024741","price":"1101.00","name":"戒托4（测试商品请勿购买） G18K白 10","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140439979746323_240.png"}]
     * list_count : 4
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
         * proId : 1024720
         * price : 2500.00
         * pic : http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05139592603749367_240.png
         * attr : ["图形: 心形","主石重量: 0.4","钻石颜色: K-L","钻石净度: VS","钻石切工: EX"]
         */

        private DiamondInfoEntity diamond_info;
        private String list_count;
        /**
         * proId : 1024721
         * price : 1001.00
         * name : 戒托1（测试商品请勿购买） G18K白 9
         * pic : http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437521932211_240.png
         */

        private List<RingInfoEntity> ring_info;

        public void setDiamond_info(DiamondInfoEntity diamond_info) {
            this.diamond_info = diamond_info;
        }

        public void setList_count(String list_count) {
            this.list_count = list_count;
        }

        public void setRing_info(List<RingInfoEntity> ring_info) {
            this.ring_info = ring_info;
        }

        public DiamondInfoEntity getDiamond_info() {
            return diamond_info;
        }

        public String getList_count() {
            return list_count;
        }

        public List<RingInfoEntity> getRing_info() {
            return ring_info;
        }

        public static class DiamondInfoEntity {
            private String proId;
            private String price;
            private String pic;
            private List<String> attr;

            public void setProId(String proId) {
                this.proId = proId;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public void setAttr(List<String> attr) {
                this.attr = attr;
            }

            public String getProId() {
                return proId;
            }

            public String getPrice() {
                return price;
            }

            public String getPic() {
                return pic;
            }

            public List<String> getAttr() {
                return attr;
            }
        }

        public static class RingInfoEntity {
            private String proId;
            private String price;
            private String name;
            private String pic;

            public void setProId(String proId) {
                this.proId = proId;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getProId() {
                return proId;
            }

            public String getPrice() {
                return price;
            }

            public String getName() {
                return name;
            }

            public String getPic() {
                return pic;
            }
        }
    }
}
