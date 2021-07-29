package ru.saiushev.sps.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import ru.saiushev.sps.utils.AnswerTask;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MainController {
    @FXML
    private Button btnFind;
    @FXML
    private Button btnCancel;
    @FXML
    private Label labelTip;
    @FXML
    private TextField inputPath;
    @FXML
    private TextField inputWord;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ComboBox extensionList;
    @FXML
    private ComboBox roots;
    @FXML
    private TableView tableView;
    private Thread thread;

    @FXML
    void initialize(){
        ObservableList<String> list_root = FXCollections.observableArrayList();
        File[] local_roots = File.listRoots();
        for (File file: local_roots) {
            list_root.add(file.getAbsolutePath());
        }
        roots.setItems(list_root);
        roots.setValue(list_root.get(0));
        TableColumn<Map.Entry<String, Integer>, Integer> numberColumn = new TableColumn<>("Number of entries");
        numberColumn.setCellValueFactory((p) -> {
            return new ReadOnlyObjectWrapper<>(p.getValue().getValue());
        });
        numberColumn.setStyle("-fx-background-color: rgba(255, 255, 255, 0)");

        TableColumn<Map.Entry<String, Integer>, String> pathColumn = new TableColumn<>("Path to file");
        pathColumn.setCellValueFactory((p)->{
            return new SimpleStringProperty(p.getValue().getKey());
        });
        pathColumn.setStyle("-fx-background-color: rgba(255, 255, 255, 0)");

        tableView.getColumns().add(numberColumn);
        tableView.getColumns().add(pathColumn);
        numberColumn.setPrefWidth(145);
        pathColumn.setPrefWidth(400);
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    if(!tableView.getSelectionModel().getSelectedItem().toString().equals(null)){
                        Desktop.getDesktop().open(new File(tableView.getSelectionModel().getSelectedItem().toString().substring(0, tableView.getSelectionModel().getSelectedItem().toString().lastIndexOf("="))));
                        tableView.getSelectionModel().clearSelection();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void findAction() throws IOException {
        String path = roots.getValue()+inputPath.getText().trim().replaceAll("[\\s]", "");
        String word = inputWord.getText().trim();
        if(path.isEmpty() || word.isEmpty()){
            labelTip.setText("Write a path and a word!");
        }else{
            labelTip.setText("");
            changeButton(false);
            progressIndicator.setVisible(true);

            Task<Map<String, Integer>> task = new AnswerTask(path, extensionList.getValue() == null ? "pptx" : extensionList.getValue().toString(), word);
            thread = new Thread(task);
            thread.start();
            tableView.getItems().clear();
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, workerStateEvent -> {
                task.getValue().entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).forEach(x -> {
                    if(x.getValue() != 0){
                        tableView.getItems().add(x);
                    }
                });
                changeButton(true);
            });
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, workerStateEvent -> {
                String result = task.getException().getMessage();
                Platform.runLater(() -> labelTip.setText(result));
                changeButton(true);
            });
        }
    }
    @FXML
    private void cancelAction(){
        thread.interrupt();
        changeButton(true);
    }

    public void changeButton(Boolean isVisible){
        btnFind.setVisible(isVisible);
        btnCancel.setVisible(!isVisible);
        progressIndicator.setVisible(!isVisible);
    }
}