package dev.samir.vmdconverter;

import dev.samir.vmdconverter.utils.ConsoleOutput;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import dev.samir.vmdconverter.converter.VMDConverter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;


public class AppController {

    private static String fileName;

    @FXML
    private TextField inputField;

    @FXML
    private TextField outputField;

    @FXML
    private RadioButton rb3;

    @FXML
    private RadioButton rb4;

    @FXML
    private TextArea consoleLog;
    @FXML
    private ComboBox<String> comboBox;
    private final ObservableList<String> languagesList = FXCollections.observableArrayList("Deutsch (German)", "Englisch (US)");


    @FXML
    public void inputFieldBrowseButton(final ActionEvent actionEvent) {
        actionEvent.consume();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select your VMD File!");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("VMD File (*.vmd)", "*.vmd"));
        final File selectedFile = fileChooser.showOpenDialog(App.getScene().getWindow());
        if (selectedFile != null) {
            inputField.setText(selectedFile.getAbsolutePath());
            fileName = selectedFile.getName().replaceAll(".VMD", "").replace(".vmd", "");

        }
    }

    @FXML
    public void outputFieldBrowseButton(final ActionEvent actionEvent) {
        actionEvent.consume();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select your output file location!");
        final File selectedDirectory = directoryChooser.showDialog(App.getScene().getWindow());

        if (selectedDirectory != null) {
            outputField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    public void initialize(){
        ConsoleOutput consoleOutput = new ConsoleOutput(consoleLog);
        PrintStream consolePrintStream = new PrintStream(consoleOutput);
        System.setOut(consolePrintStream);
        comboBox.setItems(languagesList);

    }


    @FXML
    public void switchLanguage(final ActionEvent actionEvent) throws IOException {
        actionEvent.consume();
        if (comboBox.getValue().equals(languagesList.get(0))) {
            App.setRoot("german");
            return;
        }
        App.setRoot("englisch");
    }


    @FXML
    public void startButton(final ActionEvent actionEvent) {
        actionEvent.consume();
        if (inputField.getText().isEmpty() || inputField.getText() == null) {
            showErrorMessage();
            return;
        }
        if (outputField.getText().isEmpty() || outputField.getText() == null) {
            showErrorMessage();
            return;
        }
        if (!rb3.isSelected() && !rb4.isSelected()) {
            showErrorMessage();
            return;
        }

        if (rb4.isSelected()) {
            Task<Void> conversionTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    VMDConverter.convertVideoMP4(inputField.getText(), outputField.getText());
                    return null;
                }
            };
            ConsoleOutput consoleOutput = new ConsoleOutput(consoleLog);
            PrintStream consolePrintStream = new PrintStream(consoleOutput);
            System.setOut(consolePrintStream);

            Thread conversionThread = new Thread(conversionTask);
            conversionThread.setDaemon(true);
            conversionThread.start();

            return;
        }
        if (rb3.isSelected()) {
            Task<Void> conversionTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    VMDConverter.convertVideoMP3(inputField.getText(), outputField.getText());
                    return null;
                }
            };
            ConsoleOutput consoleOutput = new ConsoleOutput(consoleLog);
            PrintStream consolePrintStream = new PrintStream(consoleOutput);
            System.setOut(consolePrintStream);

            Thread conversionThread = new Thread(conversionTask);
            conversionThread.setDaemon(true);
            conversionThread.start();

        }
    }


    private void showErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Look, an Error Dialog");
        alert.setContentText("Please fill out all fields properly.");
        alert.showAndWait();
    }

    public static String getFilename() {
        return fileName;
    }
}

