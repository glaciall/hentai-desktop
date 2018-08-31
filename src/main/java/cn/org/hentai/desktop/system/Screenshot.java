package cn.org.hentai.desktop.system;

import java.awt.image.BufferedImage;

/**
 * Created by matrixy on 2018/4/9.
 */
public final class Screenshot
{
    // 截屏时间
    public long captureTime;

    // 宽度
    public int width;

    // 高度
    public int height;

    // RGB
    public int[] bitmap;

    // 压缩后的图像数据
    public byte[] compressedData;

    public Screenshot(int width, int height, long captureTime, byte[] compressedData)
    {
        this.width = width;
        this.height = height;
        this.captureTime = captureTime;
        this.compressedData = compressedData;
    }

    public Screenshot(BufferedImage img)
    {
        this.captureTime = System.currentTimeMillis();
        this.width = img.getWidth();
        this.height = img.getHeight();
        this.bitmap = img.getRGB(0, 0, this.width, this.height, null, 0, this.width);
    }

    // 截屏是否己过期，超过1秒的不需要再发送了
    public boolean isExpired()
    {
        return System.currentTimeMillis() - this.captureTime > 1000;
    }
}
