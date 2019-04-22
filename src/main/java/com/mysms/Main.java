package com.mysms;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;


import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import javafx.scene.text.Font;


import javafx.stage.Stage;

import javafx.stage.WindowEvent;
import tencentsms.SendMsgUtil;
import tencentsms.SmsResponse;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        primaryStage.setTitle("Tencent SMS v0.2");
        primaryStage.getIcons().add(new Image("icon.bmp"));
        settingUI(primaryStage, root);

        primaryStage.setOnCloseRequest(existWin());
        primaryStage.show();
    }

    private void settingUI(Stage primaryStage, GridPane root) {
        root.setVgap(20);
        root.setHgap(10);
        root.setAlignment(Pos.TOP_CENTER);
        Label label1 = new Label("\u624b\u673a\u53f7"); //phoneNum
        TextField textFieldWorkInterval = new TextField("13262272821");

        Label label2 = new Label("\u5151\u6362\u7801"); //coupon code
        TextField textFieldRestInterval = new TextField("1234");

        Label label3 = new Label("ok?");
        Button saveConfig = new Button("Send Msg");
        saveConfig.setOnMouseClicked(event -> {
            String phone = textFieldWorkInterval.getText();
            String code = textFieldRestInterval.getText();
            SmsResponse rsp = SendMsgUtil.sendMsg(phone, code);
            label3.setText(rsp.errmsg());
        });

        //check box
        CheckBox testEnv = new CheckBox("Test Env");
        CheckBox OnlineEnv = new CheckBox("Online Env");


        testEnv.setOnMouseEntered(e -> testEnv.setEffect(new DropShadow()));
        OnlineEnv.setOnMouseEntered(e -> OnlineEnv.setEffect(new DropShadow()));
        testEnv.selectedProperty().addListener((observable, oldValue, newValue) -> {
            testEnv.setEffect(null);
            testEnv.setSelected(false);
        });
        OnlineEnv.selectedProperty().addListener((observable, oldValue, newValue) -> {
            OnlineEnv.setEffect(newValue ? new DropShadow() : null);
        });

        Label batchInput = new Label("\u6279\u91cf\u5904\u7406");
        TextArea textArea = new TextArea();

        textArea.setWrapText(true);
        textArea.setPrefColumnCount(TextArea.DEFAULT_PREF_COLUMN_COUNT - 20);
        textArea.setPrefRowCount(TextArea.DEFAULT_PREF_ROW_COUNT - 3);
        textArea.setPromptText("\u6bcf\u884c\u4e00\u6761\u6570\u636e\uff0c\u683c\u5f0f\u4e3a : \r\n13262272821,xxxxxeedd");
        Button send2 = new Button("Send Msg");
        send2.setOnMouseClicked(e -> {
            String[] str = textArea.getText().split("\n");
            int sendOk = 0;
            int sendError = 0;
            StringBuilder errorMsg = new StringBuilder();
            for (String res : str) {
                String[] strs = res.split("[,]");
                if (strs.length == 2) {
                    SmsResponse rep = SendMsgUtil.sendMsg(strs[0].trim(), strs[1].trim());
                    if (rep.result() == 0) {
                        sendOk++;
                    } else {
                        sendError++;
                        errorMsg.append("\n" + res + "\n" + rep.errmsg());
                    }
                } else {
                    sendError++;
                    errorMsg.append("\n" + res + "\u53c2\u6570\u9519\u8bef\uff0c\u4f7f\u7528\u82f1\u6587\u9017\u53f7");
                }
            }
            textArea.setText(String.format("Success: %d, Fail: %d \nError msg: %s", sendOk, sendError, sendError == 0 ? "None" : errorMsg.toString()));

        });

        Label setting = new Label("Send SMS");
        setting.setFont(Font.font(20));
        GridPane.setHalignment(setting, HPos.CENTER);
        root.add(setting, 0, 0, 2, 1);

//        root.add(testEnv, 0, 1);
//        root.add(OnlineEnv, 1, 1);

        root.add(label1, 0, 2);
        root.add(textFieldWorkInterval, 1, 2);
        root.add(label2, 0, 3);
        root.add(textFieldRestInterval, 1, 3);
        root.add(label3, 0, 4, 2, 1);
        root.add(saveConfig, 1, 4);

        root.add(batchInput, 0, 5);
        root.add(textArea, 1, 5);
        root.add(send2, 1, 6);


        primaryStage.setScene(new Scene(root, 450, 400));
    }


    private EventHandler<WindowEvent> existWin() {
        return (va) -> {
            System.out.println(" i will be closed");
            System.exit(1);
        };
    }


    public static void main(String[] args) {
        launch(args);

    }
}