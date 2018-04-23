package com.dawiddc.totalcommander.controllers;

import com.dawiddc.totalcommander.helpers.BundleManager;
import com.dawiddc.totalcommander.models.SystemObject;
import com.dawiddc.totalcommander.models.SystemObjectFileType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainPaneController implements Observer {

    private BundleManager bundleManager = new BundleManager(new Locale("en"));

    private String leftCurrentPath;
    private String rightCurrentPath;

    private Task copyTask;
    private ResourceBundle resourceBundle;

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
    private ChoiceBox leftRootChoicebox;
    @FXML
    private ChoiceBox rightRootChoicebox;
    @FXML
    private TableView<SystemObject> rightTableView;
    @FXML
    private TableView<SystemObject> leftTableView;
    @FXML
    private TableColumn<SystemObject, String> leftTableNameColumn;
    @FXML
    private TableColumn<SystemObject, Long> leftTableSizeColumn;
    @FXML
    private TableColumn<SystemObject, Date> leftTableDateColumn;
    @FXML
    private TableColumn<SystemObject, String> leftTableTypeColumn;
    @FXML
    private TableColumn<SystemObject, String> rightTableNameColumn;
    @FXML
    private TableColumn<SystemObject, Long> rightTableSizeColumn;
    @FXML
    private TableColumn<SystemObject, Date> rightTableDateColumn;
    @FXML
    private TableColumn<SystemObject, String> rightTableTypeColumn;
    @FXML
    private TextField leftCurrentPathField;
    @FXML
    private TextField rightCurrentPathField;

    public MainPaneController() {
    }


    @FXML
    protected void initialize() {
        resourceBundle = ResourceBundle.getBundle("bundles.lang", Locale.getDefault());
        bundleManager.addObserver(this);

        leftCurrentPathField.setEditable(false);
        rightCurrentPathField.setEditable(false);

        prepareTables();
        listRoots();
        selectDefaultLanguage();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof BundleManager)
            changeLanguage((Locale) arg);
    }

    private void selectDefaultLanguage() {
        if (Locale.getDefault().toString().contains("pl")) {
            changeLanguageToPolish();
        } else if (Locale.getDefault().toString().contains("en")) {
            changeLanguageToEng();
        }
    }

    public void changeLanguageToEng() {
        polishMenuItem.setVisible(true);
        englishMenuItem.setVisible(false);
        bundleManager.changeAndNotify(new Locale("en"));
    }

    public void changeLanguageToPolish() {
        polishMenuItem.setVisible(false);
        englishMenuItem.setVisible(true);
        bundleManager.changeAndNotify(new Locale("pl"));
    }


    public void closeApplication() {
        Platform.exit();
        System.exit(0);
    }

    private void changeLanguage(Locale locale) {
        resourceBundle = ResourceBundle.getBundle("bundles.lang", locale);
        fileMenu.setText(resourceBundle.getString("file"));
        closeMenuItem.setText(resourceBundle.getString("file.close"));
        languageMenu.setText(resourceBundle.getString("language"));
        englishMenuItem.setText(resourceBundle.getString("language.english"));
        polishMenuItem.setText(resourceBundle.getString("language.polish"));
        leftDeleteButton.setText(resourceBundle.getString("delete"));
        leftRefreshButton.setText(resourceBundle.getString("refresh"));
        leftCopyButton.setText(resourceBundle.getString("copy"));
        leftMoveButton.setText(resourceBundle.getString("move"));
        rightDeleteButton.setText(resourceBundle.getString("delete"));
        rightRefreshButton.setText(resourceBundle.getString("refresh"));
        rightCopyButton.setText(resourceBundle.getString("copy"));
        rightMoveButton.setText(resourceBundle.getString("move"));
        leftTableNameColumn.setText(resourceBundle.getString("table.name"));
        leftTableSizeColumn.setText(resourceBundle.getString("table.size"));
        leftTableDateColumn.setText(resourceBundle.getString("table.date"));
        leftTableTypeColumn.setText(resourceBundle.getString("table.type"));
        rightTableNameColumn.setText(resourceBundle.getString("table.name"));
        rightTableSizeColumn.setText(resourceBundle.getString("table.size"));
        rightTableDateColumn.setText(resourceBundle.getString("table.date"));
        rightTableTypeColumn.setText(resourceBundle.getString("table.type"));
        refreshTableViews();
    }

    private void listRoots() {
        ObservableList<String> rootsList = FXCollections.observableArrayList();
        File[] roots = File.listRoots();
        for (File root : roots) {
            if (!root.toString().equals("A:\\"))
                rootsList.add(root.toString());
        }
        leftRootChoicebox.setItems(rootsList);
        rightRootChoicebox.setItems(rootsList);
        leftRootChoicebox.getSelectionModel().selectFirst();
        rightRootChoicebox.getSelectionModel().selectFirst();
    }

    public void prepareTables() {

        leftTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        leftTableDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));
        leftTableSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        leftTableTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        rightTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        rightTableDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));
        rightTableSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        rightTableTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));

        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void leftRootChoicebox_OnAction(ActionEvent e) {
        try {
            refreshTableView(leftRootChoicebox.getSelectionModel().getSelectedItem().toString(), leftTableView, leftCurrentPathField);
            int leftChoiceBoxLastSelected = leftRootChoicebox.getSelectionModel().getSelectedIndex();
            leftRootChoicebox.getSelectionModel().select(leftChoiceBoxLastSelected);
        } catch (Exception ex) {
            ex.printStackTrace();
//            try {
//                showReadErrorMessage(leftRootChoicebox.getSelectionModel().getSelectedItem().toString());
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
    }

    public void rightRootChoicebox_OnAction(ActionEvent e) {
        try {
            refreshTableView(leftRootChoicebox.getSelectionModel().getSelectedItem().toString(), rightTableView, rightCurrentPathField);
            int rightChoiceBoxLastSelected = leftRootChoicebox.getSelectionModel().getSelectedIndex();
            leftRootChoicebox.getSelectionModel().select(rightChoiceBoxLastSelected);
        } catch (Exception ex) {
            ex.printStackTrace();
//            try {
////                showReadErrorMessage(leftRootChoicebox.getSelectionModel().getSelectedItem().toString());
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
        }
    }

    public void refreshLeft() {
        refreshTableView(leftCurrentPathField.getText(), leftTableView, leftCurrentPathField);
    }

    public void refreshRight() {
        refreshTableView(rightCurrentPathField.getText(), rightTableView, rightCurrentPathField);
    }

    public void refreshTableViews() {
        refreshLeft();
        refreshRight();
    }

    private void refreshTableView(String refreshPath, TableView<SystemObject> tableView, TextField pathTextField) {
        File file;
        List<SystemObject> systemObjectList = new ArrayList<>();

        SystemObject rootFile = new SystemObject(new File(refreshPath), resourceBundle);
        if (rootFile.exists() && rootFile.isDirectory()) {
            systemObjectList = rootFile.listSystemFiles(resourceBundle);
        }

        ObservableList<SystemObject> observableList = FXCollections.observableArrayList(systemObjectList);

        String rootParentName = rootFile.getFile().getParent();
        SystemObject rootParentFile = null;
        if (rootParentName != null) {
            try {
                rootParentFile = new SystemObject(new File(rootParentName), resourceBundle);
                rootParentFile.setFileName(new File(rootParentName).getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            rootParentFile = rootFile;
            rootParentFile.setFileName("");
        }

        rootParentFile.setLastModified("");
        rootParentFile.setFileType("");
        rootParentFile.setTypeOfFile(SystemObjectFileType.ROOT);
        rootParentFile.setSize("");
        ImageView leftImageView = new ImageView(new Image("/images/go-up.png"));
        leftImageView.setFitHeight(20);
        leftImageView.setFitWidth(20);
        rootParentFile.setImage(leftImageView);

        // adding root file and other files to tables
        observableList.add(0, rootParentFile);
        tableView.setItems(observableList);

        // adding path to text box
        try {
            pathTextField.setText(new File(refreshPath).getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyToRight() {
        List<SystemObject> selectedObjects = leftTableView.getSelectionModel().getSelectedItems();
        copyFiles(selectedObjects, rightCurrentPath);
    }

    public void copyToLeft() {
        List<SystemObject> selectedObjects = rightTableView.getSelectionModel().getSelectedItems();
        copyFiles(selectedObjects, leftCurrentPath);
    }


    public void copyFiles(List<SystemObject> selectedObjects, String targetDirectoryPath) {
        for (SystemObject object : selectedObjects) {
            if (selectedObjects != null && object.getTypeOfFile() != SystemObjectFileType.ROOT) {
                Path sourcePath = object.getFile().toPath();
                Path targetPath = new File(targetDirectoryPath + "\\" + sourcePath.getFileName()).toPath();

                File source = new File(sourcePath.toString());
                File dest = new File(targetPath.toString());

                try {
                    /* Check whether in the same directory*/
                    if (source.getCanonicalPath().equals(dest.getCanonicalPath())) {
                        return;
                    }
                    if (dest.getName().equals(source.getName()))
                        dest = throwOverwriteAlert(targetPath);
                    if (dest != null)
                        runCopyTask(source, dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File throwOverwriteAlert(Path destPath) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("overwriteTitle"));
        alert.setHeaderText(resourceBundle.getString("overwriteHeader"));
        alert.setContentText(resourceBundle.getString("overwriteMessage") + "\n" + destPath.toString());

        ButtonType buttonTypeOverwrite = new ButtonType(resourceBundle.getString("overwrite"));
        ButtonType buttonTypeRename = new ButtonType(resourceBundle.getString("rename"));
        ButtonType buttonTypeCancel = new ButtonType(resourceBundle.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOverwrite, buttonTypeRename, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeRename) {
            destPath = getUniqueFilePath(destPath);
        } else if (result.get() == buttonTypeCancel) {
            return null;
        }

        return new File(destPath.toString());
    }

    private Path getUniqueFilePath(Path destPath) {
        File destFile = new File(destPath.toString());
        File[] dirFiles = new File(destFile.getParent()).listFiles();
        String newFileName = destFile.getName();
        boolean unique = false;
        int i = 1;
        while (!unique) {
            for (File file : dirFiles) {
                unique = true;
                if (file.getName().equals(newFileName)) {
                    newFileName = " (" + i + ")" + destFile.getName();
                    i++;
                    unique = false;
                }
            }
        }
        return Paths.get(dirFiles + "\\" + newFileName);
    }

    private void runCopyTask(File source, File dest) {

        copyTask = new Task() {
            @Override
            protected Void call() throws Exception {
                if (!source.isDirectory()) {
                    FileUtils.copyFile(source, dest, true);
                } else {
                    FileUtils.copyDirectory(source, dest, true);
                }
                return null;
            }
        };
        new Thread(copyTask).start();

        copyTask.setOnSucceeded(e -> {
//            waitingBoxStage.close();
            refreshTableViews();
        });

        copyTask.setOnCancelled(e -> {
            refreshTableViews();
        });
    }

    public void moveToRight() {

    }

    public void moveToLeft() {

    }

    public void deleteFromLeft() {

    }

    public void deleteFromRight() {

    }

    public void cancelAction() {

    }
}
