package com.taotao.search.service;

import com.taotao.search.pojo.SearchResult;
import com.taotao.search.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ItemSearchService {

    @Autowired
    private HttpSolrServer httpSolrServer ;

    public SearchResult search(String keyWords, Integer page, int rows) {
        //查询对象
        SolrQuery solrQuery = new SolrQuery() ;
        //构造查询条件,搜索关键词和商品状态
        solrQuery .setQuery("title:" + keyWords + "AND status = 1");

        //设置分页，start = 0就是从0开始，rows=5，当前放回5条数据，第二页就是变化start这个值为5 就可以
        //第一页第一条数据从0开始
        solrQuery .setStart((Math.max(page, 1)-1) * rows);
        solrQuery .setRows(rows);

        //是否需要高亮,如果不是全部搜索，关键字不为空就高亮显示
        boolean isHighlighting = StringUtils.endsWith("*",keyWords) && StringUtils .isNotEmpty(keyWords);

        //设置高亮
        if(isHighlighting){

            //开启高亮组件
            solrQuery .setHighlight(true);
            //设置高亮字段
            solrQuery.addHighlightField("title");
            //标记，高亮关键字前缀
            solrQuery.setHighlightSimplePre("<em>");
            //后缀
            solrQuery.setHighlightSimplePost("</em>");
        }

        //执行查询
        try {
            QueryResponse queryResponse = this.httpSolrServer.query(solrQuery);
            //获取所有的数据
            List<TbItem> items = queryResponse.getBeans(TbItem.class);

            if(isHighlighting){
                //将高亮的标题数据写回到数据对象中
                Map<String , Map<String,List<String>>> map = queryResponse.getHighlighting();
                for(Map.Entry<String,Map<String,List<String>>> highLighting : map.entrySet()){
                    for(TbItem item : items){

                        //如果需要高亮的数据id与获取的数据id不相等就不做处理
                        if(!highLighting .getKey().equals(item.getId().toString())){
                            continue;
                        }
                        //将高亮的标题设置为商品标题数据内容
                        item .setTitle(StringUtils.join(highLighting.getValue().get("title"),""));
                        break;
                    }
                }

            }
            return new SearchResult(queryResponse.getResults().getNumFound(),items);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return null;
    }

}
