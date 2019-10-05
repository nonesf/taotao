package com.taotao.cart.service;

import com.taotao.cart.bean.TbItem;
import com.taotao.cart.bean.TbUser;
import com.taotao.cart.mapper.TbCartMapper;
import com.taotao.cart.pojo.TbCart;
import com.taotao.cart.pojo.TbCartExample;
import com.taotao.cart.threaLocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private TbCartMapper cartMapper;
    @Autowired
    private ItemService itemService ;

    public void addItemToCart(Long itemId ,Integer num){

        //判断购物车中是否存在该商品
        TbUser user = UserThreadLocal .get();
        TbCart record = new TbCart() ;
        record .setItemId(itemId );
        record .setUserId(user.getId());
        TbCart cart = cartMapper.selectByRecord(record);

        if(null == cart){
            //商品不存在，直接将商品存入
            cart = new TbCart();
            cart.setItemId(itemId);
            cart.setUserId(user.getId());
            cart.setCreated(new Date());
            cart.setUpdated(cart.getCreated());
            cart.setNum(num);//默认数量为1

            //通过后台系统查询商品的基本数据
            TbItem item = itemService.queryItemById(itemId);
            cart.setItemTitle(item.getTitle());
            cart.setItemPrice(item.getPrice());
            cart.setItemImage(StringUtils.split(item.getImage(),",")[0]);
            //保存到数据库
            cartMapper.insert(cart);
        }else{
            //商品存在
            cart.setNum(cart.getNum() + num);
            cart.setUpdated(new Date());
            cartMapper.updateByPrimaryKeySelective(cart);
        }

    }

    /**
     * 根据用户id查询购物车数据
     *
     * @param userId
     * @return
     */
    public List<TbCart> queryCartList(Long userId) {
        TbCartExample example = new TbCartExample();

        //设置排序条件
        example.setOrderByClause("created DESC");
        //设置查询条件
        example.createCriteria().andUserIdEqualTo(userId);
        return cartMapper.selectByExample(example);
    }

    /**
     * 查询购物车商品列表
     * @return
     */
    public List<TbCart> queryCartList() {
        return queryCartList(UserThreadLocal.get().getId());
    }

    /**
     * 修改商品数量
     *
     * @param itemId
     * @param num
     */
    public void updateNum(Long itemId, Integer num) {

        //设置更新条件
        TbCartExample example = new TbCartExample();
        example.createCriteria().andUserIdEqualTo(UserThreadLocal.get().getId()).andItemIdEqualTo(itemId);

        //设置更新内容
        TbCart record = new TbCart() ;
        record.setNum(num);
        record.setUpdated(new Date());

        //执行更新
        cartMapper.updateByExampleSelective(record,example);
    }

    public void deleteItem(Long itemId) {
        //实现物理删除,根据itemId 和 userId 删除对应的商品
        TbCart record = new TbCart() ;
        record.setUserId(UserThreadLocal.get().getId());
        record.setItemId(itemId);
        cartMapper.deleteItem(record);
    }

    /**
     * 合并购物车
     * @return
     */
    public void magerCartList(List<TbCart> cartList){
        //遍历cookie购物车列表，将其加入购物车
        for(TbCart cart : cartList){
            addItemToCart(cart.getItemId(),cart.getNum());
        }
    }


}
