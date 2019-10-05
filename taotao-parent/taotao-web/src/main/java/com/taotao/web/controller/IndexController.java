package com.taotao.web.controller;

import com.taotao.web.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 显示首页
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView indexAD1(){
        ModelAndView mv = new ModelAndView("index");
        //首页大广告
        String indexAD1 = this.indexService.queryIndexAD1();
        //小广告
        String indexAD2 = this.indexService.queryIndexAD2();
        //添加到模型中
        mv.addObject("IndexAD1" , indexAD1);
        mv.addObject("IndexAD2" , indexAD2);
        return mv;
    }
}
