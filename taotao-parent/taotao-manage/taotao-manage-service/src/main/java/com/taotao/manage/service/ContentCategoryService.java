package com.taotao.manage.service;

import com.taotao.manage.mapper.TbContentCategoryMapper;
import com.taotao.manage.pojo.TbContentCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;


    public List<TbContentCategory> queryListByParentId(TbContentCategory contentCategory) {
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(contentCategory.getCreated());
        return contentCategoryMapper.queryListByParentId(contentCategory);
    }


    public void saveContentCategory(TbContentCategory contentCategory) {
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(contentCategory.getCreated());
        contentCategory.setId(null);
        contentCategory.setIsParent(false);
        contentCategory.setSortOrder(1);
        contentCategory.setStatus(1);
        //新增子节点
        contentCategoryMapper.insert(contentCategory);


        //判断该节点的父节点的IsParent是否为true，如果不是,修改为true
        //该节点的父节点id就是父节点的id
        TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(contentCategory.getParentId());
        if(!parent.getIsParent()){
            parent.setIsParent(true);
            contentCategoryMapper.updateByPrimaryKey(parent);
        }
    }


    public void rename(TbContentCategory contentCategory) {
        //updateByPrimaryKeySelective：根据不为null的条件进行修改
        //updateByPrimaryKey:所有字段全部修改
        contentCategory.setUpdated(new Date());
        contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
    }


    public void deleteAll(TbContentCategory contentCategory) {
        //装所有的需要删除的节点id
        List<Object> ids = new ArrayList<Object>();
        ids.add(contentCategory.getId());

        //递归查询该节点的所有子节点,传递的参数为ids 、 该节点的id
        findAllSubNode(ids , contentCategory.getId());

        System.out.println(ids.size());
        //删除节点
        contentCategoryMapper.deleteByIds(ids);

        //判断该节点是否还有兄弟节点，如果没有，将父节点的IsParent设为false
        TbContentCategory contentCategory1 = new TbContentCategory();
        //contentCategory只是用来设置查询条件
        contentCategory1.setParentId(contentCategory.getParentId());
        List<TbContentCategory> list = contentCategoryMapper.queryListByParentId(contentCategory1);
        if(list == null || list.isEmpty()){
            TbContentCategory parent = new TbContentCategory();
            parent.setParentId(contentCategory.getParentId());
            parent.setIsParent(false);
            contentCategoryMapper.updateByPrimaryKeySelective(parent);
        }
    }

    public void findAllSubNode(List<Object> ids , Long pid){
        //设置条件
        TbContentCategory record = new TbContentCategory();
        record.setParentId(pid);
        //根据条件查询该节点下的所有子节点
        List <TbContentCategory> list = contentCategoryMapper.queryListByParentId(record);
        for(TbContentCategory contentCategory : list){
            //把子节点id加入ids
            ids.add(contentCategory.getId());
            //判断该节点是否为父节点，若是，继续调用此方法查找其子节点
            if(contentCategory.getIsParent()){
                //开始递归
                findAllSubNode(ids , contentCategory.getId());
            }
        }
    }
}
