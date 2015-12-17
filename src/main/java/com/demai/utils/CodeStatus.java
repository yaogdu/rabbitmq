package com.demai.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dear on 15/11/30.
 */
public class CodeStatus {

    public static ConcurrentHashMap<String, String> codeMap = new ConcurrentHashMap<>();

    static {
        codeMap.put("-1", "参数为空。信息、电话号码等有空指针，登陆失败");
        codeMap.put("-2", "电话号码个数超过100");
        codeMap.put("-10", "申请缓存空间失败");
        codeMap.put("-11", "电话号码中有非数字字符");
        codeMap.put("-12", "有异常电话号码");
        codeMap.put("-13", "电话号码个数与实际个数不相等");
        codeMap.put("-14", "实际号码个数超过100");
        codeMap.put("-101", "发送消息等待超时");
        codeMap.put("-102", "发送或接收消息失败");
        codeMap.put("-103", "接收消息超时");
        codeMap.put("-200", "其他错误");
        codeMap.put("-999", "web服务器内部错误");
        codeMap.put("-10001", "用户登陆不成功");
        codeMap.put("-10002", "提交格式不正确");
        codeMap.put("-10003", "用户余额不足");
        codeMap.put("-10004", "手机号码不正确");
        codeMap.put("-10005", "计费用户帐号错误");
        codeMap.put("-10006", "计费用户密码错");
        codeMap.put("-10007", "账号已经被停用");
        codeMap.put("-10008", "账号类型不支持该功能");
        codeMap.put("-10009", "其它错误");
        codeMap.put("-10010", "企业代码不正确");
        codeMap.put("-10011", "信息内容超长");
        codeMap.put("-10012", "不能发送联通号码");
        codeMap.put("-10013", "操作员权限不够");
        codeMap.put("-10014", "费率代码不正确");
        codeMap.put("-10015", "服务器繁忙");
        codeMap.put("-10016", "企业权限不够");
        codeMap.put("-10017", "此时间段不允许发送");
        codeMap.put("-10018", "经销商用户名或密码错");
        codeMap.put("-10019", "手机列表或规则错误");
        codeMap.put("-10021", "没有开停户权限");
        codeMap.put("-10022", "没有转换用户类型的权限");
        codeMap.put("-10023", "没有修改用户所属经销商的权限");
        codeMap.put("-10024", "经销商用户名或密码错");
        codeMap.put("-10025", "操作员登陆名或密码错误");
        codeMap.put("-10026", "操作员所充值的用户不存在");
        codeMap.put("-10027", "操作员没有充值商务版的权限");
        codeMap.put("-10028", "该用户没有转正不能充值");
        codeMap.put("-10029", "此用户没有权限从此通道发送信息");
        codeMap.put("-10030", "不能发送移动号码");
        codeMap.put("-10031", "手机号码(段)非法");
        codeMap.put("-10032", "用户使用的费率代码错误");
        codeMap.put("-10033", "非法关键词");
    }
}
