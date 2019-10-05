package com.taotao.order.mapper;

import java.util.Date;


import com.taotao.order.pojo.Order;
import org.apache.ibatis.annotations.Param;


public interface OrderMapper extends IMapper<Order>{
	
	public void paymentOrderScan(@Param("date") Date date);

}
