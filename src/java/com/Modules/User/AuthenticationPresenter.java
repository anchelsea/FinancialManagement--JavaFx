package com.Modules.User;


import com.Infrastructure.Exception.NotFoundException;
import com.Main;
import com.Infrastructure.Base.BaseViewPresenter;
import com.Infrastructure.Handle.MyTooltip;
import com.Infrastructure.Handle.HandleError;

import com.Other.ConfirmEmail.ConfirmController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AuthenticationPresenter extends BaseViewPresenter implements Initializable {
    private StringProperty changeMainScene;

    public int idUserAfterLogin;

    @FXML
    private JFXTextField userNameTextField;
    @FXML
    private JFXPasswordField passTextField;

    @FXML
    private JFXPasswordField confirmPassTextField;

    @FXML
    private Label errorLabel;
    @FXML
    private JFXButton loginBtn;
    @FXML
    private JFXTextField emailTextField;
    @FXML
    private JFXButton signUpBtn;
    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Label userWarnIcon;
    @FXML
    private Label emailWarnIcon;
    @FXML
    private Label passWarnIcon;
    @FXML
    private Label confirmWarnIcon;

    private Service<Void> thread;

    private UserController userController;
    private ConfirmController confirmController;


    private void hideWarnIcon() {
        userWarnIcon.setVisible(false);
        emailWarnIcon.setVisible(false);
        passWarnIcon.setVisible(false);
        confirmWarnIcon.setVisible(false);
    }


    public AuthenticationPresenter(StringProperty changeMainScene) throws SQLException, ClassNotFoundException {
        this.changeMainScene = changeMainScene;
        this.userController = new UserController();
    }

    public Scene loadLoginForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/authentication/login.fxml"));
        fxmlLoader.setController(this);
        AnchorPane pane = fxmlLoader.load();

        return new Scene(pane);
    }

    @FXML
    private void changeScene() {
        if (this.changeMainScene.get().equals("login")) {
            this.changeMainScene.set("sign_up");
        } else {
            this.changeMainScene.set("login");
        }
    }

    @FXML
    private void loginBtnClicked() throws SQLException, NotFoundException {
        String userName = this.userNameTextField.getText().trim();
        String password = this.passTextField.getText().trim();


        if (userName.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill out user name and password");
            errorLabel.setVisible(true);
            return;
        }
//        progressBar.setVisible(true);
//        loginBtn.setDisable(true);
//        signUpBtn.setDisable(true);
        User user = new User(userName, password);

        try {
            UserPresenter.setUser(this.userController.login(user));

            this.changeMainScene.set("transaction");
        } catch (NotFoundException e) {

            progressBar.setVisible(false);
            loginBtn.setVisible(true);
            signUpBtn.setVisible(true);
            this.setFieldsNull(this.passTextField);
            errorLabel.setText("Wrong username or password!!!");
            errorLabel.setVisible(true);

        }
        if (user == null) {
            errorLabel.setVisible(true);
            return;
        }


    }


    public Scene loadSignUpForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/authentication/sign_up.fxml"));
        fxmlLoader.setController(this);
        AnchorPane pane = fxmlLoader.load();

        return new Scene(pane);
    }


    public Scene loadConfirmForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/authentication/confirm.fxml"));
        fxmlLoader.setController(this);
        AnchorPane pane = fxmlLoader.load();

        return new Scene(pane);
    }

    public String getemailTf() {
        String emailTf;
        emailTf = this.emailTextField.getText();
        return emailTf;
    }

    @FXML
    private void handelClickedSignUpBtn() throws Exception {
        String userName = this.userNameTextField.getText().trim();
        String email = this.emailTextField.getText().trim();
        String password = this.passTextField.getText().trim();
        String passConfirmation = this.confirmPassTextField.getText().trim();


        // HANDLE constraints
        hideWarnIcon();
        if (!HandleError.isValidUsername(userName)) {
            userWarnIcon.getTooltip().setText("Username must have at least 4 character and only contain these character 'a-z' 'A-Z' '0-9' '_' .");
            userWarnIcon.setVisible(true);
            return;
        }


        if (userController.existedUser(userName)) {
            userWarnIcon.getTooltip().setText("Username already existed");
            userWarnIcon.setVisible(true);
            return;
        }

        if (userName.length() > 80) {
            this.showErrorDialog("Name is not valid");
            return;
        }

        if (!HandleError.isValidEmail(email)) {
            emailWarnIcon.getTooltip().setText("Email is invalid");
            emailWarnIcon.setVisible(true);
            return;
        }


        if (!HandleError.isValidPassword(password)) {
            passWarnIcon.setVisible(true);
            return;
        }


        if (!password.equals(passConfirmation)) {
            confirmWarnIcon.setVisible(true);
            return;
        }

        if (userController.existedEmail(email)) {
            emailWarnIcon.getTooltip().setText("Email has used by another account");
            emailWarnIcon.setVisible(true);
            return;
        }


        User user = new User(userName, email, password);

        try {
            this.userController.getUserByEmail(user.getEmail());
            this.setFieldsNull(this.passTextField, this.confirmPassTextField);
            this.emailWarnIcon.getTooltip().setText("Email has used by another user!");
            return;
        } catch (NotFoundException e) {
//            try {
//                this.confirmController.send();
//            }
//            catch (NullPointerException exception){
//
//            }

            this.changeMainScene.set("login");
            this.userController.create(user);
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            userWarnIcon.setTooltip(new MyTooltip("Username must have at least 4 character and only contain these character 'a-z' 'A-Z' '0-9' '_' ."));
            emailWarnIcon.setTooltip(new MyTooltip("Email is invalid"));
            passWarnIcon.setTooltip(new MyTooltip("Password must have at least 8 character, include lettering and number "));
            confirmWarnIcon.setTooltip(new MyTooltip("Confirm does not match password."));

        } catch (NullPointerException e) {

        }

    }


}
