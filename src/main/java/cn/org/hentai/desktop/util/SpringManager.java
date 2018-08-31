package cn.org.hentai.desktop.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Created by matrixy on 2017/12/12.
 */
@Service
public class SpringManager implements ApplicationListener<ContextRefreshedEvent>
{
    private static ApplicationContext applicationContext = null;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        if (applicationContext == null)
        {
            applicationContext = event.getApplicationContext();
        }
    }

    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
}