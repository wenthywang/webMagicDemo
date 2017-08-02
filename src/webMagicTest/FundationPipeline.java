/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package webMagicTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import test.ESAPITest;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * <pre>
 * 数据结果处理。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class FundationPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		 double currentVal=0.00;
		 if( resultItems.get("currentVal")!=null){
			 currentVal= resultItems.get("currentVal");
		 }
		 double pastVal=0.00;
		 if( resultItems.get("pastVal")!=null){
			 pastVal= resultItems.get("pastVal");
		 }

		 String title=resultItems.get("title");
		 String id=resultItems.get("id");
		 if(StringUtils.isEmpty(id)){
			 return;
		 }
		 
		 String openDay=resultItems.get("openDay");
		 String type=resultItems.get("type");
		 double pastValPercent=0.00;
		 if( resultItems.get("pastValPercent")!=null){
			 pastValPercent= resultItems.get("pastValPercent");
		 }
		 double sumVal=0.00;
		 if( resultItems.get("sumVal")!=null){
			 sumVal= resultItems.get("sumVal");
		 }
		 double oneMonthPercent=0.00;
		 if( resultItems.get("oneMonthPercent")!=null){
			 oneMonthPercent= resultItems.get("oneMonthPercent");
		 }
		 double threeMonthPercent=0.00;
		 if( resultItems.get("threeMonthPercent")!=null){
			 threeMonthPercent= resultItems.get("threeMonthPercent");
		 }
		 double sixMonthPercent=0.00;
		 if( resultItems.get("sixMonthPercent")!=null){
			 sixMonthPercent= resultItems.get("sixMonthPercent");
		 }
		 double oneYearPercent=0.00;
		 if( resultItems.get("oneYearPercent")!=null){
			 oneYearPercent= resultItems.get("oneYearPercent");
		 }
		 double threeYearPercent=0.00;
		 if( resultItems.get("threeYearPercent")!=null){
			 threeYearPercent= resultItems.get("threeYearPercent");
		 }
		 double openPercent=0.00;
		 if( resultItems.get("openPercent")!=null){
			 openPercent= resultItems.get("openPercent");
		 }
		 
//		 Map<String,Double> dataMap2=resultItems.get("data");
		
		 
			ESAPITest test=new ESAPITest();
			Map<String,Object>dataMap=new HashMap<String,Object>();
       if(StringUtils.isNotEmpty(id)&&StringUtils.isNotEmpty(title)){
    		
			dataMap.put("id", id);
			dataMap.put("fund_id", id);
			dataMap.put("name", title);
			dataMap.put("pastVal", pastVal);
			dataMap.put("currentVal", currentVal);
			dataMap.put("openDay", openDay);
			dataMap.put("type", type);
			dataMap.put("pastValPercent", pastValPercent);
			dataMap.put("sumVal", sumVal);
			dataMap.put("oneMonthPercent", oneMonthPercent);
			dataMap.put("threeMonthPercent", threeMonthPercent);
			dataMap.put("sixMonthPercent", sixMonthPercent);
			dataMap.put("oneYearPercent", oneYearPercent);
			dataMap.put("threeYearPercent", threeYearPercent);
			dataMap.put("openPercent", openPercent);
			 double   bonus_temp =  sumVal-pastVal;  
			 BigDecimal   b  =   new BigDecimal(bonus_temp);  
			 double   bonus   =  b.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();  
//			 if(bonus<0.00){
//				 bonus=0.00;
//			 }
			dataMap.put("bonus", bonus);
			test.putSDRFromCsdn(FoundationProcessor.FUND_INFO_INDEX, 
					FoundationProcessor.FUND_INFO_INDEX.toUpperCase(), dataMap);
       }else{
//    	   for (Entry<String, Double> entry :   dataMap2.entrySet()) {
//    			dataMap.put("date", entry.getKey());
//    			dataMap.put("value", entry.getValue());
//    			dataMap.put("fund_id", id);
//    			test.putSDRFromCsdn("fund_val", "FUND_VAL_"+id, dataMap);
//		}
    	 
		
       }
	}

}
