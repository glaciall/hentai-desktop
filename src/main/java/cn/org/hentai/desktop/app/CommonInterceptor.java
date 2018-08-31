package cn.org.hentai.desktop.app;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by matrixy on 2017/8/26.
 */
public class CommonInterceptor extends HandlerInterceptorAdapter
{
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception
    {
        // 项目环境变量
        String path = request.getContextPath();
        if (!path.endsWith("/")) path = path + "/";
        request.setAttribute("context", "/".equals(path) ? "" : path);
        request.setAttribute("web_resource", path + "static/");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception
    {
        // do nothing here...
    }
}
