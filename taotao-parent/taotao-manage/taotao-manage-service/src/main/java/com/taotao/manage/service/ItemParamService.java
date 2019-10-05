package com.taotao.manage.service;

import com.taotao.manage.mapper.TbItemParamMapper;
import com.taotao.manage.pojo.TbItemParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemParamService {
    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    public void saveItemParam(TbItemParam record) {
        tbItemParamMapper.insert(record);
    }


    public TbItemParam queryByItemCatId(Long itemCatId){
        return tbItemParamMapper.selectByKey(itemCatId);
    }
}
