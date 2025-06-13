package org.example.minesweeper;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MinesweeperController implements Initializable {
    @FXML private TextField rowsField;
    @FXML private TextField colsField;
    @FXML private TextField minesField;
    @FXML private Button newGameButton;
    @FXML private Button exitButton;
    @FXML private Label movesLabel;
    @FXML private Label gameStateLabel;
    @FXML private Label minesLabel;
    @FXML private GridPane gameGrid;
    @FXML private Label totalGamesLabel;
    @FXML private Label wonGamesLabel;
    @FXML private Label winRateLabel;
    @FXML private Button historyButton;
    @FXML private Label timeLabel;



    private Timeline gameTimer;
    private long gameStartTimeMillis;

    private GameLogic gameLogic;
    private GameData gameData;
    private Button[][] cellButtons;

    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEventHandlers();
        startNewGame(9, 9, 10); // Default game

    }

    private void startClock() {
        gameStartTimeMillis = System.currentTimeMillis();

        gameTimer = new Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(1), event -> {
                    long elapsedMillis = System.currentTimeMillis() - gameStartTimeMillis;
                    long seconds = (elapsedMillis / 1000) % 60;
                    long minutes = (elapsedMillis / 1000 / 60) % 60;
                    long hours = (elapsedMillis / 1000 / 60 / 60);
                    String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    timeLabel.setText("Time: " + formattedTime);
                })
        );
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }




    private void stopClock() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }


    private void setupEventHandlers() {
        newGameButton.setOnAction(e -> handleNewGame());
        exitButton.setOnAction(e -> handleExit());
        historyButton.setOnAction(e -> showGameHistory());
    }

    @FXML
    private void handleNewGame() {
        try {
            int rows = Integer.parseInt(rowsField.getText());
            int cols = Integer.parseInt(colsField.getText());
            int mines = Integer.parseInt(minesField.getText());

            if (rows < 5 || rows > 20 || cols < 5 || cols > 20) {
                showAlert("Invalid size! Rows and columns must be between 5 and 20.");
                return;
            }

            if (mines >= rows * cols || mines < 1) {
                showAlert("Invalid mine count! Must be less than total cells and greater than 0.");
                return;
            }
            if (mines >= rows * cols - 8 || mines < 1) {
                showAlert("Invalid mine count! Must be less than total cells minus 9 (safe zone) and greater than 0.");
                //was not able to solve this problem yet, having on 9x9 75 mines and more costs programs crash
                return;
            }

            startNewGame(rows, cols, mines);
        } catch (NumberFormatException ex) {
            showAlert("Please enter valid numbers!");
        }
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void startNewGame(int rows, int cols, int mines) {
        stopClock(); // vypni predchádzajúce hodiny
        timeLabel.setText("Time: 00:00:00"); // resetuj displej

        gameLogic = new GameLogic(rows, cols, mines);
        cellButtons = new Button[rows][cols];
        createGameGrid();
        updateDisplay();
    }


    private void createGameGrid() {
        gameGrid.getChildren().clear();
        gameGrid.setAlignment(Pos.CENTER);
        gameGrid.setHgap(1);
        gameGrid.setVgap(1);

        for (int i = 0; i < gameLogic.getRows(); i++) {
            for (int j = 0; j < gameLogic.getCols(); j++) {
                Button button = new Button();
                button.setPrefSize(30, 30);
                button.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                button.setStyle("-fx-background-color: #d0d0d0; -fx-border-color: #888; -fx-border-width: 1;");

                final int row = i;
                final int col = j;

                button.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (gameTimer == null || !gameTimer.getStatus().equals(Timeline.Status.RUNNING)) {
                            startClock(); // spustí sa len pri prvom kliknutí
                        }

                        if (gameLogic.revealCell(row, col)) {
                            updateDisplay();
                            checkGameEnd();
                        }
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        gameLogic.toggleFlag(row, col);
                        updateDisplay();
                    }
                });




                cellButtons[i][j] = button;
                gameGrid.add(button, j, i);
            }
        }
    }

    private void updateDisplay() {
        // Update cell buttons
        for (int i = 0; i < gameLogic.getRows(); i++) {
            for (int j = 0; j < gameLogic.getCols(); j++) {
                Cell cell = gameLogic.getCell(i, j);
                Button button = cellButtons[i][j];

                if (cell.isRevealed()) {
                    if (cell.hasMine()) {
                        button.setText("💣");
                        button.setStyle("-fx-background-color: #ff4444; -fx-text-fill: black; -fx-font-weight: bold;");
                    } else {
                        int mines = cell.getNeighborMines();
                        button.setText(mines > 0 ? String.valueOf(mines) : "");
                        button.setStyle("-fx-background-color: #ffffff; -fx-text-fill: " +
                                getNumberColor(mines) + "; -fx-font-weight: bold;");
                    }
                } else if (cell.isFlagged()) {
                    button.setText("🚩");
                    button.setStyle("-fx-background-color: #ffff44; -fx-text-fill: red; -fx-font-weight: bold;");
                } else {
                    button.setText("");
                    button.setStyle("-fx-background-color: #d0d0d0; -fx-border-color: #888; -fx-border-width: 1;");
                }
            }
        }

        // Update status labels
        movesLabel.setText("Moves: " + gameLogic.getMoves());
        minesLabel.setText("Mines: " + gameLogic.getFlaggedCount() + "/" + gameLogic.getTotalMines());

        GameState state = gameLogic.getGameState();
        switch (state) {
            case PLAYING:
                gameStateLabel.setText("Status: Playing");
                gameStateLabel.setTextFill(Color.BLUE);
                break;
            case WON:
                gameStateLabel.setText("Status: YOU WON! 🎉");
                gameStateLabel.setTextFill(Color.GREEN);
                break;
            case LOST:
                gameStateLabel.setText("Status: Game Over 💥");
                gameStateLabel.setTextFill(Color.RED);
                break;
        }

        updateStatsDisplay();
    }

    private void updateStatsDisplay() {
        if (gameData != null) {
            totalGamesLabel.setText("Total Games: " + gameData.getTotalGames());
            wonGamesLabel.setText("Won Games: " + gameData.getWonGames());
            winRateLabel.setText(String.format("Win Rate: %.1f%%", gameData.getWinRate()));
        }
    }

    private String getNumberColor(int number) {
        switch (number) {
            case 1: return "blue";
            case 2: return "green";
            case 3: return "red";
            case 4: return "purple";
            case 5: return "maroon";
            case 6: return "turquoise";
            case 7: return "black";
            case 8: return "gray";
            default: return "black";
        }
    }

    private void checkGameEnd() {
        GameState state = gameLogic.getGameState();
        if (state != GameState.PLAYING) {
            // Record the game
            stopClock();
            GameRecord record = new GameRecord(
                    gameLogic.getRows(),
                    gameLogic.getCols(),
                    gameLogic.getTotalMines(),
                    gameLogic.getMoves(),
                    gameLogic.getGameDurationMinutes(),
                    state
            );
            gameData.addGameRecord(record);

            // Show result dialog
            String message = (state == GameState.WON) ?
                    "Congratulations! You won the game!" :
                    "Game Over! You hit a mine!";

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Finished");
            alert.setHeaderText(null);
            alert.setContentText(message + "\nMoves: " + gameLogic.getMoves() +
                    "\nTime: " + gameLogic.getGameDurationOutput());
            alert.showAndWait();

            updateStatsDisplay();
        }
    }

    public void showGameHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game History");
        alert.setHeaderText("Previous Games");

        StringBuilder content = new StringBuilder();
        java.util.List<GameRecord> history = gameData.getGameHistory();

        if (history.isEmpty()) {
            content.append("No games played yet.");
        } else {
            int limit = 10;  // Zobraz max 10 posledných hier
            int start = Math.max(0, history.size() - limit);
            for (int i = history.size() - 1; i >= start; i--) {
                content.append(history.get(i).toString()).append("\n");
            }
            if (history.size() > limit) {
                content.append("... and ").append(history.size() - limit).append(" more games.");
            }
        }

        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(480);
        textArea.setPrefHeight(300);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}