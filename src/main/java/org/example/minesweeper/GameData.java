package org.example.minesweeper;
import java.util.ArrayList;
import java.util.List;

public class GameData {
    private List<GameRecord> gameHistory;

    public GameData() {
        this.gameHistory = new ArrayList<>();
    }

    public void addGameRecord(GameRecord record) {
        gameHistory.add(record);
    }

    public List<GameRecord> getGameHistory() {
        return new ArrayList<>(gameHistory);
    }

    public int getTotalGames() {
        return gameHistory.size();
    }

    public int getWonGames() {
        return (int) gameHistory.stream()
                .filter(record -> record.getResult() == GameState.WON)
                .count();
    }

    public double getWinRate() {
        if (gameHistory.isEmpty()) return 0.0;
        return (double) getWonGames() / getTotalGames() * 100;
    }
}
