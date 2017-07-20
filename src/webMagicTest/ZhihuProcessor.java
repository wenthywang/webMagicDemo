package webMagicTest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * <pre>
 * 知乎爬虫。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class ZhihuProcessor implements PageProcessor {
	private static RequestConfig customizedRequestConfig = RequestConfig.custom().
			setCookieSpec(CookieSpecs.STANDARD_STRICT)
			.setConnectTimeout(60000)
			.setConnectionRequestTimeout(60000)
			.setSocketTimeout(60000)
			.setExpectContinueEnabled(true)
			.build();

	private static      HttpClientBuilder customizedClientBuilder = HttpClients.custom().setDefaultRequestConfig(customizedRequestConfig)
			.setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36");
	private static     CloseableHttpClient client = customizedClientBuilder.build(); // customized c
   //文章详情url过滤
	
    public static final String URL_POST = "https://www\\.zhihu\\.com/question/[1-9]\\d*/answer/[1-9]\\d*";
    public static final String URL_POST2 = "https://zhuanlan\\.zhihu\\.com/p/[1-9]\\d*";
   //处理文章数量
    private static int count=0;
    private Site site = Site
            .me()
            .setDomain("www.zhihu.com")
            .setRetryTimes(5)
            .setCharset("utf-8")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36");
    
    
    private static List<String>pageUrlList=new ArrayList<String>();
    

    @Override
    public void process(Page page) {
    	Document doc = Jsoup.parse(page.getHtml().get());
      //问题
        if (page.getUrl().regex(URL_POST).match()) {
        	String questionId=page.getUrl().get().split("question/")[1].split("/")[0];
        	String answerId=page.getUrl().get().split("answer/")[1];
            String id=questionId+"_"+answerId;
			String title  =doc.getElementsByClass("QuestionHeader-title").get(0).text();
			String content  =doc.getElementsByClass("RichText").get(0).text();
			Elements createDateE = doc.getElementsByAttributeValue("itemprop", "dateCreated");
			Elements content4 = doc.getElementsByAttributeValue("itemprop", "zhihu:followerCount");
			Elements keywordsE = doc.getElementsByAttributeValue("itemprop", "keywords");
			Elements commentCountE = doc.getElementsByAttributeValue("itemprop", "commentCount");
			Elements answerCountE = doc.getElementsByAttributeValue("itemprop", "answerCount");
			String NumberBoard = doc.getElementsByClass("NumberBoard-value").get(1).text();
			String followerCount=content4.get(0).attr("content");
			String keywords=keywordsE.get(0).attr("content");
			String newDate=createDateE.get(0).attr("content").replace("T", " ").replace(".000Z", "");
			String commentCount=commentCountE.get(0).attr("content");
			String answerCount=answerCountE.get(0).attr("content");
			page.putField("id", id);
        	  page.putField("title", title);
        	  //去除script 标签
              page.putField("content", content);
              page.putField("date",newDate);
              page.putField("followerCount",Integer.parseInt(followerCount));
              page.putField("keywords",keywords);
              page.putField("numberBoard",Integer.parseInt(NumberBoard));
              page.putField("commentCount",Integer.parseInt(commentCount));
              page.putField("answerCount",Integer.parseInt(answerCount));
              count++;
              
        } 
        
        //文章
        else if(page.getUrl().regex(URL_POST2).match()){
        	String url=page.getUrl().get();
        	String id=url.split("p/")[1];
        	Element input  =doc.getElementById("preloadedState");
        	String jsonResult=input.text();
        	JSONObject result=	 JSONObject.parseObject(jsonResult);
        	String  title=result.getJSONObject("database").getJSONObject("Post").getJSONObject(url.split("/p/")[1]).getString("title");
        	String  content=result.getJSONObject("database").getJSONObject("Post").getJSONObject(url.split("/p/")[1]).getString("content");
        	int  likeCount=result.getJSONObject("database").getJSONObject("Post").getJSONObject(url.split("/p/")[1]).getIntValue("likeCount");
        	String  publishedTime=result.getJSONObject("database").getJSONObject("Post").getJSONObject(url.split("/p/")[1]).getString("publishedTime");
        	String  author=result.getJSONObject("database").getJSONObject("Post").getJSONObject(url.split("/p/")[1]).getString("author");
			String newDate=publishedTime.replace("T", " ").replace("+08:00", "");
        	  
			page.putField("id", id);
        	  page.putField("title", title);
    	      page.putField("content", content);
    	      page.putField("likeCount", likeCount);
    	      page.putField("date", newDate);
    	      page.putField("author", author);
    	   	 count++;
        }
        
        
        else {
//        	//列表页
        	System.out.println(pageUrlList.size());
            page.addTargetRequests(pageUrlList);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws Exception {
    	
    	for ( int i=0;i<1;i++){
						getPageUrlList(i);
			System.out.println("第"+(i+1)+"页 url完成抓取！");
    	}
    	 
//        	//自定义处理器
        	Spider.create(new ZhihuProcessor()).
//        	//自定义
        	addPipeline(new EsPipeline()).
            addUrl("https://www.zhihu.com/explore/recommendations").thread(5)
                    .run();
        System.out.println("共"+count+"篇文章");
        
    }
    
    private static void getPageUrlList(int i) throws Exception{
        HttpPost post=new HttpPost("https://www.zhihu.com/node/ExploreRecommendListV2");
        JSONObject json =new JSONObject();
        json.put("limit", 50);
        json.put("offset", i*50);
     // 构造post数据
     		List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
     		valuePairs.add(new BasicNameValuePair("method", "next"));
     		valuePairs.add(new BasicNameValuePair("params", json.toJSONString()));
     		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
     		// //注入post数据
     		post.setEntity(entity);
     		HttpResponse httpResponse =client.execute(post);
     		JSONObject result=	 JSONObject.parseObject(EntityUtils.toString(httpResponse.getEntity(), "utf-8"));
     	    JSONArray array= result.getJSONArray("msg");
     	    for ( Object o : array) {
				String div=(String)o;
			   	Document doc = Jsoup.parseBodyFragment(div);
			   	boolean isPost=true;
			   	Elements es=doc.getElementsByClass("post-link");
			   	if(es==null||es.size()==0){
			   		es=doc.getElementsByClass("question_link");
			   		isPost=false;
			   	}
			   	if(es==null||es.size()<1){
			   		continue;
			   	}
			   	String url=null;
			   	if(!isPost){
			   		 url="https://www.zhihu.com"+es.get(0).attr("href");
			   	}else{
			   		 url=es.get(0).attr("href");
			   	}
			   	if(url!=null&&!pageUrlList.contains(url)){
		   			pageUrlList.add(url);
		   		}
     	    }
    }
    
 
}
