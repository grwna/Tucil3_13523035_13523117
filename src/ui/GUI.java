package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Board;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rush Hour Solver");

        Label titleLabel = new Label("Rush Hour Solver");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24)); 

        Label authorLabel = new Label("By: 13523035, 13523117");
        authorLabel.setFont(Font.font("Arial", 14));

        VBox titleBox = new VBox(10); 
        titleBox.getChildren().addAll(titleLabel, authorLabel);
        titleBox.setAlignment(Pos.CENTER); 

        Button startButton = new Button("Start");
        startButton.setPrefWidth(100); 
        startButton.setOnAction(e -> {
            System.out.println("Start button clicked!");
        });

        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(100); 
        exitButton.setOnAction(e -> {
            System.out.println("Exit button clicked!");
            primaryStage.close(); 
        });

        VBox buttonBox = new VBox(20); 
        buttonBox.getChildren().addAll(startButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER); 
        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20)); 

        mainLayout.setTop(titleBox);
        BorderPane.setAlignment(titleBox, Pos.CENTER); 
        BorderPane.setMargin(titleBox, new Insets(0, 0, 50, 0)); 

        mainLayout.setCenter(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER); 
        
        Scene scene = new Scene(mainLayout, 400, 300); 
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void drawBoard(Board board){

    }

    public static void main(String[] args) {
        launch(args);
    }
}
