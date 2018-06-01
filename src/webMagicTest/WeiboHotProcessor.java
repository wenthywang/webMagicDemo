package webMagicTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * <pre>
 * 微博热搜 爬虫。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class WeiboHotProcessor implements  PageProcessor,Job {
	
	private static String url="http://gz.58.com/tianhe/zufang/0/j1/@?minprice=1500_3000&pagetype=area&PGTID=0d300008-0000-334e-7826-90783a9dd383&ClickID=2";
   //文章详情url过滤
    private Site site = Site
            .me()
            .setDomain("s.weibo.com")
            .setRetryTimes(5)
            .setCharset("UTF-8")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
    Document doc=Jsoup.parse(page.getRawText());
//       Element e=	doc.getElementsByClass("listUl").get(0);
  	  System.out.println(doc);
//      Elements es= e.getElementsByTag("li");
//      for (Element element : es) {
//    	  System.out.println(element);
//    	  String title=element.getElementsByTag("h2").get(0).text();
//    	 if(title.indexOf("1室1厅1卫")>0) {
//    		 System.out.println(title);
////    		 Element node1=(Element) element.nextSibling().nextSibling();
////    		 Element node2=(Element) element.nextSibling().nextSibling().nextSibling().nextSibling();
////    		  System.out.println(element.text()+"->"+StringUtils.trim(node1.text().split(" ")[1])+"，     "+node2.text()+"，"+node3.text());
//    	 }
//    
//	}
    	
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
    	String url2=url.replace("@", "");
        List<String>urlList=new ArrayList<String>();
        urlList.add(url2);
        for (int i=2;i<=38;i++){
        	String url3=url.replace("@", "pn"+i+"/");
        	urlList.add(url3);
        }
        String[] array = urlList.toArray(new String[urlList.size()]);  
    	
        	//自定义处理器
        	Spider.create(new WeiboHotProcessor())
        	//自定义
//        	.addPipeline(new RedisPipeline())
        	.addUrl(array).thread(5)
                    .run();
    }

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		main(new String[] {});
		
	}
}
