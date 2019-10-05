<%--
  Created by IntelliJ IDEA.
  User: thinkpad
  Date: 2019/7/19
  Time: 10:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"
         pageEncoding="UTF-8"%>

    <%
        //接收前端传来的参数
        String callback = request.getParameter("callback");
        //后端返回js脚本，定义一个回调(fun)函数
        if(null == callback || "".equals(callback)){
            //说明不需要回调，正常输出数据
            out.print("fun({\"abc\":123})");
        }else{
            out.print(callback + "({\"abc\":123})");
        }

    %>


