package ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithm.AStar;
import algorithm.BlockingCarsHeuristic;
import algorithm.GreedyBestFirst;
import algorithm.IDDFS;
import algorithm.ManhattanToExitHeuristic;
import algorithm.Pathfinder;
import algorithm.UCS;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import model.Piece;
import model.Position;
import model.State;
import parser.InputParser;
import utils.OutputWriter;

public class GUI extends Application {
    public String algorithm;
    public String heuristic;
    public String filePath;
    public Board board;
    public Pathfinder solver;
    public List<State> solution;
    public Stage primaryStage;
    public StackPane boardDisplayArea;
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
        startButton.setPrefWidth(170);
        startButton.setPrefHeight(40);
        startButton.setOnAction(e -> {
            System.out.println("Start button clicked on title screen!");
            userInputs();
        });

        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(170);
        exitButton.setPrefHeight(40);
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

        aStarRadio.setSelected(true);
        this.algorithm = "A* Search";

        algoToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                this.algorithm = (String) newVal.getUserData();
                System.out.println("Algorithm selected: " + this.algorithm);
            }
        });

        VBox radioButtonBox = new VBox(10, aStarRadio, greedyRadio, ucsRadio, iddfsRadio);
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

        
        Button browseButton = createButton("Browse...", e -> {
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

        
        Button nextButton = createButton("Next", e -> {
             this.filePath = filePathField.getText();

            if (this.algorithm == null || this.algorithm.isEmpty()) {
                System.out.println("Please select an algorithm.");
                return;
            }
            if (this.filePath == null || this.filePath.isEmpty()) {
                System.out.println("Please enter or browse for a configuration file.");
                return;
            }
            if (this.algorithm.equals("A* Search") || this.algorithm.equals("Greedy Best First Search")){
                heuristicInput();
            } else {
                processInput();
            }
        });

        
        Button backButton = createButton("Back", e-> {titleScreen();});

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

        blockingCarsRadio.setSelected(true);
        this.heuristic = "Blocking Cars";

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

        Button nextButton = createButton("Next", e->{
            if (this.heuristic == null || this.heuristic.isEmpty()) {
                System.out.println("Please select a heuristic.");
                return;
            }
            processInput();
        });

        Button backButton = createButton("Back", e->{userInputs();});

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

        if (("A* Search".equals(this.algorithm) || "Greedy Best First Search".equals(this.algorithm))
            && this.heuristic != null && !this.heuristic.isEmpty()) {
            Label heuristicDisplayLabel = new Label("Heuristic: " + this.heuristic);
            heuristicDisplayLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            infoBox.getChildren().add(heuristicDisplayLabel);
        }
        
        Label initialBoardLabel = new Label("Initial Board");
        initialBoardLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        initialBoardLabel.setPadding(new Insets(0,0,10,0));

        // below the above label and before the board is drawn add an input field for delay in ms, (make sure only double values are allowed)

        Button startSearchButton = createButton("Start Search", e -> {});
        Button backButton = createButton("Back to Inputs", e -> {userInputs();});
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
            }
        } else if ("A* Search".equals(this.algorithm)) {
            if ("Blocking Cars".equals(this.heuristic)) {
                solver = new AStar(new BlockingCarsHeuristic());
            } else if ("Manhattan To Exit".equals(this.heuristic)) {
                solver = new AStar(new ManhattanToExitHeuristic());
            }
        } else if ("Iterative Deepening Search".equals(this.algorithm)){
            solver = new IDDFS();
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
        double frameDelay = 500; // Base delay between frames in ms
        
        // Track our position in the animation timeline
        double totalDuration = 0;
        
        for (int i = 0; i < this.solution.size(); i++) {
            final State currentState = this.solution.get(i);
            final State prevState = (i > 0) ? this.solution.get(i-1) : null;
            
            if (prevState != null) {
                // Find the piece that moved and how far it moved
                String move = currentState.move;
                if (move != null && !move.equals("Start")) {
                    // Parse the move string (e.g. "C-right-2")
                    String[] parts = move.split("-");
                    if (parts.length >= 2) {
                        char pieceId = parts[0].charAt(0);
                        String direction = parts[1];
                        int steps = (parts.length > 2) ? Integer.parseInt(parts[2]) : 1;
                        
                        // Create intermediate frames for multi-step moves
                        for (int step = 1; step <= steps; step++) {
                            final int currentStep = step;
                            KeyFrame intermediateFrame = new KeyFrame(
                                Duration.millis(totalDuration + step * 200), // 200ms per step
                                event -> {
                                    Board intermediateBoard = createIntermediateBoard(
                                        prevState.board, 
                                        currentState.board, 
                                        pieceId, 
                                        direction, 
                                        currentStep,
                                        steps
                                    );
                                    
                                    VBox newBoardView = drawBoard(intermediateBoard);
                                    if (newBoardView != null) {
                                        this.boardDisplayArea.getChildren().clear();
                                        newBoardView.setAlignment(Pos.CENTER);
                                        this.boardDisplayArea.getChildren().add(newBoardView);
                                    }
                                }
                            );
                            timeline.getKeyFrames().add(intermediateFrame);
                        }
                        // Update the total duration based on the number of steps
                        totalDuration += steps * 200;
                    }
                }
            } else {
                // First state (starting position)
                KeyFrame startFrame = new KeyFrame(Duration.ZERO, event -> {
                    VBox newBoardView = drawBoard(currentState.board);
                    if (newBoardView != null) {
                        this.boardDisplayArea.getChildren().clear();
                        newBoardView.setAlignment(Pos.CENTER);
                        this.boardDisplayArea.getChildren().add(newBoardView);
                    }
                });
                timeline.getKeyFrames().add(startFrame);
            }
            
            // Update the current board reference at the end
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

    // Add this new helper method to create intermediate boards
    private Board createIntermediateBoard(Board startBoard, Board endBoard, char pieceId, String direction, int currentStep, int totalSteps) {
        // Create a copy of the start board
        Board intermediateBoard = startBoard.copy();
        
        // Find the piece in the start board
        Piece piece = intermediateBoard.pieces.get(pieceId);
        if (piece == null) {
            return intermediateBoard; // Return the board as is if piece not found
        }
        
        // Clear the current piece from the grid
        for (int r = 0; r < intermediateBoard.rows; r++) {
            for (int c = 0; c < intermediateBoard.cols; c++) {
                if (intermediateBoard.grid[r][c] == pieceId) {
                    intermediateBoard.grid[r][c] = '.';
                }
            }
        }
        
        // Calculate the new position based on the direction and current step
        int deltaRow = 0;
        int deltaCol = 0;
        
        switch (direction.toLowerCase()) {
            case "up":
                deltaRow = -currentStep;
                break;
            case "down":
                deltaRow = currentStep;
                break;
            case "left":
                deltaCol = -currentStep;
                break;
            case "right":
                deltaCol = currentStep;
                break;
        }
        
        // Set the new piece position
        piece.start = new Position(startBoard.pieces.get(pieceId).start.row + deltaRow, 
                                startBoard.pieces.get(pieceId).start.col + deltaCol);
        
        // Redraw the piece on the grid
        int r = piece.start.row;
        int c = piece.start.col;
        for (int i = 0; i < piece.length; i++) {
            intermediateBoard.grid[r][c] = pieceId;
            if (piece.isHorizontal) c++;
            else r++;
        }
        
        return intermediateBoard;
    }

    public void solutionsFound(){
        this.boardDisplayArea.getChildren().clear();

        VBox finalBoardView = drawBoard(this.board);
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
        
        Button backToTitleButton = createButton("Back to Inputs", e -> userInputs());
        Button replayButton = createButton("Replay", e -> {
            if (this.solution != null && !this.solution.isEmpty()) {
                System.out.println("Replay button clicked!");
                ((Button)e.getSource()).setDisable(true);
                backToTitleButton.setDisable(true);

                animateSolution();
            } else {
                inputError("No solution path available to replay.", false);
            }
        });
        Button saveButton = createButton("Save Solution", e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Save Solution");
            alert.setHeaderText("Save Solution to File");
            alert.setContentText("Do you want to save the complete solution to a file?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        String outputPath = OutputWriter.writeSolution(
                            this.solution, 
                            this.solver.getName(), 
                            this.solver.getRuntimeNano()/1e6);
                        Alert successAlert = new Alert(AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Solution saved successfully to:\n" + outputPath);
                        successAlert.showAndWait();
                    } catch (IOException ex) {
                        Alert errorAlert = new Alert(AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Failed to save solution");
                        errorAlert.setContentText(ex.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });
        });

        HBox buttonControlBox = new HBox(20, backToTitleButton, replayButton, saveButton);
        buttonControlBox.setAlignment(Pos.CENTER);
        buttonControlBox.setPadding(new Insets(0, 0, 50, 0));

        VBox solutionScreenLayout = new VBox(20);
        solutionScreenLayout.setAlignment(Pos.CENTER);
        solutionScreenLayout.setPadding(new Insets(20));
        solutionScreenLayout.getChildren().addAll(finalStateTitleLabel,this.boardDisplayArea,infoBox,buttonControlBox);

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

    public VBox drawBoard(Board board) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(1);
        gridPane.setVgap(1);

        int displayRows = board.rows;
        int displayCols = board.cols;

        double cellSize = 50;
        gridPane.setMinSize(displayCols * cellSize, displayRows * cellSize);

        for (int i = 0; i < displayRows; i++) {
            for (int j = 0; j < displayCols; j++) {
                StackPane cellPane = new StackPane();
                cellPane.setPrefSize(cellSize, cellSize);

                Rectangle cellRect = new Rectangle(cellSize, cellSize);
                Label pieceLabel = new Label("");
                pieceLabel.setFont(Font.font("Arial", FontWeight.BOLD, cellSize * 0.6));

                char pieceChar = (board.grid != null && i < board.grid.length && j < board.grid[i].length)? board.grid[i][j] : '.';
                pieceLabel.setText(String.valueOf(pieceChar));

                if (pieceChar == '.') {
                    pieceLabel.setText("");
                    cellRect.setFill(Color.LIGHTGRAY);
                    cellRect.setStroke(Color.DARKGRAY);
                } else if (pieceChar == 'P') {
                    cellRect.setFill(Color.RED);
                    cellRect.setStroke(Color.DARKRED);
                    pieceLabel.setTextFill(Color.WHITE);
                } else if (pieceChar == 'K') {
                    cellRect.setFill(Color.LIME);
                    cellRect.setStroke(Color.GREEN);
                    pieceLabel.setTextFill(Color.BLACK);
                }else if (pieceChar == ' '){
                    continue;
                }else {
                    Color pieceColor = pieceColors.get(pieceChar);
                    cellRect.setFill(pieceColor);
                    cellRect.setStroke(pieceColor.darker());

                    // contrasting text color
                    if (pieceColor.getBrightness() * pieceColor.getSaturation() < 0.15) {
                        pieceLabel.setTextFill(Color.WHITE);
                    } else {
                        pieceLabel.setTextFill(Color.BLACK);
                    }
                }
                cellPane.getChildren().addAll(cellRect, pieceLabel);
                gridPane.add(cellPane, j, i);
            }
        }

        VBox boardLayout = new VBox(20, gridPane);
        boardLayout.setAlignment(Pos.CENTER);
        boardLayout.setPadding(new Insets(20));

        return boardLayout;
    }

    public Button createButton(String title, EventHandler<ActionEvent> action) {
        Button button = new Button(title);
        button.setPrefWidth(INPUT_BTN_WIDTH);
        button.setPrefHeight(INPUT_BTN_HEIGHT);
        button.setOnAction(action);
        return button;
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

        Button backButton = createButton("Back to inputs", e-> {userInputs();});

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
