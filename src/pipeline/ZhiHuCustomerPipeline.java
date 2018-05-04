/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package pipeline;

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
public class ZhiHuCustomerPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		 String name=resultItems.get("name");
		 String location=resultItems.get("location");
		 Integer follower_count=resultItems.get("follower_count");
		 Integer articles_count=resultItems.get("articles_count");
		 Integer gender=resultItems.get("gender");
		 String url_token=resultItems.get("url_token");
       if(StringUtils.isEmpty(url_token)){
    	   return;
       }
				ESAPITest test=new ESAPITest();
				Map<String,Object>dataMap=new HashMap<String,Object>();
				dataMap.put("name", name);
				dataMap.put("follower_count", follower_count);
				dataMap.put("articles_count", articles_count);
				dataMap.put("id", url_token);
				dataMap.put("url_token", url_token);
				dataMap.put("gender", gender);
				dataMap.put("loc", location);
				test.putSDRFromCsdn("zhihu_info", "ZHIHU_INFO", dataMap);
		
	}

}
