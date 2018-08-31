package cn.org.hentai.desktop.util;

import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * Created by matrixy on 2017-02-23.
 */
public final class Log
{
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static enum Type { ERROR, INFO, DEBUG };

    private static OutputStream logWriter = System.err;

    private static synchronized String getCurrentTime()
    {
        return sdf.format(new java.util.Date());
    }

    private static byte[] toBytes(String message, Type type)
    {
        try
        {
            return (getCurrentTime() + " " + type + ": " + message).getBytes("UTF-8");
        }
        catch(Exception e)
        {
            // ..
        }
        return new byte[0];
    }

    public static void error(Throwable ex)
    {
        try
        {
            ex.printStackTrace();
            logWriter.write(toBytes(ex.toString(), Type.ERROR));
            logWriter.write('\n');
        }
        catch(Exception e) { }
    }

    public static void error(String message)
    {
        try
        {
            logWriter.write(toBytes(message, Type.ERROR));
            logWriter.write('\n');
        }
        catch(Exception e) { }
    }

    public static void info(String message)
    {
        try
        {
            logWriter.write(toBytes(message, Type.INFO));
            logWriter.write('\n');
        }
        catch(Exception e) { }
    }

    public static void debug(String message)
    {
        try
        {
            logWriter.write(toBytes(message, Type.DEBUG));
            logWriter.write('\n');
        }
        catch(Exception e) { }
    }
}
