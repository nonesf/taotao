package com.taotao.manage.controller.api;

import com.taotao.manage.pojo.TbItem;
import com.taotao.manage.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("api/item")
public class ApiItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 对外接口服务，根据商品id查询商品基本数据
     * @return
     */
    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public ResponseEntity<TbItem> queryItemById(@PathVariable("itemId") Long itemId){
        try {
            TbItem item = itemService.queryItemById(itemId);
            if(null == item){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }
}
