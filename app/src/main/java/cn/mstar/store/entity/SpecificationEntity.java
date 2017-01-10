package cn.mstar.store.entity;

import java.util.List;

/**
 * Created by Shinelon on 2016/1/11.
 */
public class SpecificationEntity {

    public String spId;
    public String speValue;
    public String stocks;
    public String price;
    public String costPrice;
    public String marketPrice;
    public String netWeight;
    public String basicCost;
    public String goodsId;

    public SpecificationEntity() {
    }

    public SpecificationEntity(List<String> params) {
        speValue = params.get(0);
        spId = params.get(1);
        stocks = params.get(2);
        costPrice = params.get(3);
        marketPrice = params.get(4);
        price = params.get(5);
        netWeight = params.get(6);
        basicCost = params.get(7);
    }

    @Override
    public String toString() {
        return speValue + "," + spId + "," + stocks + "," + costPrice
                + "," + marketPrice + "," + price + "," + netWeight + "," + basicCost;
    }

    public String getString() {
        return goodsId + "," + price;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public String getSpeValue() {
        return speValue;
    }

    public void setSpeValue(String speValue) {
        this.speValue = speValue;
    }

    public String getStocks() {
        return stocks + "件";
    }

    public void setStocks(String stocks) {
        this.stocks = stocks;
    }

    public String getPrice() {
        return "¥" + price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }

    public String getMarketPrice() {
        return "¥" + marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getNetWeight() {
        return netWeight + "g";
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getBasicCost() {
        return basicCost;
    }

    public void setBasicCost(String basicCost) {
        this.basicCost = basicCost;
    }
}
