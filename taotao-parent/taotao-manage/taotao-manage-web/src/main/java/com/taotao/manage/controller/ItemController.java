package com.taotao.manage.controller;

import com.mysql.jdbc.StringUtils;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.pojo.TbItem;
import com.taotao.manage.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("item")
public class ItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemService itemService;

    /**
     * @RequestParam :请求参数
     *
     * @param tbItem
     * @param desc
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> saveItem(TbItem tbItem , @RequestParam("desc")String desc ,
                                         @RequestParam("itemParams")String itemParams){
        try {
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("新增商品 ， item = {} ， desc = {}",tbItem , desc);
            }
            if(StringUtils.isNullOrEmpty(tbItem.getTitle())|| org.apache.commons.lang3.StringUtils.length(tbItem.getTitle())>100){
                //参数有误,400
                if(LOGGER.isInfoEnabled()){
                    LOGGER.info("新增商品参数不合法，item = {}，desc = {}",tbItem , desc);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            /**
             * 因为两个方法不在同一个事务中，存在事务问题，
             * 将其放在一个service中来进行事务,经测试，无法完成事务
             */
         /*   //初始值
            tbItem.setStatus((byte) 1);
            //出于安全考虑，强制设置id为null，通过数据库自增长得到
            tbItem.setId(null);
            Long itemId = itemService.saveItem(tbItem);
            itemService.saveItem(tbItem);

            TbItemDesc itemDesc = new TbItemDesc();
            itemDesc.setItemId(itemId);
            itemDesc.setItemDesc(desc);
            itemDescService.saveItem(itemDesc);*/

         //保存商品数据
            boolean bool = itemService.saveItem(tbItem, desc ,itemParams);
            if(! bool){
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("新增商品成功，id = {}", tbItem.getId());
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 后台查询商品列表
     * @param page  当前页
     * @param rows  显示的条数
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<EasyUIResult> queryItemList(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "30")Integer rows){
        try {
            EasyUIResult easyUIResult = itemService.queryItemList(page , rows);
            return ResponseEntity.ok(easyUIResult);
        } catch (Exception e) {
            e.printStackTrace();
            //打印日志
            LOGGER.error("查询商品失败，page = " + page + ",rows = " + rows);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 商品编辑提交
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Void> updateItem(TbItem item ,@RequestParam("desc")String desc,
                                           @RequestParam("itemParams")String itemParams){
        try {
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("编辑商品，item ={}，desc ={}",item , desc);
            }
            if(org.apache.commons.lang3.StringUtils.isEmpty(item.getTitle()) ||
                    org.apache.commons.lang3.StringUtils.length(item.getTitle())>100){
                //参数不合法,400
                if(LOGGER.isInfoEnabled()){
                    LOGGER.info("编辑商品参数不合法，item = {} ，desc ={}" ,item ,desc);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            //保存商品数据
            Boolean bool = itemService.updateItem(item , desc , itemParams);
            if(bool){
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("编辑商品成功，id = {}" , item.getId());
                }
                //204
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            LOGGER.error("编辑商品失败! item = " + item + ", desc = " + desc , e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
