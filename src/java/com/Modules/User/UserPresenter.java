package com.Modules.User;


import com.Infrastructure.Exception.NotFoundException;
import com.Infrastructure.Interface.LoaderInterface;
import com.Other.Base.PagePresenter;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class UserPresenter extends PagePresenter implements LoaderInterface {
    private UserController userController;

    private static User user;

    public UserPresenter() throws SQLException, ClassNotFoundException {
        this.userController = new UserController();
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        UserPresenter.user = user;
    }

    /*========================== Draw ==========================*/
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private TextField textFieldPasswordConfirmation;



    public void loadPresenter() {
        User user = UserPresenter.getUser();
        this.textFieldName.setText(user.getUsername());
        this.textFieldEmail.setText(user.getEmail());
    }

    @FXML
    private void updateUser() {
        String username = this.textFieldName.getText();
        String password = this.textFieldPassword.getText();
        String passwordConfirmation = this.textFieldPasswordConfirmation.getText();
        username = (username == null) ? "" : username.trim();
        password = (password == null) ? "" : password.trim();
        passwordConfirmation = (passwordConfirmation == null) ? "" : passwordConfirmation.trim();

        if (username.isEmpty()) {
            this.showErrorDialog("Please input all needed information!");
            return;
        }

        if (username.length() > 80) {
            this.showErrorDialog("Name is not valid");
            return;
        }


        if ((password.isEmpty() || passwordConfirmation.isEmpty()) && !password.equals(passwordConfirmation)) {
            this.setFieldsNull(this.textFieldPassword);
            this.showErrorDialog("Password Confirmation is invalid!");
            return;
        }

        User user = new User();
        user.setId(UserPresenter.getUser().getId());
        user.setUsername(username);
        user.setPassword(password);

        try {
            this.userController.update(user, user.getId());
            UserPresenter.setUser(this.userController.getDetail(user.getId()));
            this.setFieldsNull(this.textFieldPassword, this.textFieldPasswordConfirmation);
        } catch (SQLException | NotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }
}
