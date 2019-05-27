package com.mysms.service;

import com.mysms.ValueConstant;
import tencentsms.SendMsgUtil;
import tencentsms.SmsResponse;
import tencentsms.Telphone;
import tencentsms.TmpContent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SomeService {

    public static boolean checkFile(String filePathName) {

        boolean checkResult = false;

        try {
            BufferedReader bf = new BufferedReader(new FileReader(new File(filePathName)));
            String tmp = bf.readLine();

            if (checkMsgPhoneAndContent(tmp)) {
                checkResult = true;

            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return checkResult;

    }


    /**
     * 检查短信发送内容参数是否正确
     *
     * @param content 短信内容参数，第一个参数是
     * @return 短信参数时是否正确
     */
    public static boolean checkMsgPhoneAndContent(String content) {
        if (content == null) {
            return false;
        }
        int count = content.split(",").length;
        TmpContent tmpContent = ValueConstant.All_SMS.get(ValueConstant.SELECTED_ENVIRONMENT).get(ValueConstant.SMS_TMP_ID);
        String text = tmpContent.text();
        int variableCount = getVariableCount(text);
        int status = tmpContent.status();
        return status == 0 && count - 1 == variableCount;

    }

    private static int getVariableCount(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == '}') {
                count++;
            }
        }
        return count;
    }

    public static boolean checkMultInputMsg(String msgContents) {
        if (msgContents == null) {
            return false;
        }
        String[] msgContent = msgContents.split("\n");
        return checkMsgPhoneAndContent(msgContent[0]);
    }

    public static SmsResponse sendSingleMsg(String phoneAndVariable) {
        String[] strs = phoneAndVariable.split(",");
        List<String> msg = new ArrayList<>();
        for (String str : strs) {
            msg.add(str.trim());
        }

        msg.remove(0);

        String[] params = new String[msg.size()];
        for (int i = 0; i < msg.size(); i++) {
            params[i] = msg.get(i);
        }
        return SendMsgUtil.sendMessage(Telphone.apply(strs[0].trim(), "86"), params,
                Integer.valueOf(ValueConstant.SMS_TMP_ID), ValueConstant.SELECTED_ENVIRONMENT);

    }

    public static SmsResponse sendSingleMsg(String phone, String variable) {
        if (!checkMsgPhoneAndContent(phone + "," + variable)) {
            return SmsResponse.apply(1, "短信参数数量不匹配");
        } else {
            return SendMsgUtil.sendMessage(Telphone.apply(phone, "86"), variable.equals("") ? new String[0] : variable.split(","),
                    Integer.valueOf(ValueConstant.SMS_TMP_ID), ValueConstant.SELECTED_ENVIRONMENT);
        }
    }

}
