package cn.org.hentai.desktop.wss;

import cn.org.hentai.desktop.app.CLI;
import cn.org.hentai.desktop.app.GetHttpSessionConfigurator;
import cn.org.hentai.desktop.util.Configs;
import cn.org.hentai.desktop.util.Log;
import cn.org.hentai.desktop.util.Packet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by matrixy on 2018/4/12.
 */
@Component
@ServerEndpoint(value = "/desktop/wss", configurator = GetHttpSessionConfigurator.class)
public class DesktopWSS
{
    Session session;
    // RDSession rdSession = null;
    HttpSession httpSession = null;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config)
    {
        System.out.println("websocket opened: " + session);
        this.session = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
    }

    @OnMessage
    public void onMessage(String message, Session session)
    {
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
        String type = json.get("type").getAsString();
        if ("command".equals(type))
        {
            String cmd = json.get("command").getAsString();
            if ("login".equals(cmd))
            {
                // TODO: 身份校验
                if (!CLI.getPassword().equals(json.get("password").getAsString()))
                {
                    this.sendResponse("login", "密码错误");
                    return;
                }
                this.httpSession.setAttribute("isLogin", true);
                this.sendResponse("login", "success");
                requestDesktopSharing();
            }
        }
    }

    private void requestDesktopSharing()
    {
        try
        {
            WSSessionManager.getInstance().register(this);
            this.sendResponse("request-control", "success");

            // TODO: 需要发送最近的完整画面，以及最近一次的压缩祯
        }
        catch(Exception ex)
        {
            this.sendResponse("request-control", ex.getMessage());
        }
    }

    // 下发控制响应
    public void sendControlResponse(int compressMethod, int bandWidth, int colorBits, int screenWidth, int screenHeight)
    {
        // sendText("{ \"action\" : \"setup\", \"compressMethod\" : " + compressMethod + ", \"bandWidth\" : " + bandWidth + ", \"colorBits\" : " + colorBits + ", \"screenWidth\" : " + screenWidth + ", \"screenHeight\" : " + screenHeight + " }");
    }

    // 下发屏幕截图
    public void sendScreenshot(byte[] screenshot)
    {
        try
        {
            if (!this.session.isOpen()) throw new Exception("websocket was closed");
            sendBinary(screenshot);
        }
        catch(Exception e)
        {
            Log.error(e);
        }
    }

    private void sendResponse(String action, String result)
    {
        JsonObject resp = new JsonObject();
        resp.addProperty("action", action);
        resp.addProperty("result", result);
        sendText(resp.toString());
    }

    public void sendText(String text)
    {
        try
        {
            this.session.getBasicRemote().sendText(text);
        }
        catch(IOException e)
        {
            try { this.session.close(); } catch(Exception ex) { }
        }
    }

    private void sendBinary(byte[] data)
    {
        try
        {
            this.session.getBasicRemote().sendBinary(ByteBuffer.wrap(data));
        }
        catch(IOException e)
        {
            try { this.session.close(); } catch(Exception ex) { }
        }
    }

    @OnClose
    public void onClose()
    {
        System.out.println("websocket closed...");
        this.httpSession.removeAttribute("isLogin");
        WSSessionManager.getInstance().unregister(this);
    }

    @OnError
    public void onError(Session session, Throwable ex)
    {
        ex.printStackTrace();
    }
}
