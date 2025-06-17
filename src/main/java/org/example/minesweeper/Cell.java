package org.example.minesweeper;

public abstract class Cell {
    private int neighborMines;
    private CellState state;

    public Cell() {
        this.neighborMines = 0;
        this.state = CellState.HIDDEN;
    }

    public abstract boolean hasMine();  // abstraktná metóda

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
