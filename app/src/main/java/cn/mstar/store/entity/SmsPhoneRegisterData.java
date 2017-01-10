package cn.mstar.store.entity;

/**
 * Created by Ultima on 7/14/2015.
 */
public class SmsPhoneRegisterData {

    // i only need to check whether the sent is ok... then it's ok.

    public String reponse, error, message;
    public InnerData data;


    @Override
    public String toString() {
        return "SmsPhoneRegisterData{" +
                "reponse='" + reponse + '\'' +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public class InnerData {
        public int code;
        public String msg;
        Result result;

        @Override
        public String toString() {
            return "InnerData{" +
                    "code=" + code +
                    ", isOk='" + msg + '\'' +
                    ", result=" + result +
                    '}';
        }

        public class Result {
            public int count, fee;
            public long sid;

            @Override
            public String toString() {
                return "Result{" +
                        "count=" + count +
                        ", fee=" + fee +
                        ", sid=" + sid +
                        '}';
            }
        }
    }

}
