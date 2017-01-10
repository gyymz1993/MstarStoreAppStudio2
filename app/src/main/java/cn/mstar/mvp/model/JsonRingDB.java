package cn.mstar.mvp.model;

import java.util.List;

import cn.mstar.store.entity.JsonResult;

/**
 * Created by Administrator on 2016/4/13.
 */
public class JsonRingDB extends JsonResult{

    private DataEntity data;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        /**
         * name : 图形
         * value : [{"attrValueId":"3334","attrValueName":"http://qx.fanershop.com/data/upload/shop/diamond/circular.png"},{"attrValueId":"3335","attrValueName":"http://qx.fanershop.com/data/upload/shop/diamond/square.png"},{"attrValueId":"3336","attrValueName":"http://qx.fanershop.com/data/upload/shop/diamond/olivary.png"},{"attrValueId":"3337","attrValueName":"http://qx.fanershop.com/data/upload/shop/diamond/teardrop.png"},{"attrValueId":"3338","attrValueName":"http://qx.fanershop.com/data/upload/shop/diamond/cordate.png"}]
         */

        private List<ProInfoEntity> proInfo;

        public void setProInfo(List<ProInfoEntity> proInfo) {
            this.proInfo = proInfo;
        }

        public List<ProInfoEntity> getProInfo() {
            return proInfo;
        }

        public static class ProInfoEntity {
            private String name;
            /**
             * attrValueId : 3334
             * attrValueName : http://qx.fanershop.com/data/upload/shop/diamond/circular.png
             */

            private List<ValueEntity> value;

            public void setName(String name) {
                this.name = name;
            }

            public void setValue(List<ValueEntity> value) {
                this.value = value;
            }

            public String getName() {
                return name;
            }

            public List<ValueEntity> getValue() {
                return value;
            }

            public static class ValueEntity {
                private String attrValueId;
                private String attrValueName;

                public void setAttrValueId(String attrValueId) {
                    this.attrValueId = attrValueId;
                }

                public void setAttrValueName(String attrValueName) {
                    this.attrValueName = attrValueName;
                }

                public String getAttrValueId() {
                    return attrValueId;
                }

                public String getAttrValueName() {
                    return attrValueName;
                }
            }
        }
    }
}
