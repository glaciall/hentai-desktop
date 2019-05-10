package cn.org.hentai.desktop.system;

import java.awt.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by matrixy on 2018/4/9.
 */
public final class LocalComputer
{
    static Robot robot = null;
    static String osname = "unknown";

    /**
     * 创建整屏截图
     * @return
     */
    public static Screenshot captureScreen()
    {
        return new Screenshot(robot.createScreenCapture(getScreenSize()));
    }

    public static Pointer getPointer()
    {
        PointerInfo pinfo = MouseInfo.getPointerInfo();
        Point p = pinfo.getLocation();
        Pointer pointer = new Pointer();
        pointer.x = (int)p.getX();
        pointer.y = (int)p.getY();
        pointer.style = getCursorStyle();
        return pointer;
    }

    private static int getCursorStyle()
    {
        if (osname.startsWith("Windows") == false) return 0;
        User32 user32 = User32.INSTANCE;
        User32.CURSORINFO cursorinfo = new User32.CURSORINFO();
        int success = user32.GetCursorInfo(cursorinfo);
        if (success != 1) return 0;
        int style = 0;
        if (cursorinfo.hCursor == null) return 0;
        int val = Integer.parseInt(cursorinfo.hCursor.toNative().toString().replaceAll("native@0x", ""), 16);
        switch(val)
        {
            case 65545 : style = 1; break;      // crosshair
            case 65541 : style = 2; break;      // text
            case 65539 : style = 0; break;      // default
            case 65567 : style = 12; break;     // hand
            case 1902083 : style = 12; break;   // hand
            case 65555 : style = 8; break;      // n-resize
            case 65549 : style = 6; break;      // nw-resize
            case 65551 : style = 4; break;      // sw-resize
            case 65553 : style = 11; break;     // e-resize
            case 65557 : style = 13; break;     // move
            case 65543 : style = 3; break;      // wait
            case 65559 : style = 0; break;      // not-allowed
            case 7932567 : style = 0; break;    // grab
            case 65561 : style = 3; break;      // progress
            case 2688043 : style = 0; break;    // zoom-in
            case 132773 : style = 0; break;     // zoom-out
        }
        return style;
    }



    /**
     * 获取屏幕分辨率
     * @return
     */
    public static Rectangle getScreenSize()
    {
        return new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    }

    public static void init()
    {
        try
        {
            robot = new Robot();
            osname = System.getProperty("os.name");
        }
        catch(AWTException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public static ArrayList<String> getLocalIP()
    {
        ArrayList<String> ipAddresses = new ArrayList<String>();
        try
        {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements())
            {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements())
                {
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":")==-1)
                    {
                        ipAddresses.add(ip.getHostAddress());
                    }
                }
            }

            return ipAddresses;
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception
    {
        for (int i = 0; i < 10000; i++)
        {
            int cursor = getCursorStyle();
            Thread.sleep(1000);
        }
    }
}
