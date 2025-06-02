package org.example.minesweeper;

import javafx.stage.Stage;

public class MinesweeperGame {
    private GameData gameData;
    private MinesweeperGUI gui;

    public MinesweeperGame() {
        this.gameData = new GameData();
    }

    public void start(Stage primaryStage) {
        gui = new MinesweeperGUI(primaryStage, gameData);
    }
}