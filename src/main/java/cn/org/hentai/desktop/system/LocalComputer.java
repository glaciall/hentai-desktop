package cn.org.hentai.desktop.system;

import java.awt.*;

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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Rectangle((int)screenSize.getWidth(), (int)screenSize.getHeight());
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

    public static void main(String[] args) throws Exception
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();//本地环境
        GraphicsDevice[] gs = ge.getScreenDevices();
    }
}
