package cn.mstar.mvp.model;

import cn.mstar.store.entity.JsonResult;

/**
 * Created by Administrator on 2016/3/23.
 */
public class JsonRetuenFlowDB extends JsonResult{


    /**
     * oneStepTime : 2016-03-22 11:38:09
     * oneStep : 买家发起退货申请.原因：sdaf
     * twoStepTime : 2016-03-22 14:31:53
     * twoStep : 商家已同意。备注：aaab
     * threeStepTime : 2016-03-22 14:40:01
     * threeStep : 买家已发货。物流公司：顺丰快递，物流单号：123456789
     * fourStepTime : 2016-03-22 14:56:21
     * fourStep : 商家已收到货。
     * fineStepTime : 2016-03-22 15:01:49
     * fineStep : 平台审核完成！
     */

    private DataEntity data;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        private String oneStepTime;
        private String oneStep;
        private String twoStepTime;
        private String twoStep;
        private String threeStepTime;
        private String threeStep;
        private String fourStepTime;
        private String fourStep;
        private String fineStepTime;
        private String fineStep;

        public void setOneStepTime(String oneStepTime) {
            this.oneStepTime = oneStepTime;
        }

        public void setOneStep(String oneStep) {
            this.oneStep = oneStep;
        }

        public void setTwoStepTime(String twoStepTime) {
            this.twoStepTime = twoStepTime;
        }

        public void setTwoStep(String twoStep) {
            this.twoStep = twoStep;
        }

        public void setThreeStepTime(String threeStepTime) {
            this.threeStepTime = threeStepTime;
        }

        public void setThreeStep(String threeStep) {
            this.threeStep = threeStep;
        }

        public void setFourStepTime(String fourStepTime) {
            this.fourStepTime = fourStepTime;
        }

        public void setFourStep(String fourStep) {
            this.fourStep = fourStep;
        }

        public void setFineStepTime(String fineStepTime) {
            this.fineStepTime = fineStepTime;
        }

        public void setFineStep(String fineStep) {
            this.fineStep = fineStep;
        }

        public String getOneStepTime() {
            return oneStepTime;
        }

        public String getOneStep() {
            return oneStep;
        }

        public String getTwoStepTime() {
            return twoStepTime;
        }

        public String getTwoStep() {
            return twoStep;
        }

        public String getThreeStepTime() {
            return threeStepTime;
        }

        public String getThreeStep() {
            return threeStep;
        }

        public String getFourStepTime() {
            return fourStepTime;
        }

        public String getFourStep() {
            return fourStep;
        }

        public String getFineStepTime() {
            return fineStepTime;
        }

        public String getFineStep() {
            return fineStep;
        }
    }
}
