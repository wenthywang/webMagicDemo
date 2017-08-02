package webMagicTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.StringUtils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import test.ESAPITest;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * <pre>
 *天天 基金 爬虫。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class FoundationProcessor implements PageProcessor {
    public static final String FUND_INFO = "http://fund.eastmoney.com/\\d{6,20}.html";
    public static final String FUND_INFO_2 = "http://fund.eastmoney.com/$.html";
    public static final String FUND_INFO_3 = "http://fund\\.eastmoney\\.com/pingzhongdata/\\d{6,20}\\.js";
    public static final String FUND_INFO_BASE = "http://fund.eastmoney.com/pingzhongdata/$.js";
    //查询基金列表 （可购基金）
    public static final String BASE_URL = "http://fund.eastmoney.com/Data/Fund_JJJZ_Data.aspx?"
    		+ "t=1&lx=1&sort=zdf,desc&page=1,9999&onlySale=1";
    
   //基金总数
    private static int count=0;
    //es 索引名称
    public static String FUND_INFO_INDEX="fund_info";
   
   
    private Site site = Site
            .me()
            .setDomain("fund.eastmoney.com")
            .setRetryTimes(5)
            .setCharset("utf-8")
            .setSleepTime(5000)
            .setTimeOut(100000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
   private static   ScriptEngine engine=null;
	  
	  static{
		  ScriptEngineManager manager = new ScriptEngineManager();
		   engine = manager.getEngineByName("javascript");
	  }
	  
	  
    @Override
    public void process(Page page) {
    	Document doc = Jsoup.parse(page.getHtml().get());
      //如果是详情页url 则 分析 处理
        if (page.getUrl().regex(FUND_INFO).match()) {
        	//当前净值
        	String currentVal=doc.getElementById("gz_gsz").text();
        	
        	//前一次闭盘后净值
        	String pastVal=doc.getElementsByClass("dataNums").get(1).child(0).text();
        	
        	//前一次闭盘后净值涨幅
        	String pastValPercent=doc.getElementsByClass("dataNums").get(1).child(1).text().replace("%", "");
        	
        	//基金名称
        	String title = doc.getElementsByClass("fundDetail-tit").get(0).child(0).text();
        	
        	//基金类型
        	String type =	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[1]/a/text()").get();
         
        	//成立日
        	String openDay=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[2]/div[1]/div[2]/table/tbody/tr[2]/td[1]/text()").get();
        
        	if(StringUtils.isEmpty(openDay)){
        			openDay=page.getHtml().xpath("//*[@id=\"body\"]/div[13]/div/div/div[2]/div[1]/div[2]/table/tbody/tr[2]/td[1]/text()").get();
        		}
        	
        	openDay=openDay.split("：")[1];
            
        	//获取基金代码
             String id=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[1]/div[1]/div/span[2]/text()").get();
             String id1=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[1]/div[1]/div/span[2]/span[1]/text()").get();
             String id2=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[1]/div[1]/div/span[2]/span[2]/text()").get();
          
             //累计净值
             String sumVal=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[2]/div[1]/div[1]/dl[3]/dd[1]/span/text()").get();
             
             //近1个月
             String onemonthPercent=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[2]/div[1]/div[1]/dl[1]/dd[2]/span[2]//text()").get().replace("%", "");
             //近3个月
             String threemonthPercent=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[2]/div[1]/div[1]/dl[2]/dd[2]/span[2]//text()").get().replace("%", "");
             //近6个月
             String sixmonthPercent=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[2]/div[1]/div[1]/dl[3]/dd[2]/span[2]/text()").get().replace("%", "");
             //近1年
             String oneyearPercent=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[2]/div[1]/div[1]/dl[1]/dd[3]/span[2]/text()").get().replace("%", "");
             //近3年
             String threeyearPercent=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[2]/div[1]/div[1]/dl[2]/dd[3]/span[2]/text()").get().replace("%", "");
             //成立以来
             String createPercent=	page.getHtml().xpath("//*[@id=\"body\"]/div[12]/div/div/div[2]/div[1]/div[1]/dl[3]/dd[3]/span[2]/text()").get().replace("%", "");
        
             if(NumberUtils.isNumber(currentVal)){
                page.putField("currentVal", Double.valueOf(currentVal));
            }else{
                page.putField("currentVal", 0.00);
            }
            if(NumberUtils.isNumber(pastVal)){
            	page.putField("pastVal", Double.valueOf(pastVal));
            }else{
            	page.putField("pastVal", Double.valueOf(0.00));
            }
            //处理基金代码存在前后端代码的
            if(!NumberUtils.isNumber(id)){
            	id=id1+"_"+id2;
            }
            double oneMonthPercent=0.00;
          
            if(NumberUtils.isNumber(onemonthPercent)){
            	oneMonthPercent=Double.valueOf(onemonthPercent);
            }
            double threeMonthPercent=0.00;
            
            if(NumberUtils.isNumber(threemonthPercent)){
            	threeMonthPercent=Double.valueOf(threemonthPercent);
            }
            double sixMonthPercent=0.00;
            
            if(NumberUtils.isNumber(sixmonthPercent)){
            	sixMonthPercent=Double.valueOf(sixmonthPercent);
            }
            double oneYearPercent=0.00;
            
            if(NumberUtils.isNumber(oneyearPercent)){
            	oneYearPercent=Double.valueOf(oneyearPercent);
            }
            double threeYearPercent=0.00;
            
            if(NumberUtils.isNumber(threeyearPercent)){
            	threeYearPercent=Double.valueOf(threeyearPercent);
            }
            double openPercent=0.00;
            
            if(NumberUtils.isNumber(createPercent)){
            	openPercent=Double.valueOf(createPercent);
            }
            double pastValPercentTemp=0.00;
            if(NumberUtils.isNumber(pastValPercent)){
            	pastValPercentTemp=Double.valueOf(pastValPercent);
            }
        	page.putField("title", title.split("\\(")[0]);
        	page.putField("id", id);
        	page.putField("openDay", openDay);
        	page.putField("type", type);
        	page.putField("oneMonthPercent", oneMonthPercent);
        	page.putField("threeMonthPercent", threeMonthPercent);
        	page.putField("sixMonthPercent", sixMonthPercent);
        	page.putField("oneYearPercent", oneYearPercent);
        	page.putField("threeYearPercent", threeYearPercent);
        	page.putField("openPercent", openPercent);
        	page.putField("pastValPercent", pastValPercentTemp);
        	page.putField("sumVal", Double.valueOf(sumVal));
        	count++;
        } else if(page.getUrl().regex(FUND_INFO_3).match()){
        	try {
        	  
				engine.eval(page.getRawText());
				Object m1= engine.get("fS_code");
				page.putField("id",m1.toString());
				
				ScriptObjectMirror m=(ScriptObjectMirror) engine.get("Data_netWorthTrend");
				Map<String,Double>dataMap=new HashMap<String,Double>();
			    for (String key: m.keySet()) {
			    	ScriptObjectMirror o=(ScriptObjectMirror) m.get(key);
			    	int index=0;
			    	String date=null;
			    	   for (String key2: o.keySet()) {
			    			String value=o.get(key2).toString();
			    	    		    	if(index==0){
			    	    		    	 date= 	TimeStamp2Date(value);
			    	    		    	 date=date.split(" ")[0];
//			    	    		    	page.putField("date",date);
			    	    		    	}
			    	    		    	if(index==1){
//			    	    		    		page.putField("value",Double.valueOf(value));
			    	    		    		dataMap.put(date, Double.valueOf(value));
			    	    		    		break;
			    	    		    	}
			    	    		    	index++;
			    	   }
				}
			    page.putField("data", dataMap);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
        	try {
				engine.eval(page.getRawText());
				@SuppressWarnings("unchecked")
				Map<String,Object> dataMap= (Map<String, Object>) engine.get("db");
				ScriptObjectMirror m= (ScriptObjectMirror) dataMap.get("datas");
		    for (String key: m.keySet()) {
		    	ScriptObjectMirror o=(ScriptObjectMirror) m.get(key);
		    	   for (String key2: o.keySet()) {
		    	    	Integer i=Integer.parseInt(key2);
		    	    	if(i==0){
		    	    		String id=o.get(key2).toString();
		    	    		String targetUrl=FUND_INFO_2.replace("$", id);
//		    	    		String targetUrl2=FUND_INFO_BASE.replace("$", id);
		    				page.addTargetRequest(targetUrl);
//		    				page.addTargetRequest(targetUrl2);
		    	    	}
		    	   }
			}
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
    	ESAPITest.deleteAllIndex();
    	try {
			ESAPITest.createFundIndex(FUND_INFO_INDEX);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        	//自定义处理器
        	Spider.create(new FoundationProcessor())
        	.addPipeline(new FundationPipeline())
        	//自定义
        	.addUrl(BASE_URL)
        	.thread(10)
            .run();
        System.out.println("共"+count+"个基金");
        
        
    }
    
    //Convert Unix timestamp to normal date style  
    public static String TimeStamp2Date(String timestampString){  
     Long timestamp = Long.parseLong(timestampString);  
      String date = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date(timestamp));  
     return date;  
    }
}
