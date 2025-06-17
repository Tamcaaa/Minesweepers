package org.example.minesweeper;

public class EmptyCell extends Cell {
    @Override
    public boolean hasMine() {
        return false;
    }
}
