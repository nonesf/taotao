package com.taotao.web.controller;


import com.taotao.manage.pojo.TbItem;
import com.taotao.manage.pojo.TbItemDesc;
import com.taotao.web.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "{itemId}",method = RequestMethod.GET)
    public ModelAndView ItemDetail(@PathVariable("itemId")Long itemId){
        ModelAndView modelAndView = new ModelAndView("item");
        //设置模型数据
        //查询商品数据
        TbItem item = itemService.queryItemById(itemId);
        modelAndView.addObject("item", item);

        //查询商品描述数据
        TbItemDesc itemDesc = itemService.queryItemDescById(itemId);
        modelAndView.addObject("itemDesc", itemDesc);

        return modelAndView;
    }
}
