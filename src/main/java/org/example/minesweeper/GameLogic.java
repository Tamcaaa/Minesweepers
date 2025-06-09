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
                grid[i][j] = new Cell();
            }
        }
    }

    private void placeMines() {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < totalMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);

            if (!grid[row][col].hasMine()) {
                grid[row][col].setMine(true);
                minesPlaced++;
            }
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
    private void placeMinesAvoidingFirstClick(int safeRow, int safeCol) {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < totalMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);

            if (grid[row][col].hasMine()) continue;

            // Vynechaj 3x3 oblasť okolo prvého kliknutia
            if (Math.abs(row - safeRow) <= 1 && Math.abs(col - safeCol) <= 1) {
                continue;
            }

            grid[row][col].setMine(true);
            minesPlaced++;
        }
    }

    public boolean revealCell(int row, int col) {
        if (gameState != GameState.PLAYING || !isValidCell(row, col)) {
            return false;
        }

        if (!minesGenerated) {
            placeMinesAvoidingFirstClick(row, col);
            calculateNeighborMines();
            minesGenerated = true;
            startTime = System.currentTimeMillis(); // začni čas až po prvom kliknutí
        }

        Cell cell = grid[row][col];
        if (cell.isRevealed() || cell.isFlagged()) {
            return false;
        }

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

    public void chordCell(int row, int col) {
        Cell cell = grid[row][col];
        if (cell.isRevealed() && (cell.getNeighborMines() == countNeighborFlags(row, col))) {
            for (int[] coord : getHiddenCells(row, col)) {
                if (coord != null) {
                    revealCell(coord[0], coord[1]);
                }
            }
        } else {
            return;
        }
    }
    private int countNeighborFlags(int row, int col) {
        int flags = 0;
        int rows = grid.length;
        int cols = grid[0].length;
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int neighborRow = row + dr[i];
            int neighborCol = col + dc[i];
            if (neighborRow >= 0 && neighborRow < rows && neighborCol >= 0 && neighborCol < cols) {
                if (grid[neighborRow][neighborCol].isFlagged()) flags++;
            }
        }
        return flags;
    }
    private int[][] getHiddenCells(int row, int col) {
        int[][] arr =  new int[8][];
        int rows = grid.length;
        int cols = grid[0].length;
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int neighborRow = row + dr[i];
            int neighborCol = col + dc[i];
            if (neighborRow >= 0 && neighborRow < rows && neighborCol >= 0 && neighborCol < cols) {
                if (grid[neighborRow][neighborCol].isHidden()) {
                    arr[i] = new int[]{neighborRow, neighborCol};
                }
            }
        }
        return arr;
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
        if (gameState != GameState.PLAYING || !isValidCell(row, col)) {
            return;
        }

        Cell cell = grid[row][col];
        if (cell.isRevealed()) {
            return;
        }

        if (cell.isFlagged()) {
            cell.setState(CellState.HIDDEN);
        } else {
            cell.setState(CellState.FLAGGED);
        }
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

    // Getters
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
        long cas = System.currentTimeMillis() -startTime ;
        return (String.format("%02d:%02d", ((cas) / 1000 / 60), ((cas / 1000) % 60)));
    }

    public int getFlaggedCount() {
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j].isFlagged()) {
                    count++;
                }
            }
        }
        return count;
    }
}
