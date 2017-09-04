package webMagicTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import entity.Train;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import utils.QuartzUtil;

/**
 * <pre>
 * 12306 列车信息 爬虫。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class TrainTicketProcessor implements PageProcessor,Job {
    private static Logger log = LoggerFactory.getLogger(TrainTicketProcessor.class);//日志
    private Site site = Site
            .me()
            .setDomain("kyfw.12306.cn")
            .setRetryTimes(5)
            .setCharset("utf-8")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    private static String  BOOK_DATE="2017-10-01";
//    private static String  fromCity="广州";
//    private static String  toCity="潮汕";
    
    
    /**
     * 编号转文字
     * @param number
     * @return
     */
	private static String convertArea(String number) {
		switch (number) {
		case "IZQ":
			return "广州南";
		case "GZQ":
			return "广州";
		case "CBQ":
			return "潮汕";
		case "GGQ":
			return "广州东";
		default:
			return "未知";
		}
	}
    
    
    @Override
    public void process(Page page) {
    	String result=page.getRawText();
       if(result.contains("<!DOCTYPE html")){
    	   log.error("服务器繁忙，网络超时！");
    	   return;
       }
    	JSONObject obj=JSONObject.parseObject(result);
    	JSONArray list=obj.getJSONObject("data").getJSONArray("result");
    	List<Train>tList=new ArrayList<Train>();
    	for (Object object : list) {
			String info=(String) object;
	    	String[] data=	info.split("\\|");
			String trainNumber=data[3];
			String trainAreaNumber=data[4];
			String trainEndAreaNumber=data[5];
			String startTime=data[8];
			String arriveTime=data[9];
			String[] data2=	info.split("\\|\\|\\|\\|\\|\\|\\|");
			String secondSeatCout=data2[1].split("\\|\\|\\|\\|")[1].split("\\|")[0];
			if(secondSeatCout!=null&&secondSeatCout.equals("无")){
				continue;
			}

			if(data[0].equals("预订")){
				trainNumber=data[2];
				trainAreaNumber=data[3];
				trainEndAreaNumber=data[4];
				startTime=data[7];
				arriveTime=data[8];
			}
			Train t=new Train(trainNumber, convertArea(trainAreaNumber), convertArea(trainEndAreaNumber), startTime, arriveTime, secondSeatCout);
	
			tList.add(t);
    	}
    	if(CollectionUtils.isEmpty(tList)){
    		   log.info("{} 当前日期暂时无二等座车票！",BOOK_DATE);
    	}else{
    		   log.info("当前日期暂时还有二等座车票！ {} ",JSONObject.toJSONString(tList));
    	}
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
    	 //添加第一个任务  每隔10秒执行一次
        try {
			QuartzUtil.addJob("TrainTicketJob", "TrainTicketJobTrigger", TrainTicketProcessor.class, 10);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
    }

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		String url="https://kyfw.12306.cn/otn/leftTicket/query?"
				+ "leftTicketDTO.train_date="+BOOK_DATE
				+ "&leftTicketDTO.from_station=GZQ"
				+ "&leftTicketDTO.to_station=CBQ"
				+ "&purpose_codes=ADULT";
    	//自定义处理器
    	Spider.create(new TrainTicketProcessor())
    	//自定义
//    	.addPipeline(new EsPipeline()).
        .addUrl(url)
                .run();
	}
}
