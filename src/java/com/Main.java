package com;

import com.Modules.User.AuthenticationPresenter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;

import java.io.IOException;

public class Main extends Application {
    private Stage primaryStage;

    private AuthenticationPresenter authenticationController;

    private HBox layout = new HBox();

    private boolean firstLogin = true;

    private StringProperty changeMainScene = new SimpleStringProperty("login");

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.authenticationController = new AuthenticationPresenter(changeMainScene);
        this.listenSceneChanging();
    }

    private void listenSceneChanging() throws IOException {
        this.changeMainScene.addListener((observableValue, oldValue, newValue) -> {
            try {
                this.layout.getChildren().clear();

                if (newValue.equals("login") || newValue.equals("sign_up")||newValue.equals("confirm")) {
                    this.primaryStage.setMinWidth(320);
                    this.primaryStage.setMinHeight(320);

                    if (newValue.equals("sign_up")) {
                        this.primaryStage.setScene(this.authenticationController.loadSignUpForm());
                        this.primaryStage.setTitle("Sign up");
                        this.primaryStage.getIcons().add(new Image("/image/Other/sign_up.png"));
                        this.primaryStage.setMinWidth(320);
                        this.primaryStage.show();
                    }
                    else if(newValue.equals("confirm")){
                        this.primaryStage.setScene((this.authenticationController.loadConfirmForm()));
                    }
                    else {
                        this.primaryStage.setScene(this.authenticationController.loadLoginForm());
                        this.primaryStage.setTitle("Sign in");
                        this.primaryStage.getIcons().add(new Image("/image/Other/sign_in.png"));
                        this.primaryStage.setMinWidth(320);
                        this.primaryStage.show();
                    }
                } else {
                    this.loadMainScene();
                    firstLogin = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.primaryStage.setScene(this.authenticationController.loadLoginForm());
        this.primaryStage.setTitle("Sign in");
        this.primaryStage.getIcons().add(new Image("/image/Other/sign_in.png"));
        this.primaryStage.setMinWidth(320);
        this.primaryStage.show();

    }

    private void loadMainScene() throws IOException {
        if (!firstLogin) {
            this.layout = new HBox();
        }
            try {
                FXMLLoader sidebarLoader = new FXMLLoader(Main.class.getResource("/fxml/sidebar.fxml"));
                 Parent sidebar= sidebarLoader.load();
               // System.out.println("uyty");

                this.layout.getChildren().add(sidebar);
                this.changeMainView(sidebarLoader.getController());
                this.primaryStage.setTitle("FinancialManagement");
                this.primaryStage.getIcons().add(new Image("/image/Other/home_icon.png"));
                this.primaryStage.setScene(new Scene(this.layout, 850, 650));
                this.primaryStage.setMinWidth(680);
                this.primaryStage.setMinHeight(550);
                this.primaryStage.show();
            }catch (Exception e){
                System.out.println(e);
            }
    }

    private void changeMainView(MainPresenter mainPresenter) {
        mainPresenter.getChangeScene().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                if (this.layout.getChildren().size() == 2) {
                    this.layout.getChildren().set(1, mainPresenter.getMainView());
                } else {
                    this.layout.getChildren().add(mainPresenter.getMainView());
                }

                mainPresenter.setChangeScene(false);
            }
        });
        mainPresenter.setChangeScene(true);
        mainPresenter.setChangeMainScene(this.changeMainScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}



