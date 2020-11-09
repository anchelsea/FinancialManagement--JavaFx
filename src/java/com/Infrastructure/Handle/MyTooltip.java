package com.Infrastructure.Handle;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class MyTooltip extends Tooltip {

    public MyTooltip(String text){
        this.setText(text);
        this.setWrapText(true);
        this.setShowDelay(Duration.seconds(0.1));
        this.setPrefWidth(150);
        this.setStyle("-fx-text-fill:#fafafa grey lighten-5\n;");
        this.setStyle("-fx-background-color:#f44336 red");
    }


}
