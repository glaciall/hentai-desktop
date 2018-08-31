package cn.org.hentai.desktop.compress;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * Created by matrixy on 2018/4/11.
 */
public class RLEncoding extends BaseCompressProcessor
{
    // 压缩后的图像字节数组
    private static ByteArrayOutputStream compressedData = new ByteArrayOutputStream(1024 * 1024 * 2);

    // 以RGB作为数组下标的数组容器，用于保存颜色的出现次数，或是颜色表的下标
    private static int[] colortable = new int[1 << 24];

    // 保存己出现的颜色，假设同屏最多出现1920 * 1080种不同的颜色
    private static int[] colors = new int[1920 * 1080];

    // 最少需要N次出现才会加入到颜色表中
    private static int N = 20;

    // 最多保存256个出现最多次的颜色
    private static int[] mainColors = new int[510];

    // mainColors数组的有效数据下标，也指代了颜色个数
    private static int colorIndex = 0;

    private int colorBitMask = 0xffffff;

    public synchronized byte[] compress(int[] bitmap, int from, int to)
    {
        // 初始化
        compressedData.reset();

        // 查找出现次数最多的颜色，建立颜色表
        findMainColors(bitmap, from, to);

        // 写入颜色表
        compressedData.write((byte)((colorIndex - 1) & 0xff));
        for (int i = 0; i < colorIndex - 1; i++)
        {
            int rgb = mainColors[i * 2 + 1] & 0xffffff;
            compressedData.write((rgb >> 16) & 0xff);
            compressedData.write((rgb >> 8) & 0xff);
            compressedData.write(rgb & 0xff);
        }

        // 行程编码
        int rl = 1;
        int color, lastColor = bitmap[0] & 0xffffff;
        for (int i = 1, l = to; i < to; i++)
        {
            color = bitmap[i];
            if (color == lastColor && rl < 32767)
            {
                rl += 1;
                continue;
            }
            if (lastColor == 0)
            {
                compressedData.write((rl | 0x8000) >> 8);
                compressedData.write(rl);
                compressedData.write(0);
            }
            else if (colortable[detract(lastColor & 0xffffff)] > 0)
            {
                compressedData.write((rl | 0x8000) >> 8);
                compressedData.write(rl);
                compressedData.write(colortable[detract(lastColor & 0xffffff)]);
            }
            else
            {
                lastColor = detract(lastColor & 0xffffff);
                compressedData.write((rl & 0x7fff) >> 8);
                compressedData.write(rl);
                compressedData.write((byte) ((lastColor >> 16) & 0xff));
                compressedData.write((byte) ((lastColor >> 8) & 0xff));
                compressedData.write((byte) (lastColor & 0xff));
            }
            rl = 1;
            lastColor = color;
        }
        if (lastColor == 0)
        {
            compressedData.write((rl | 0x8000) >> 8);
            compressedData.write(rl);
            compressedData.write(0);
        }
        else if (colortable[detract(lastColor & 0xffffff)] > 0)
        {
            compressedData.write((rl | 0x8000) >> 8);
            compressedData.write(rl);
            compressedData.write(colortable[detract(lastColor & 0xffffff)]);
        }
        else
        {
            lastColor = detract(lastColor & 0xffffff);
            compressedData.write((rl & 0x7fff) >> 8);
            compressedData.write(rl);
            compressedData.write((byte) ((lastColor >> 16) & 0xff));
            compressedData.write((byte) ((lastColor >> 8) & 0xff));
            compressedData.write((byte) (lastColor & 0xff));
        }

        // 清空colortable
        for (int i = 0; i < mainColors.length; i+=2) colortable[mainColors[i + 1]] = 0;

        return compressedData.toByteArray();
    }

    // 查找次数出现最多的颜色
    public static void findMainColors(int[] bitmap, int from, int to)
    {
        // 重置
        colorIndex = 0;
        Arrays.fill(mainColors, 0);

        // 颜色计数
        for (int i = from; i < to; i++)
        {
            int color = detract(bitmap[i] & 0xffffff);
            if (bitmap[i] == 0) continue;
            if (colortable[color] == 0) colors[colorIndex++] = color;
            colortable[color] += 1;
        }

        // 查找主颜色
        int minCount = 0;
        for (int i = 0; i < colorIndex; i++)
        {
            int color = colors[i];
            int count = colortable[color];

            // 将colorCounting清零
            colortable[color] = 0;

            if (count < N) continue;

            // 如果比mainColors里最小的都还要少，后面的事情也不用弄了
            if (count < minCount) continue;

            int k = 0;
            for (; k < mainColors.length; k+=2)
            {
                if (mainColors[k] < count)
                {
                    if (k < mainColors.length - 2) System.arraycopy(mainColors, k, mainColors, k + 2, mainColors.length - k - 2);

                    mainColors[k] = count;
                    mainColors[k + 1] = color;
                    break;
                }
            }
            minCount = mainColors[mainColors.length - 2];
        }

        colorIndex = 1;
        for (int i = 0; i < mainColors.length; i+=2)
        {
            int count = mainColors[i];
            if (count == 0) continue;
            colortable[mainColors[i + 1]] = colorIndex++;
        }
    }

    // 针对颜色值进行减位，用于压缩处理
    static int detract(int c)
    {
        // 灰色RGB不减位
        if ((((c >> 16) & 0xff) ^ ((c >> 8) & 0xff)) == (c & 0xff)) return c;
        return c & 0xf0f0f0;
    }

    // 压缩后的图像数据解压
    public int[] decompress(int width, int height, byte[] compressedData)
    {
        int[] bitmap = new int[width * height];
        for (int k = 0, i = ((compressedData[0] & 0xff) * 3) + 1; i < compressedData.length; )
        {
            int rl = (((compressedData[i] & 0xff) << 8) | (compressedData[i + 1] & 0xff)) & 0xffff;
            int red, green, blue;
            if ((rl & 0x8000) > 0)
            {
                int index = (compressedData[i + 2] & 0xff);
                if (index == 0)
                {
                    k += (rl & 0x7fff);
                    i += 3;
                    continue;
                }
                index = (index - 1) * 3 + 1;
                red = compressedData[index] & 0xff;
                green = compressedData[index + 1] & 0xff;
                blue = compressedData[index + 2] & 0xff;
                i += 3;
            }
            else
            {
                red = compressedData[i + 2] & 0xff;
                green = compressedData[i + 3] & 0xff;
                blue = compressedData[i + 4] & 0xff;
                i += 5;
            }
            for (int s = 0, l = rl & 0x7fff; s < l; s++)
                bitmap[k++] = 0xff000000 | (red << 16) | (green << 8) | blue;
        }
        return bitmap;
    }

    public static void init()
    {
        // 初始化
    }
}
