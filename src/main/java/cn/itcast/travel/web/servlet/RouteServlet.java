package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {
    private RouteService routeService = new RouteServiceImpl();
    /**
     * 分页查询
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void pageQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.接受参数
        String currentPageStr = request.getParameter("currentPage");
        String pageSizeStr = request.getParameter("pageSize");
        String cidStr = request.getParameter("cid");
        //接受rname参数
        String rname = request.getParameter("rname");
        rname=new String(rname.getBytes("iso-8859-1"),"utf-8");

        //2.处理参数
        int cid=0;  //cid
        if(cidStr!=null&&cidStr.length()>0){
             cid=Integer.parseInt(cidStr);
        }
        int currentPage=0;  //当前页码,如果不传递,则默认值为1
        if(currentPageStr!=null&&currentPageStr.length()>0 && !"null".equals(cidStr)){
            currentPage=Integer.parseInt(currentPageStr);
        }else{
            currentPage=1;
        }
        int pageSize=0;  //每页显示条数,如果不传递则默认值为5
        if(pageSizeStr!=null&&pageSizeStr.length()>0){
            pageSize=Integer.parseInt(pageSizeStr);
        }else {
            pageSize=5;
        }

         //3. 调用service查询PageBean对象
        PageBean<Route> pb = routeService.pageQuery(cid, currentPage, pageSize,rname);

        //4. 将pageBean对象序列化为json，返回
        writeValue(pb,response);
    }

    public void findOne(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //1.获取参数
        String rid = request.getParameter("rid");
        //2.调用service
        Route route=routeService.findOne(rid);
        //3.封装为json
        writeValue(route,response);
    }
}