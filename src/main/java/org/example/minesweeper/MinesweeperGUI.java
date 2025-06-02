package org.example.minesweeper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MinesweeperGUI {
    private Stage primaryStage;
    private GameLogic gameLogic;
    private GameData gameData;
    private Button[][] cellButtons;
    private Label movesLabel;
    private Label gameStateLabel;
    private Label minesLabel;
    private TextField rowsField;
    private TextField colsField;
    private TextField minesField;

    public MinesweeperGUI(Stage primaryStage, GameData gameData) {
        this.primaryStage = primaryStage;
        this.gameData = gameData;
        setupWindow();
        startNewGame(9, 9, 10); // Default game
    }

    private void setupWindow() {
        primaryStage.setTitle("Minesweeper - OOP Final Project");
        primaryStage.setResizable(false);
    }

    private void startNewGame(int rows, int cols, int mines) {
        gameLogic = new GameLogic(rows, cols, mines);
        cellButtons = new Button[rows][cols];
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Top panel
        VBox topPanel = createTopPanel();

        // Game grid
        GridPane gameGrid = createGameGrid();

        // Statistics panel
        VBox statsPanel = createStatsPanel();

        root.getChildren().addAll(topPanel, gameGrid, statsPanel);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateDisplay();
    }

    private VBox createTopPanel() {
        VBox topPanel = new VBox(5);
        topPanel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 10; -fx-border-color: #888; -fx-border-width: 1;");

        // Settings row
        HBox settingsRow = new HBox(10);
        settingsRow.setAlignment(Pos.CENTER);

        Label rowsLabel = new Label("Rows:");
        rowsField = new TextField("9");
        rowsField.setPrefWidth(50);

        Label colsLabel = new Label("Cols:");
        colsField = new TextField("9");
        colsField.setPrefWidth(50);

        Label minesLabelField = new Label("Mines:");
        minesField = new TextField("10");
        minesField.setPrefWidth(50);

        Button newGameButton = new Button("New Game");
        newGameButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        newGameButton.setOnAction(e -> {
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

                startNewGame(rows, cols, mines);
            } catch (NumberFormatException ex) {
                showAlert("Please enter valid numbers!");
            }
        });

        Button exitButton = new Button("Exit Game");
        exitButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        exitButton.setOnAction(e -> primaryStage.close());

        settingsRow.getChildren().addAll(rowsLabel, rowsField, colsLabel, colsField,
                minesLabelField, minesField, newGameButton, exitButton);

        // Status row
        HBox statusRow = new HBox(20);
        statusRow.setAlignment(Pos.CENTER);

        movesLabel = new Label("Moves: 0");
        movesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        gameStateLabel = new Label("Status: Playing");
        gameStateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gameStateLabel.setTextFill(Color.BLUE);

        minesLabel = new Label("Mines: 0/0");
        minesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        statusRow.getChildren().addAll(movesLabel, gameStateLabel, minesLabel);

        topPanel.getChildren().addAll(settingsRow, statusRow);
        return topPanel;
    }

    private GridPane createGameGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setStyle("-fx-background-color: #888;");

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
                grid.add(button, j, i);
            }
        }

        return grid;
    }

    private VBox createStatsPanel() {
        VBox statsPanel = new VBox(5);
        statsPanel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 10; -fx-border-color: #888; -fx-border-width: 1;");

        Label statsTitle = new Label("Game Statistics");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label totalGamesLabel = new Label("Total Games: " + gameData.getTotalGames());
        Label wonGamesLabel = new Label("Won Games: " + gameData.getWonGames());
        Label winRateLabel = new Label(String.format("Win Rate: %.1f%%", gameData.getWinRate()));

        Button historyButton = new Button("Show Game History");
        historyButton.setOnAction(e -> showGameHistory());

        statsPanel.getChildren().addAll(statsTitle, totalGamesLabel, wonGamesLabel, winRateLabel, historyButton);
        return statsPanel;
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
                    "\nTime: " + gameLogic.getGameDurationMinutes() + " minutes");
            alert.showAndWait();
        }
    }

    private void showGameHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game History");
        alert.setHeaderText("Previous Games");

        StringBuilder content = new StringBuilder();
        java.util.List<GameRecord> history = gameData.getGameHistory();

        if (history.isEmpty()) {
            content.append("No games played yet.");
        } else {
            for (int i = history.size() - 1; i >= Math.max(0, history.size() - 10); i--) {
                content.append(history.get(i).toString()).append("\n");
            }
            if (history.size() > 10) {
                content.append("... and ").append(history.size() - 10).append(" more games");
            }
        }

        alert.setContentText(content.toString());
        alert.getDialogPane().setPrefWidth(500);
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