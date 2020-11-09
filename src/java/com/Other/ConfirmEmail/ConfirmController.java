package com.Other.ConfirmEmail;


import com.Modules.User.UserService;
import com.Modules.User.AuthenticationPresenter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


class ResendEmail extends Task{
    private String randomCode;
    private JFXProgressBar progressBar;
    private JFXButton confirmBtn;
    private JFXTextField confirmTextField;
    private Label resendLabel;
    @FXML
    public JFXTextField emailTextField;
    UserService service;
    AuthenticationPresenter authentication;



    public ResendEmail(String randomCode, JFXProgressBar progressBar, JFXButton confirmBtn, JFXTextField confirmTextField, Label resendLabel) {
        this.randomCode = randomCode;
        this.progressBar = progressBar;
        this.confirmBtn = confirmBtn;
        this.confirmTextField = confirmTextField;
        this.resendLabel = resendLabel;
    }

    @Override
    protected Object call() throws Exception {
        progressBar.setVisible(true);
        confirmBtn.setDisable(true);
        confirmTextField.setDisable(true);
        resendLabel.setDisable(true);
        SendEmail.sendMail(authentication.getemailTf(),randomCode);
        progressBar.setVisible(false);
        confirmBtn.setDisable(false);
        confirmTextField.setDisable(false);
        resendLabel.setDisable(false);
        return null;
    }
}

public class ConfirmController implements Initializable {

    private String randomCode;
    @FXML
    private Label textLabel;
    @FXML
    private JFXTextField codeTextField;
    @FXML
    private Label resendLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private JFXButton confirmBtn;

    @FXML
    private JFXTextField emailTextField;
    AuthenticationPresenter authentication; private StringProperty changeMainScene = new SimpleStringProperty("");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generateCode();

        textLabel.setText(textLabel.getText() + authentication.getemailTf());
        new Thread(new ResendEmail(randomCode,progressBar,confirmBtn,codeTextField,resendLabel)).start();

        resendLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                generateCode();
                ResendEmail resendEmail = new ResendEmail(randomCode,progressBar,confirmBtn,codeTextField,resendLabel);
                new Thread(resendEmail).start();

            }
        });
    }

    @FXML
    private void submitCode() throws SQLException, ClassNotFoundException {
        if(codeTextField.getText().equals(randomCode)){
            errorLabel.setVisible(false);
            this.authentication=new AuthenticationPresenter(changeMainScene);
            this.changeMainScene.setValue("login");
        }
        else{
            errorLabel.setVisible(true);
        }
    }

    private void generateCode(){
        String code = "";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for(int i = 0 ; i < 8;i++){
            int rand = (int)(Math.random() * chars.length());
            code += chars.charAt(rand);
        }

        randomCode = code;
    }




    public void send() throws Exception {
        generateCode();

        textLabel.setText(textLabel.getText() + authentication.getemailTf());
        progressBar.setVisible(true);
        confirmBtn.setDisable(true);
        resendLabel.setDisable(true);
        SendEmail.sendMail(authentication.getemailTf(),randomCode);
        progressBar.setVisible(false);
        confirmBtn.setDisable(false);
        resendLabel.setDisable(false);
    }

}
