package com.taotao.manage.controller;

import com.taotao.manage.pojo.TbContentCategory;
import com.taotao.manage.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("content/category")
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    /**
     * 根据父节点id查询内容分类列表
     *
     * 因为是树形结构所以返回值存储在list中
     * 需要在表单中获取id值，判断在哪个节点先进行操作
     * @param parentId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET )
    public ResponseEntity<List<TbContentCategory>>queryListByParentId
            (@RequestParam(value = "id" , defaultValue = "0")Long parentId){
        try {
            TbContentCategory contentCategory = new TbContentCategory();
            contentCategory.setParentId(parentId);
            List<TbContentCategory> contentCategories = contentCategoryService.queryListByParentId(contentCategory);
            if(contentCategories == null || contentCategories.isEmpty()){
                //404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(contentCategories);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 新增子节点
     *
     * 在新增的前端程序中需要返回一个data，故返回值不为空
     * 前端需要传递两个参数，故传递contentCategory对象可直接接收那两个参数
     * 保证事务：将操作放在同一个service的同一个方法中
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TbContentCategory> saveContentCategory(TbContentCategory contentCategory){
        try {
            contentCategoryService.saveContentCategory(contentCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(contentCategory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 根据节点id重命名
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Void> rename(@RequestParam("id")Long id , @RequestParam("name")String name){
        try {
            TbContentCategory contentCategory = new TbContentCategory();
            contentCategory.setId(id);
            contentCategory.setName(name);
            contentCategoryService.rename(contentCategory);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 删除所有节点，包含其子节点，通过递归的方式查询其所有子节点
     * @param contentCategory
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void>delete(TbContentCategory contentCategory){
        try {
            contentCategoryService.deleteAll(contentCategory);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
