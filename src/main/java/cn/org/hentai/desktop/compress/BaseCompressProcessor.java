package cn.org.hentai.desktop.compress;

/**
 * Created by matrixy on 2018/4/9.
 */
public abstract class BaseCompressProcessor
{
    public abstract byte[] compress(int[] bitmap, int from, int to);
}
