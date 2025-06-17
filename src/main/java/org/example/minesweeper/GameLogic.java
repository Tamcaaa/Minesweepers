package org.example.minesweeper;

import java.util.Random;

public class GameLogic {
    private Cell[][] grid;
    private int rows;
    private int cols;
    private int totalMines;
    private int revealedCells;
    private GameState gameState;
    private int moves;
    private long startTime;
    private boolean minesGenerated = false;

    public GameLogic(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.totalMines = mines;
        this.revealedCells = 0;
        this.gameState = GameState.PLAYING;
        this.moves = 0;
        this.startTime = System.currentTimeMillis();
        initializeGrid();
    }

    private void initializeGrid() {
        grid = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new EmptyCell();
            }
        }
    }
    private void placeMinesAvoidingFirstClick(int safeRow, int safeCol) {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < totalMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);

            if (grid[row][col].hasMine()) continue;
            if (Math.abs(row - safeRow) <= 1 && Math.abs(col - safeCol) <= 1) continue;

            grid[row][col] = new MineCell();
            minesPlaced++;
        }
    }

    private void calculateNeighborMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!grid[i][j].hasMine()) {
                    int count = 0;
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            int ni = i + di;
                            int nj = j + dj;
                            if (isValidCell(ni, nj) && grid[ni][nj].hasMine()) {
                                count++;
                            }
                        }
                    }
                    grid[i][j].setNeighborMines(count);
                }
            }
        }
    }

    public boolean revealCell(int row, int col) {
        if (gameState != GameState.PLAYING || !isValidCell(row, col)) return false;

        if (!minesGenerated) {
            placeMinesAvoidingFirstClick(row, col);
            calculateNeighborMines();
            minesGenerated = true;
            startTime = System.currentTimeMillis();
        }

        Cell cell = grid[row][col];
        if (cell.isRevealed() || cell.isFlagged()) return false;

        moves++;
        cell.setState(CellState.REVEALED);
        revealedCells++;

        if (cell.hasMine()) {
            gameState = GameState.LOST;
            revealAllMines();
            return true;
        }

        if (cell.getNeighborMines() == 0) {
            revealEmptyNeighbors(row, col);
        }

        checkWinCondition();
        return true;
    }

    private void revealEmptyNeighbors(int row, int col) {
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                int ni = row + di;
                int nj = col + dj;
                if (isValidCell(ni, nj)) {
                    Cell neighbor = grid[ni][nj];
                    if (neighbor.isHidden() && !neighbor.hasMine()) {
                        neighbor.setState(CellState.REVEALED);
                        revealedCells++;
                        if (neighbor.getNeighborMines() == 0) {
                            revealEmptyNeighbors(ni, nj);
                        }
                    }
                }
            }
        }
    }

    public void toggleFlag(int row, int col) {
        if (gameState != GameState.PLAYING || !isValidCell(row, col)) return;

        Cell cell = grid[row][col];
        if (cell.isRevealed()) return;

        cell.setState(cell.isFlagged() ? CellState.HIDDEN : CellState.FLAGGED);
    }

    private void revealAllMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j].hasMine()) {
                    grid[i][j].setState(CellState.REVEALED);
                }
            }
        }
    }

    private void checkWinCondition() {
        int totalCells = rows * cols;
        if (revealedCells == totalCells - totalMines) {
            gameState = GameState.WON;
        }
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public Cell getCell(int row, int col) {
        return isValidCell(row, col) ? grid[row][col] : null;
    }

    public GameState getGameState() { return gameState; }
    public int getMoves() { return moves; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTotalMines() { return totalMines; }

    public long getGameDurationMinutes() {
        return (System.currentTimeMillis() - startTime) / 60000;
    }

    public String getGameDurationOutput() {
        long cas = System.currentTimeMillis() - startTime;
        return String.format("%02d:%02d", (cas / 1000 / 60), (cas / 1000) % 60);
    }

    public int getFlaggedCount() {
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j].isFlagged()) count++;
            }
        }
        return count;
    }
}
