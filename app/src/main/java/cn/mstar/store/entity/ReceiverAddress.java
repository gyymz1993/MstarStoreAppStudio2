package cn.mstar.store.entity;

import java.io.Serializable;

import cn.mstar.store.utils.L;

/**收货地址
 * @author wenjundu
 *
 */
public class ReceiverAddress implements Serializable{

	private int addressId;
	private String fullPostAddress;
	private String name;
	private String address;
	private String phone;

	private Boolean isDefalutAddress;
	private int provinceId;
	private int cityId;
	private int countyId;
	private String email;
	private String zipCode;
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Boolean getIsDefalutAddress() {
		return isDefalutAddress;
	}
	public void setIsDefalutAddress(Boolean isDefalutAddress) {
		this.isDefalutAddress = isDefalutAddress;
	}

	public ReceiverAddress(){

	}
	public ReceiverAddress(String name,String phone,String address,Boolean isDefalutAddress){
		this.name=name;
		this.phone=phone;
		this.address=address;
		this.isDefalutAddress=isDefalutAddress;
	}

	public int getAddressId() {
		return addressId;
	}
	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}
	public String getFullPostAddress() {
		return fullPostAddress;
	}
	public void setFullPostAddress(String fullPostAddress) {
		this.fullPostAddress = fullPostAddress;
	}
	public int getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public int getCountyId() {
		return countyId;
	}
	public void setCountyId(int countyId) {
		this.countyId = countyId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}


	@Override
	public String toString() {
		return "ReceiverAddress{" +
				"addressId=" + addressId +
				", fullPostAddress='" + fullPostAddress + '\'' +
				", name='" + name + '\'' +
				", address='" + address + '\'' +
				", phone='" + phone + '\'' +
				", isDefalutAddress=" + isDefalutAddress +
				", provinceId=" + provinceId +
				", cityId=" + cityId +
				", countyId=" + countyId +
				", email='" + email + '\'' +
				", zipCode='" + zipCode + '\'' +
				'}';
	}

	public boolean isNull() {

		L.d("add::", toString());
		if (addressId == 0)
			return true;
		if ("".equals(fullPostAddress))
			return true;
		if ("".equals(name))
			return true;
		/*if ("".equals(address))
			return true;*/
		if ("".equals(phone))
			return true;
	/*	if (provinceId == 0)
			return true;
		if (cityId == 0)
			return true;
		if (countyId == 0)
			return true;
		if ("".equals(zipCode))
			return true;*/
		return false;
	}
}
