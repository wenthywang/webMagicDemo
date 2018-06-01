/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package pipeline;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import utils.RedisUtils;

/**
 * <pre>
 * 数据结果处理。
 * </pre>
 * 
 * @author 王文辉 wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 *          </pre>
 */
public class RedisPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		Map<String,String>songMap = resultItems.get("SONG");
		if (MapUtils.isNotEmpty(songMap)) {
			RedissonClient client = RedisUtils.getInstance().getRedisson();
			RMap<String,String> rMap = RedisUtils.getInstance().getRMap(client, "QQ_MUSIC");
			rMap.putAll(songMap);
			RedisUtils.getInstance().closeRedisson(client);
			System.out.println("处理成功！");
		}
	}
}
