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
import javafx.scene.input.MouseEvent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainPaneController implements Observer {

    private final BundleManager bundleManager = new BundleManager(new Locale("en"));

    private String leftCurrentPath;
    private String rightCurrentPath;

    private Task copyTask;
    private Task moveTask;
    private Task deleteTask;
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
    private TableColumn<SystemObject, ImageView> leftTableImageColumn;
    @FXML
    private TableColumn<SystemObject, String> leftTableNameColumn;
    @FXML
    private TableColumn<SystemObject, Long> leftTableSizeColumn;
    @FXML
    private TableColumn<SystemObject, Date> leftTableDateColumn;
    @FXML
    private TableColumn<SystemObject, String> leftTableTypeColumn;
    @FXML
    private TableColumn<SystemObject, ImageView> rightTableImageColumn;
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
    @FXML
    private ProgressBar progressBar;

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

    @FXML
    private void changeLanguageToEng() {
        polishMenuItem.setVisible(true);
        englishMenuItem.setVisible(false);
        bundleManager.changeAndNotify(new Locale("en"));
    }

    @FXML
    private void changeLanguageToPolish() {
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
                if (root.listFiles() != null)
                    rootsList.add(root.toString());
        }
        leftRootChoicebox.setItems(rootsList);
        rightRootChoicebox.setItems(rootsList);
        leftRootChoicebox.getSelectionModel().selectFirst();
        rightRootChoicebox.getSelectionModel().selectFirst();
    }

    private void prepareTables() {

        leftTableImageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        leftTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        leftTableDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        leftTableSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        leftTableTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        rightTableImageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        rightTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        rightTableDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        rightTableSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        rightTableTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));

        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void leftRootChoicebox_OnAction(ActionEvent e) {
        rootChoicebox_onAction(leftRootChoicebox, leftTableView, leftCurrentPathField);
    }

    public void rightRootChoicebox_OnAction(ActionEvent e) {
        rootChoicebox_onAction(rightRootChoicebox, rightTableView, rightCurrentPathField);
    }

    private void rootChoicebox_onAction(ChoiceBox rootChoiceBox, TableView<SystemObject> tableView, TextField currentPathField) {
        try {
            refreshTableView(rootChoiceBox.getSelectionModel().getSelectedItem().toString(), tableView, currentPathField);
            int leftChoiceBoxLastSelected = rootChoiceBox.getSelectionModel().getSelectedIndex();
            rootChoiceBox.getSelectionModel().select(leftChoiceBoxLastSelected);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void leftTableView_OnMouseClicked(MouseEvent event) {
        tableView_onMouseClicked(event, leftTableView, leftCurrentPathField);
    }

    public void rightTableView_OnMouseClicked(MouseEvent event) {
        tableView_onMouseClicked(event, rightTableView, rightCurrentPathField);
    }

    private void tableView_onMouseClicked(MouseEvent event, TableView<SystemObject> tableView, TextField currentPathField) {
        if (event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem().getFileName() != null) {
            String name = "";
            if (!currentPathField.getText().endsWith("\\"))
                name = "\\";
            name += tableView.getSelectionModel().getSelectedItem().getFileName();
            String path = currentPathField.getText() + name;
            refreshTableView(path, tableView, currentPathField);
        }
    }

    @FXML
    private void refreshLeft() {
        refreshTableView(leftCurrentPathField.getText(), leftTableView, leftCurrentPathField);
    }

    @FXML
    private void refreshRight() {
        refreshTableView(rightCurrentPathField.getText(), rightTableView, rightCurrentPathField);
    }

    private void refreshTableViews() {
        refreshLeft();
        refreshRight();
    }

    private void refreshTableView(String refreshPath, TableView<SystemObject> tableView, TextField pathTextField) {
        List<SystemObject> systemObjectList = new ArrayList<>();
        SystemObject rootFile = new SystemObject(new File(refreshPath), resourceBundle);
        String normalizedPath = FilenameUtils.normalize(rootFile.getFile().getAbsolutePath());
        rootFile = new SystemObject(new File(normalizedPath), resourceBundle);
        if (rootFile.exists() && rootFile.isDirectory()) {
            systemObjectList = rootFile.listSystemFiles(resourceBundle);
        }

        ObservableList<SystemObject> observableList = FXCollections.observableArrayList(systemObjectList);

        String rootParentName = rootFile.getFile().getParent();
        SystemObject rootParentFile;
        if (rootParentName != null) {
            rootParentFile = new SystemObject(new File(rootParentName), resourceBundle);
            rootParentFile.setFileName("\\..");
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
        copyFiles(selectedObjects, rightCurrentPathField.getText());
    }

    public void copyToLeft() {
        List<SystemObject> selectedObjects = rightTableView.getSelectionModel().getSelectedItems();
        copyFiles(selectedObjects, leftCurrentPathField.getText());
    }


    private void copyFiles(List<SystemObject> selectedObjects, String targetDirectoryPath) {
        for (SystemObject object : selectedObjects) {
            if (object.getTypeOfFile() != SystemObjectFileType.ROOT) {
                Path sourcePath = object.getFile().toPath();
                Path targetPath = new File(targetDirectoryPath + "\\" + sourcePath.getFileName()).toPath();

                File source = new File(sourcePath.toString());
                File dest = new File(targetPath.toString());

                try {
                    /* Check whether in the same directory*/
                    if (source.getCanonicalPath().equals(dest.getCanonicalPath())) {
                        return;
                    }
                    if (dest.exists())
                        dest = throwOverwriteAlert(targetPath);
                    if (dest != null) {
                        runCopyTask(source, dest);
                        activateProgressBar(copyTask);
                    }
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
            unique = true;
            for (File file : dirFiles) {
                if (file.getName().equals(newFileName)) {
                    newFileName = " (" + i + ")" + destFile.getName();
                    i++;
                    unique = false;
                }
            }
        }
        return Paths.get(destFile.getParent() + "\\" + newFileName);
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
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            refreshTableViews();
        });

        copyTask.setOnCancelled(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            refreshTableViews();
        });
    }

    public void moveToRight() {
        List<SystemObject> selectedObjects = leftTableView.getSelectionModel().getSelectedItems();
        moveFiles(selectedObjects, rightCurrentPathField.getText());
    }

    public void moveToLeft() {
        List<SystemObject> selectedObjects = rightTableView.getSelectionModel().getSelectedItems();
        moveFiles(selectedObjects, leftCurrentPathField.getText());
    }

    private void moveFiles(List<SystemObject> selectedObjects, String targetDirectoryPath) {
        for (SystemObject object : selectedObjects) {
            if (object.getTypeOfFile() != SystemObjectFileType.ROOT) {
                Path sourcePath = object.getFile().toPath();
                Path targetPath = new File(targetDirectoryPath + "\\" + sourcePath.getFileName()).toPath();

                File source = new File(sourcePath.toString());
                File dest = new File(targetPath.toString());

                try {
                    /* Check whether in the same directory*/
                    if (source.getCanonicalPath().equals(dest.getCanonicalPath())) {
                        return;
                    }
                    if (dest.exists())
                        dest = throwOverwriteAlert(targetPath);
                    if (dest != null) {
                        runMoveTask(source, dest);
                        activateProgressBar(moveTask);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void runMoveTask(File source, File dest) {

        moveTask = new Task() {
            @Override
            protected Void call() throws Exception {
                if (!source.isDirectory()) {
                    FileUtils.moveFile(source, dest);
                } else {
                    FileUtils.moveDirectory(source, dest);
                }
                return null;
            }
        };
        new Thread(moveTask).start();

        moveTask.setOnSucceeded(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            refreshTableViews();
        });

        moveTask.setOnCancelled(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            refreshTableViews();
        });
    }

    public void deleteFromLeft() {
        deleteFiles(leftTableView, leftCurrentPathField.getText());
    }

    public void deleteFromRight() {
        deleteFiles(rightTableView, rightCurrentPathField.getText());
    }

    private void deleteFiles(TableView tableView, String currentPath) {
        List<SystemObject> fileObjects;
        List<SystemObject> filesToDelete = new ArrayList<>();
        if ((fileObjects = tableView.getSelectionModel().getSelectedItems()) != null) {
            for (SystemObject fileObject : fileObjects) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(resourceBundle.getString("alertDeleteTitle"));
                alert.setHeaderText(resourceBundle.getString("alertDeleteHeader"));
                alert.setContentText(resourceBundle.getString("alertDeleteContent") + "\n" + fileObject.getFileName());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    filesToDelete.add(new SystemObject(fileObject.getFile(), resourceBundle));
                }
            }
            if (!filesToDelete.isEmpty()) {
                runDeleteTask(filesToDelete);
                activateProgressBar(deleteTask);
            }
        }
    }

    private void runDeleteTask(List<SystemObject> filesToDelete) {
        deleteTask = new Task() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000);
                for (SystemObject fileToDelete : filesToDelete) {
                    if (!fileToDelete.isDirectory()) {
                        FileUtils.forceDelete(fileToDelete.getFile());
                    } else {
                        FileUtils.deleteDirectory(fileToDelete.getFile());
                    }
                }
                return null;
            }
        };
        new Thread(deleteTask).start();

        deleteTask.setOnSucceeded(e -> {
//            waitingBoxStage.close();
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            refreshTableViews();
        });
        deleteTask.setOnCancelled(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            refreshTableViews();
        });
    }

    private void activateProgressBar(final Task<?> task) {
        progressBar.progressProperty().unbind();
        progressBar.setProgress(-1F);
        progressBar.progressProperty().bind(task.progressProperty());
    }


    public void cancelAction() {
        if (copyTask != null)
            copyTask.cancel();
        if (deleteTask != null)
            deleteTask.cancel();
        if (moveTask != null)
            moveTask.cancel();
    }
}