package cn.mstar.store.entity;

import java.util.Arrays;



/**
 * Created by 1 on 2015/7/22.
 */
public class FavoriteTransactionManagerItem {


    /*

    \{
    "data": {
        "addProIds": null,
        "deleteFavoriteIds": null,
        "tokenKey": "312ef3dd-33d3-440a-96e2-f35da7e4c074"
    },
    "response": "",
    "error": "",
    "message": ""
}

    \*/

    public InnerData data;
    public String response, error, message;



    @Override
    public String toString() {
        return "FavoriteManagerItem{" +
                "data=" + data +
                ", response='" + response + '\'' +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                '}';
    }


    public class InnerData {


        @Override
        public String toString() {
            return "InnerData{" +
                    "addProIds=" + Arrays.toString(addProIds) +
                    ", deleteFavoriteIds=" + Arrays.toString(deleteFavoriteIds) +
                    ", tokenKey=" + (tokenKey) +
                    '}';
        }

        public  ProId[] addProIds;
        public  Ids[] deleteFavoriteIds;
        public String tokenKey;

    }

    public class ProId {

        private String proId;

        @Override
		public String toString() {
			return "ProId [proId=" + proId + "]";
		}

		public ProId(String p) {
            this.proId = p;
        }
    }

    
    public class Ids {

        @Override
		public String toString() {
			return "Ids [id=" + id + "]";
		}

		private String id;

        public Ids(String p) {
            this.id = p;
        }
    }

}
