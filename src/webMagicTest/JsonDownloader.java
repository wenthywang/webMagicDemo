package webMagicTest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.downloader.HttpClientRequestContext;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.proxy.Proxy;


public class JsonDownloader   {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    private HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();


    public void setHttpUriRequestConverter(HttpUriRequestConverter httpUriRequestConverter) {
        this.httpUriRequestConverter = httpUriRequestConverter;
    }

    private CloseableHttpClient getHttpClient(Site site) {

            return httpClientGenerator.getClient(site);
    }


    public String download(Request request,Site site) {
      
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = getHttpClient(site);
        Proxy proxy = null;
        HttpClientRequestContext requestContext = httpUriRequestConverter.convert(request, site, proxy);
        String resp=null;
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());
            resp = handleResponse(request, "UTF-8", httpResponse);
            logger.info("downloading page success {}", request.getUrl());
            return resp;
        } catch (IOException e) {
            logger.warn("download page {} error", request.getUrl(), e);
            return resp;
        } finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
        }
    }

    protected String handleResponse(Request request, String charset, HttpResponse httpResponse) throws IOException {
        return getResponseContent(charset, httpResponse);
    }

    private String getResponseContent(String charset, HttpResponse httpResponse) throws IOException {
    	return EntityUtils.toString(httpResponse.getEntity(), charset);
    }
}
