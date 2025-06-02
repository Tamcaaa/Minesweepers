package org.example.minesweeper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameRecord {
    private int rows;
    private int cols;
    private int mines;
    private int moves;
    private long durationMinutes;
    private GameState result;
    private LocalDateTime timestamp;

    public GameRecord(int rows, int cols, int mines, int moves, long durationMinutes, GameState result) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.moves = moves;
        this.durationMinutes = durationMinutes;
        this.result = result;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getMines() { return mines; }
    public int getMoves() { return moves; }
    public long getDurationMinutes() { return durationMinutes; }
    public GameState getResult() { return result; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("%s | %dx%d (%d mines) | %d moves | %d min | %s",
                timestamp.format(formatter), rows, cols, mines, moves, durationMinutes,
                result == GameState.WON ? "WON" : "LOST");
    }
}