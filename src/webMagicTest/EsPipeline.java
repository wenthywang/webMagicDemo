/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package webMagicTest;

import org.apache.commons.lang.StringUtils;

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
public class EsPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		String title=resultItems.get("title");
		String date=resultItems.get("date");
		String content=resultItems.get("content");
		if(StringUtils.isNotEmpty(title)){
			System.out.println(title);
//			ESAPITest test=new ESAPITest();
//			Map<String,Object>dataMap=new HashMap<String,Object>();
//			dataMap.put("title", title);
//			dataMap.put("date", date);
//			dataMap.put("content", content);
//			//调用es 方法插入数据
//			test.putSDRFromCsdn("csdn_post", "CSDN_POST", dataMap);
			System.out.println("处理成功！");
		}
	}

}
