package cn.mstar.store.app;

import android.os.Environment;

import java.util.HashMap;
import java.util.Map;

import cn.mstar.store.entity.RegisterOb;

public class AppURL {
    //    public static final String BASE_URL = "http://www.fanershop.com/";
    //public static final String BASE_URL = "http://kaifa.fanershop.com/";
    //	public final static String BASE_URL="http://www.fanershop.com/mobile/index.php?";
    // public final static String BASE_URL = "http://kaifa.fanershop.com/mobile/index.php?";
    public static final String BASE_URL = "http://qx.fanershop.com/mobile/index.php?";
    public static final String BASE_URL1 = "http://qx.fanershop.com/";  //用于微信支付
    //public static final String BASE_URL = "http://kaifa.fanershop.com/";
//    public static final  String OLD_BASE_URL = "http://kaifa.fanershop.com/mobile/index.php?";

    public static final class RequestType {
        public static Map<String, Class<RegisterOb>> typeDb;

        static {
            typeDb = new HashMap<>();
            typeDb.put(REGISTERING_ACT, RegisterOb.class);
        }

    }

    public final static String HOME_URL=BASE_URL+"act=mobile_store";
    public final static String BASE_URL0 = "http://m.fanershop.com/index.php?";
    public final static String BASE_URL_ZHIFUBAO = "http://kaifa.fanershop.com/mobile/";
    public final static String APPLY_FOR_AGENT_CALLBACK = "http://kaifa.fanershop.com/mobile/api/payment/alipay/call_back_url_store.php";
    // x首页路径
    public static final String IMAGE_URL = BASE_URL + "act=home";

    //首页路径2
    public static final String HOME_PAGE_URL = BASE_URL + "act=mobile_store";

    // 搜索路径,产品列表路径
    public static final String SEARCH_URL = BASE_URL + "act=goodsb&op=goods_list";

    // x产品详情路径
    public static final String PRODUCTDETAIL_URL = BASE_URL + "act=goodsb&op=goods_detail";

    // 筛选路径
    public static final String SEARCH_FILTERCLASS_URL = BASE_URL + "/App/FilterClassType";

    //x 更多分类路径
    public static final String MORECLASSIFY_URL = BASE_URL + "act=goods_list";

    // x商品规格选择路径
    public static final String CUSTOM_MADE_URL = BASE_URL + "act=goods_spc";

    // 未登陆购物篮路径
    public static final String SHOP_CART_URL = BASE_URL + "/App/CartView/Temp";

    //商品筛选接口
    public static final String PRODUCT_FILTER = BASE_URL + "/App/ProductList/FilterClassList?";

    //x选择区域
    public static final String SELECT_PROVINCE_URL = BASE_URL + "act=area_list";
    //选择城市接口
    public static final String SELECT_CITY_URL = BASE_URL + "/App/AreaAddress/city?";
    //选择区县接口
    public static final String SELECT_AREA_URL = BASE_URL + "/App/AreaAddress/county?";
    //获取用户地址管理模板
    public static final String GET_USER_MANAGE_MOUDLE = BASE_URL + "/App/UserAddress/entity";
    //x获取用户地址list
    public static final String GET_USER_ADDRESS_LIST = BASE_URL + "act=member_address&op=address_list";
    //x添加收货地址
    public static final String ADD_RECEIVER_ADDRESS = BASE_URL + "act=member_address&op=address_add";
    //x修改收货地址
    public static final String EDIT_RECEIVER_ADDRESS = BASE_URL + "act=member_address&op=address_edit";
    //x删除收货地址
    public static final String DELETE_RECEIVER_ADDRESS = BASE_URL + "act=member_address&op=address_del";
    //x设置默认地址
    public static final String SET_DEFAULT_RECEIVER_ADDRESS = BASE_URL + "act=member_address&op=address_default";
    //管理用户地址
    public static final String POST_USER_MANAGE_ADDRESS = BASE_URL + "/App/UserAddress/post";
    //获取设置默认地址模型
    public static final String GET_SET_DEFAULT_ADDRESS_MODEL = BASE_URL + "/App/UserAddress/setdefaultentity";
    //提交用户默认地址
    public static final String POST_USER_DEFAULT_ADDRESS = BASE_URL + "/App/UserAddress/setdefaultpost";

    //是否登录
    public static final String IS_LOGGED_LINK = BASE_URL + "/App/UserAction/islogin";

    //x登录接口
    public static final String LOGIN_URL = BASE_URL + "act=login";

    //x立即订购页,获取产品信息和默认地址
    public static final String ORDER_SHOW_NOW = BASE_URL + "act=member_buy_now&op=buy_now";
    //x立即订购也，获取产品信息和默认地址 从购物篮传过来时
    public static final String CART_SHOW_NOW = BASE_URL + "act=shop_basket&op=Commit";
    //x提交立即购买订单
    public static final String POST_INDENT_URL = BASE_URL + "act=member_buy_now&op=create_order";
    //x立即订购页，提交购物篮传过来的，提交订单
    public static final String POST_CART_INDENT_URL = BASE_URL + "act=shop_basket&op=mobileCreateOrder";
    // 订单以生产、立即购买
    public static final String POST_BUY_NOW_INDENT_URL = BASE_URL + "act=member_buy_now&op=buy_now";
    //x修改密码
    public static final String CHANGE_PASSWORD_URL = BASE_URL + "act=member_info&op=update_member_password";
    //x获取验证码
    public static final String GET_AUTH_CODE_URL = BASE_URL + "act=getcode";
    //x比对验证码
    public static final String EQUELSE_AUTH_CODE_URL = BASE_URL + "act=member_password&op=password_find";
    //x重置密码
    public static final String RESET_PASSWORD_URL = BASE_URL + "act=member_password&op=password_reset";
    //x获取物流信息
    public static final String GET_LOGISTICS_INFO_URL = BASE_URL + "act=shipping_info&op=return_application";

    //获取订单模型
    public static final String GET_INDENT_MODEL = BASE_URL + "/App/GoToPay/entity";
    //提交立即购买订单
    public static final String COMMIT_INDENT = BASE_URL + "/App/GoToPay/nowpost";

    // 保存参数文件夹名称
    public static final String SHARED_PREFERENCE_NAME = "mstar_store_prefs";


    // SDCard路径

    public static final String SD_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath();

    // 图片存储路径
    public static final String BASE_PATH = SD_PATH + "/mstar/store/";

    // 缓存图片路径
    public static final String BASE_IMAGE_CACHE = BASE_PATH + "cache/images/";

    // 需要分享的图片
    public static final String SHARE_FILE = BASE_PATH + "QrShareImage.png";


    //附近门店的信息
    public static final String NEARSTORE_URL = BASE_URL + "act=nearby_shop";

    //促销管理
    public static final String PROMOTION_MANAGE = BASE_URL + "act=mstore_promotion";

    //促销提交
    public static final String PROMOTION_SUBMIT = BASE_URL + "act=mstore_promotion&op=add_promotions";

    public static final String PROMOTION_CANCEL = BASE_URL + "act=mstore_promotion&op=del_promotions";

    //选择分类
    public static final String CATEGORY_SELECT = BASE_URL + "act=goods_add";

    //上传商品
    public static final String UPLOAD_GOODS = BASE_URL + "act=goods_add&op=add_goods_one";

    //我的名片
    public static final String MY_CALLING_CARD = BASE_URL + "act=store_info";

    //我的培训
    public static final String MY_TRAIN_URL = BASE_URL + "act=article&op=get_article";

    //内容详情
    public static final String CONTENT_DETAIL = BASE_URL + "act=article&op=get_article_detail";

    //商品管理
    public static final String GOODS_MANAGEMENT = BASE_URL + "act=mgoods";

    //商品上下架
    public static final String GOODS_UP_DOWN_CARRIAGE = BASE_URL + "act=mgoods&op=undergoods";

    //商品删除
    public static final String GOODS_DELETE = BASE_URL + "act=mgoods&op=goodsdel";

    //修改商品
    public static final String MODIFICATION_GOODS = BASE_URL + "act=goods_add&op=edit_goods_one";

    public static final String SUBMIT_MODIFYCATION = BASE_URL + "act=goods_add&op=edit_goods_two";

    //修改名片
    public static final String MODIFY_CALLING_CARD = BASE_URL + "act=mstore_info&op=update_mstore_info";

    //本店订单
    public static final String MY_STORE_INDENT = BASE_URL + "act=mstore_order&op=all_order_list";

    //本店订单详情
    public static final String MY_STORE_ORDER_DETAIL = BASE_URL + "act=mstore_order&op=order_detail";

    //物流公司
    public static final String LOGISTICS_COMPANY = BASE_URL + "act=mstore_order&op=get_express";

    //确定发货
    public static final String CONFIRM_SEND = BASE_URL + "act=mstore_order&op=confirm_express";

    //申请退货
    public static final String APPLICATION_FOR_REFOUND = BASE_URL + "act=mstore_order&op=store_return_goods";

    //提交是否同意退货
    public static final String SUBMIT_RETURN_GOODS = BASE_URL + "act=mstore_order&op=return_goods_sub";

    //提交收货
    public static final String SUBMIT_RECEIVE = BASE_URL + "act=mstore_order&op=return_goods_receiver";

    //支付宝回调
    public static final String ALIPY_CALL_BACK = BASE_URL_ZHIFUBAO + "api/payment/alipay/call_back_url.php";

    //申请代理
    public static final String APPLY_FOR_AGENT = BASE_URL + "act=mstore&op=open_store_two";


    // links
    public static final String REGISTERING_AUTH_CODE = BASE_URL + "act=getcode&phone=";
    public static final String REGISTERING_ACT = BASE_URL + "act=login&op=register";
    public static final String LOGIN_ACT = BASE_URL + "act=login";
    public static final String LOGOUT_ACT = BASE_URL + "act=logout";
    public static final String CHECK_LOG_STATUS_ACT = BASE_URL + "act=login&op=login_pd";
    public static final String ADD_FAVORITE_ACT = BASE_URL + "act=member_favorites&op=favorites_add";
    public static final String DEL_FAVORITE_ACT = BASE_URL + "act=member_favorites&op=favorites_del";
    public static final String GET_FAVORITE_LIST = BASE_URL + "act=member_favorites&op=favorites_list";
    public static final String DELETE_FAVORITE_ITEM = BASE_URL + "act=member_favorites&op=favorites_del";
    public static final String GET_ORDER_LIST_ALL = BASE_URL + "act=member_order&op=order_list";
    public static final String GET_ORDER_LIST_WAITING_FOR_PAY = BASE_URL + "act=member_order&op=order_list&state=10";
    public static final String GET_ORDER_LIST_WAITING_FOR_SEND = BASE_URL + "act=member_order&op=order_list&state=20";
    public static final String GET_ORDER_LIST_WAITING_FOR_RECEIVE = BASE_URL + "act=member_order&op=order_list&state=30";
    public static final String GET_ORDER_RETURN_SHOP_COUNT=BASE_URL+"act=member_info&op=edit_member_info";
    public static final String ADD_TO_SHOPPING_CART = BASE_URL + "act=member_cart&op=cart_add";
    public static final String DEL_FROM_SHOPPING_CART = BASE_URL + "act=member_cart&op=cart_del";
    public static final String LIST_SHOPPING_CART = BASE_URL + "act=member_cart&op=cart_list&page=10000";
    public static final String LOCAL_SHOPPING_CART = BASE_URL + "act=cart_view&page=10000";
    public static final String GOPAY_FOR_ORDER = BASE_URL + "act=member_order&op=order_detail";
    public static final String REQUEST_GOOD_RETURN_GETINFO = BASE_URL + "act=member_return&op=return_application";
    public static final String REQUEST_GOOD_RETURN_SEND = BASE_URL + "act=member_return&op=return_application_submit";
    public static final String REQUEST_USER_INFO = BASE_URL + "act=member_info&op=edit_member_info";
    public static final String MODIFY_USER_INFO = BASE_URL + "act=member_info&op=edit_member_info";
    public static final String UPDATE_USER_INFO = BASE_URL + "act=member_info&op=update_member_info";
    public static final String UPLOAD_PIC = BASE_URL + "act=member_info&op=update_avatar";
    public static final String RETURN_PRODUCT_PROGRESS = BASE_URL + "act=member_return&op=return_application_list";
    public static final String CONFIRM_RECEIVING_PRODUCT = BASE_URL + "act=member_order&op=order_receive";
    public static final String HISTORY_BROWSE = BASE_URL + "act=history_browse";
    public static final String LOGISTICS_INFO = BASE_URL + "act=shipping_info&op=expressList";
    public static final String PREFERENCE_INFO = BASE_URL + "act=activity";
    public static final String PREFERENCE_CONTENT = BASE_URL + "act=activity&op=list";
    public static final String PREFERENCE_CONTENT_LIST = BASE_URL + "act=activity&op=goods_list&curpage=1&page=5&order=1";
    public static final String COMPANY_PROFILE = BASE_URL + "act=company_introduction&op=get_company_info";
    public static final String MY_COUPON = BASE_URL + "act=member_voucher&op=get_voucher_info";
    public static final String HISTORY_DELETE_SINGLE = BASE_URL + "act=history_browse&op=delOne";
    public static final String HISTORY_DELETE_ALL = BASE_URL + "act=history_browse&op=delAll";
    public static final String SELECT_COUPON = BASE_URL + "act=voucher";
    public static final String COMMENT_LSIT = BASE_URL + "act=member_evaluate&op=list";
    // cancel order
    public static final String CANCEL_ORDER_LINK = BASE_URL + "act=member_order&op=order_cancel";
    public static final String SUBMIT_COMMENT = BASE_URL + "act=member_evaluate&op=index";
    public static final String CHECK_UPGRADE = BASE_URL + "act=uversion&client=android";



    public static final String PAY_FOR_WEIXIN = BASE_URL1 + "mobile/api/payment/wxpay/redirect_uri.php?";
    public static final String NEAR_STORE = BASE_URL + "act=nearby_shop";

    //我的分销
    public static final String MYDISTRIBUTION = BASE_URL + "act=member_info&op=edit_member_info";

    //申请分销
    public static final String APPDISTRIBUTION = BASE_URL + "act=mregister&op=mreg";

    //二维码退推广
    public static final String SHARE_QRCODE = BASE_URL + "act=spread&op=spreadurl";


    //二维码退推广
    public static final String SHARE_URL = BASE_URL + "act=spread&op=spreadewm";


    //我的佣金
    public static final String MYCOMMISSION = BASE_URL + "act=commission&op=index";

    //版本更新
    public static final String UPDATE_APK = BASE_URL + "act=uversion&client=android&ifupdate=1";

    //到店自取
    public static final String GET_IN_SHOP = BASE_URL + "act=member_buytoshop&op=show_stocklist";

    //加盟代理页面weburl
    public  static final String WEBURL = "http://qxm.fanershop.com/index.php?m=QxWeb&c=Agent&a=agentApply&isApp=1";

    //获取下下级代理 人
    public static final String  AGENT=BASE_URL+"act=branch_mstore&op=untgmember&memberId=";

    //获取下下级推广
    public static final String  LOWER=BASE_URL+"act=branch_mstore&op=untgmember&memberId=";

    public static final String RETURN_SHOP_lIST=BASE_URL+"act=member_return&op=member_return_list";

    public static final String RETUTN_SHOP_DETAILS=BASE_URL+"act=member_return&op=member_return_detail&";

    //退款流程
    public static final String RETUTN_SHOP_FLOW=BASE_URL+"act=member_return&op=member_return_process";

    //退货物流
    public static final String RETURN_SHOP_EXPRESS=BASE_URL+"act=member_return&op=member_return_delivery&";


    //定制钻戒
    public static final String NAKED_DIAMOND=BASE_URL+"act=naked_diamond";

    //钻戒列表
    public static final String RINGG_LIST=BASE_URL+"act=naked_diamond&op=diamond_list&";

    //选择戒托
    public static final String CHOOSE_RING_FRAG=BASE_URL+"act=naked_diamond&op=ring_list";

    public static final String RING_FRAG_DETATIL=BASE_URL+"act=naked_diamond&op=ring_detail";

    //裸戒定制
    public static final String RING_FRAG_NAKED=BASE_URL+"act=naked_diamond&op=bare_drill";


    //定制预览
    public static final String RING_SHOW=BASE_URL+"act=naked_diamond&op=div_view";

    //定制付款
    public static final String RING_PAY=BASE_URL+"act=shop_basket&op=Commit";

    //定制提交
    public static final String RING_NAKED_BARE=BASE_URL+"act=naked_diamond&op=bare_drill";

//  微信授权
    public static String GET_REQUEST_ACCESS_TOKEN =
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    public static String GET_REQUEST_USER_INFO =
            "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
}


