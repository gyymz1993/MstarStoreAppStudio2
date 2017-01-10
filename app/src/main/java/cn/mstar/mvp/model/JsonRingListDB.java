package cn.mstar.mvp.model;

import java.util.List;

import cn.mstar.store.entity.JsonResult;

/**
 * Created by Administrator on 2016/4/14.
 */
public class JsonRingListDB extends JsonResult {

    /**
     * proInfo : [{"proId":"1024716","price":"1500.00","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05139590125641272_240.png","attr":["图形: 圆形","主石重量: 0.4","钻石颜色: K-L","钻石净度: VS","钻石切工: EX"]},{"proId":"1024715","price":"1000.00","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05139589723500324_240.png","attr":["图形: 圆形","主石重量: 0.4","钻石颜色: K-L","钻石净度: VS","钻石切工: EX"]}]
     * list_count : 2
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
         * proId : 1024716
         * price : 1500.00
         * pic : http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05139590125641272_240.png
         * attr : ["图形: 圆形","主石重量: 0.4","钻石颜色: K-L","钻石净度: VS","钻石切工: EX"]
         */

        private List<ProInfoEntity> proInfo;

        public void setList_count(String list_count) {
            this.list_count = list_count;
        }

        public void setProInfo(List<ProInfoEntity> proInfo) {
            this.proInfo = proInfo;
        }

        public String getList_count() {
            return list_count;
        }

        public List<ProInfoEntity> getProInfo() {
            return proInfo;
        }

        public static class ProInfoEntity {
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
    }
}
