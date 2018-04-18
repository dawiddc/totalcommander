package com.dawiddc.totalcommander.controllers;

import com.dawiddc.totalcommander.helpers.BundleManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MainPaneController implements Observer {

    private BundleManager bundleManager = new BundleManager(new Locale("en"));

    private String leftDisplayPath;
    private String righyDisplayPath;

    @FXML
    private Menu fileMenu;
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private Menu languageMenu;
    @FXML
    private MenuItem englishMenuItem;
    @FXML
    private MenuItem polishMenuItem;
    @FXML
    private Button leftCopyButton;
    @FXML
    private Button leftMoveButton;
    @FXML
    private Button leftDeleteButton;
    @FXML
    private Button leftRefreshButton;
    @FXML
    private Button rightCopyButton;
    @FXML
    private Button rightMoveButton;
    @FXML
    private Button rightDeleteButton;
    @FXML
    private Button rightRefreshButton;
    @FXML
    private ChoiceBox leftRootDropdown;
    @FXML
    private ChoiceBox rightRootDropdown;


    @FXML
    protected void initialize() {
        bundleManager.addObserver(this);
        listRoots();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof BundleManager)
            changeLanguage((Locale) arg);
    }

    public void changeLanguageToEng(ActionEvent actionEvent) {
        bundleManager.changeAndNotify(new Locale("en"));
    }

    public void changeLanguageToPolish(ActionEvent actionEvent) {
        bundleManager.changeAndNotify(new Locale("pl"));
    }

    public void closeApplication(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    private void changeLanguage(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.lang", locale);
        fileMenu.setText(bundle.getString("file"));
        closeMenuItem.setText(bundle.getString("file.close"));
        languageMenu.setText(bundle.getString("language"));
        englishMenuItem.setText(bundle.getString("language.english"));
        polishMenuItem.setText(bundle.getString("language.polish"));
        leftDeleteButton.setText(bundle.getString("delete"));
        leftRefreshButton.setText(bundle.getString("refresh"));
        leftCopyButton.setText(bundle.getString("copy"));
        leftMoveButton.setText(bundle.getString("move"));
        rightDeleteButton.setText(bundle.getString("delete"));
        rightRefreshButton.setText(bundle.getString("refresh"));
        rightCopyButton.setText(bundle.getString("copy"));
        rightMoveButton.setText(bundle.getString("move"));
    }

    private void listRoots() {
        ObservableList<String> rootsList = FXCollections.observableArrayList();
        File[] roots = File.listRoots();
        for (File root : roots) {
            if (!root.toString().equals("A:\\"))
                rootsList.add(root.toString());
        }
        leftRootDropdown.setItems(rootsList);
        rightRootDropdown.setItems(rootsList);
        leftRootDropdown.getSelectionModel().selectFirst();
        rightRootDropdown.getSelectionModel().selectFirst();
    }
}
