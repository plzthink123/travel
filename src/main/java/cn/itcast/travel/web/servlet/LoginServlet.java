package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.获取用户名和密码数据
        Map<String, String[]> map = request.getParameterMap();

        //2.封装为user对象
        User user=new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用service查询
        UserService service=new UserServiceImpl();
        User u=service.login(user);

        ResultInfo info=new ResultInfo();
        //判断用户名是否为null
        if(u==null){
            //用户名或密码错误
             info.setFlag(false);
             info.setErrorMsg("用户名或密码错误!");
        }
        //判断用户是否激活
        if(u!=null && u.getStatus().equals("N")){
            info.setFlag(false);
            info.setErrorMsg("您尚未激活,请登录邮箱激活!");
        }
        //登陆成功判断
        if(u!=null && u.getStatus().equals("Y")){
            request.getSession().setAttribute("user",u);//登录成功标记
            //登陆成功
            info.setFlag(true);
        }
        //相应数据
        ObjectMapper mapper=new ObjectMapper();
        String s = mapper.writeValueAsString(info);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
