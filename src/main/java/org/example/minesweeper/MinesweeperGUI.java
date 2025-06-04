package org.example.minesweeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MinesweeperGUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(loader.load());

        MinesweeperController controller = loader.getController();
        controller.setGameData(new GameData());

        stage.setTitle("Minesweeper");
        stage.setScene(scene);
        stage.show();
    }
}
