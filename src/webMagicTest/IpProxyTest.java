/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package webMagicTest;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.proxy.Proxy;
import utils.NewTest;
import utils.RedisUtils;

/**
 * <pre>
 * 程序的中文名称。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * @date 2018年3月12日 
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class IpProxyTest  implements Job{
	
    private static Site site = Site
            .me()
//            .setDomain("https://www.baidu.com")
            .setRetryTimes(5)
            .setCharset("utf-8")
            .setSleepTime(3000);
//            .addHeader("Host", "https://www.douban.com")
//            .setUserAgent(
//                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
	public static void testIpSSL(String ip,int port) throws Exception {
		NewTest n=new NewTest();
		CloseableHttpClient client=	n.getClient(site,ip,port);
		HttpGet get=new HttpGet("http://ip.chinaz.com/getip.aspx");
		try{
			CloseableHttpResponse		httpResponse= client.execute(get);
			  System.out.println(EntityUtils.toString(httpResponse.getEntity(),"utf-8"));
		}catch(Exception e){
			throw e;
		}finally{
			client.close();
		}
	}

	
	public static void main(String[] args) {
		try {
			testIpSSL("220.165.188.84", 61202);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
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
		        	boolean sslversion=false;
		        	try {
					    testIpSSL(ip, port);
						sslversion=true;
					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
		        	if(sslversion){
		        		System.out.println(ip+":"+port+"->可用");
			        	proxyList.add(p);
		        	}else{
		        		System.out.println(ip+":"+port+"->ssl-version-不可用");
		        		rMap.removeAsync(ip);
		        	}
		        	
		        }else{
		         	System.out.println(ip+":"+port+"->删除！");
		        	rMap.removeAsync(ip);
		        }
    	 
    	}
        RedisUtils.getInstance().closeRedisson(client);  
		
	}

}
