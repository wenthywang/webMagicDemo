package webMagicTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RedissonClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant.Method;
import utils.RedisUtils;

/**
 * <pre>
 * 拉勾 爬虫。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class LagouProcessor implements PageProcessor,Job {
    //职位详情
    public static final String URL_POST = "https://www\\.lagou\\.com/jobs/\\d{7,20}.html";
   //cookie 
    public static final String COOKIE_STORE = "WEBTJ-ID=20180503110629-16323f725191628-053442c9f9b979-b34356b-1440000-16323f7251bc09; _ga=GA1.2.2036526936.1525316790; _gid=GA1.2.553928601.1525316790; user_trace_token=20180503110621-f4aa150b-4e7e-11e8-bd65-5254005c3644; LGUID=20180503110621-f4aa1ac1-4e7e-11e8-bd65-5254005c3644; X_HTTP_TOKEN=cd1b4836e65c22fba63cac30298531f5; JSESSIONID=ABAAABAAADEAAFI939EEF4E269486561D71ED1604D6E9D4; Hm_lvt_4233e74dff0ae5bd0a3d81c6ccf756e6=1525316790,1525316850; index_location_city=%E5%B9%BF%E5%B7%9E; TG-TRACK-CODE=search_code; _gat=1; LGSID=20180503151318-743cd1d9-4ea1-11e8-b8d6-525400f775ce; PRE_UTM=; PRE_HOST=; PRE_SITE=; PRE_LAND=https%3A%2F%2Fwww.lagou.com%2Fjobs%2Flist_Java%3Fpx%3Ddefault%26city%3D%25E5%25B9%25BF%25E5%25B7%259E; LGRID=20180503151318-743cd32c-4ea1-11e8-b8d6-525400f775ce; Hm_lpvt_4233e74dff0ae5bd0a3d81c6ccf756e6=1525331607; SEARCH_ID=914e815b4f03447fb9f55ee3c38bc703";
    //职位详情2 用于替换
    public static final String URL_POST_2 = "https://www.lagou.com/jobs/@.html";
  //职位总数
    private static int totalCount=0;
    private Site site = Site
            .me()
            .setDomain("www.lagou.com")
            .setRetryTimes(5)
            .setCharset("utf-8")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    
    @Override
    public void process(Page page) {
    	//匹配职位详情
        if (page.getUrl().regex(URL_POST).match()) {
        	Document doc = Jsoup.parse(page.getHtml().get());
        Node node=	doc.getElementsByClass("job_bt").first();
        if(node!=null) {
        	//找到职位描述node
        	node=node.childNode(3);
        	 String jd=node.toString();
        	  page.putField("jd", jd);
        	  
        	  System.out.println(jd);
        	 totalCount++;
        }else {
        	System.out.println(doc);
        }
        } else {
        	String json=page.getJson().toString();
        	JSONObject obj=JSONObject.parseObject(json);
        	JSONObject content=obj.getJSONObject("content");
        	if(content==null) {
        		System.out.println(json);
        	}else {
        		//找到职位详情的编号
        		//
        		JSONArray array=content.getJSONObject("positionResult").getJSONArray("result");
            	for(int i=0;i<array.size();i++) {
            		JSONObject newObject=	array.getJSONObject(i);
            		String positionId=newObject.getString("positionId");
            		String postUrl=URL_POST_2.replace("@", positionId);
            		//构造 职位详情链接的请求
            		Request request=new Request();
                	request.setUrl(postUrl);
                	request.setMethod(Method.GET);
                	request.addHeader("Cookie", COOKIE_STORE);
                	page.addTargetRequest(request);
            	}
        	}
        	
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
    	String url="https://www.lagou.com/jobs/positionAjax.json?px=default&city=%E5%B9%BF%E5%B7%9E&needAddtionalResult=false";
    	List<Request>requests=new ArrayList<Request>();
    	//30页
    	for (int i=1;i<=30;i++){
        	Request request=new Request();
        	request.setUrl(url);
        	request.setMethod(Method.POST);
        	request.addHeader("Referer", "https://www.lagou.com/jobs/list_Java?px=default&city=%E5%B9%BF%E5%B7%9E");
        	request.addHeader("Cookie", COOKIE_STORE);
        	Map<String,Object>params=new HashMap<String,Object>();
        	if(i==1) {
        		params.put("first", "true");
        	}else {
         		params.put("first", "false");
        	}
        	params.put("kd", "java");
        	params.put("pn", i);
        	try {
    			request.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	requests.add(request);
        }

    	//清空redis 数据
    	RedissonClient	 client = RedisUtils.getInstance().getRedisson();
    	RedisUtils.getInstance().getRSet(client, "LAGOU_JD").clear();
    	RedisUtils.getInstance().closeRedisson(client);  
    	
        Spider.create(new LagouProcessor())
      	//数据储存 到redis
//      	.addPipeline(new RedisPipeline())
         .startRequest(requests).
          thread(10)
                  .run();
        System.out.println("共"+totalCount+"职位描述");
    }

    //定时任务
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		main(new String[] {});
		
	}
}
