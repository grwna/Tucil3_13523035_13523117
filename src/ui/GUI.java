package ui;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Board;
import parser.InputParser;

public class GUI extends Application {
    public String algorithm;
    public String filePath;
    public Stage primaryStage;
    static int TITLE_BTN_WIDTH = 170;
    static int TITLE_BTN_HEIGHT = 40;
    static int FILE_PATH_FIELD_WIDTH = 300;
    static int INPUT_BTN_WIDTH = 120;
    static int INPUT_BTN_HEIGHT = 30;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Rush Hour Solver");
        titleScreen();
        this.primaryStage.show();
        this.primaryStage.centerOnScreen();
    }

    public void titleScreen(){
        Label titleLabel = new Label("Rush Hour Solver");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        Label authorLabel = new Label("By: 13523035, 13523117");
        authorLabel.setFont(Font.font("Arial", 18));

        VBox titleBox = new VBox(10);
        titleBox.getChildren().addAll(titleLabel, authorLabel);
        titleBox.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start");
        startButton.setPrefWidth(TITLE_BTN_WIDTH);
        startButton.setPrefHeight(TITLE_BTN_HEIGHT);
        startButton.setOnAction(e -> {
            System.out.println("Start button clicked on title screen!");
            userInputs();
        });

        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(TITLE_BTN_WIDTH);
        exitButton.setPrefHeight(TITLE_BTN_HEIGHT);
        exitButton.setOnAction(e -> {
            System.out.println("Exit button clicked!");
            this.primaryStage.close();
        });

        VBox buttonBox = new VBox(30);
        buttonBox.getChildren().addAll(startButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(150, 20, 20, 20));

        mainLayout.setTop(titleBox);
        BorderPane.setAlignment(titleBox, Pos.CENTER);
        // BorderPane.setMargin(titleBox, new Insets(0, 0, 0, 0));

        mainLayout.setCenter(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        Scene scene = new Scene(mainLayout, 800, 600);
        this.primaryStage.setScene(scene);
    }

    public void userInputs(){
        // Algorithms Elements
        Label algoLabel = new Label("Choose your algorithm:");
        algoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        ToggleGroup algoToggleGroup = new ToggleGroup();

        RadioButton aStarRadio = new RadioButton("A* Search");
        aStarRadio.setToggleGroup(algoToggleGroup);
        aStarRadio.setUserData("A*");

        RadioButton greedyRadio = new RadioButton("Greedy Best First Search");
        greedyRadio.setToggleGroup(algoToggleGroup);
        greedyRadio.setUserData("Greedy");

        RadioButton ucsRadio = new RadioButton("Uniform Cost Search");
        ucsRadio.setToggleGroup(algoToggleGroup);
        ucsRadio.setUserData("UCS");

        aStarRadio.setSelected(true);
        this.algorithm = "A*";

        algoToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                this.algorithm = (String) newVal.getUserData();
                System.out.println("Algorithm selected: " + this.algorithm);
            }
        });

        VBox radioButtonBox = new VBox(10, aStarRadio, greedyRadio, ucsRadio);
        radioButtonBox.setAlignment(Pos.CENTER_LEFT);

        HBox radioContainer = new HBox(radioButtonBox);
        radioContainer.setAlignment(Pos.CENTER);
        radioContainer.setMinWidth(300);

        HBox labelContainer = new HBox(algoLabel);
        labelContainer.setAlignment(Pos.CENTER);

        VBox algoBox = new VBox(10, labelContainer, radioContainer);
        algoBox.setAlignment(Pos.CENTER);
        // End of Algorithm Elements

        // File Input Elements
        Label fileLabel = new Label("Browse your board configuration file:");
        fileLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        TextField filePathField = new TextField();
        filePathField.setPromptText("e.g., /path/to/your/puzzle.txt");
        filePathField.setPrefWidth(FILE_PATH_FIELD_WIDTH);
        filePathField.setPrefHeight(INPUT_BTN_HEIGHT);
        filePathField.setEditable(false);

        Button browseButton = new Button("Browse...");
        browseButton.setPrefWidth(INPUT_BTN_WIDTH);
        browseButton.setPrefHeight(INPUT_BTN_HEIGHT);
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Configuration File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showOpenDialog(this.primaryStage);
            if (selectedFile != null) {
                filePathField.setText(selectedFile.getAbsolutePath());
                this.filePath = selectedFile.getAbsolutePath();
                System.out.println("File selected: " + this.filePath);
            }
        });

        HBox fileInputBox = new HBox(10, filePathField, browseButton);
        fileInputBox.setAlignment(Pos.CENTER);

        // End of File Input Elements

        Button nextButton = new Button("Next");
        nextButton.setPrefWidth(INPUT_BTN_WIDTH);
        nextButton.setPrefHeight(INPUT_BTN_HEIGHT);
        nextButton.setOnAction(e -> {
            this.filePath = filePathField.getText();

            if (this.algorithm == null || this.algorithm.isEmpty()) {
                System.out.println("Please select an algorithm.");
                return;
            }
            if (this.filePath == null || this.filePath.isEmpty()) {
                System.out.println("Please enter or browse for a configuration file.");
                return;
            }

            System.out.println("Next button clicked!");
            System.out.println("Chosen Algorithm: " + this.algorithm);
            System.out.println("Configuration File: " + this.filePath);

            Label nextScreenLabel = new Label("Algorithm : " + this.algorithm + ", File: " + this.filePath);
            nextScreenLabel.setWrapText(true);
            Scene tempNextScene = new Scene(new VBox(20, nextScreenLabel), 500, 400);
            this.primaryStage.setScene(tempNextScene);
            processInput();
        });

        Button backButton = new Button("Back");
        backButton.setPrefWidth(INPUT_BTN_WIDTH);
        backButton.setPrefHeight(INPUT_BTN_HEIGHT);
        backButton.setOnAction(e -> {
            System.out.println("Back button clicked, navigate to previous screen.");
            titleScreen();
        });

        HBox navigationButtonBox = new HBox(20, backButton, nextButton);
        navigationButtonBox.setAlignment(Pos.CENTER);

        VBox userInputLayout = new VBox(25);
        userInputLayout.setPadding(new Insets(30));
        userInputLayout.setAlignment(Pos.CENTER);
        userInputLayout.getChildren().addAll(algoBox, fileLabel, fileInputBox, navigationButtonBox);

        Scene userInputScene = new Scene(userInputLayout, 800, 600);
        this.primaryStage.setScene(userInputScene);
    }

    public void processInput(){
        Board initialBoard = null;
        try {
            initialBoard = InputParser.parseFromFile(this.filePath);
            System.out.println("Berhasil membaca file.");
            initialBoard.print();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
        }
    }

    public void drawBoard(Board board) {
        // Implementasi untuk menggambar papan permainan
        // Anda dapat menggunakan JavaFX untuk menggambar papan dan mobil
        // Misalnya, menggunakan GridPane untuk menampilkan grid
    }

    public void inputError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.initOwner(this.primaryStage);

        alert.showAndWait();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
