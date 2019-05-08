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
        pointer.style = Cursor.getDefaultCursor().getType();
        return pointer;
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
        // GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();//本地环境
        // GraphicsDevice[] gs = ge.getScreenDevices();
        System.out.println(getLocalIP());
    }
}
