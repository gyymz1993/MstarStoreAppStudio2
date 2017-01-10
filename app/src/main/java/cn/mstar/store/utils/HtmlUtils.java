package cn.mstar.store.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.mstar.store.app.MyApplication;

/**
 * Created by Shinelon on 2016/1/18.
 */
public class HtmlUtils {

//    public static String parseHtml(String html) {
//        int width = new Screen(MyApplication.getInstance().getBaseContext()).getWidth();
//        Document dom = Jsoup.parse(html);
//        Elements imgEles = dom.getElementsByTag("img");
//        for (Element e : imgEles) {
//            e.attr("style", "width:" + (width-30) + "px;");
//        }
//        Elements divEles = dom.getElementsByTag("div");
//        String[] styles = new String[]{"font-family:宋体;color:#6c6c6c;margin-left:10px;font-size:13px;",
//                "line-height:29px;font-family:宋体;color:#000000;font-size:12px;"};
//        for (Element e : divEles) {
//            if (styles[0].equals(e.attr("style"))) {
//                e.attr("style", "font-family:宋体;color:#6c6c6c;margin-left:10px;margin-right:10px;font-size:36px;"
//                        + "width:" + (width - 30) + "px;");
//            }
//        }
//        Elements spanEles = dom.getElementsByTag("span");
//        for (Element e : spanEles) {
//            if (styles[1].equals(e.attr("style"))) {
//                e.attr("style", "line-height:29px;font-family:宋体;color:#000000;font-size:36px;");
//            }
//        }
//        return dom.outerHtml();
//    }
}
