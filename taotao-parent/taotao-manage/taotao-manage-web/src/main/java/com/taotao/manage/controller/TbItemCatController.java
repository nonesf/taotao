package com.taotao.manage.controller;

import com.taotao.manage.service.ItemCatService;
import com.taotao.manage.pojo.TbItemCat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("item/cat")
public class TbItemCatController {
    @Autowired
    private ItemCatService itemCatService;

    //动态获取parentId参数
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<TbItemCat>> queryItemCat(
            @RequestParam(value = "id",defaultValue = "0")Long parentId){
        try {
            List<TbItemCat>list = itemCatService.queryItemCat(parentId);
            if(null == list || list.isEmpty()){
                //没有资源，响应404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}
