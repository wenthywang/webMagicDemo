/**
 * Copyright(c) Beijing Kungeek Science & Technology Ltd. 
 */
package webMagicTest;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * <pre>
 * 程序的中文名称。
 * </pre>
 * @author mmr  mmr@kungeek.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class MyProcess implements PageProcessor {

	// 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    public static int count =0;
	/* (non-Javadoc)
	 * @see us.codecraft.webmagic.processor.PageProcessor#getSite()
	 */
	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	/* (non-Javadoc)
	 * @see us.codecraft.webmagic.processor.PageProcessor#process(us.codecraft.webmagic.Page)
	 */
	@Override
	public void process(Page page) {
		 //判断链接是否符合http://www.cnblogs.com/任意个数字字母-/p/7个数字.html格式
        if(!page.getUrl().regex("http://www.cnblogs.com/[a-z 0-9 -]+/p/[0-9]{7}.html").match()){
            //加入满足条件的链接
            page.addTargetRequests(
                    page.getHtml().xpath("//*[@id=\"post_list\"]/div/div[@class='post_item_body']/h3/a/@href").all());
        }else{                             
            //获取页面需要的内容
            System.out.println("抓取的内容："+
                    page.getHtml().xpath("//*[@id=\"Header1_HeaderTitle\"]/text()").get()
                    );
            count ++;
        }
		
	}

}
