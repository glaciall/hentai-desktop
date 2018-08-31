package cn.org.hentai.desktop.wss;

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

    public void register(DesktopWSS connection)
    {
        sessions.put(connection.hashCode(), new WSSession(connection));
    }

    public void unregister(DesktopWSS connection)
    {
        sessions.remove(connection.hashCode());
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
                session.sendScreenshot(screenshot);
                session.sendPointerInfo(pointer);
            }
            catch(Exception ex)
            {
                Log.error(ex);
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
}
