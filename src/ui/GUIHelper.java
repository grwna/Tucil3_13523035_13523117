package ui;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Board;

public class GUIHelper {
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

    public static Button createButton(String title, EventHandler<ActionEvent> action) {
        Button button = new Button(title);
        button.setPrefWidth(INPUT_BTN_WIDTH);
        button.setPrefHeight(INPUT_BTN_HEIGHT);
        button.setOnAction(action);

        String modernStyle = "-fx-background-color:rgb(0, 128, 255); " +
                             "-fx-text-fill: white; " +
                             "-fx-font-size: 14px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 5px; " +
                             "-fx-border-radius: 5px; " +
                             "-fx-padding: 8px 18px; " +
                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);";
        button.setStyle(modernStyle);

        String hoverStyle = "-fx-background-color:rgb(0, 81, 194); " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 5px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-padding: 8px 18px; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);";

        String pressedStyle = "-fx-background-color:rgb(0, 98, 255); " +
                              "-fx-text-fill: white; " +
                              "-fx-font-size: 14px; " +
                              "-fx-font-weight: bold; " +
                              "-fx-background-radius: 5px; " +
                              "-fx-border-radius: 5px; " +
                              "-fx-padding: 8px 18px; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1);";

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(modernStyle));
        button.setOnMousePressed(e -> button.setStyle(pressedStyle));
        button.setOnMouseReleased(e -> button.setStyle(hoverStyle));

        return button;
    }

    public static VBox drawBoard(Board board) {
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
}
