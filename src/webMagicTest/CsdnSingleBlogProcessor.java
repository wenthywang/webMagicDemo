package webMagicTest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import utils.HtmlUtil;

/**
 * <pre>
 * Csdn 博客文章 爬虫。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class CsdnSingleBlogProcessor implements PageProcessor {
	private static String username="buptgshengod";
   //文章详情url过滤
    public static final String URL_POST = "http://blog\\.csdn\\.net/"+username+"/article/details/\\d{8,20}";
    public static final String URL_POST_HREF = "/buptgshengod/article/details/\\d{8,20}";
   //处理文章数量
    private static int count=0;
    private Site site = Site
            .me()
            .setDomain("blog.csdn.net")
            .setRetryTimes(5)
            .setCharset("utf-8")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
    	Document doc = Jsoup.parse(page.getHtml().get());
      //如果是详情页url 则 分析 处理
        if (page.getUrl().regex(URL_POST).match()) {
			String title  =doc.getElementsByClass("link_title").get(0).child(0).text();
			Elements content3 = doc.getElementsByClass("link_postdate");
        	  page.putField("title", title);
        	  //去除script 标签
              page.putField("content", HtmlUtil.delHTMLTag(page.getHtml().xpath("//div[@id='article_content']").get()));
              page.putField("date",
            		  content3.get(0).text());
              count++;
              
        } else {
        	//列表页
            page.addTargetRequests(page.getHtml().xpath("//h1/span[@class=\"link_title\"]/a").links().all());
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        	//自定义处理器
        	Spider.create(new CsdnSingleBlogProcessor())
        	//自定义
//        	.addPipeline(new EsPipeline())
        	//最后的数字本来是页码 但是因为页码超出的时候 后台查询数据就是全部的文章数  
           //这个估计应该有个上限
        	.addUrl("http://blog.csdn.net/gshengod/article/list/50")
                    .run();
        System.out.println("共"+count+"篇文章");
    }
}
