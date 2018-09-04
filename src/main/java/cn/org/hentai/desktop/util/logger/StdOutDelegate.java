package cn.org.hentai.desktop.util.logger;

import java.io.*;

/**
 * Created by matrixy on 2018/9/4.
 */
public class StdOutDelegate extends PrintStream
{
    static OutputStream printer = new ByteArrayOutputStream();
    PrintStream writer = null;

    public StdOutDelegate(OutputStream out)
    {
        super(out);
    }

    private StdOutDelegate withPrintStream(PrintStream printStream)
    {
        this.writer = printStream;
        return this;
    }

    public static StdOutDelegate newInstance(PrintStream printStream)
    {
        return new StdOutDelegate(printer).withPrintStream(printStream);
    }

    @Override
    public void println()
    {
        writer.println();
    }

    @Override
    public void println(boolean x)
    {
        writer.println(x);
    }

    @Override
    public void println(char x) {
        writer.println(x);
    }

    @Override
    public void println(int x) {
        writer.println(x);
    }

    @Override
    public void println(long x) {
        writer.println(x);
    }

    @Override
    public void println(float x) {
        writer.println(x);
    }

    @Override
    public void println(double x) {
        writer.println(x);
    }

    @Override
    public void println(char[] x) {
        writer.println(x);
    }

    @Override
    public void println(Object x) {
        writer.println(x);
    }

    public void println(String text)
    {
        writer.println(text);
    }

    public void print(String text)
    {
        writer.print(text);
    }

    public static void main(String[] args) throws Exception
    {
        System.setOut(StdOutDelegate.newInstance(System.out));
        System.out.println("xxxxxxxxxxx");
    }
}
