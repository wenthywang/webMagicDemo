package webMagicTest;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * <pre>
 * 知乎用户爬虫（json数据）。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class ZhihuCustomerProcessor implements PageProcessor {
	
    public static final String FOLLOWEES_URL = "https://www.zhihu.com/api/v4/members/.*/followees?.*";
    
    public static final String PERSONALINFO_URL = "https://www.zhihu.com/api/v4/members/$"
    		+ "?include=locations,employments,gender,educations,business,voteup_count,thanked_Count,follower_count,following_count,cover_url,following_topic_count,following_question_count,following_favlists_count,following_columns_count,avatar_hue,answer_count,articles_count,pins_count,question_count,columns_count,commercial_question_count,favorite_count,favorited_count,logs_count,marked_answers_count,marked_answers_text,message_thread_token,account_status,is_active,is_bind_phone,is_force_renamed,is_bind_sina,is_privacy_protected,sina_weibo_url,sina_weibo_name,show_sina_weibo,is_blocking,is_blocked,is_following,is_followed,mutual_followees_count,vote_to_count,vote_from_count,thank_to_count,thank_from_count,thanked_count,description,hosted_live_count,participated_live_count,allow_message,industry_category,org_name,org_homepage,badge[?(type=best_answerer)].topics";
   
    public static final String  FOLLOWEES_URL2="https://www.zhihu.com/api/v4/members/$/followees?"
	 		+ "include=data[*].answer_count,articles_count,gender,follower_count,is_followed,is_following,badge[?(type=best_answerer)].topics"
	 		+ "&limit=20"
	 		+ "&offset=20";
    
    private static int count=0;
    
    private Site site = Site
            .me()
            .setDomain("www.zhihu.com")
            .setRetryTimes(5)
            .setCharset("utf-8")
            .setSleepTime(3000)
            .setTimeOut(100000)
            .setUserAgent(
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36")
            .addHeader("authorization", "oauth c3cef7c66a1843f8b3a9e6a1e3160e20");
    
    
    
    @Override
    public void process(Page page) {
    	 JSONObject o=JSONObject.parseObject(page.getJson().get());
    	 List<String>followeeUrlList=new ArrayList<String>();
        if (!page.getUrl().regex(FOLLOWEES_URL).match()) {
    	  JSONArray  locations = o .getJSONArray("locations");
    	  String location="";
    	  if(locations!=null&&locations.size()>0){
    		JSONObject o1=  (JSONObject) locations.get(0);
    		location=o1.getString("name");
    	  }
    	  String url_token=o.getString("url_token");
    	  page.putField("location", location);
    	  page.putField("name", o.getString("name"));
    	  page.putField("follower_count", o.getIntValue("follower_count"));
    	  page.putField("articles_count", o.getIntValue("articles_count"));
    	  page.putField("gender", o.getIntValue("gender"));
    	  page.putField("url_token", url_token);
    	
            count++;		
            String newUrl=	FOLLOWEES_URL2.replace("$", url_token);
            followeeUrlList.add(newUrl);
            page.addTargetRequests(followeeUrlList);
        }else{
        	
            JSONArray data=  o.getJSONArray("data");
            for(int j=0;j<data.size();j++){
           	 JSONObject persionInfo=  (JSONObject) data.get(j);
           	 String url_token= persionInfo.getString("url_token");
             String newUrl=	PERSONALINFO_URL.replace("$", url_token);
              followeeUrlList.add(newUrl);
              }
//            System.out.println(followeeUrlList.size());
            
            page.addTargetRequests(followeeUrlList);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws Exception {
    	
//    	HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//    	HttpClientDownloader httpClientDownloader2 = new HttpClientDownloader();
//    	String html=httpClientDownloader.download("http://www.66ip.cn/areaindex_19/1.html").get();
//    	Document doc=Jsoup.parse(html);
//    	Elements es=doc.getElementsByTag("tbody").get(2).getElementsByTag("tr");
//    	List<Proxy>proxyList=new ArrayList<Proxy>();
//    	 for (Element element : es) {
//    	String ip=	 element.child(0).text();
//    	String port=	 element.child(1).text();
//    	if(NumberUtils.isNumber(port)){
//    		System.out.println(ip);
//        	System.out.println(port);
//        	boolean result=ProxyUtils.validateProxy(new Proxy(ip,Integer.parseInt(port)));
//        	if(result){
//        	 Proxy p=new Proxy(ip,Integer.parseInt(port));
//        	 if(!proxyList.contains(p)){
//        	  	 proxyList.add(p);
//        	 }
//        	}
//    	}else{
//    		continue;
//    	}
//    	
//		}
//    	List<String>result2=html.$("#ip_list").css(".odd").all();
//    	System.out.println(result2);
    	 
    	 
    	
    	
    	 
//        httpClientDownloader2.setProxyProvider(SimpleProxyProvider.from(proxyList.toArray(new Proxy[proxyList.size()])));
//        httpClientDownloader2.setProxyProvider(SimpleProxyProvider.from(new Proxy("203.93.0.115",80)));
        String url=FOLLOWEES_URL2.replace("$", "jinzui");
//        	//自定义处理器
        	Spider.create(new ZhihuCustomerProcessor()).
//        	//自定义
//        	addPipeline(new ZhiHuCustomerPipeline()).
//        	setDownloader(httpClientDownloader2).
            addUrl(url).
            thread(10)
                    .run();
        System.out.println("共"+count+"个用户！");
    }

 
}
