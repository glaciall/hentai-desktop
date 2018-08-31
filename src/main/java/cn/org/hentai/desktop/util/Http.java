package cn.org.hentai.desktop.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by matrixy on 2016/12/29.
 */
public final class Http
{
    public static Map<String, String> h(String... pairs) throws Exception
    {
        HashMap<String, String> headers = new HashMap<String, String>();
        if (pairs.length % 2 != 0) throw new Exception("参数必须成对");
        for (int i = 0; i < pairs.length; i+=2)
            headers.put(pairs[i], pairs[i + 1]);

        return headers;
    }

    public static Map<String, String> p(String... pairs) throws Exception
    {
        return h(pairs);
    }

    public static String join(Map<String, String> parameters) throws UnsupportedEncodingException
    {
        StringBuffer sb = new StringBuffer();
        Iterator<String> keys = parameters.keySet().iterator();
        while (keys.hasNext())
        {
            String key = keys.next();
            String value = parameters.get(key);
            sb.append(key);
            sb.append('=');
            sb.append(java.net.URLEncoder.encode(value, "UTF-8"));
            sb.append('&');
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    public static String get(String url) throws Exception
    {
        return get(url, null);
    }

    public static String get(String url, Map<String, String> headers) throws Exception
    {
        return get(url, headers, "UTF-8");
    }

    public static String get(String url, Map<String, String> headers, String encoding) throws Exception
    {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        try
        {
            HttpGet httpGet = new HttpGet(url);

            if (headers != null)
            {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                Set<String> keySet = headers.keySet();
                for (String key : keySet) httpGet.addHeader(key,  headers.get(key));
            }
            client = HttpClients.createDefault();
            // TODO: 什么时候试一试超时的可用性再说
            // host.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 4000);

            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            entity.writeTo(baos);
            return baos.toString(encoding);
        }
        finally
        {
            try { response.close(); } catch(Exception e) { }
            try { client.close(); } catch(Exception e) { }
        }
    }

    public static String post(String url) throws Exception
    {
        return post(url, "UTF-8", (Map<String, String>)null);
    }

    public static String post(String url, String encoding, Map<String, String> params) throws Exception
    {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        try
        {
            HttpPost httpPost = new HttpPost(url);

            if (params != null)
            {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                Set<String> keySet = params.keySet();
                for (String key : keySet) nvps.add(new BasicNameValuePair(key, params.get(key)));
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
            }

            client = HttpClients.createDefault();
            // TODO: 什么时候试一试超时的可用性再说
            // host.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 4000);

            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();

            return (EntityUtils.toString(entity, encoding));
        }
        finally
        {
            try { response.close(); } catch(Exception e) { }
            try { client.close(); } catch(Exception e) { }
        }
    }

    public static String post(String url, String encoding, Map<String, String> headers, String data) throws Exception
    {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        try
        {
            HttpPost httpPost = new HttpPost(url);
            if (headers != null)
            {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                Set<String> keySet = headers.keySet();
                for (String key : keySet) httpPost.setHeader(key, headers.get(key));
            }
            httpPost.setEntity(new StringEntity(data, encoding));

            client = HttpClients.createDefault();
            // TODO: 什么时候试一试超时的可用性再说
            // host.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 4000);

            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();

            return (EntityUtils.toString(entity, encoding));
        }
        finally
        {
            try { response.close(); } catch(Exception e) { }
            try { client.close(); } catch(Exception e) { }
        }
    }

    public static String post(String url, String encoding, String data) throws Exception
    {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        try
        {
            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(new StringEntity(data, encoding));

            client = HttpClients.createDefault();
            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();

            return (EntityUtils.toString(entity, encoding));
        }
        finally
        {
            try { response.close(); } catch(Exception e) { }
            try { client.close(); } catch(Exception e) { }
        }
    }

    public static String upload()
    {
        return null;
    }

    public static void download(String url, OutputStream output) throws Exception
    {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        try
        {
            HttpGet httpGet = new HttpGet(url);
            client = HttpClients.createDefault();
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            entity.writeTo(output);
        }
        finally
        {
            try { response.close(); } catch(Exception e) { }
            try { client.close(); } catch(Exception e) { }
        }
    }
}
