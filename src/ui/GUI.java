package ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.UnaryOperator;

import algorithm.heuristic.BlockingCarsHeuristic;
import algorithm.heuristic.CombinedHeuristic;
import algorithm.heuristic.ManhattanToExitHeuristic;
import algorithm.pathfinding.AStar;
import algorithm.pathfinding.GreedyBestFirst;
import algorithm.pathfinding.HillClimbing;
import algorithm.pathfinding.IDDFS;
import algorithm.pathfinding.Pathfinder;
import algorithm.pathfinding.UCS;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.Board;
import model.State;
import utils.InputParser;
import utils.OutputWriter;


public class GUI extends Application {
    public String algorithm;
    public String heuristic;
    public String filePath;
    public Board board;
    public Pathfinder solver;
    public List<State> solution;
    public double animationDelay; 

    public Stage primaryStage;
    public StackPane boardDisplayArea;

    static int FILE_PATH_FIELD_WIDTH = 300;

    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Rush Hour Solver");
        initGui();
        this.primaryStage.show();
        this.primaryStage.centerOnScreen();
    }


    public void initGui(){
        Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(70),
            ae -> titleScreen()));
        timeline.play();titleScreen();
    }


    public void titleScreen(){
        Label titleLabel = new Label("Rush Hour Solver");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        Label authorLabel = new Label("By: 13523035, 13523117");
        authorLabel.setFont(Font.font("Arial", 18));

        VBox titleBox = new VBox(10);
        titleBox.getChildren().addAll(titleLabel, authorLabel);
        titleBox.setAlignment(Pos.CENTER);

        Button startButton = GUIHelper.createButton("Start", e -> {userInputs();});
        startButton.setPrefWidth(170);
        startButton.setPrefHeight(40);

        Button exitButton = GUIHelper.createButton("Exit", e -> {this.primaryStage.close();});
        exitButton.setPrefWidth(170);
        exitButton.setPrefHeight(40);

        VBox buttonBox = new VBox(30);
        buttonBox.getChildren().addAll(startButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(150, 20, 20, 20));

        mainLayout.setTop(titleBox);
        BorderPane.setAlignment(titleBox, Pos.CENTER);

        mainLayout.setCenter(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        Scene scene = new Scene(mainLayout, 800, 600);
        this.primaryStage.setScene(scene);
        this.primaryStage.centerOnScreen(); 
    }


    public void userInputs(){
        // Algorithms Elements
        Label algoLabel = new Label("Choose your algorithm:");
        algoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        ToggleGroup algoToggleGroup = new ToggleGroup();

        RadioButton aStarRadio = new RadioButton("A* Search");
        aStarRadio.setToggleGroup(algoToggleGroup);
        aStarRadio.setUserData("A* Search");
        aStarRadio.setFont(Font.font("Arial", 16));

        RadioButton greedyRadio = new RadioButton("Greedy Best First Search");
        greedyRadio.setToggleGroup(algoToggleGroup);
        greedyRadio.setUserData("Greedy Best First Search");
        greedyRadio.setFont(Font.font("Arial", 16));

        RadioButton ucsRadio = new RadioButton("Uniform Cost Search");
        ucsRadio.setToggleGroup(algoToggleGroup);
        ucsRadio.setUserData("Uniform Cost Search");
        ucsRadio.setFont(Font.font("Arial", 16));

        RadioButton iddfsRadio = new RadioButton("Iterative Deepening Search");
        iddfsRadio.setToggleGroup(algoToggleGroup);
        iddfsRadio.setUserData("Iterative Deepening Search");
        iddfsRadio.setFont(Font.font("Arial", 16));

        RadioButton hillClimbRadio = new RadioButton("Hill Climbing Search");
        hillClimbRadio.setToggleGroup(algoToggleGroup);
        hillClimbRadio.setUserData("Hill Climbing Search");
        hillClimbRadio.setFont(Font.font("Arial", 16));

        aStarRadio.setSelected(true);
        this.algorithm = "A* Search";

        algoToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                this.algorithm = (String) newVal.getUserData();
                System.out.println("Algorithm selected: " + this.algorithm);
            }
        });

        VBox radioButtonBox = new VBox(10, aStarRadio, greedyRadio, ucsRadio, iddfsRadio,hillClimbRadio);
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
        filePathField.setPrefHeight(GUIHelper.INPUT_BTN_HEIGHT);
        filePathField.setEditable(false);

        
        Button browseButton = GUIHelper.createButton("Browse...", e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Configuration File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
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

        
        Button nextButton = GUIHelper.createButton("Next", e -> {
             this.filePath = filePathField.getText();

            if (this.algorithm == null || this.algorithm.isEmpty()) {
                System.out.println("Please select an algorithm.");
                return;
            }
            if (this.filePath == null || this.filePath.isEmpty()) {
                System.out.println("Please enter or browse for a configuration file.");
                return;
            }
            if (this.algorithm.equals("A* Search") || this.algorithm.equals("Greedy Best First Search") || this.algorithm.equals("Hill Climbing Search")){
                heuristicInput();
            } else {
                processInput();
            }
        });

        
        Button backButton = GUIHelper.createButton("Back", e-> {titleScreen();});

        HBox navigationButtonBox = new HBox(20, backButton, nextButton);
        navigationButtonBox.setAlignment(Pos.CENTER);

        VBox userInputLayout = new VBox(25);
        userInputLayout.setPadding(new Insets(30));
        userInputLayout.setAlignment(Pos.CENTER);
        userInputLayout.getChildren().addAll(algoBox, fileLabel, fileInputBox, navigationButtonBox);

        Scene userInputScene = new Scene(userInputLayout, 800, 600);
        this.primaryStage.setScene(userInputScene);
        this.primaryStage.centerOnScreen(); 
    }


    public void heuristicInput(){
        Label heuristicLabel = new Label("Choose your Heuristic:");
        heuristicLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        ToggleGroup heuristicToggleGroup = new ToggleGroup();

        RadioButton blockingCarsRadio = new RadioButton("Blocking Cars");
        blockingCarsRadio.setToggleGroup(heuristicToggleGroup);
        blockingCarsRadio.setUserData("Blocking Cars");
        blockingCarsRadio.setFont(Font.font("Arial", 16));

        RadioButton manhattanRadio = new RadioButton("Manhattan To Exit");
        manhattanRadio.setToggleGroup(heuristicToggleGroup);
        manhattanRadio.setUserData("Manhattan To Exit");
        manhattanRadio.setFont(Font.font("Arial", 16));

        RadioButton combinedRadio = new RadioButton("Combined Heuristic");
        combinedRadio.setToggleGroup(heuristicToggleGroup);
        combinedRadio.setUserData("Combined Heuristic");
        combinedRadio.setFont(Font.font("Arial", 16));

        blockingCarsRadio.setSelected(true);
        this.heuristic = "Blocking Cars";

        heuristicToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                this.heuristic = (String) newVal.getUserData();
                System.out.println("Heuristic Selected: " + this.heuristic);
            }
        });

        VBox radioButtonBox = new VBox(10, blockingCarsRadio, manhattanRadio, combinedRadio);
        radioButtonBox.setAlignment(Pos.CENTER_LEFT);

        HBox radioContainer = new HBox(radioButtonBox);
        radioContainer.setAlignment(Pos.CENTER);
        radioContainer.setMinWidth(300);

        HBox labelContainer = new HBox(heuristicLabel);
        labelContainer.setAlignment(Pos.CENTER);

        VBox heuristicBox = new VBox(10, labelContainer, radioContainer);
        heuristicBox.setAlignment(Pos.CENTER);
        // End of Heuristic Elements

        Button nextButton = GUIHelper.createButton("Next", e->{
            if (this.heuristic == null || this.heuristic.isEmpty()) {
                System.out.println("Please select a heuristic.");
                return;
            }
            processInput();
        });

        Button backButton = GUIHelper.createButton("Back", e->{userInputs();});

        HBox navigationButtonBox = new HBox(20, backButton, nextButton);
        navigationButtonBox.setAlignment(Pos.CENTER);

        VBox userInputLayout = new VBox(25);
        userInputLayout.setPadding(new Insets(30));
        userInputLayout.setAlignment(Pos.CENTER);
        userInputLayout.getChildren().addAll(heuristicBox, navigationButtonBox);

        Scene userInputScene = new Scene(userInputLayout, 800, 600);
        this.primaryStage.setScene(userInputScene);
        this.primaryStage.centerOnScreen(); 
    }


    public void processInput(){
        try {
            this.board = InputParser.parseFromFile(this.filePath);
            System.out.println("Berhasil membaca file.");
            this.board.print();

            initSearch();
        } catch (IOException e) {
            inputError(e.getMessage(), true);
        }
    }


    public void initSearch() {
        this.boardDisplayArea = new StackPane();

        VBox boardViewFromDrawBoard = GUIHelper.drawBoard(this.board);
        if (boardViewFromDrawBoard != null) {
            this.boardDisplayArea.getChildren().add(boardViewFromDrawBoard);
        }

        Label algoDisplayLabel = new Label("Algorithm: " + this.algorithm);
        algoDisplayLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.getChildren().add(algoDisplayLabel);

        if (("A* Search".equals(this.algorithm) || "Greedy Best First Search".equals(this.algorithm) || "Hill Climbing Search".equals(this.algorithm))
            && this.heuristic != null && !this.heuristic.isEmpty()) {
            Label heuristicDisplayLabel = new Label("Heuristic: " + this.heuristic);
            heuristicDisplayLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            infoBox.getChildren().add(heuristicDisplayLabel);
        }
        
        Label initialBoardLabel = new Label("Initial Board");
        initialBoardLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label delayInputLabel = new Label("Animation Delay (ms):");
        delayInputLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        this.animationDelay = 100;
        TextField delayTextField = new TextField();
        delayTextField.setPrefWidth(80);
        delayTextField.setPromptText("100");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*\\.?\\d*")) { // digits and only one decimal point
                return change;
            }
            return null;
        };
        TextFormatter<Double> delayFormatter = new TextFormatter<>(new StringConverter<Double>() {
                @Override
                public String toString(Double object) {
                    return object == null ? "" : object.toString();
                }
                @Override
                public Double fromString(String string) {
                    try {
                        return Double.parseDouble(string);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }, this.animationDelay, filter);
        delayTextField.setTextFormatter(delayFormatter);
        delayTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double delay = Double.parseDouble(newVal);
                if (delay > 0) {this.animationDelay = delay;}
            } catch (NumberFormatException e) {
                System.out.println("Invalid delay input: " + newVal);
            }
        });
        
        HBox delayInputBox = new HBox(10, delayInputLabel, delayTextField);
        delayInputBox.setAlignment(Pos.CENTER);
        
        
        Button startSearchButton = GUIHelper.createButton("Start", e -> {});
        Button backButton = GUIHelper.createButton("Back", e -> {userInputs();});
        startSearchButton.setOnAction(e -> {
            startSearchButton.setDisable(true);
            backButton.setDisable(true);
            startSearch();
        });


        HBox buttonControlBox = new HBox(20, backButton, startSearchButton);
        buttonControlBox.setPadding(new Insets(0,0,40,0));
        buttonControlBox.setAlignment(Pos.CENTER);

        VBox searchScreenLayout = new VBox(20);
        searchScreenLayout.setAlignment(Pos.CENTER);

        searchScreenLayout.getChildren().add(initialBoardLabel);
        searchScreenLayout.getChildren().add(delayInputBox);
        if (this.boardDisplayArea != null) {
            searchScreenLayout.getChildren().add(this.boardDisplayArea);
        }
        searchScreenLayout.getChildren().add(infoBox);
        searchScreenLayout.getChildren().add(buttonControlBox);

        double sceneWidth = 800;
        double sceneHeight = 800;
        if (this.primaryStage.getScene() != null) {
            sceneWidth = Math.max(sceneWidth, this.primaryStage.getScene().getWidth());
            sceneHeight = Math.max(sceneHeight, this.primaryStage.getScene().getHeight());
        }

        Scene searchScene = new Scene(searchScreenLayout, sceneWidth, sceneHeight);
        this.primaryStage.setScene(searchScene);
        this.primaryStage.centerOnScreen();
    }


    public void startSearch() {
        this.solver = null;
        if ("Uniform Cost Search".equals(this.algorithm)) {
            solver = new UCS();
        } else if ("Greedy Best First Search".equals(this.algorithm)) {
            if ("Blocking Cars".equals(this.heuristic)) {
                solver = new GreedyBestFirst(new BlockingCarsHeuristic());
            } else if ("Manhattan To Exit".equals(this.heuristic)) {
                solver = new GreedyBestFirst(new ManhattanToExitHeuristic());
            } else if("Combined Heuristic".equals(this.heuristic)){
                solver = new GreedyBestFirst(new CombinedHeuristic());
            }
        } else if ("A* Search".equals(this.algorithm)) {
            if ("Blocking Cars".equals(this.heuristic)) {
                solver = new AStar(new BlockingCarsHeuristic());
            } else if ("Manhattan To Exit".equals(this.heuristic)) {
                solver = new AStar(new ManhattanToExitHeuristic());
            } else if("Combined Heuristic".equals(this.heuristic)){
                solver = new AStar(new CombinedHeuristic());
            }
        } else if ("Iterative Deepening Search".equals(this.algorithm)){
            solver = new IDDFS();
        } else if ("Hill Climbing Search".equals(this.algorithm)){
             if ("Blocking Cars".equals(this.heuristic)) {
                solver = new HillClimbing(new BlockingCarsHeuristic());
            } else if ("Manhattan To Exit".equals(this.heuristic)) {
                solver = new HillClimbing(new ManhattanToExitHeuristic());
            } else if("Combined Heuristic".equals(this.heuristic)){
                solver = new HillClimbing(new CombinedHeuristic());
            }
        }
        this.solution = this.solver.solve(this.board);
        if (this.solution.isEmpty()) {
            inputError("No solution found.", false);
            return;
        }
        animateSolution();
    }


    public void animateSolution() {
        Timeline timeline = new Timeline();
        double totalDuration = 0;
        
        for (int i = 0; i < this.solution.size(); i++) {
            final State currentState = this.solution.get(i);
            final State prevState = (i > 0) ? this.solution.get(i-1) : null;
            
            if (prevState != null) {
                String move = currentState.move;
                if (move != null && !move.equals("Start")) {
                    String[] parts = move.split("-");
                    if (parts.length >= 2) {
                        char pieceId = parts[0].charAt(0);
                        String direction = parts[1];
                        int steps = (parts.length > 2) ? Integer.parseInt(parts[2]) : 1;
                        
                        for (int step = 1; step <= steps; step++) {
                            final int currentStep = step;
                            KeyFrame intermediateFrame = new KeyFrame(
                                Duration.millis(totalDuration + step * this.animationDelay),
                                event -> {
                                    Board intermediateBoard = Board.createIntermediateBoard(prevState.board, currentState.board, pieceId, direction, currentStep, steps);
                                    
                                    VBox newBoardView = GUIHelper.drawBoard(intermediateBoard);
                                    if (newBoardView != null) {
                                        this.boardDisplayArea.getChildren().clear();
                                        newBoardView.setAlignment(Pos.CENTER);
                                        this.boardDisplayArea.getChildren().add(newBoardView);
                                    }
                                }
                            );
                            timeline.getKeyFrames().add(intermediateFrame);
                        }
                        totalDuration += steps * this.animationDelay;
                    }
                }
            } else {
                KeyFrame startFrame = new KeyFrame(Duration.ZERO, event -> {
                    VBox newBoardView = GUIHelper.drawBoard(currentState.board);
                    if (newBoardView != null) {
                        this.boardDisplayArea.getChildren().clear();
                        newBoardView.setAlignment(Pos.CENTER);
                        this.boardDisplayArea.getChildren().add(newBoardView);
                    }
                });
                timeline.getKeyFrames().add(startFrame);
            }
            
            if (i == this.solution.size() - 1) {
                this.board = currentState.board;
            }
        }
        
        timeline.setOnFinished(event -> {
            System.out.println("Animation finished.");
            solutionsFound();
        });
        
        System.out.println("Playing animation with " + timeline.getKeyFrames().size() + " frames.");
        timeline.play();
    }


    public void solutionsFound(){
        this.boardDisplayArea.getChildren().clear();

        VBox finalBoardView = GUIHelper.drawBoard(this.board);
        if (finalBoardView != null) {
            finalBoardView.setAlignment(Pos.CENTER);
            this.boardDisplayArea.getChildren().add(finalBoardView);
        }

        Label timeLabel = new Label("Execution time: " + this.solver.getRuntimeNano()/1e6 + "ms");
        timeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        Label nodesLabel = new Label("Nodes visited: " + this.solver.getNodes());
        nodesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(timeLabel, nodesLabel);
        infoBox.setAlignment(Pos.CENTER);


        Label finalStateTitleLabel = new Label("Solution Found");
        finalStateTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        finalStateTitleLabel.setPadding(new Insets(10, 0, 0, 0));
        
        Button backToTitleButton = GUIHelper.createButton("Back", e -> userInputs());
        Button replayButton = GUIHelper.createButton("Replay", e -> {
            if (this.solution != null && !this.solution.isEmpty()) {
                System.out.println("Replay button clicked!");
                ((Button)e.getSource()).setDisable(true);
                backToTitleButton.setDisable(true);

                animateSolution();
            } else {
                inputError("No solution path available to replay.", false);
            }
        });
        Button saveButton = GUIHelper.createButton("Save", e->{});
        saveButton.setOnAction(e -> {saveToFile(this.solution, this.algorithm, this.solver.getRuntimeNano()/1e6 , saveButton);});

        HBox buttonControlBox = new HBox(20, backToTitleButton, replayButton, saveButton);
        buttonControlBox.setAlignment(Pos.CENTER);
        buttonControlBox.setPadding(new Insets(0, 0, 50, 0));

        VBox solutionScreenLayout = new VBox(20);
        solutionScreenLayout.setAlignment(Pos.CENTER);
        solutionScreenLayout.setPadding(new Insets(20));
        solutionScreenLayout.getChildren().addAll(finalStateTitleLabel, this.boardDisplayArea, infoBox, buttonControlBox);

        double sceneWidth = 800;
        double sceneHeight = 700;
        if (this.primaryStage.getScene() != null) {
            sceneWidth = Math.max(sceneWidth, this.primaryStage.getScene().getWidth());
            sceneHeight = Math.max(sceneHeight, this.primaryStage.getScene().getHeight());
        }

        Scene solutionScene = new Scene(solutionScreenLayout, sceneWidth, sceneHeight);
        this.primaryStage.setScene(solutionScene);
        this.primaryStage.centerOnScreen();
    }


    public void saveToFile(List<State> solution, String algorithmName, double executionTime, Button callerButton){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Solution As");

        // Default file name
        String initialFileName = "solution.txt";

        fileChooser.setInitialFileName(initialFileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));

        Stage stage = (Stage) callerButton.getScene().getWindow();
        File fileToSave = fileChooser.showSaveDialog(stage);
        if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".txt")) fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
        if (fileToSave != null) {
            try {
                String outputPath = OutputWriter.writeSolution( solution, algorithmName, executionTime,  fileToSave.getAbsolutePath());

                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Solution saved successfully to:\n" + outputPath);
                successAlert.showAndWait();
            } catch (Exception ex) {
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("An unexpected error occurred while saving");
                errorAlert.setContentText(ex.getMessage());
                errorAlert.showAndWait();
            }
        } else {
            System.out.println("Save operation cancelled by user.");
        }
    }


    // initially just for error, but used for no solutions too
    public void inputError(String errorMessage, boolean isError) {
        String label;
        if (isError) {
            label = "Error";
        } else {
            label = "Result:";
        }
        Label errorTitleLabel = new Label(label);
        errorTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        errorTitleLabel.setTextFill(Color.RED);

        Label errorMessageLabel = new Label(errorMessage);
        errorMessageLabel.setFont(Font.font("Arial", 16));
        errorMessageLabel.setWrapText(true);
        errorMessageLabel.setTextAlignment(TextAlignment.CENTER);
        errorMessageLabel.setMaxWidth(400);

        Button backButton = GUIHelper.createButton("Back", e-> {userInputs();});

        VBox errorLayout = new VBox(30);
        errorLayout.getChildren().addAll(errorTitleLabel, errorMessageLabel, backButton);
        errorLayout.setAlignment(Pos.CENTER);
        errorLayout.setPadding(new Insets(40));

        Scene errorScene = new Scene(errorLayout, 500, 300);
        this.primaryStage.setScene(errorScene);
        this.primaryStage.centerOnScreen();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
