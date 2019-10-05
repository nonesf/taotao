package com.taotao.manage.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.ItemCatResult;

import com.taotao.manage.service.ItemCatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("api/item/cat")
public class ApiItemCatController {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private ItemCatService itemCatService;

    /**
     * 对外提供接口查询商品类目
     * @return
     */
/*
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> queryItemCatList(
            @RequestParam(value = "callback" , required = false)String callback){
        try {
            //查询所有的类目，并生成树形结构
            ItemCatResult itemCatResult = itemCatService .queryAllToTree();

            if(null == itemCatResult){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            //将java对象转换成json数据
            String result = MAPPER.writeValueAsString(itemCatResult);

            if(StringUtils.isEmpty(callback)){
                //无需跨域支持
                return ResponseEntity.ok(result);
            }else{
                //需要跨域支持
                return ResponseEntity.ok(callback + "(" + result + ");");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
*/


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<ItemCatResult> queryItemCatList(){
        try {
            //查询所有的类目，并生成树形结构
            ItemCatResult itemCatResult = itemCatService .queryAllToTree();

            return ResponseEntity.ok(itemCatResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
