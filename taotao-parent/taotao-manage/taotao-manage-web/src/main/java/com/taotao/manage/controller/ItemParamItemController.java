package com.taotao.manage.controller;

import com.taotao.manage.pojo.TbItemParamItem;
import com.taotao.manage.service.ItemParamItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("item/param/item")
public class ItemParamItemController {

    @Autowired
    private ItemParamItemService itemParamItemService;
    /**
     * 根据商品id查询商品规格参数
     * @return
     */
    @RequestMapping(value = "{itemId}" , method = RequestMethod.GET)
    public ResponseEntity<TbItemParamItem> queryByItemId(@PathVariable("itemId")Long itemId){
        try {
            TbItemParamItem tbItemParamItem = itemParamItemService.queryByItemId(itemId);
            if(null == tbItemParamItem){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(tbItemParamItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
