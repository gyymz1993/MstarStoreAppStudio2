package cn.mstar.mvp.model;

/**
 * Created by Administrator on 2016/3/22.
 */
public class ReturnFlowDB {
    private String date;
    private String message;
    private  boolean  isComMeg=true;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isComMeg() {
        return isComMeg;
    }

    public void setIsComMeg(boolean isComMeg) {
        this.isComMeg = isComMeg;
    }
}
