module org.example.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.minesweeper to javafx.fxml;
    exports org.example.minesweeper;
}