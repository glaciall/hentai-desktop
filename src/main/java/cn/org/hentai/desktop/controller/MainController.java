package cn.org.hentai.desktop.controller;

import cn.org.hentai.desktop.model.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

/**
 * Created by matrixy on 2018/4/12.
 */
@RequestMapping("/")
@Controller
public class MainController
{
    @RequestMapping("/")
    public String index(HttpSession session, HttpServletRequest request)
    {
        session.setMaxInactiveInterval(60);
        session.setAttribute("remote-addr", request.getRemoteAddr());
        return "index";
    }

    @RequestMapping("/keepalive")
    @ResponseBody
    public Result keepalive()
    {
        // 什么都不做，只是为了保持会话
        return new Result();
    }

    private void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(Exception e) { }
    }
}
