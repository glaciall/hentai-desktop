package cn.org.hentai.desktop.system.worker;

import cn.org.hentai.desktop.compress.CompressUtil;
import cn.org.hentai.desktop.system.LocalComputer;
import cn.org.hentai.desktop.system.Screenshot;
import cn.org.hentai.desktop.util.Log;
import cn.org.hentai.desktop.util.Packet;
import cn.org.hentai.desktop.wss.WSSessionManager;

/**
 * Created by matrixy on 2018/4/10.
 */
public class CompressWorker extends BaseWorker
{
    String compressMethod = "rle";          // 压缩方式
    Screenshot lastScreen = null;           // 上一屏的截屏，用于比较图像差
    Packet packet = Packet.create(1024 * 1024 * 10);
    int sequence = 0;
    long lastSentTime = 0L;

    public CompressWorker()
    {
        // do nothing here
    }

    public CompressWorker(String method)
    {
        this.compressMethod = method;
    }

    public void resetScreen()
    {
        this.lastScreen = null;
    }

    private void compress() throws Exception
    {
        Screenshot screenshot = null;
        while (true)
        {
            if (!ScreenImages.hasScreenshots()) break;
            screenshot = ScreenImages.getScreenshot();
        }
        if (screenshot == null || screenshot.isExpired()) return;

        // 分辨率是否发生了变化？
        if (lastScreen != null && (lastScreen.width != screenshot.width || lastScreen.height != screenshot.height)) lastScreen = null;

        // 1. 求差
        int[] bitmap = new int[screenshot.bitmap.length];
        int changedColors = 0, start = -1, end = bitmap.length;
        if (lastScreen != null)
        {
            for (int i = 0; i < bitmap.length; i++)
            {
                if (lastScreen.bitmap[i] == screenshot.bitmap[i])
                {
                    bitmap[i] = 0;
                }
                else
                {
                    if (start == -1) start = i;
                    else end = i;
                    changedColors += 1;
                    bitmap[i] = screenshot.bitmap[i];
                }
            }
        }
        else bitmap = screenshot.bitmap;

        if (lastScreen != null && changedColors == 0) return;
        // Log.debug("Changed colors: " + changedColors);

        // 2. 压缩
        start = Math.max(start, 0);
        start = 0;
        end = bitmap.length;
        byte[] compressedData = CompressUtil.process(this.compressMethod, bitmap, start, end);

        // Log.debug("Compress Ratio: " + (screenshot.bitmap.length * 4.0f / compressedData.length));
        // Log.debug("After: " + (compressedData.length / 1024));

        // 3. 入队列
        // Packet packet = Packet.create((byte)0x01, compressedData.length + 16);
        packet.reset();
        packet.addShort((short)screenshot.width)
                .addShort((short)screenshot.height)
                .addLong(screenshot.captureTime)
                .addInt(sequence++);
        packet.addBytes(compressedData);

        // ScreenImages.addCompressedScreen(packet);
        // 推送到每一个WebSocket会话去

        WSSessionManager.getInstance().broadcast(packet.getBytes(), LocalComputer.getPointer());
        lastSentTime = System.currentTimeMillis();
        lastScreen = screenshot;
    }

    public void run()
    {
        while (!this.isTerminated())
        {
            try
            {
                if (System.currentTimeMillis() - lastSentTime > 20)
                {
                    lastSentTime = System.currentTimeMillis();
                    WSSessionManager.getInstance().broadcast(null, LocalComputer.getPointer());
                }
                compress();
                sleep(5);
            }
            catch(Exception e)
            {
                Log.error(e);
            }
        }
    }
}
