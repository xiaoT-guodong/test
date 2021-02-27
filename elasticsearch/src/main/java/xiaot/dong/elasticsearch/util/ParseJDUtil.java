package xiaot.dong.elasticsearch.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xiaot.dong.elasticsearch.pojo.JDPojo;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ParseJDUtil {

    private static final String url = "https://search.jd.com/Search?enc=utf-8&keyword=";

    public static void main(String[] args) throws IOException {
        List<JDPojo> java = parseJD("java");
        java.forEach(System.out::println);
    }

    public static List<JDPojo> parseJD(String key) throws IOException {
        Document parse = Jsoup.parse(new URL(url + key), 30000);
        Element j_goodsList = parse.getElementById("J_goodsList");
        Elements li = j_goodsList.getElementsByTag("li");
        List<JDPojo> pojos = new ArrayList<>();
        for (Element element : li) {
            String name = element.getElementsByClass("p-name").text();
            String img = element.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = element.getElementsByClass("p-price").eq(0).text();
            pojos.add(new JDPojo(name, img, price));
        }
        return pojos;
    }

}
