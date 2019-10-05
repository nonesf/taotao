package com.taotao.manage.controller;

import com.taotao.manage.pojo.TbItemParam;
import com.taotao.manage.service.ItemParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@RequestMapping("item/param")
public class ItemParamContreller {

    @Autowired
    private ItemParamService itemParamService;

    /**
     * 根据商品类目id查询规格参数模板
     * @param itemCatId
     * @return
     */
    @RequestMapping(value = "{itemCatId}" , method = RequestMethod.GET)
    public ResponseEntity<TbItemParam>queryByItemCatId(@PathVariable("itemCatId")Long itemCatId){
        try {
            TbItemParam tbItemParam  = itemParamService.queryByItemCatId(itemCatId);
            if(null == tbItemParam){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(tbItemParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 根据商品类目id新增规格参数模板
     * @param itemCatId
     * @param paramData
     * @return
     */
    @RequestMapping(value = "{itemCatId}",method = RequestMethod.POST)
    public ResponseEntity<Void> saveItemParam(@PathVariable("itemCatId")Long itemCatId ,
              @RequestParam(value = "paramData" , required = false)String paramData){
        try {
            TbItemParam record = new TbItemParam();
            record.setId(null);
            record.setItemCatId(itemCatId);
            record.setCreated(new Date());
            record.setUpdated(record.getCreated());
            record.setParamData(paramData);
            itemParamService.saveItemParam(record);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
