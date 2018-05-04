package webMagicTest;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import pipeline.RedisPipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import utils.RedisUtils;

/**
 * <pre>
 * 豆瓣小组 爬虫。
 * 评论的时候才需要登陆
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class BaiduTestProcessor implements PageProcessor {
	
    private Site site = Site
            .me()
//            .setDomain("https://www.douban.com")
            .setRetryTimes(5)
            .setCharset("utf-8")
            .setSleepTime(3000)
//            .addHeader("Host", "https://www.douban.com")
            .setUserAgent(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");

    @Override
    public void process(Page page) {
    	Document doc = Jsoup.parse(page.getHtml().get());
    	System.out.println(doc);
//      //如果是详情页url 则 分析 处理
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
    	RedissonClient	 client = RedisUtils.getInstance().getRedisson();  
    	RMap<String,Integer> rMap=RedisUtils.getInstance().getRMap(client, "IpProxy");
    	System.out.println("redis ip 池->"+rMap.size());
    	List<Proxy>proxyList=new ArrayList<Proxy>();
    	for(Entry<String,Integer>entry:rMap.entrySet()){
    		String ip=entry.getKey();
    		Integer port=entry.getValue();
    	 	 Proxy p=new Proxy(ip,port);
			 Socket connect = new Socket();  
		      boolean res=false;
		        try {  
		            connect.connect(new InetSocketAddress(ip, port),1000);  
		             res = connect.isConnected();  
		        } catch (Exception e) {  
		        }finally{  
		            try {  
		                connect.close();  
		            } catch (Exception e) {  
		            }  
		        }  
		        if(res){
		        	System.out.println(ip+":"+port+"->可用");
		        	proxyList.add(p);
		        }else{
		         	System.out.println(ip+":"+port+"->删除！");
		        	rMap.removeAsync(ip);
		        }
    	 
    	}
        RedisUtils.getInstance().closeRedisson(client);  
//    	
//    	
    	HttpClientDownloader httpClientDownloader2 = new HttpClientDownloader();
      httpClientDownloader2.setProxyProvider(SimpleProxyProvider.from(proxyList.toArray(new Proxy[proxyList.size()])));
       List<String>urlList=new ArrayList<String>();
////        for (int i=0;i<=10;i++){
////        	int count=i*25;
////        	String url="https://www.douban.com/group/tianhezufang/discussion?start="+count;
////        	urlList.add(url);
////        }
   	urlList.add("https://www.baidu.com");
    String[] array = urlList.toArray(new String[urlList.size()]);  
//        String[] array = urlList.toArray(new String[urlList.size()]);  
        	//自定义处理器
        	Spider.create(new BaiduTestProcessor())
        	//自定义
        	.addPipeline(new RedisPipeline())
        	.addUrl(array)
        	.setDownloader(httpClientDownloader2)
            .thread(10)
                    .run();
    }
}
