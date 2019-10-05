package com.taotao.manage.controller;

import com.taotao.manage.pojo.TbItemDesc;
import com.taotao.manage.service.ItemDescService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("item/desc")
public class ItemDescController {

    @Autowired
    private ItemDescService itemDescService;

    /**
     * @PathVariable :路径变量，就是要获取一个url 地址中的一部分值，即value{}中定义的内容
     *
     * 根据商品ID查询商品描述，回显数据
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{itemId}" , method = RequestMethod.GET)
    public ResponseEntity<TbItemDesc> queryItemDescById(@PathVariable("itemId")Long itemId){
        try {
            TbItemDesc itemDesc = itemDescService.queryItemDescById(itemId);
            if(itemDesc == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(itemDesc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}
