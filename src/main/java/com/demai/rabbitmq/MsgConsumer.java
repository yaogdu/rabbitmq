package com.demai.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.demai.rabbitmq.bean.SmsMsg;
import com.demai.utils.SMSUtil;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * Created by dear on 15/12/1.
 */
public class MsgConsumer {


    private final static Logger logger = LoggerFactory.getLogger(MsgConsumer.class);
    private static Connection connection;

    private static Channel channel;

    private SMSUtil smsUtil;

    QueueingConsumer consumer;

    ExecutorService es = Executors.newFixedThreadPool(5);

    private ConnectionFactory factory;

    private String queueName = "";

    public SMSUtil getSmsUtil() {
        return smsUtil;
    }

    public void setSmsUtil(SMSUtil smsUtil) {
        this.smsUtil = smsUtil;
    }

    private String userName;

    private String pwd;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    private Integer port;

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getHost() {
        return host;

    }

    public void setHost(String host) {
        this.host = host;
    }

    private String host;

    private String exchangeName;


    boolean run = false;

    /**
     * 初始化 rabbitmq factory
     *
     * @throws IOException
     */
    private void channelRecovery() throws IOException {
        try {
            factory = new ConnectionFactory();
            factory.setUsername(getUserName());
            factory.setPassword(getPwd());
            factory.setPort(getPort());
            factory.setHost(getHost());
            factory.setAutomaticRecoveryEnabled(true);
            factory.setConnectionTimeout(1000);
            factory.setNetworkRecoveryInterval(10);
            factory.setRequestedHeartbeat(10);
            factory.setTopologyRecoveryEnabled(true);
            connection = factory.newConnection(es);
            channel = connection.createChannel(10);

            channel.exchangeDeclare(exchangeName, "direct", true);
            logger.info("queueName is {}", queueName);
            Map<String, Object> args = new HashMap<>();
            args.put("x-max-priority", 10);//定义优先级
            channel.queueDeclare(queueName, true, false, false, args);//durable,exclusive,autodelete
            channel.queueBind(queueName, exchangeName, "");
            channel.basicQos(100);
            consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueName, false, consumer);

            run =true;
            logger.info("facetory inited successfully..");
        } catch (Exception e) {
            try {
                Thread.sleep(factory.getNetworkRecoveryInterval()*1000);
                logger.error("channelRecovery error occurred", e);
                channelRecovery();
            } catch (InterruptedException e1) {
                logger.error("init factory error", e);
                channelRecovery();
            }
        }

    }

    /**
     * 接收并消费消息
     *
     * @throws IOException
     */
    private void consume() throws IOException {
        try {

            es.execute(new Runnable() {

                @Override
                public void run() {
                    while (run) {
                        String message = null;
                        QueueingConsumer.Delivery delivery = null;
                        try {
                            logger.info("ready to receive...");
                            delivery = consumer.nextDelivery();
                            message = new String(delivery.getBody(), "utf-8");
                            if (!StringUtils.isEmpty(message)) {
                                Integer priority = delivery.getProperties().getPriority();
                                logger.info("message {} priority is {}", message, priority);
                                consumeMessage(message);
                                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                                logger.info("ack msg {}", message);
                            }
                        } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException cce) {//channel Recovery
                            logger.error("connection error", cce);
                            try {
                                run  = false;
                                channelRecovery();
                                logger.info("trying to recovery connection and channel");
                            } catch (Exception e) {
                                logger.error("channel Recovery error", e);
                            }
                        } catch (Exception e) {//channelRecovery and nack msg in order to requeue it.
                            try {
                                run  = false;
                                channelRecovery();
                                if (delivery != null) {//requeue the message after consumeMessage error
                                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);//requeue
                                    logger.info("nack msg : {}", message);
                                }
                            } catch (IOException e1) {
                                logger.error("basic nack error ", e1);
                            }
                            logger.info("rabbitmq subscribe message error", e);
                        }
                    }
                }
            });

        } catch (Exception e) {
            logger.error("receiver init channel error", e);
        }
    }

    public MsgConsumer() throws IOException, TimeoutException,
            InterruptedException {
        logger.info("msgConsumer default constructor invoked");

    }


    public void run() {
        try {

            channelRecovery();//init channel and MsgConsumer
            //factoryExceptionHandle();
            consume();
        } catch (Exception e) {
            logger.error("error occurred when invoking msgConsumer", e);
        }

    }

    /**
     * 消费
     *
     * @param msg
     */
    public void consumeMessage(final String msg) {
        try {//要求不得抛出异常，这里try{}catch掉
            logger.debug("收到队列消息<--- thread:{} msg:{}", Thread.currentThread().getName(), msg);
            es.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        SmsMsg sm = JSON.parseObject(msg, SmsMsg.class);
                        if (sm != null) {
                            String mobiles = sm.getTargetMobiles();
                            String content = sm.getContent();

                            if (!com.demai.common.utils.StringUtils.isEmpty(mobiles)) {
                                String[] targetMobiles = mobiles.split(",");
                                if (targetMobiles != null && targetMobiles.length > 0) {

                                    if (!com.demai.common.utils.StringUtils.isEmpty(content)) {
                                        smsUtil.send(targetMobiles, content);
                                        logger.info("sent msg content {} to {}", content, mobiles);
                                    } else {
                                        logger.info("sending sms content {} is not correct.", content);
                                    }
                                }
                            } else {
                                logger.info("sending sms target mobiles {} are not correct.", mobiles);
                            }
                        }

                    } catch (Exception e) {
                        logger.error("异步发消息出错", e);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("消费消息出错", e);
        }

    }
}
