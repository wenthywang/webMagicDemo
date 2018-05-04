/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package pipeline;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
public class EsPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		String title=resultItems.get("title");
		String date=resultItems.get("date");
		String content=resultItems.get("content");
		String id=resultItems.get("id");
		
		Integer likeCount=resultItems.get("likeCount");
		String author=resultItems.get("author");
		String keywords=resultItems.get("keywords");
		Integer followerCount=resultItems.get("followerCount");
		Integer numberBoard=resultItems.get("numberBoard");
		Integer commentCount=resultItems.get("commentCount");
		Integer answerCount=resultItems.get("answerCount");
		if(StringUtils.isNotEmpty(title)){
			System.out.println(title);
			ESAPITest test=new ESAPITest();
			Map<String,Object>dataMap=new HashMap<String,Object>();
			dataMap.put("title", title);
			dataMap.put("date", date);
			dataMap.put("content", content);
			dataMap.put("id", id);
			
			if(StringUtils.isEmpty(author)||author.equals("null")){
				dataMap.put("keywords", keywords);
				dataMap.put("followerCount", followerCount);
				dataMap.put("numberBoard", numberBoard);
				dataMap.put("commentCount", commentCount);
				dataMap.put("answerCount", answerCount);
				test.putSDRFromCsdn("zhihu_question", "ZHIHU_QUESTION", dataMap);
			}else{
				dataMap.put("author", author);
				dataMap.put("likeCount", likeCount);
				test.putSDRFromCsdn("zhihu_post", "ZHIHU_POST", dataMap);
			}
			//调用es 方法插入数据
			System.out.println("处理成功！");
		}
	}

}
