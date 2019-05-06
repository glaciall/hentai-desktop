package cn.org.hentai.desktop.wss;

import cn.org.hentai.desktop.app.CLI;
import cn.org.hentai.desktop.system.Pointer;
import cn.org.hentai.desktop.system.Screenshot;
import cn.org.hentai.desktop.util.Log;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by matrixy on 2018/8/31.
 */
public final class WSSessionManager
{
    ConcurrentHashMap<Integer, WSSession> sessions = new ConcurrentHashMap<Integer, WSSession>(10);

    public int register(DesktopWSS connection)
    {
        sessions.put(connection.getId(), new WSSession(connection));
        try { CLI.resetScreen(); } catch(Exception e) { }
        return connection.getId();
    }

    public void unregister(DesktopWSS connection)
    {
        sessions.remove(connection.hashCode());
    }

    public WSSession[] list()
    {
        return sessions.values().toArray(new WSSession[0]);
    }

    // 广播屏幕画面，发送到每一个活动的连接上去
    public void broadcast(byte[] screenshot, Pointer pointer)
    {
        Iterator<WSSession> itr = sessions.values().iterator();
        while (itr.hasNext())
        {
            WSSession session = itr.next();
            try
            {
                if (screenshot != null)
                {
                    boolean result = session.sendScreenshot(screenshot);
                    if (result == false)
                    {
                        itr.remove();
                    }
                }
                if (pointer != null) session.sendPointerInfo(pointer);
            }
            catch(Exception ex)
            {
                // Log.error(ex);
                // System.out.println(ex.toString());
            }
        }
    }

    private static WSSessionManager instance;
    private WSSessionManager()
    {
        // ..
    }

    public static synchronized WSSessionManager getInstance()
    {
        if (null == instance) instance = new WSSessionManager();
        return instance;
    }

    public void kick(int id)
    {
        WSSession session = sessions.remove(id);
        if (session != null)
        {
            // TODO: 应该限定目标IP在一定的时间内不能再连接进来
            try
            {
                session.getConnection().shutdown();
            }
            catch(Exception ex) { }
        }
    }
}
