package cn.org.hentai.desktop.system.worker;

import cn.org.hentai.desktop.system.Screenshot;
import cn.org.hentai.desktop.util.Packet;

import java.util.LinkedList;

/**
 * Created by matrixy on 2018/4/9.
 */
public final class ScreenImages
{
    static LinkedList<Screenshot> screenshotImages = new LinkedList<Screenshot>();
    static LinkedList<Packet> compressedScreens = new LinkedList<Packet>();

    // 清空缓存
    public static void clear()
    {
        synchronized (screenshotImages)
        {
            screenshotImages.clear();
        }
        synchronized (compressedScreens)
        {
            compressedScreens.clear();
        }
    }

    // 原始截图相关
    public static void addScreenshot(Screenshot screenshot)
    {
        synchronized (screenshotImages)
        {
            screenshotImages.addLast(screenshot);
        }
    }

    public static Screenshot getScreenshot()
    {
        synchronized (screenshotImages)
        {
            if (screenshotImages.size() == 0) return null;
            return screenshotImages.removeFirst();
        }
    }

    public static boolean hasScreenshots()
    {
        return screenshotImages.size() > 0;
    }

    // 压缩后的图像数据相关
    public static boolean hasCompressedScreens()
    {
        return compressedScreens.size() > 0;
    }

    public static void addCompressedScreen(Packet packet)
    {
        synchronized (compressedScreens)
        {
            compressedScreens.addLast(packet);
        }
    }

    public static Packet getCompressedScreen()
    {
        synchronized (compressedScreens)
        {
            if (compressedScreens.size() == 0) return null;
            return compressedScreens.removeFirst();
        }
    }
}
