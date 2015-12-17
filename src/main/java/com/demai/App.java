package com.demai;

import com.demai.rabbitmq.MsgConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) {

        try {
            ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[]{
                    "../conf/applicationContext.xml"});
            MsgConsumer msgConsumer = (MsgConsumer) appContext.getBean("msgConsumer");
            msgConsumer.run();
        } catch (Exception e) {
            System.out.println(e);
            logger.error("sms_consumer服务启动出错", e);
            System.exit(-1);
        }

        logger.info("service sms MsgConsumer has started successfully!");
        //死锁等待
        synchronized (App.class) {
            while (true) {
                try {
                    App.class.wait();
                } catch (Throwable e) {
                    logger.error("error occurred!");
                }
            }
        }
    }

}
