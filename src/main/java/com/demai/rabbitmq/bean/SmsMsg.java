package com.demai.rabbitmq.bean;

/**
 * Created by dear on 15/11/30.
 */
public class SmsMsg {

    private String targetMobiles;

    public String getTargetMobiles() {
        return targetMobiles;
    }

    public void setTargetMobiles(String targetMobiles) {
        this.targetMobiles = targetMobiles;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;


}
