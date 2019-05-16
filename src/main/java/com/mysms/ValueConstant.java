package com.mysms;

import tencentsms.Env;
import tencentsms.TmpContent;

import java.util.*;


public class ValueConstant {
    public static int SELECTED_ENVIRONMENT = Env.NoThing();
    public static String SMS_TMP_ID = "";
    public static List<String> SMS_TMP_ID_LIST = new ArrayList<>();

    public static Map<String, TmpContent> SMS_CONTENT = new HashMap<>(16);

    public static final String READ_ME = "1、选择测试环境或者线上环境\n" +
            "2、选择短信模板\n" +
            "3、每个号码一行，含有参数则和号码用英文逗号分隔";

    public static void processTmpSms(scala.collection.immutable.List<TmpContent> list) {
        SMS_TMP_ID_LIST.clear();
        SMS_CONTENT.clear();
        list.foreach(t -> {
            switch (t.status()) {
                case 0:
                    SMS_TMP_ID_LIST.add(t.id() + ":可用");
                    break;
                case 1:
                    SMS_TMP_ID_LIST.add(t.id() + ":待审核");
                    break;
                default:
                    SMS_TMP_ID_LIST.add(t.id() + ":不可用");
            }
            SMS_CONTENT.put("" + t.id(), t);
            return null;
        });
    }
}
