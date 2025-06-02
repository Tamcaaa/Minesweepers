package org.example.minesweeper;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MinesweeperGame game = new MinesweeperGame();
        game.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
