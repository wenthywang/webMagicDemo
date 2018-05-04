/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package pipeline;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import utils.RedisUtils;

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
public class RedisPipeline implements Pipeline {


	@Override
	public void process(ResultItems resultItems, Task task) {
		String jd=resultItems.get("jd");
		if(StringUtils.isNotBlank(jd)){
			RedissonClient	 client = RedisUtils.getInstance().getRedisson();
			RSet<String> rSet=RedisUtils.getInstance().getRSet(client, "LAGOU_JD");
			rSet.add(jd);
	        RedisUtils.getInstance().closeRedisson(client);  
				System.out.println("处理成功！");
		}
	}
}
