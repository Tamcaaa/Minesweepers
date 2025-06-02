package org.example.minesweeper;

public class Cell {
    private boolean hasMine;
    private int neighborMines;
    private CellState state;

    public Cell() {
        this.hasMine = false;
        this.neighborMines = 0;
        this.state = CellState.HIDDEN;
    }

    // Getters and Setters
    public boolean hasMine() {
        return hasMine;
    }

    public void setMine(boolean hasMine) {
        this.hasMine = hasMine;
    }

    public int getNeighborMines() {
        return neighborMines;
    }

    public void setNeighborMines(int neighborMines) {
        this.neighborMines = neighborMines;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public boolean isRevealed() {
        return state == CellState.REVEALED;
    }

    public boolean isFlagged() {
        return state == CellState.FLAGGED;
    }

    public boolean isHidden() {
        return state == CellState.HIDDEN;
    }
}