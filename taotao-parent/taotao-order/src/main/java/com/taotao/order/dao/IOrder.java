package com.taotao.order.dao;

import com.taotao.order.pojo.Order;
import com.taotao.order.pojo.PageResult;
import com.taotao.order.pojo.ResultMsg;

/**
 * 订单DAO接口
 */
public interface IOrder {

    /**
     * 创建订单
     */
    public void createOrder(Order order );

    /**
     * 根据订单Id查询订单
     */

    public Order queryOrderById(String orderId);

    /**
     * 根据用户分页查询订单信息
     *
     *@param buyerNick 买家昵称，用户名
     * @param page 分页起始取数位置
     * @param count 查询数据条数
     * @return 分页结果集
     */

    public PageResult<Order> queryOrderByUserNameAndPage(String buyerNick, Integer page, Integer count);

    /**
     * 更改订单状态，有service层控制修改逻辑
     * @param order
     * @return
     */
    public ResultMsg changeOrderStatus(Order order );
}
