package webMagicTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import entity.FoundationEntity;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * <pre>
 *天天 基金指定基金 历史净值 爬虫。
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
public class FoundationValueProcessor implements PageProcessor {

	/**
	 * 基金 代码
	 */
	private static String FUND_CODE = "050002";

	/**
	 * 访问链接时需要带上的refer 链接 （必须）
	 */
	public static final String FUND_REFER_URL = "http://fund.eastmoney.com/f10/jjjz_" + FUND_CODE + ".html";
	/**
	 * 分页大小 默认是20 改1000 也可行
	 */
	private static int pageSize = 1000;

	/**
	 * 查询基金净值 url
	 */
	static String FUND_BASE_VALUE = "http://api.fund.eastmoney.com/f10/lsjz?" + "fundCode=" + FUND_CODE
			+ "&pageIndex=$pageIndex" + "&pageSize=" + String.valueOf(pageSize);

	/**
	 * 当前基金 净值个数 用来计算 多少页
	 */
	private static int totalCount = 0;

	/**
	 * 线程安全 list 存放 基金净值 统计计算排序等
	 */
	private static List<FoundationEntity> list = Collections.synchronizedList(new ArrayList<FoundationEntity>());

	private Site site = Site.me().setRetryTimes(5).setCharset("utf-8").setSleepTime(5000).setTimeOut(100000)
			.setUserAgent(
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
			.addHeader("Referer", FUND_REFER_URL);

	@Override
	public void process(Page page) {
		String resultJson = page.getJson().toString();
		JSONObject obj = JSONObject.parseObject(resultJson);
		totalCount = obj.getIntValue("TotalCount");
		int pageCount = totalCount / pageSize;
		int pageLot = totalCount % pageSize;
		if (pageLot > 0) {
			pageCount++;
		}
		int pageIndex = obj.getIntValue("PageIndex");

		pageIndex++;
		// 如果是详情页url 则 分析 处理
		System.out.println("**************page" + (pageIndex - 1) + "*****************");

		JSONArray array = obj.getJSONObject("Data").getJSONArray("LSJZList");

		for (int i = 0; i < array.size(); i++) {
			JSONObject dataObject = array.getJSONObject(i);
			Double DWJZ = dataObject.getDouble("DWJZ");
			Double JZZZL = dataObject.getDouble("JZZZL");
			String date = dataObject.getString("FSRQ");
			if (JZZZL == null) {
				JZZZL = 0.00;
			}
			FoundationEntity entity = new FoundationEntity();
			entity.setDate(date);
			entity.setJjzzl(JZZZL);
			entity.setDwjz(DWJZ);
			list.add(entity);
			System.out.println("时间->" + date + ",单位净值->" + DWJZ + ",增长率->" + JZZZL);
		}
		 page.putField("data", obj);
		System.out.println("");
		if (pageIndex <= pageCount) {
			String newUrl = FUND_BASE_VALUE.replace("$pageIndex", String.valueOf(pageIndex));
			page.addTargetRequest(newUrl);
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		// 自定义处理器
		String baseUrl = FUND_BASE_VALUE.replace("$pageIndex", "1");

		Spider.create(new FoundationValueProcessor())
				// 自定义
				.addUrl(baseUrl).thread(10).run();

		System.out.println("共" + list.size() + "个基金净值");
//       long date
		double max = 0.0;
		double min = 0.0;
		double avg = 0.0;
		double count = 0.0;
		for (FoundationEntity entity : list) {
			if (entity.getDwjz() > max) {
				max = entity.getDwjz();
			}
			if (min == 0) {
				min = entity.getDwjz();
			} else {
				if (entity.getDwjz() < min) {
					min = entity.getDwjz();
				}
			}

			count = count + entity.getDwjz();
			
			String longdate=entity.getDate().replace("-", "");
			Long date=Long.valueOf(longdate);
			
		}
		avg = count / list.size();
		System.out.println("最大净值->" + max);
		System.out.println("最小净值->" + min);
		System.out.println("平均净值->" + avg);
		System.out.println("成立日->" + avg);

		// 单位净值 升序 排序
		Collections.sort(list, new Comparator<FoundationEntity>() {

			@Override
			public int compare(FoundationEntity o1, FoundationEntity o2) {

				if (o1.getDwjz() < o2.getDwjz()) {
					return -1;
				} else if (o1.getDwjz() > o2.getDwjz()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		System.out.println(JSONObject.toJSONString(list));

	}
}
