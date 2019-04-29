package cn.org.hentai.desktop.wss;

import cn.org.hentai.desktop.system.Pointer;
import cn.org.hentai.desktop.system.Screenshot;
import cn.org.hentai.desktop.util.Log;
import com.google.gson.JsonObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by matrixy on 2018/8/31.
 */
public class WSSession
{
    DesktopWSS connection;
    Pointer lastPointer;

    public WSSession(DesktopWSS connection)
    {
        this.connection = connection;
    }

    public int getId()
    {
        return connection.getId();
    }

    // 下发屏幕画面
    public boolean sendScreenshot(byte[] screenshot)
    {
        return connection.sendScreenshot(screenshot);
    }

    // 下发鼠标指针信息
    public void sendPointerInfo(Pointer pointer)
    {
        if (pointer.equals(lastPointer)) return;
        sendJson("action", "pointer", "x", pointer.x, "y", pointer.y, "style", pointer.style);
        lastPointer = pointer;
    }

    // 下发控制信息
    public void sendControlResponse(int compressMethod, int bandWidth, int colorBits, int screenWidth, int screenHeight)
    {
        sendJson("action", "setup", "compressMethod", compressMethod, "bandWidth", bandWidth, "colorBits", colorBits, "screenWidth", screenWidth, "screenHeight", screenHeight);
    }

    private void sendJson(Object...args)
    {
        JsonObject json = new JsonObject();
        for (int i = 0; i < args.length; i += 2)
        {
            Object val = args[i + 1];
            String key = (String)args[i];
            if (val instanceof Number) json.addProperty(key, (Number)val);
            else if (val instanceof String) json.addProperty(key, (String)val);
            else json.addProperty(key, (String)val);
        }
        // 发送
        connection.sendText(json.toString());
    }

    public DesktopWSS getConnection()
    {
        return this.connection;
    }

    public String getRemoteAddr()
    {
        return this.connection.getRemoteAddr();
    }
}
