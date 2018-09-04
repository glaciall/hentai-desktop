package cn.org.hentai.desktop.system.worker;

import cn.org.hentai.desktop.util.Log;

/**
 * Created by Expect on 2018/4/19.
 */
public class BaseWorker extends Thread
{
    private boolean isTerminated = false;
    public void terminate()
    {
        this.isTerminated = true;
        // Log.info("Terminate: " + this.getName() + ": " + this.getClass().getName());
    }

    public boolean isTerminated()
    {
        return this.isTerminated;
    }

    protected void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(Exception e) { }
    }
}
