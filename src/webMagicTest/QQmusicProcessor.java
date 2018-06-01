package webMagicTest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import pipeline.RedisPipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant.Method;

/**
 * <pre>
 * 微博热搜 爬虫。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class QQmusicProcessor implements  PageProcessor,Job {
	
	private static String searchUrl="https://c.y.qq.com/soso/fcgi-bin/client_search_cp?"
			+ "ct=24"
			+ "&qqmusic_ver=1298"
			+ "&new_json=1"
			+ "&remoteplace=txt.yqq.song"
			+ "&searchid=67371728965497168"
			+ "&t=0&aggr=1"
			+ "&cr=1"
			+ "&catZhida=1"
			+ "&lossless=0"
			+ "&flag_qc=0"
			+ "&p=1"
			+ "&n=@pageSize"
			+ "&w=@searchContent"
			+ "&g_tk=5381"
			+ "&loginUin=0&hostUin=0&format=jsonp"
			+ "&inCharset=utf8&outCharset=utf-8&notice=0"
			+ "&platform=yqq&needNewCode=0";
	
	private static final String getSongInfo="https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=5381"
			+ "&loginUin=0&hostUin=0&format=json&inCharset=utf8"
			+ "&outCharset=utf-8&notice=0&platform=yqq"
			+ "&needNewCode=0&cid=205361747"
			+ "&uin=0"
			+ "&songmid=@@"
			+ "&filename=C400@@.m4a&guid=8825271035";
	
	
	private static final String getSongUrl="http://dl.stream.qqmusic.qq.com/{fileName}"
			+ "?vkey={vkey}"
			+ "&guid=8825271035"
			+ "&uin=0"
			+ "&fromtag=66";
	
	
   //文章详情url过滤
    private Site site = Site
            .me()
            .setDomain("c.y.qq.com")
            .setRetryTimes(5)
            .setCharset("UTF-8")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    private static int totalNum=-1;
    
    
    private static final String getSongLyric="https://u.y.qq.com/cgi-bin/musicu.fcg?_="+System.currentTimeMillis();
    
    private static final String getAlbumPhoto="http://y.gtimg.cn/music/photo_new/T002R300x300M000@albumId.jpg?max_age=2592000";
    @Override
    public void process(Page page) {
    	String json=page.getJson().toString();
    	String searchContent=page.getUrl().toString().split("&w=")[1].split("&g_tk=")[0];
    	
    	//获取搜索内容
    	try {
			searchContent=URLDecoder.decode(searchContent, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	String json1=json.split("callback\\(")[1];
    	String json2=json1.substring(0, json1.length()-1);
    	JSONObject obj=JSONObject.parseObject(json2);
    	//音乐总数
    	 int totalnum=obj.getJSONObject("data").getJSONObject("song").getIntValue("totalnum");
    	 
    	if(totalNum==totalnum) {
    		
           JSONArray array=obj.getJSONObject("data").getJSONObject("song").getJSONArray("list");
           Map<String,String>songMap=new HashMap<String,String>();
           JSONArray songArray=new JSONArray();
        	for(int i=0;i<array.size();i++) {
        		JSONObject song=	array.getJSONObject(i);
        	String mid=	song.getString("mid");
        	String name=song.getString("name");
        	String albumId=song.getJSONObject("album").getString("mid");
        	String singer=song.getJSONArray("singer").getJSONObject(0).getString("name");
        	String musicId=song.getString("id");
        	JsonDownloader download=new JsonDownloader();
        	Request request=new Request();
        	String requestUrl=getSongInfo.replaceAll("@@", mid);
        	request.setUrl(requestUrl);
        	String infoJson=download.download(request, site);
        	
        	Request requestLyric=new Request();
        	
//        	requestLyric.addHeader("referer ", "https://y.qq.com/n/yqq/song/"+mid+".html");
//        	String requestLyricUrl=getSongLyric.replace("@musicid", musicId);
//        	requestLyric.addHeader(":path", requestLyricUrl);
//        	requestLyric.addHeader("accept-encoding", "gzip, deflate, br");
        	requestLyric.setUrl(getSongLyric);
//        	requestLyric.setMethod("GET");
//        	System.out.println(requestLyricUrl);
        	Map<String,Object>params=new HashMap<String,Object>();
          	Map<String,Object>params1=new HashMap<String,Object>();
          	Map<String,Object>params2=new HashMap<String,Object>();
          	Map<String,Object>params3=new HashMap<String,Object>();
          	params1.put("g_tk", 5381);
          	params1.put("uin", 0);
          	params1.put("format", "json");
          	params1.put("inCharset", "utf-8");
          	params1.put("outCharset", "utf-8");
          	params1.put("notice", "0");
          	params1.put("platform", "h5");
          	params1.put("needNewCode", "1");
        	params.put("comm", params1);
        	
        	params2.put("module", "music.pf_song_detail_svr");
        	params2.put("method", "get_song_detail");
        	params3.put("song_id", musicId);
        	params2.put("param", params3);
        	params.put("song_detail", params2);
        	try {
				requestLyric.setRequestBody(HttpRequestBody.form(params, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	requestLyric.addHeader("origin", "https://y.qq.com");
        	requestLyric.addHeader("accept", "application/json");
        	requestLyric.addHeader("referer", "https://y.qq.com/m/client/v5detail/songDetail.html?songid="+musicId+"&_hidehd=1");
        	requestLyric.setMethod(Method.POST);
//        	String lyRicJson=download.download(requestLyric, site);
//        	System.out.println(lyRicJson);
        	
        	
        	JSONObject obj2=JSONObject.parseObject(infoJson);
           String vkey=	obj2.getJSONObject("data").getJSONArray("items").getJSONObject(0).getString("vkey");
           String fileName=	obj2.getJSONObject("data").getJSONArray("items").getJSONObject(0).getString("filename");
        	String finalSongUrl=getSongUrl.replaceAll("\\{fileName\\}", fileName).replaceAll("\\{vkey\\}", vkey);
        	JSONObject redisJson=new JSONObject();
        	redisJson.put("name", name);
        	redisJson.put("url", finalSongUrl);
        	redisJson.put("mid", mid);
        	redisJson.put("author", singer);
        	redisJson.put("album_Image", getAlbumPhoto.replace("@albumId", albumId));
        	songArray.add(redisJson);
        	}
        	songMap.put(searchContent, songArray.toJSONString());
        	page.putField("SONG", songMap);
    	}else {
    		if(totalnum>0) {
    			totalNum=totalnum;
    			String newUrl=searchUrl.replace("20", String.valueOf(totalnum));
    			page.addTargetRequest(newUrl);
    		}
    	}
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
    	String searchContent="五月天";
		try {
			searchUrl = searchUrl.replace("@searchContent", URLEncoder.encode(searchContent, "UTF-8")).replace("@pageSize", "20");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        	//自定义处理器
        	Spider.create(new QQmusicProcessor())
        	//自定义
        	.addPipeline(new RedisPipeline())
        	.addUrl(searchUrl).thread(5)
                    .run();
    }

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		main(new String[] {});
		
	}
}
