package cn.org.hentai.desktop.compress;

/**
 * Created by matrixy on 2018/4/9.
 */
public final class CompressUtil
{
    /**
     * 使用指定的压缩方法进行数据压缩
     * @param method 压缩方法，如rle或huffman
     * @param argbArray ARGB序列的颜色数组
     * @return
     */
    public static byte[] process(String method, int[] argbArray, int from, int to)
    {
        return new RLEncoding().compress(argbArray, from, to);
    }
}
