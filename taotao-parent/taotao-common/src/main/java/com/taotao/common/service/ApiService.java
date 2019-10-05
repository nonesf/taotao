package com.taotao.common.service;


import com.taotao.common.httpclient.HttpResult;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ApiService implements BeanFactoryAware {

    @Autowired(required = false )
    private RequestConfig requestConfig;

    private BeanFactory beanFactory;

    /**
     * 指定GET请求，返回：null，请求失败，String数据，请求成功
     * @param url
     * @return
     * @throws IOException
     */
    public String doGet(String url) throws ClientProtocolException, IOException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getHttpClient().execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 带有参数的GET请求
     * @return
     */
    public String doGet(String url , Map<String , String>params) throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(url);
        //循环设置参数
        for(Map.Entry<String , String> entry : params.entrySet()){
            builder.setParameter(entry.getKey(),entry.getValue());
        }
        return this.doGet(builder.build().toString());
    }

    /**
     * 带有参数的POST请求
     * @return
     */
     public HttpResult doPost(String url , Map<String , String>params) throws IOException {
         HttpPost httpPost = new HttpPost(url);
         httpPost.setConfig(requestConfig);

         if(null != params){
             //设置post参数
             List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
             for(Map.Entry<String ,String> entry : params.entrySet()){
                 parameters.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
             }

             //构造一个form表单式的实体
             UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
             //将请求实体设置到httpPost对象中
             httpPost.setEntity(formEntity);
         }
         CloseableHttpResponse response = null;
         try {
             response = getHttpClient().execute(httpPost);
             return new HttpResult(response.getStatusLine().getStatusCode(),
                     EntityUtils.toString(response.getEntity(),"UTF-8"));
         } catch (IOException e) {
             e.printStackTrace();
         }finally {
             if(response != null){
                 response.close();
             }
         }
         return null;
     }

    /**
     * 带有参数的POST请求请求参数为json
     * @return
     */
    public HttpResult doPostJson(String url , String json) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        if(null != json){
            //构造一个form表单式的实体
            StringEntity stringEntity = new StringEntity(json,ContentType.APPLICATION_JSON);
            //将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient().execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(response.getEntity(),"UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(response != null){
                response.close();
            }
        }
        return null;
    }

    /**
     * 没有参数的POST请求
     * @return
     */
    public HttpResult doPost(String url) throws IOException {
        return this.doPost(url , null);
    }


    private CloseableHttpClient getHttpClient(){
        //通过BeanFactory获取bean，保证httpClient对象是多例的
        return this.beanFactory.getBean(CloseableHttpClient.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
