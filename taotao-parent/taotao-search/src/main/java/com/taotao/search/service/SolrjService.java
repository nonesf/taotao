package com.taotao.search.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.search.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;


public class SolrjService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private HttpSolrServer httpSolrServer;

    @Before
    public void setUp() throws Exception {
        // 在url中指定core名称：taotao
        //http://solr.taotao.com/#/taotao  -- 界面地址
        String url = "http://localhost:8983/taotao"; //服务地址
        HttpSolrServer httpSolrServer = new HttpSolrServer(url); //定义solr的server
        httpSolrServer.setParser(new XMLResponseParser()); // 设置响应解析器
        httpSolrServer.setMaxRetries(1); // 设置重试次数，推荐设置为1
        httpSolrServer.setConnectionTimeout(500); // 建立连接的最长时间

        this.httpSolrServer = httpSolrServer;
    }

    /**
     * 将商品数据导入到solr中
     */
    @Test
    public void testData(){
        //通过后台系统的接口查询商品数据
        String url = "http://localhost:8081/rest/item?page={page}&rows=100";
        int page = 1;
        int pageSize = 0;
        do{
            //将URL传入，用replacement 替换 searchString
            String u = StringUtils.replace(url, "{page}", "" + page);
//            System.out.println(u);


            try {
                //从后台系统中将数据读出来再存入solr中
                String jsonData = doGet(u);
                //直接将数据读成需要的结构
                JsonNode jsonNode = MAPPER .readTree(jsonData);
                String rows = jsonNode.get("rows").toString();

                List <TbItem> lists = MAPPER.readValue(rows, MAPPER.getTypeFactory().constructCollectionType(List.class,TbItem.class));
                pageSize = lists.size();
                this.httpSolrServer.addBeans(lists);
                this.httpSolrServer.commit();
                page ++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }while (pageSize == 100);
    }

    private String doGet(String url) throws IOException {
        //创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建http get 请求
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse = null;

        try {
            httpResponse = httpClient.execute(httpGet);
            //判断返回状态码是否为200
            if(httpResponse.getStatusLine().getStatusCode() == 200){
                return EntityUtils .toString(httpResponse.getEntity(),"UTF-8");
            }
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
        return null;
    }
}
