package cn.mstar.store.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/1/22.
 */
public class Json_LowBra {

    /**
     * response :
     * error : 0
     * message : ok
     * data : {"storeInfo":[{"storeName":"测试","addtime":"2015-08-11 14:36:44","trueName":"test","storeTel":"","amount":0}],"list_count":"1"}
     */

    private String response;
    private String error;
    private String message;
    /**
     * storeInfo : [{"storeName":"测试","addtime":"2015-08-11 14:36:44","trueName":"test","storeTel":"","amount":0}]
     * list_count : 1
     */

    private DataEntity data;

    public void setResponse(String response) {
        this.response = response;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public String getResponse() {
        return response;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        private String list_count;
        /**
         * storeName : 测试
         * addtime : 2015-08-11 14:36:44
         * trueName : test
         * storeTel :
         * amount : 0
         */

        private List<StoreInfoEntity> storeInfo;

        public void setList_count(String list_count) {
            this.list_count = list_count;
        }

        public void setStoreInfo(List<StoreInfoEntity> storeInfo) {
            this.storeInfo = storeInfo;
        }

        public String getList_count() {
            return list_count;
        }

        public List<StoreInfoEntity> getStoreInfo() {
            return storeInfo;
        }

        public static class StoreInfoEntity {
            private String storeName;
            private String addtime;
            private String trueName;
            private String storeTel;
            private int amount;
            private String memberId;

            public void setStoreName(String storeName) {
                this.storeName = storeName;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public void setTrueName(String trueName) {
                this.trueName = trueName;
            }

            public void setStoreTel(String storeTel) {
                this.storeTel = storeTel;
            }

            public void setAmount(int amount) {
                this.amount = amount;
            }

            public String getStoreName() {
                return storeName;
            }

            public String getAddtime() {
                return addtime;
            }

            public String getTrueName() {
                return trueName;
            }

            public String getStoreTel() {
                return storeTel;
            }

            public int getAmount() {
                return amount;
            }

            public String getMemberId() {
                return memberId;
            }

            public void setMemberId(String memberId) {
                this.memberId = memberId;
            }
        }
    }
}
