package cn.itcast.travel.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CharchaterFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        //将父接口转为子接口
        HttpServletRequest request= (HttpServletRequest) req;
        HttpServletResponse response= (HttpServletResponse) resp;
        //获取请求方法
        String method = request.getMethod();
        if(method.equalsIgnoreCase("post")){
            request.setCharacterEncoding("utf-8");
        }
      /*  if(method.equalsIgnoreCase("get")){
            myEncodingRequest enRequest=new myEncodingRequest(request);
        }*/
        response.setContentType("text/html;charset=utf-8");
        chain.doFilter(request,response);
    }

    public void init(FilterConfig config) throws ServletException {

    }
}
    /**
     * 装饰者模式
     *  要求:
     *      1.增强类与被增强类实现同一个接口
     *      2.在增强类中传入被增强的类
     *      3.需要增强的方法重写,不需要增强的方法调用被增强对象的
     */
/*class myEncodingRequest extends HttpServletRequestWrapper {
    private HttpServletRequest request;
    public myEncodingRequest(HttpServletRequest request) {
        super(request);
        this.request=request;//将需要增强的request赋值给本类的request
    }
    //对getParameter进行增强
        @Override
        public String getParameter(String name) {
            String parameter = request.getParameter(name);//乱码
            try {
                parameter=new String(parameter.getBytes("iso8859-1"),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return parameter;
        }
    //对getParameterMap增强
        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> map = request.getParameterMap();
            if(map==null){
                return super.getParameterMap();
            }else{
                Set<String> set = map.keySet();
                for(String key:set){
                    String[] strings = map.get(key);
                    for(int i =0;i< strings.length;i++){
                        try {
                            strings[i]=new String(strings[i].getBytes("iso-8859-1"),"utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return map;
            }

        }
    }*/
