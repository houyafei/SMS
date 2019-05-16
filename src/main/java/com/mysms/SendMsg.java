package com.mysms;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//import com.mysms.sms.SendMsgUtil;
//
//import java.io.*;
//
public class SendMsg {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(""));
        String res = "";
        while (res != null) {
            res = reader.readLine();
            if (res != null) {
                String[] strs = res.split(",");
//                System.out.println(strs[0].trim() + "," + strs[1].trim());
//                            SendMsgUtil.sendMsg(strs[0].trim(), strs[1].trim());

            }


        }
//        new Thread(() -> com.mysms.sms.SendMsg.sendMsg("15002114482","122222")).start();

    }
//
}
