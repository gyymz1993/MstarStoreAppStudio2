package cn.mstar.store.entity;

import java.io.Serializable;

import cn.mstar.store.entity.OrderDetailsEntity;

/**
 * 购物车 listitem entity
 * @author Administrator
 *
 */
public class BuyProductEntity implements Serializable {


	private Product product;
	private int buyNum=1;//购买数量
	private String norms="";//规格
	private int cartId;//购物篮id
	public int refund_state, evaluation_state;

	public BuyProductEntity(OrderDetailsEntity.OrderItems instance) {

		this.buyNum = instance.num;
		this.norms = instance.specialTitle;
//		this.cartId = instance. 没有购物篮ID
		product = new Product(instance);
		refund_state = instance.refund_state;
		evaluation_state = instance.evaluation_state;
	}

	public BuyProductEntity() {
	}

	@Override
	public String toString() {
		return "BuyProductEntity{" +
				"product=" + product +
				", buyNum=" + buyNum +
				", norms='" + norms + '\'' +
				", cartId=" + cartId +
				", refund_state=" + refund_state +
				'}';
	}

	/*
	  "proNo": "DEFHJ0005",
                "itemId": "281",
                "orderId": "7000000000037201",
                "title": "千禧之星 足金金条黄金砖投资收藏结婚送礼正品 百年好合",
                "proSpecialId": "440",
                "proId": "440",
                "price": "5020.00",
                "totalPrice": 0,
                "num": "0",
                "specialTitle": null,
                "pic": "http://www.fanerjewelry.com/data/upload/shop/store/goods/1/1_04922802529231243_240.jpg"



	    */

	public int getCartId() {
		return cartId;
	}

	public void setCartId(int cartId) {
		this.cartId = cartId;
	}


	public String getNorms() {
		return norms;
	}
	public void setNorms(String norms) {
		this.norms = norms;
	}

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getBuyNum() {
		return buyNum;
	}
	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}


}
