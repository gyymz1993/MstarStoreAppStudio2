package cn.mstar.mvp.model;

import java.io.Serializable;
import java.util.List;

import cn.mstar.store.entity.JsonResult;

/**
 * Created by Administrator on 2016/4/18.
 */
public class JsonRingDetail extends JsonResult implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * product : {"ringId":"1024721","diamondId":"1024720","name":"戒托1（测试商品请勿购买） G18K白 9","pic":"http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437521932211_240.png","pics":["http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437521932211_240.png","http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437741627266_240.png","http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437772145385_240.png","http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437804232476_240.png","http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437835519110_240.png"],"price":"1001.00","proSpecialID":"1024721","stock":"10","desc":"\"image\"商品描述1<\/span>\"image\"\"image\"","ifmshop":0}
     * productSpecList : [{"id":24,"title":"成色","attrName":"attr1","specSubTitle":["G18K白","PT950","G18K玫瑰金"]},{"id":25,"title":"手寸","attrName":"attr2","specSubTitle":["9","10","14","16"]}]
     * productSpecPrice : [{"id":"1024721","price":"1001.00","attrs1":"G18K白","attrs2":"9","attrs3":"","stock":"10"},{"id":"1024722","price":"1002.00","attrs1":"G18K白","attrs2":"10","attrs3":"","stock":"11"},{"id":"1024723","price":"1003.00","attrs1":"G18K白","attrs2":"14","attrs3":"","stock":"12"},{"id":"1024724","price":"1004.00","attrs1":"G18K白","attrs2":"16","attrs3":"","stock":"13"},{"id":"1024725","price":"1005.00","attrs1":"PT950","attrs2":"9","attrs3":"","stock":"14"},{"id":"1024726","price":"1006.00","attrs1":"PT950","attrs2":"10","attrs3":"","stock":"15"},{"id":"1024727","price":"1007.00","attrs1":"PT950","attrs2":"14","attrs3":"","stock":"16"},{"id":"1024728","price":"1008.00","attrs1":"PT950","attrs2":"16","attrs3":"","stock":"17"},{"id":"1024729","price":"1009.00","attrs1":"G18K玫瑰金","attrs2":"9","attrs3":"","stock":"18"},{"id":"1024730","price":"1010.00","attrs1":"G18K玫瑰金","attrs2":"10","attrs3":"","stock":"19"},{"id":"1024731","price":"1011.00","attrs1":"G18K玫瑰金","attrs2":"14","attrs3":"","stock":"20"},{"id":"1024732","price":"1012.00","attrs1":"G18K玫瑰金","attrs2":"16","attrs3":"","stock":"21"}]
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
         * ringId : 1024721
         * diamondId : 1024720
         * name : 戒托1（测试商品请勿购买） G18K白 9
         * pic : http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437521932211_240.png
         * pics : ["http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437521932211_240.png","http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437741627266_240.png","http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437772145385_240.png","http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437804232476_240.png","http://qx.fanershop.com/data/upload/shop/store/goods/1/1_05140437835519110_240.png"]
         * price : 1001.00
         * proSpecialID : 1024721
         * stock : 10
         * desc : "image"商品描述1</span>"image""image"
         * ifmshop : 0
         */

        private ProductEntity product;
        /**
         * id : 24
         * title : 成色
         * attrName : attr1
         * specSubTitle : ["G18K白","PT950","G18K玫瑰金"]
         */

        private List<ProductSpecListEntity> productSpecList;
        /**
         * id : 1024721
         * price : 1001.00
         * attrs1 : G18K白
         * attrs2 : 9
         * attrs3 :
         * stock : 10
         */

        private List<ProductSpecPriceEntity> productSpecPrice;

        public void setProduct(ProductEntity product) {
            this.product = product;
        }

        public void setProductSpecList(List<ProductSpecListEntity> productSpecList) {
            this.productSpecList = productSpecList;
        }

        public void setProductSpecPrice(List<ProductSpecPriceEntity> productSpecPrice) {
            this.productSpecPrice = productSpecPrice;
        }

        public ProductEntity getProduct() {
            return product;
        }

        public List<ProductSpecListEntity> getProductSpecList() {
            return productSpecList;
        }

        public List<ProductSpecPriceEntity> getProductSpecPrice() {
            return productSpecPrice;
        }

        public static class ProductEntity {
            private String ringId;
            private String diamondId;
            private String name;
            private String pic;
            private String price;
            private String proSpecialID;
            private String stock;
            private String desc;
            private int ifmshop;
            private List<String> pics;

            public void setRingId(String ringId) {
                this.ringId = ringId;
            }

            public void setDiamondId(String diamondId) {
                this.diamondId = diamondId;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public void setProSpecialID(String proSpecialID) {
                this.proSpecialID = proSpecialID;
            }

            public void setStock(String stock) {
                this.stock = stock;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public void setIfmshop(int ifmshop) {
                this.ifmshop = ifmshop;
            }

            public void setPics(List<String> pics) {
                this.pics = pics;
            }

            public String getRingId() {
                return ringId;
            }

            public String getDiamondId() {
                return diamondId;
            }

            public String getName() {
                return name;
            }

            public String getPic() {
                return pic;
            }

            public String getPrice() {
                return price;
            }

            public String getProSpecialID() {
                return proSpecialID;
            }

            public String getStock() {
                return stock;
            }

            public String getDesc() {
                return desc;
            }

            public int getIfmshop() {
                return ifmshop;
            }

            public List<String> getPics() {
                return pics;
            }
        }

        public static class ProductSpecListEntity {
            private int id;
            private String title;
            private String attrName;
            private List<String> specSubTitle;

            public void setId(int id) {
                this.id = id;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public void setAttrName(String attrName) {
                this.attrName = attrName;
            }

            public void setSpecSubTitle(List<String> specSubTitle) {
                this.specSubTitle = specSubTitle;
            }

            public int getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getAttrName() {
                return attrName;
            }

            public List<String> getSpecSubTitle() {
                return specSubTitle;
            }
        }

        public static class ProductSpecPriceEntity {
            private String id;
            private String price;
            private String attrs1;
            private String attrs2;
            private String attrs3;
            private String stock;

            public void setId(String id) {
                this.id = id;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public void setAttrs1(String attrs1) {
                this.attrs1 = attrs1;
            }

            public void setAttrs2(String attrs2) {
                this.attrs2 = attrs2;
            }

            public void setAttrs3(String attrs3) {
                this.attrs3 = attrs3;
            }

            public void setStock(String stock) {
                this.stock = stock;
            }

            public String getId() {
                return id;
            }

            public String getPrice() {
                return price;
            }

            public String getAttrs1() {
                return attrs1;
            }

            public String getAttrs2() {
                return attrs2;
            }

            public String getAttrs3() {
                return attrs3;
            }

            public String getStock() {
                return stock;
            }
        }
    }
}
