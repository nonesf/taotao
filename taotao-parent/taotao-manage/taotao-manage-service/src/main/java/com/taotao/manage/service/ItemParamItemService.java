package com.taotao.manage.service;

import com.taotao.manage.mapper.TbItemParamItemMapper;
import com.taotao.manage.pojo.TbItemParamItem;
import com.taotao.manage.pojo.TbItemParamItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ItemParamItemService {
    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;
    public Integer saveItem(TbItemParamItem tbItemParamItem){
        //保存商品规格参数
        tbItemParamItem.setCreated(new Date());
        tbItemParamItem.setUpdated(tbItemParamItem.getCreated());
        return tbItemParamItemMapper.insert(tbItemParamItem);
    }

    //根据商品id查询商品规格参数
    public TbItemParamItem queryByItemId(Long itemId){
        return tbItemParamItemMapper.selectByKey(itemId);
    }

    //更新商品规格参数数据
    public Integer updateItemParamItem(Long itemId , String itemParams ){
        //更新数据
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setUpdated(new Date());
        tbItemParamItem.setParamData(itemParams);

        //设置更新条件
        TbItemParamItemExample example  = new TbItemParamItemExample();
        example.createCriteria().andItemIdEqualTo(itemId);
        return tbItemParamItemMapper.updateByExampleSelective(tbItemParamItem,example);
    }
}
