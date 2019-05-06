package cn.org.hentai.desktop.app;

import cn.org.hentai.desktop.system.LocalComputer;
import cn.org.hentai.desktop.util.Configs;
import cn.org.hentai.desktop.util.logger.StdOutDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by matrixy on 2017-12-12.
 */
@ComponentScan(value = {"cn.org.hentai"})
@EnableAutoConfiguration
@SpringBootApplication
public class ServerApp
{
    @Autowired
    private Environment env;

    public static void main(String[] args) throws Exception
    {
        System.setProperty("java.awt.headless", "false");
        StdOutDelegate stdWriter = StdOutDelegate.newInstance(System.out);
        System.setOut(stdWriter);
        System.setErr(stdWriter);

        ApplicationContext context = SpringApplication.run(ServerApp.class, args);
        Configs.init("/application.properties");
        // new Thread(new RDServer()).start();

        LocalComputer.init();
        CLI.init();
    }

    @Bean
    public DataSource dataSource()
    {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        return dataSource;
    }

    @Autowired
    private RequestListener requestListener;

    @Bean
    public ServletListenerRegistrationBean<RequestListener> servletListenerRegistrationBean()
    {
        ServletListenerRegistrationBean<RequestListener> servletListenerRegistrationBean = new ServletListenerRegistrationBean<RequestListener>();
        servletListenerRegistrationBean.setListener(requestListener);
        return servletListenerRegistrationBean;
    }
}
