package com.Infrastructure.Base;

import javafx.scene.Parent;
import javafx.scene.Scene;

abstract public class BaseView {
    abstract protected Parent getView();

    public Scene getScene() {
        Scene scene = new Scene(getView());

        return scene;
    }
}