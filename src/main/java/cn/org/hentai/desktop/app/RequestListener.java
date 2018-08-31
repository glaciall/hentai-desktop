package cn.org.hentai.desktop.app;

import org.springframework.stereotype.Component;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by matrixy on 2018/5/16.
 */
@Component
public class RequestListener implements ServletRequestListener
{
    public void requestInitialized(ServletRequestEvent sre)
    {
        ((HttpServletRequest) sre.getServletRequest()).getSession();
    }

    public RequestListener()
    {

    }

    public void requestDestroyed(ServletRequestEvent arg0)
    {

    }
}