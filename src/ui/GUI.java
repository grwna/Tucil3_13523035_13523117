package ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithm.AStar;
import algorithm.BlockingCarsHeuristic;
import algorithm.GreedyBestFirst;
import algorithm.ManhattanToExitHeuristic;
import algorithm.Pathfinder;
import algorithm.UCS;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Board;
import model.State;
import parser.InputParser;

public class GUI extends Application {
    public String algorithm;
    public String heuristic;
    public String filePath;
    public Board board;
    public Stage primaryStage;
    public StackPane boardDisplayArea;
    static int TITLE_BTN_WIDTH = 170;
    static int TITLE_BTN_HEIGHT = 40;
    static int FILE_PATH_FIELD_WIDTH = 300;
    static int INPUT_BTN_WIDTH = 120;
    static int INPUT_BTN_HEIGHT = 30;
    final static private Map<Character, Color> pieceColors = new HashMap<>();
    static {
        pieceColors.put('A', Color.CYAN); pieceColors.put('B', Color.MAGENTA); pieceColors.put('C', Color.YELLOW);
        pieceColors.put('D', Color.ORANGE); pieceColors.put('E', Color.SPRINGGREEN); pieceColors.put('F', Color.DODGERBLUE);
        pieceColors.put('G', Color.VIOLET); pieceColors.put('H', Color.GOLD); pieceColors.put('I', Color.LIGHTPINK);
        pieceColors.put('J', Color.TURQUOISE); pieceColors.put('L', Color.SALMON); pieceColors.put('M', Color.KHAKI);  
        pieceColors.put('N', Color.SKYBLUE); pieceColors.put('O', Color.PLUM); pieceColors.put('Q', Color.LIGHTGREEN);
        pieceColors.put('R', Color.CORAL); pieceColors.put('S', Color.HOTPINK); pieceColors.put('T', Color.DARKVIOLET);
        pieceColors.put('U', Color.BEIGE); pieceColors.put('V', Color.ORANGERED); pieceColors.put('W', Color.BROWN);
        pieceColors.put('X', Color.BLUE); pieceColors.put('Y', Color.AZURE); pieceColors.put('Z', Color.OLIVEDRAB);
    }


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
        aStarRadio.setUserData("A*");
        aStarRadio.setFont(Font.font("Arial", 16));

        RadioButton greedyRadio = new RadioButton("Greedy Best First Search");
        greedyRadio.setToggleGroup(algoToggleGroup);
        greedyRadio.setUserData("Greedy");
        greedyRadio.setFont(Font.font("Arial", 16));

        RadioButton ucsRadio = new RadioButton("Uniform Cost Search");
        ucsRadio.setToggleGroup(algoToggleGroup);
        ucsRadio.setUserData("UCS");
        ucsRadio.setFont(Font.font("Arial", 16));

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
            if (this.algorithm.equals("A*") || this.algorithm.equals("Greedy")){
                heuristicInput();
            } else {
                processInput();
            }
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
        this.primaryStage.centerOnScreen(); 
    }

    public void heuristicInput(){
        Label heuristicLabel = new Label("Choose your Heuristic:");
        heuristicLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        ToggleGroup heuristicToggleGroup = new ToggleGroup();

        RadioButton blockingCarsRadio = new RadioButton("Blocking Cars");
        blockingCarsRadio.setToggleGroup(heuristicToggleGroup);
        blockingCarsRadio.setUserData("Blocking");
        blockingCarsRadio.setFont(Font.font("Arial", 16));

        RadioButton manhattanRadio = new RadioButton("Manhattan To Exit");
        manhattanRadio.setToggleGroup(heuristicToggleGroup);
        manhattanRadio.setUserData("Manhattan");
        manhattanRadio.setFont(Font.font("Arial", 16));

        blockingCarsRadio.setSelected(true);
        this.heuristic = "Blocking";

        heuristicToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                this.heuristic = (String) newVal.getUserData();
                System.out.println("Heuristic Selected: " + this.heuristic);
            }
        });

        VBox radioButtonBox = new VBox(10, blockingCarsRadio, manhattanRadio);
        radioButtonBox.setAlignment(Pos.CENTER_LEFT);

        HBox radioContainer = new HBox(radioButtonBox);
        radioContainer.setAlignment(Pos.CENTER);
        radioContainer.setMinWidth(300);

        HBox labelContainer = new HBox(heuristicLabel);
        labelContainer.setAlignment(Pos.CENTER);

        VBox heuristicBox = new VBox(10, labelContainer, radioContainer);
        heuristicBox.setAlignment(Pos.CENTER);
        // End of Heuristic Elements

        Button nextButton = new Button("Next");
        nextButton.setPrefWidth(INPUT_BTN_WIDTH);
        nextButton.setPrefHeight(INPUT_BTN_HEIGHT);
        nextButton.setOnAction(e -> {
            if (this.heuristic == null || this.heuristic.isEmpty()) {
                System.out.println("Please select a heuristic.");
                return;
            }
            processInput();
        });

        Button backButton = new Button("Back");
        backButton.setPrefWidth(INPUT_BTN_WIDTH);
        backButton.setPrefHeight(INPUT_BTN_HEIGHT);
        backButton.setOnAction(e -> {
            System.out.println("Back button clicked, navigate to previous screen.");
            userInputs();
        });

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

        VBox boardViewFromDrawBoard = drawBoard(this.board);
        if (boardViewFromDrawBoard != null) {
            this.boardDisplayArea.getChildren().add(boardViewFromDrawBoard);
        }

        Label algoDisplayLabel = new Label("Algorithm: " + this.algorithm);
        algoDisplayLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.getChildren().add(algoDisplayLabel);

        if (("A*".equals(this.algorithm) || "Greedy".equals(this.algorithm))
            && this.heuristic != null && !this.heuristic.isEmpty()) {
            Label heuristicDisplayLabel = new Label("Heuristic: " + this.heuristic);
            heuristicDisplayLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            infoBox.getChildren().add(heuristicDisplayLabel);
        }
        
        Label initialBoardLabel = new Label("Initial Board");
        initialBoardLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        initialBoardLabel.setPadding(new Insets(0,0,10,0));


        Button startSearchButton = new Button("Start Search");
        startSearchButton.setPrefWidth(INPUT_BTN_WIDTH + 50);
        startSearchButton.setPrefHeight(INPUT_BTN_HEIGHT);
        startSearchButton.setOnAction(e -> {
            System.out.println("Start Search button clicked!");
            startSearchButton.setDisable(true);
            startSearch();
        });

        Button backButton = new Button("Back to Inputs");
        backButton.setPrefWidth(INPUT_BTN_WIDTH + 50);
        backButton.setPrefHeight(INPUT_BTN_HEIGHT);
        backButton.setOnAction(e -> userInputs());

        HBox buttonControlBox = new HBox(20, backButton, startSearchButton);
        buttonControlBox.setAlignment(Pos.CENTER);

        VBox searchScreenLayout = new VBox(20);
        searchScreenLayout.setAlignment(Pos.CENTER);
        searchScreenLayout.setPadding(new Insets(20));

        searchScreenLayout.getChildren().add(initialBoardLabel);
        if (this.boardDisplayArea != null) {
            searchScreenLayout.getChildren().add(this.boardDisplayArea);
        }
        searchScreenLayout.getChildren().add(infoBox);
        searchScreenLayout.getChildren().add(buttonControlBox);

        double sceneWidth = 800;
        double sceneHeight = 700;
        if (this.primaryStage.getScene() != null) {
            sceneWidth = Math.max(sceneWidth, this.primaryStage.getScene().getWidth());
            sceneHeight = Math.max(sceneHeight, this.primaryStage.getScene().getHeight());
        }

        Scene searchScene = new Scene(searchScreenLayout, sceneWidth, sceneHeight);
        this.primaryStage.setScene(searchScene);
        this.primaryStage.centerOnScreen();
    }

    public void startSearch() {
        Pathfinder solver = null;
        if ("UCS".equals(this.algorithm)) {
            solver = new UCS();
        } else if ("Greedy".equals(this.algorithm)) {
            if ("Blocking".equals(this.heuristic)) {
                solver = new GreedyBestFirst(new BlockingCarsHeuristic());
            } else if ("Manhattan".equals(this.heuristic)) {
                solver = new GreedyBestFirst(new ManhattanToExitHeuristic());
            }
        } else if ("A*".equals(this.algorithm)) {
            if ("Blocking".equals(this.heuristic)) {
                solver = new AStar(new BlockingCarsHeuristic());
            } else if ("Manhattan".equals(this.heuristic)) {
                solver = new AStar(new ManhattanToExitHeuristic());
            }
        }
        List<State> solution = solver.solve(this.board);
        if (solution.isEmpty()) {
            inputError("No solution found.", false);
            return;
        }
        animateSolution(solution, solver);
    }

    public void animateSolution(List<State> solution, Pathfinder solver) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < solution.size(); i++) {
            final State currentState = solution.get(i); 
            if (i == solution.size() - 1) {
                this.board = currentState.board;
            }
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 700), event -> {
                VBox newBoardView = drawBoard(currentState.board);
                if (newBoardView != null) {
                    this.boardDisplayArea.getChildren().clear();
                    newBoardView.setAlignment(Pos.CENTER);
                    this.boardDisplayArea.getChildren().add(newBoardView);
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.setOnFinished(event -> {
            System.out.println("Animation finished.");
            solutionsFound(solver);
        });

        System.out.println("Playing animation with " + timeline.getKeyFrames().size() + " frames.");
        timeline.play();
    }

    public void solutionsFound(Pathfinder solver){
        VBox boardViewLayout = drawBoard(this.board);

        Label timeLabel = new Label("Execution time: " + solver.getRuntimeNano()/1e6);
        timeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        VBox infoBox = new VBox(5);
        infoBox.getChildren().add(timeLabel);

        Label nodesLabel = new Label("Heuristic: " + this.heuristic);
        nodesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        infoBox.getChildren().add(nodesLabel);
        infoBox.setAlignment(Pos.CENTER);

        Label initialBoardLabel = new Label("Final State");
        initialBoardLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        VBox boardLabelBox = new VBox(5);
        boardLabelBox.getChildren().add(initialBoardLabel);
        boardLabelBox.setAlignment(Pos.CENTER);
        boardLabelBox.setPadding(new Insets(30,0,0,0));

        Button backButton = new Button("Back to Title");
        backButton.setPrefWidth(INPUT_BTN_WIDTH + 50);
        backButton.setPrefHeight(INPUT_BTN_HEIGHT);
        backButton.setOnAction(e -> titleScreen());

        HBox buttonControlBox = new HBox(20, backButton);
        buttonControlBox.setAlignment(Pos.CENTER);
        BorderPane searchScreenLayout = new BorderPane();
        searchScreenLayout.setPadding(new Insets(20));

        VBox topControls = new VBox(15, infoBox, buttonControlBox);
        topControls.setAlignment(Pos.CENTER);
        topControls.setPadding(new Insets(0, 0, 20, 0));

        searchScreenLayout.setCenter(boardViewLayout);
        searchScreenLayout.setBottom(topControls);
        searchScreenLayout.setTop(boardLabelBox);

        double sceneWidth = Math.max(800, this.primaryStage.getScene().getWidth());
        double sceneHeight = Math.max(600, this.primaryStage.getScene().getHeight());

        Scene searchScene = new Scene(searchScreenLayout, sceneWidth, sceneHeight);
        this.primaryStage.setScene(searchScene);
        this.primaryStage.centerOnScreen();
        this.primaryStage.centerOnScreen();
    }

    public VBox drawBoard(Board board) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(1);
        gridPane.setVgap(1);

        int displayRows = board.rows - 2;
        int displayCols = board.cols - 2;

        double cellSize = 60;
        gridPane.setMinSize(displayCols * cellSize, displayRows * cellSize);

        for (int i = 1; i < displayRows; i++) {
            for (int j = 1; j < displayCols; j++) {
                StackPane cellPane = new StackPane();
                cellPane.setPrefSize(cellSize, cellSize);

                Rectangle cellRect = new Rectangle(cellSize, cellSize);
                Label pieceLabel = new Label(""); // Initialize empty label
                pieceLabel.setFont(Font.font("Arial", FontWeight.BOLD, cellSize * 0.6)); // Adjust font size

                char pieceChar = (board.grid != null && i < board.grid.length && j < board.grid[i].length)
                        ? board.grid[i][j]
                        : '.'; // Default to empty if grid is not as expected

                if (pieceChar == '.') {
                    cellRect.setFill(Color.LIGHTGRAY);
                    cellRect.setStroke(Color.DARKGRAY);
                } else if (pieceChar == 'P') { // Primary Piece
                    cellRect.setFill(Color.RED);
                    cellRect.setStroke(Color.DARKRED);
                    pieceLabel.setText("P");
                    pieceLabel.setTextFill(Color.WHITE); // White text on red
                } else { // Other pieces (A-Z etc.)
                    Color pieceColor = pieceColors.get(pieceChar);
                    cellRect.setFill(pieceColor);
                    cellRect.setStroke(pieceColor.darker());
                    pieceLabel.setText(String.valueOf(pieceChar));

                    // contrasting text color
                    if (pieceColor.getBrightness() * pieceColor.getSaturation() < 0.15) {
                        pieceLabel.setTextFill(Color.WHITE);
                    } else {
                        pieceLabel.setTextFill(Color.BLACK);
                    }
                }
                cellPane.getChildren().addAll(cellRect, pieceLabel);
                gridPane.add(cellPane, j-1, i-1);
            }
        }

        VBox boardLayout = new VBox(20, gridPane);
        boardLayout.setAlignment(Pos.CENTER);
        boardLayout.setPadding(new Insets(20));

        return boardLayout;
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

        Button backButton = new Button("Back to Inputs");
        backButton.setPrefWidth(INPUT_BTN_WIDTH + 20);
        backButton.setPrefHeight(INPUT_BTN_HEIGHT);
        backButton.setOnAction(e -> {
            System.out.println("Back button clicked from error screen.");
            userInputs();
        });

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
