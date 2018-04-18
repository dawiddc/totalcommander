package com.dawiddc.totalcommander;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(new Locale("pl"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainPane.fxml"));
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.lang");
        loader.setResources(bundle);

        Parent mainPane = loader.load();

        primaryStage.setTitle("Total Commander");
        primaryStage.setScene(new Scene(mainPane, 900, 500));
        primaryStage.show();
    }

}
