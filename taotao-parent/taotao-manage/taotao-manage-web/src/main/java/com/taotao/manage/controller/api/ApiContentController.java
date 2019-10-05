package com.taotao.manage.controller.api;

import com.taotao.common.bean.EasyUIResult;

import com.taotao.manage.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("api/content")
public class ApiContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 根据categoryId查询内容列表，并按照更新时间顺序倒序排列
     * @param categoryId
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<EasyUIResult>queryListByCategoryId(@RequestParam("categoryId")Long categoryId ,
                                                             @RequestParam(value = "page" , defaultValue = "1") Integer page ,
                                                             @RequestParam(value = "rows" , defaultValue = "15") Integer rows ){
        try {
            EasyUIResult result = contentService.queryListByCategoryId(categoryId  , page , rows);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
