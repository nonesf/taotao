package com.taotao.search.controller;

import com.taotao.search.pojo.SearchResult;
import com.taotao.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("search")
public class SearchController {

    //设置每页的数据条数
    private int ROWS = 32;

    @Autowired
    private ItemSearchService itemSearchService ;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView search(@RequestParam("q")String keyWords,
                               @RequestParam(value = "page", defaultValue = "1")Integer page){
        ModelAndView modelAndView =  new ModelAndView("search");
        SearchResult searchResult = null;
        searchResult = this.itemSearchService.search(keyWords, page,ROWS);

        //解决中文乱码问题
        try {
            keyWords = new String(keyWords .getBytes("ISO-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            keyWords = null;
        }

        //搜索关键字
        modelAndView .addObject("query", keyWords) ;
        //搜索结果集
        modelAndView .addObject("itemList",searchResult.getList()) ;
        //当前页数
        modelAndView .addObject("page", page) ;
        //总页数
        Long total = searchResult .getTotal();
        Long pages = total % ROWS == 0 ? total / ROWS : total / ROWS + 1;
        modelAndView .addObject("pages", pages);
        return modelAndView;
    }
}
