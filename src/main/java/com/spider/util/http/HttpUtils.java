package com.spider.util.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description http工具类
 * @Author: fk
 * @Date: 2020/3/2 18:50
 */
public class HttpUtils {

    /**
     * @Author: fk
     * @Description: httppost请求
     * @Date: 17:46 2020/2/29
     * @Param: [url, paramMap]
     * @return: com.alibaba.fastjson.JSONObject
     **/
    public static JSONObject postRequest(String url, Map<String, String> paramMap) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            //使用HttpGet的方式请求网址
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));// 固定
            }
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            //获取网址的返回结果
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            //获取返回结果中的实体
            HttpEntity entity = (HttpEntity) response.getEntity();

            //将返回的实体输出
            try {
                String result = EntityUtils.toString(entity);
                JSONObject jsonObject = JSONObject.parseObject(result);
                System.out.println(jsonObject);
                return jsonObject;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}
