package com.mysms;

import tencentsms.Env;
import tencentsms.TmpContent;

import java.util.*;


public final class ValueConstant {
    public static int SELECTED_ENVIRONMENT = Env.NoThing();
    public static String SMS_TMP_ID = "";
    public static String SGIN_NAME = "";
    public static final int  AVAIL_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static Map<Integer, Map<String, TmpContent>> All_SMS = new HashMap<>(2);
    public static Map<Integer, List<String>> All_SMS_TMP = new HashMap<>(2);

    public static final String READ_ME = "" +
            "0、如需要配置短信签名请点击【统计与配置】中进行配置\n" +
            "1、选择测试环境或者线上环境\n" +
            "2、选择短信模板\n" +
            "3、每个号码一行，含有参数则和号码用英文逗号分隔";

    public static void processTmpSms(scala.collection.immutable.List<TmpContent> list) {

        List<String> smsTmpList = new ArrayList<>();
        Map<String, TmpContent> smsContents = new HashMap<>(16);
        list.foreach(t -> {
            switch (t.status()) {
                case 0:
                    smsTmpList.add(t.id() + ":可用");
                    break;
                case 1:
                    smsTmpList.add(t.id() + ":待审核");
                    break;
                default:
                    smsTmpList.add(t.id() + ":不可用");
            }
            smsContents.put("" + t.id(), t);
            return null;
        });
        All_SMS_TMP.put(SELECTED_ENVIRONMENT, smsTmpList);
        All_SMS.put(SELECTED_ENVIRONMENT, smsContents);
    }
}
