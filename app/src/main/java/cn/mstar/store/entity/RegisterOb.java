package cn.mstar.store.entity;

import java.util.Arrays;

/**
 * Created by Ultima on 7/15/2015.
 */
public class RegisterOb {


    public String response, error, message;
    public InnerData data;


    @Override
    public String toString() {
        return "RegisterOb{" +
                "response='" + response + '\'' +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public class InnerData {
        public String userName, phoneNum, password, checkWord, error;
        public long[] specialIdAndNumbs, proIdAndNums;
        public String tokenKey;

        @Override
        public String toString() {
            return "InnerData{" +
                    "username='" + userName + '\'' +
                    ", phoneNum='" + phoneNum + '\'' +
                    ", password='" + password + '\'' +
                    ", checkWord='" + checkWord + '\'' +
                    ", specialIdAndNumbs=" + Arrays.toString(specialIdAndNumbs) +
                    ", proIdAndNums=" + Arrays.toString(proIdAndNums) +
                    ", tokenKey='" + tokenKey + '\'' +
                    '}';
        }
    }



}
