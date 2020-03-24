package com.xzy.read.util;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.xzy.read.VO.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

/**
 * @author XieZhongYi
 * 2020/03/24 15:07
 */
@Component
@Slf4j
public class SmsUtil {

    @Value("${tx.sms.appid}")
    private Integer appid;

    @Value("${tx.sms.appkey}")
    private String appkey;

    @Value("${tx.sms.smsSign}")
    private String smsSign;


    @Value("${tx.sms.templateId}")
    private Integer templateId;

    public ResultVo txSmsSend(String phoneNumber) {
        SmsSingleSender ssender = new SmsSingleSender(appid,appkey);
        String code = generateCode();
        String[] params = {code};
//        try {
//            SmsSingleSenderResult result = ssender.sendWithParam(
//                    "86", phoneNumber, templateId, params , smsSign, "", "");
//            log.info("验证码:" + code);
//            return ResultVoUtil.success(code);
//        } catch (HTTPException | JSONException | IOException e) {
//            e.printStackTrace();
//        }
//        return ResultVoUtil.error(0,"发生未知错误，请稍后再试");
        return ResultVoUtil.success(code);
    }

    public String generateCode(){
        String str="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb=new StringBuilder(4);
        for(int i=0;i<4;i++)
        {
            char ch=str.charAt(new Random().nextInt(str.length()));
            sb.append(ch);
        }
        return sb.toString();
    }
}
