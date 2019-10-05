package com.taotao.cart.mapper;

import com.taotao.cart.pojo.TbCart;
import com.taotao.cart.pojo.TbCartExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbCartMapper {
    long countByExample(TbCartExample example);

    int deleteByExample(TbCartExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbCart record);

    int insertSelective(TbCart record);

    List<TbCart> selectByExample(TbCartExample example);

    TbCart selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbCart record, @Param("example") TbCartExample example);

    int updateByExample(@Param("record") TbCart record, @Param("example") TbCartExample example);

    int updateByPrimaryKeySelective(TbCart record);

    int updateByPrimaryKey(TbCart record);

    TbCart selectByRecord(TbCart record);

    int deleteItem(TbCart record);
}