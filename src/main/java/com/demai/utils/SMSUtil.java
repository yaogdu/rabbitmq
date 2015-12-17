package com.demai.utils;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dear on 15/11/30.
 */
@Component
public class SMSUtil {

    private static Logger logger = LoggerFactory.getLogger(SMSUtil.class);

    private String URL;//= "http://61.145.229.29:9003/MWGate/wmgw.asmx";

    private String action;//= "MongateCsSpSendSmsNew";

    private String namespace;//= "http://tempuri.org/";

//    private static final String op = "MongateCsSpSendSmsNew";

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    private String soapAction;//= "http://tempuri.org/MongateCsSpSendSmsNew";


    private String userName;

    private String password;

    private Service service;

    public static void main(String[] args) {
        //new SMSUtil().send();
    }

    public void send(String[] targetMobiles, String content) {

        String mobiles = "";
        for (String mobile : targetMobiles) {
            mobiles += mobile + ",";
        }
        mobiles = mobiles.substring(0, mobiles.length() - 1);
        Map<String, Object> result = new ConcurrentHashMap<>();
        Call call = null;
        try {
            call = (Call) service.createCall();

            if (call != null) {
                call.setTargetEndpointAddress(new java.net.URL(URL));
                call.setUsername(userName); // 用户名
                call.setPassword(password); // 密码
                call.setUseSOAPAction(true);
                call.setSOAPActionURI(namespace + action); // action uri
                call.setOperationName(new QName(namespace, action));// 设置要调用哪个方法
                // 设置参数名称，具体参照从浏览器中看到的
                call.addParameter(new QName(namespace, "userId"), XMLType.XSD_STRING, ParameterMode.IN);
                call.addParameter(new QName(namespace, "password"), XMLType.XSD_STRING, ParameterMode.IN);
                call.addParameter(new QName(namespace, "pszMobis"), XMLType.XSD_STRING, ParameterMode.IN);
                call.addParameter(new QName(namespace, "pszMsg"), XMLType.XSD_STRING, ParameterMode.IN);
                call.addParameter(new QName(namespace, "iMobiCount"), XMLType.XSD_INT, ParameterMode.IN);
                call.addParameter(new QName(namespace, "pszSubPort"), XMLType.XSD_STRING, ParameterMode.IN);
                call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING); // 要返回的数据类型
                logger.info("userName is {} and password is {} and mobiles is {} and content is {} and length is {} " +
                        "", userName, password, mobiles, content, targetMobiles.length);

                Object[] params = new Object[]{userName, password, mobiles, content, targetMobiles
                        .length + "", "*"};
                String results = (String) call.invoke(params); //方法执行后的返回值

                logger.info("sending msg {} to {} returned {}", content, mobiles, results);

                if (!CodeStatus.codeMap.containsKey(results)) {
                    result.put("result", results);
                    result.put("success", true);
                } else {
                    result.put("result", CodeStatus.codeMap.get(results));
                    result.put("success", false);
                }

            } else {
                result.put("success", false);
                result.put("msg", "发送短信发生错误");
            }

        } catch (ServiceException e) {
            result.put("success", false);
            result.put("msg", "发送短信发生错误");
            logger.error("ServiceException occurred when sending msg {} to {}", content, targetMobiles, e);
        } catch (RemoteException e) {
            result.put("success", false);
            result.put("msg", "发送短信发生错误");
            logger.error("RemoteException occurred when sending msg {} to {}", content, targetMobiles, e);
        } catch (MalformedURLException e) {
            result.put("success", false);
            result.put("msg", "发送短信发生错误");
            logger.error("MalformedURLException occurred when sending msg {} to {}", content, targetMobiles, e);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "发送短信发生错误");
            logger.error("error occurred when sending msg {} to {}", content, targetMobiles, e);
        }

        logger.info("sending msg {} to {} result is {}", content, targetMobiles, result);


    }
}
