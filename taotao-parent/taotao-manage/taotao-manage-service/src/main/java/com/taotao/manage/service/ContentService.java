package com.taotao.manage.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.mapper.TbContentMapper;
import com.taotao.manage.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    public void saveContent(TbContent content) {
        content.setId(null);
        content.setCreated(new Date());
        content.setUpdated(content.getCreated());
        contentMapper.insertSelective(content);
    }


    public EasyUIResult queryListByCategoryId(Long categoryId, Integer page, Integer rows) {
        PageHelper.startPage(page , rows);
        List<TbContent> contents = contentMapper.selectByCategoryId(categoryId);
        PageInfo<TbContent> pageInfo = new PageInfo<TbContent>(contents);
        return new EasyUIResult(pageInfo.getTotal() , pageInfo.getList());
    }
}
