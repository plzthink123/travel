package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    private  UserService service = new UserServiceImpl();
    /**
     *  注册功能
     */
    public void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //验证码校验
        String check = request.getParameter("check");
        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");//为了保证验证码只能使用一次
        if (checkcode_server == null || !checkcode_server.equalsIgnoreCase(check)) {
            //验证码错误
            ResultInfo info = new ResultInfo();
            info.setErrorMsg("验证码错误!");
            info.setFlag(false);
           /* ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            //设置cotent-type
            response.setContentType("application/json;charset=utf-8");
            //json写回客户端
            response.getWriter().write(json);*/
           writeValue(info,response);
            return;
        }

        //1.获取数据
        Map<String, String[]> map = request.getParameterMap();

        //2.封装对象
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用service,查询
       // UserService service = new UserServiceImpl();
        boolean flag = service.regist(user);
        ResultInfo info = new ResultInfo();
        //4.响应结果
        if (flag) {
            info.setFlag(true);
            //success
        } else {
            info.setFlag(false);
            info.setErrorMsg("注册失败!");
        }
        //将info序列化为json
       /* ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        //设置cotent-type
        response.setContentType("application/json;charset=utf-8");
        //json写回客户端
        response.getWriter().write(json);*/
       writeValue(info,response);
    }
    /**
     * 登陆功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.获取用户名和密码数据
        Map<String, String[]> map = request.getParameterMap();

        //2.封装为user对象
        User user = new User();

        try {
            BeanUtils.populate(user, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用service查询
        // service = new UserServiceImpl();
        User u = service.login(user);

        ResultInfo info = new ResultInfo();
        //判断用户名是否为null
        if (u == null) {
            //用户名或密码错误
            info.setFlag(false);
            info.setErrorMsg("用户名或密码错误!");
        }
        //判断用户是否激活
        if (u != null && u.getStatus().equals("N")) {
            info.setFlag(false);
            info.setErrorMsg("您尚未激活,请登录邮箱激活!");
        }
        //登陆成功判断
        if (u != null && u.getStatus().equals("Y")) {
            request.getSession().setAttribute("user", u);//登录成功标记
            //登陆成功
            info.setFlag(true);


        }
        //相应数据
       /* ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(info);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);*/
       writeValue(info,response);
    }
    /**
     * 查找一个用户
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Object user = request.getSession().getAttribute("user");
       // System.out.println(user);
        ObjectMapper mapper=new ObjectMapper();
        String s = mapper.writeValueAsString(user);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
       // System.out.println(s);


    }
    /**
     * 退出功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.销毁session
        request.getSession().invalidate();
        //2.跳转
        response.sendRedirect(request.getContextPath()+"/login.html");

    }
    /**
     * 激活用户
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.获取激活码
        String code = request.getParameter("code");
        if(code != null){
            //2.调用service完成激活
            //UserService service = new UserServiceImpl();
            boolean flag = service.active(code);

            //3.判断标记
            String msg = null;
            if(flag){
                //激活成功
                msg = "激活成功，请<a href="+request.getContextPath()+"/login.html>登录</a>";
            }else{
                //激活失败
                msg = "激活失败，请联系管理员!";
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }
}
