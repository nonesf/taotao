package com.taotao.cart.controller.api;

import com.taotao.cart.pojo.TbCart;
import com.taotao.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("api/cart")
public class ApiCartController {

    @Autowired
    private CartService cartService ;

    /**
     * 对外提供服务接口，根据用户id查询购物车数据
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "{userId}",method = RequestMethod.GET)
    public ResponseEntity<List<TbCart>> queryCartListByUserId(@PathVariable("userId") Long userId){
        try {
            List<TbCart> cartList = cartService .queryCartList(userId);
            if(null == cartList || cartList.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(cartList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
