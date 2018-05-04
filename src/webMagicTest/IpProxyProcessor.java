package webMagicTest;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import utils.RedisUtils;

/**
 * <pre>
 * 免费代理IP 爬虫。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class IpProxyProcessor implements PageProcessor,Job {
   //文章详情url过滤
    public static final String URL_POST = "http://blog\\.csdn\\.net/\\w+/article/details/\\d{8,20}";
    private Site site = Site
            .me()
            .setDomain("www.xicidaili.com")
            .setRetryTimes(5)
            .setCharset("utf-8")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
    	List<String>ipList=page.getHtml().xpath("//tbody//tr//td").all();
    	int index=0;
    	for (String ipContent : ipList) {
    		ipContent=	ipContent.replace("<td>", "").replace("</td>", "");
    		if(isIP(ipContent)){
    			String port=ipList.get((index+1));
    			port=	port.replace("<td>", "").replace("</td>", "");
    			System.out.println(ipContent+"->"+port);
    		
    			 Socket connect = new Socket();  
    		      boolean res=false;
    		        try {  
    		            connect.connect(new InetSocketAddress(ipContent, Integer.parseInt(port)),1000);  
    		             res = connect.isConnected();  
    		        } catch (Exception e) {  
    		        }finally{  
    		            try {  
    		                connect.close();  
    		            } catch (Exception e) {  
    		            }  
    		        }  
    		        if(res){
    		        	System.out.println("可用");
    		        	RedissonClient	 client = RedisUtils.getInstance().getRedisson();  
    		        	RMap<String,Integer> rMap=RedisUtils.getInstance().getRMap(client, "IpProxy");
    		        	rMap.put(ipContent, Integer.parseInt(port));
    		        	 RedisUtils.getInstance().closeRedisson(client);  
    		        }else{
    		        	System.out.println("不可用");
    		        }
    		    	System.out.println("");
    		}
			index++;
			
		}
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        	//自定义处理器
        	Spider.create(new IpProxyProcessor())
        	//自定义
//        	.addPipeline(new EsPipeline()).
            .addUrl("http://www.xicidaili.com/nn/").thread(10)
                    .run();
    }
    
    public static boolean isIP(String addr)  
    {  
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))  
        {  
            return false;  
        }  
        /** 
         * 判断IP格式和范围 
         */  
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";  

        Pattern pat = Pattern.compile(rexp);    

        Matcher mat = pat.matcher(addr);    

        boolean ipAddress = mat.find();  

        return ipAddress;  
    }

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//自定义处理器
    	Spider.create(new IpProxyProcessor())
    	//自定义
//    	.addPipeline(new EsPipeline()).
        .addUrl("http://www.xicidaili.com/nn/").thread(10)
                .run();
		
	}  
}
