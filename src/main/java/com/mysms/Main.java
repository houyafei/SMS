package com.mysms;


import com.mysms.service.SendMsgService;
import com.mysms.service.SomeService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;


import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import javafx.scene.text.Font;


import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.stage.WindowEvent;
import scala.collection.immutable.List;
import tencentsms.*;

import java.io.File;


public class Main extends Application {

    private VBox root = new VBox();

    private ComboBox<String> asd = new ComboBox<String>();
    private Label tmpSmSContent = new Label();

    private TextArea resultReport = new TextArea();

    /**
     * select the file to send
     */
    private Label fileName = new Label();
    private Label sendProgress = new Label("");
    private String filePath = "";

    private SmsSendReport smsSendReport = new SmsSendReport();


    @Override
    public void start(Stage primaryStage) {


        primaryStage.setTitle("短信平台 v1.0");
        primaryStage.getIcons().add(new Image("msg.jpg"));

        Separator[] separator = new Separator[]{new Separator(), new Separator()};
        separator[0].setMaxSize(100, 100);
        separator[1].setMaxSize(400, 100);

        root.getChildren().add(setTitle());
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().add(separator[0]);
        root.getChildren().add(selectEnvironment());
        root.getChildren().add(separator[1]);
        root.getChildren().add(sendMsgWay());
        root.getChildren().add(sendResult());

        primaryStage.setScene(new Scene(root, 500, 600));

        primaryStage.setOnCloseRequest(existWin());
        primaryStage.show();
    }

    private HBox setTitle() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setPadding(new Insets(20, 0, 10, 0));
        Label title = new Label("发送短信");
        title.setFont(Font.font(20));
        hBox.getChildren().add(title);
        return hBox;
    }

    private HBox selectEnvironment() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10, 0, 8, 0));
        hBox.setSpacing(20);

        CheckBox testEnv = new CheckBox("测试环境");
        CheckBox OnlineEnv = new CheckBox("线上环境");
        testEnv.selectedProperty().addListener((observable, oldValue, newValue) -> {
            testEnv.setEffect(newValue ? new DropShadow() : null);
            if (newValue) {
                ValueConstant.SELECTED_ENVIRONMENT = Env.TestEnv();
                updateComoBox(Env.TestEnv());
            } else {
                ValueConstant.SELECTED_ENVIRONMENT = Env.NoThing();
            }
        });
        OnlineEnv.selectedProperty().addListener((observable, oldValue, newValue) -> {
            OnlineEnv.setEffect(newValue ? new DropShadow() : null);
            if (newValue) {
                ValueConstant.SELECTED_ENVIRONMENT = Env.OnlineEnv();
                updateComoBox(Env.OnlineEnv());
            } else {
                ValueConstant.SELECTED_ENVIRONMENT = Env.NoThing();
            }
        });

        OnlineEnv.setOnMouseClicked(e -> {
            if (testEnv.isSelected()) {
                testEnv.setSelected(!testEnv.isSelected());
            }
        });
        testEnv.setOnMouseClicked(e -> {
            if (OnlineEnv.isSelected()) {
                OnlineEnv.setSelected(!OnlineEnv.isSelected());
            }
        });

        hBox.getChildren().add(testEnv);
        hBox.getChildren().add(OnlineEnv);
        hBox.getChildren().add(selectTmpId());
        return hBox;
    }

    private void updateComoBox(int env) {
        if (env != Env.OnlineEnv() && env != Env.TestEnv()) {
            return;
        }
        new Thread(() -> {
            List<TmpContent> tmpList = TentSmSTmp.obtainTmp(env);
            ValueConstant.processTmpSms(tmpList);
            Platform.runLater(() -> {
                ObservableList<String> data = FXCollections.observableArrayList();
                asd.setItems(data);
                asd.setPromptText("选择短信模板");
                data.addAll(ValueConstant.SMS_TMP_ID_LIST.toArray(new String[ValueConstant.SMS_TMP_ID_LIST.size()]));
            });

        }).start();

    }

    private HBox selectTmpId() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10, 0, 8, 0));
        HBox hBoxSelectTmp = new HBox();
        hBoxSelectTmp.setAlignment(Pos.CENTER);
        hBoxSelectTmp.setPadding(new Insets(10, 0, 8, 0));
        hBoxSelectTmp.setSpacing(20);
        Label tmpId = new Label("选择短信模板Id："); // select tmp_id


        ObservableList<String> data = FXCollections.observableArrayList();


        asd.setPromptText("选择短信模板");
        data.addAll(ValueConstant.SMS_TMP_ID_LIST.toArray(new String[ValueConstant.SMS_TMP_ID_LIST.size()]));
        asd.setItems(data);

        asd.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.contains(":")) {
                ValueConstant.SMS_TMP_ID = newValue.split(":")[0];
                resultReport.setText("【短信内容】：\n " + ValueConstant.SMS_CONTENT.get(ValueConstant.SMS_TMP_ID).text());
            } else {
                resultReport.setText("选择短信模板");
            }
        });

        hBoxSelectTmp.getChildren().add(tmpId);
        hBoxSelectTmp.getChildren().add(asd);

        hBox.getChildren().addAll(hBoxSelectTmp);
        return hBox;
    }

    private VBox readMe() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(10, 0, 8, 0));
        vBox.setSpacing(10);

        Label readme = new Label();
        readme.setMaxWidth(320);
        readme.setWrapText(true);
        readme.setText("\n\n\n"+ValueConstant.READ_ME);
        vBox.getChildren().addAll(readme, tmpSmSContent);
        return vBox;
    }

    private GridPane singlePhone() {
        GridPane gridPane01 = new GridPane();
        gridPane01.setVgap(20);
        gridPane01.setHgap(10);
        gridPane01.setAlignment(Pos.TOP_CENTER);
        gridPane01.setPadding(new Insets(10, 0, 0, 0));

        Label label1 = new Label("手机号"); //phoneNum
        TextField phoneNum = new TextField();
        phoneNum.setPromptText("13262272821");

        Label label2 = new Label("参数列表");
        TextField content = new TextField();
        content.setPromptText("无参数则不需要填写");

        Button sendMsgBtn = new Button("发送短信");
        sendMsgBtn.setOnMouseClicked(event -> {
            String phone = phoneNum.getText().trim();
            String code = content.getText().trim();
            new Thread(() -> {
                SmsResponse rsp = SomeService.sendSingleMsg(phone, code);
                Platform.runLater(() -> resultReport.setText(String.format("手机号: %s \n短信内容: %s \n发送结果: %d, %s",
                        phone, code, rsp.result(), rsp.errmsg())));
            }).start();

        });

        gridPane01.add(label1, 0, 0);
        gridPane01.add(phoneNum, 1, 0);
        gridPane01.add(label2, 0, 1);
        gridPane01.add(content, 1, 1);
        gridPane01.add(sendMsgBtn, 1, 2);


        return gridPane01;

    }

    private GridPane multilPhoneInput() {

        GridPane gridPane01 = new GridPane();
        gridPane01.setVgap(20);
        gridPane01.setHgap(10);
        gridPane01.setAlignment(Pos.TOP_CENTER);
        gridPane01.setPadding(new Insets(10, 0, 0, 0));

        Label batchInput = new Label("批量输入");
        TextArea textArea = new TextArea();
        Label testAreaStatusLabel = new Label();
        textArea.setWrapText(true);
        textArea.setPrefColumnCount(TextArea.DEFAULT_PREF_COLUMN_COUNT - 20);
        textArea.setPrefRowCount(TextArea.DEFAULT_PREF_ROW_COUNT - 3);
        textArea.setPromptText("包含参数的数据使用（英文逗号）分隔，例如: \r\n13262272821,xxxxxeedd");
        Button send2 = new Button("发送");
        send2.setOnMouseClicked(e -> {
            String[] str = textArea.getText().split("\n");
            if (str.length > 1000) {
                testAreaStatusLabel.setText("一次处理请少于1000行");
            } else {
                SendMsgService sendMsgService = new SendMsgService(textArea.getText(), SendMsgService.SendType.MultInputMsg);
                sendMsgService.start();
                textArea.textProperty().bind(sendMsgService.messageProperty());
                sendMsgService.setOnSucceeded(v -> {
                    smsSendReport = sendMsgService.getValue();
                    Platform.runLater(() -> {
                        textArea.textProperty().unbind();
                        textArea.setText(textArea.getText() + "\n" + smsSendReport.getReportMsg() + "\n ok ");
                        resultReport.setText(String.format("Success: %d, Fail: %d \nError msg: \n%s",
                                smsSendReport.getSuccessCount().get(),
                                smsSendReport.getFailCount().get(),
                                smsSendReport.getFailCount().get() == 0 ? "None" : smsSendReport.obtainFailMsg()));

                    });

                });
            }

        });

        gridPane01.add(batchInput, 0, 0);
        gridPane01.add(textArea, 1, 0);
        gridPane01.add(testAreaStatusLabel, 0, 1);
        gridPane01.add(send2, 1, 1);
        return gridPane01;
    }


    private VBox selectFile() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(10, 0, 8, 0));
        HBox btns = new HBox();
        btns.setAlignment(Pos.TOP_CENTER);
        btns.setSpacing(10);
        vBox.setSpacing(10);

        Button selectFile = new Button("选择文件");
        Button sendSmS = new Button("开始发送");
        Button stopSendSmS = new Button("停止发送");

        fileName.setText("【注意】确认短信数量，其将会被全部发送，不可中途暂停。");

        selectFile.setOnMouseClicked(event -> {
            final FileChooser fileChooser = new FileChooser();
            File list = fileChooser.showOpenDialog(new Stage());

            if (list != null) {
                filePath = list.getAbsolutePath();
                fileName.setText(filePath);
                fileName.setWrapText(true);
                if (ValueConstant.SMS_TMP_ID.equals("")) {
                    sendProgress.setText("-> 请先选择短信模板 <-");
                } else {
                    if (SomeService.checkFile(filePath)) {
                        sendSmS.setDisable(false);
                        sendProgress.setText("-> ok 短信格式正确");
                    } else {
                        sendProgress.setText("-> 短信格式不正确！！！");
                    }
                }

            }
        });

        sendSmS.setOnMouseClicked(event -> {
            if (filePath != null && !filePath.equals("")) {
                SendMsgService sendMsgService = new SendMsgService(new File(filePath), SendMsgService.SendType.FileMsg);
                sendMsgService.start();
                sendProgress.textProperty().bind(sendMsgService.messageProperty());
                sendMsgService.setOnSucceeded(v -> {
                    smsSendReport = sendMsgService.getValue();
                    Platform.runLater(() -> {
                        resultReport.setText(String.format("成功发送: %d, 失败发送: %d \n错误信息: \n%s",
                                smsSendReport.getSuccessCount().get(),
                                smsSendReport.getFailCount().get(),
                                smsSendReport.getFailCount().get() == 0 ? "None" : smsSendReport.obtainFailMsg()));
                        sendSmS.setDisable(true);
                        filePath = "";

                    });

                });

            }
        });

        fileName.setMaxWidth(350);
        sendProgress.setMaxWidth(350);
        stopSendSmS.setVisible(false);
        sendSmS.setDisable(true);
        btns.getChildren().addAll(selectFile, sendSmS, stopSendSmS);
        vBox.getChildren().addAll(btns, fileName, sendProgress);
        return vBox;
    }

    private VBox sendResult() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10, 0, 8, 0));
        vBox.setSpacing(5);
        Label label = new Label("结果报告");
        Separator separator = new Separator();
        separator.setMaxWidth(100);

        resultReport.setPrefRowCount(2);
        resultReport.setMaxWidth(400);
        resultReport.setWrapText(true);
        resultReport.setEditable(false);
        resultReport.setText(ValueConstant.READ_ME);
        vBox.getChildren().addAll(label, separator, resultReport);
        return vBox;
    }


    private HBox sendMsgWay() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10, 0, 8, 0));

        TabPane ways = new TabPane();

        Tab tab0 = new Tab();
        tab0.setText("说明");
        tab0.setContent(readMe());

        Tab tab1 = new Tab();
        tab1.setText("单条短信");
        tab1.setContent(singlePhone());
        tab1.setClosable(false);
        Tab tab2 = new Tab();
        tab2.setText("批量短信");
        tab2.setContent(multilPhoneInput());
        tab2.setClosable(false);
        Tab tab3 = new Tab();
        tab3.setText("批量短信文件");
        tab3.setContent(selectFile());
        tab3.setClosable(false);
        ways.getTabs().addAll(tab0, tab1, tab2, tab3);
        ways.setSide(Side.TOP);

        ways.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        hBox.getChildren().add(ways);
        return hBox;
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