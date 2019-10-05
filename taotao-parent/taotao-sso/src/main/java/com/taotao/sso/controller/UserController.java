package com.taotao.sso.controller;

import com.taotao.common.utils.CookieUtils;
import com.taotao.sso.pojo.TbUser;
import com.taotao.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService ;

    private static final String COOKIE_NAME = "TT_TOKEN";

    /**
     * 注册页面
     * @return
     */
    @RequestMapping (value = "register", method = RequestMethod.GET )
    public String toRegister(){
        return "register";
    }

    /**
     * 数据检测
     * @param param
     * @param type
     * @return
     */
    @RequestMapping(value = "check/{param}/{type}" ,method = RequestMethod.GET)
    public ResponseEntity<Boolean> check(@PathVariable("param")String param,
                                         @PathVariable ("type")Integer type){
        try {
            Boolean bool = userService .check(param , type );
            if(bool == null ){
                return ResponseEntity .status(HttpStatus.BAD_REQUEST ).body(null) ;
            }
            return ResponseEntity .status(HttpStatus.OK ).body(bool) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity .status(HttpStatus.INTERNAL_SERVER_ERROR  ).body(null) ;
    }


    /**
     * 注册
     * @param user
     * @return
     */
    @RequestMapping (value = "doRegister", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity <Map<String, Object>> doRegister(@Valid TbUser user, BindingResult bindingResult ){
        Map<String ,Object > result = new HashMap<>() ;
        try {

            if(bindingResult .hasErrors() ){
                //检验有错误
                List<String> msgs = new ArrayList();
                //获取所有错误
                List<ObjectError> objectErrors = bindingResult .getAllErrors() ;
                for(ObjectError objectError : objectErrors ){
                    msgs .add(objectError .getDefaultMessage()) ;
                }
                result .put("status", "400") ;
                //将每个错误信息之间用|分隔
                result .put("data", StringUtils .join(msgs ,'|')) ;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }

            Boolean bool = userService.doRegister(user);

            if(bool){
                result .put("status" , "200") ;
            }else {
                result .put("status" , "500") ;
                result .put("data", "注册失败") ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result .put("status" , "500") ;
            result .put("data", "注册失败") ;
        }
        return ResponseEntity .status(HttpStatus.OK).body(result) ;
    }



    /**
     * 登录页面
     * @return
     */
    @RequestMapping (value = "login", method = RequestMethod.GET )
    public String toLogin(){
        return "login";
    }

    /**
     * 登录
     * @return
     */
    @RequestMapping(value = "doLogin", method = RequestMethod.POST )
    @ResponseBody
    public Map<String, Object>doLogin(@RequestParam("username")String username,
                                      @RequestParam("password")String password,
                                      HttpServletResponse response,
                                      HttpServletRequest request ){
        Map <String, Object > result = new HashMap<>() ;
        try {
            String token = userService.doLogin(username ,password );
            if(null == token){
                //登录失败
                result .put("status", 400) ;
            }else{
                //登录成功，需要将token写入cookie中
                /* 写入cookie的时候需要注意：如果使用Nginx进行转发的话，tomcat只能知道转发的地址：127.0.0.1
                不知道实际请求的地址（sso.taotao.com），所以在写cookie的时候，需要将其写入taotao.com，
                但是这样是无法写入的,这样违反了浏览的安全的原则,（跨域）所以需要在Nginx中进行配置：host的代理头信息
                proxy_set_header Host $host;
                * */
                result .put("status", 200);
                CookieUtils.setCookie(request ,response , COOKIE_NAME, token );

            }
        } catch (Exception e) {
            e.printStackTrace();
            result .put("status", 500) ;
        }
        return result ;
    }

    /**
     * 根据token查询用户，显示当前登录人用户名
     * @param token
     * @return
     */
    @RequestMapping(value = "{token}", method = RequestMethod.GET)
    public ResponseEntity <TbUser > queryUserByToken(@PathVariable ("token")String token){
        try {
            TbUser user = userService .queryUserByToken(token);
            if(user == null ){
                //资源不存在
                return ResponseEntity .status(HttpStatus.NOT_FOUND ).body(null) ;
            }
            return ResponseEntity .status(HttpStatus.OK).body(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity .status(HttpStatus.INTERNAL_SERVER_ERROR  ).body(null) ;
    }

}
