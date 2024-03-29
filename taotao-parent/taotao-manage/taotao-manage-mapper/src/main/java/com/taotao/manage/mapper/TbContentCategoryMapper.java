package com.taotao.manage.mapper;

import com.taotao.manage.pojo.TbContentCategory;
import com.taotao.manage.pojo.TbContentCategoryExample;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface TbContentCategoryMapper {
    long countByExample(TbContentCategoryExample example);

    int deleteByExample(TbContentCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbContentCategory record);

    int insertSelective(TbContentCategory record);

    List<TbContentCategory> selectByExample(TbContentCategoryExample example);

    TbContentCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbContentCategory record, @Param("example") TbContentCategoryExample example);

    int updateByExample(@Param("record") TbContentCategory record, @Param("example") TbContentCategoryExample example);

    int updateByPrimaryKeySelective(TbContentCategory record);

    int updateByPrimaryKey(TbContentCategory record);

    List<TbContentCategory> queryListByParentId(TbContentCategory record);

    int deleteByIds(List<Object> ids);

}