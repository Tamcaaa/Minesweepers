package org.example.minesweeper;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MinyApp extends Application {
    private HraciaMapaGUI hraciaMapaGUI;
    private HernyController controller;
    private Label stavHryLabel;
    private Label pocetTahovLabel;
    private TextField riadkyField;
    private TextField stlpceField;
    private TextField minyField;
    private Button resetButton;
    private Button koniecButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Míny - Minesweeper Game");

        // Inicializácia komponentov
        initializeComponents();

        // Vytvorenie layoutu
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Horný panel
        HBox hornPanel = createHornPanel();

        // Hracia mapa
        hraciaMapaGUI = new HraciaMapaGUI(controller);

        root.getChildren().addAll(hornPanel, hraciaMapaGUI);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Spustenie novej hry
        novaHra();
    }

    private void initializeComponents() {
        controller = new HernyController();

        stavHryLabel = new Label("Stav: Hranie");
        stavHryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        pocetTahovLabel = new Label("Ťahy: 0");
        pocetTahovLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        riadkyField = new TextField("9");
        riadkyField.setPrefWidth(60);

        stlpceField = new TextField("9");
        stlpceField.setPrefWidth(60);

        minyField = new TextField("10");
        minyField.setPrefWidth(60);

        resetButton = new Button("Nová hra");
        resetButton.setOnAction(e -> novaHra());

        koniecButton = new Button("Koniec");
        koniecButton.setOnAction(e -> System.exit(0));
    }

    private HBox createHornPanel() {
        HBox panel = new HBox(15);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Nastavenia
        VBox nastavenia = new VBox(5);
        nastavenia.getChildren().addAll(
                new Label("Nastavenia:"),
                new HBox(5, new Label("Riadky:"), riadkyField),
                new HBox(5, new Label("Stĺpce:"), stlpceField),
                new HBox(5, new Label("Míny:"), minyField)
        );

        // Stav hry
        VBox stavBox = new VBox(5);
        stavBox.getChildren().addAll(stavHryLabel, pocetTahovLabel);

        // Tlačidlá
        VBox tlacidla = new VBox(5);
        tlacidla.getChildren().addAll(resetButton, koniecButton);

        panel.getChildren().addAll(nastavenia, new Separator(), stavBox, new Separator(), tlacidla);

        return panel;
    }

    private void novaHra() {
        try {
            int riadky = Integer.parseInt(riadkyField.getText());
            int stlpce = Integer.parseInt(stlpceField.getText());
            int miny = Integer.parseInt(minyField.getText());

            if (riadky < 3 || riadky > 20 || stlpce < 3 || stlpce > 20) {
                showAlert("Neplatné rozměry", "Rozměry musí být mezi 3 a 20!");
                return;
            }

            if (miny >= riadky * stlpce || miny < 1) {
                showAlert("Neplatný počet mín", "Počet mín musí být menší než počet polí!");
                return;
            }

            controller.novaHra(riadky, stlpce, miny);
            hraciaMapaGUI.aktualizujMapu();
            aktualizujStavHry();

        } catch (NumberFormatException e) {
            showAlert("Chyba", "Zadajte platné čísla!");
        }
    }

    public void aktualizujStavHry() {
        StavHry stav = controller.getStavHry();
        int tahy = controller.getPocetTahov();

        pocetTahovLabel.setText("Ťahy: " + tahy);

        switch (stav) {
            case HRANIE:
                stavHryLabel.setText("Stav: Hranie");
                stavHryLabel.setTextFill(Color.BLUE);
                break;
            case VYHRA:
                stavHryLabel.setText("Stav: Výhra!");
                stavHryLabel.setTextFill(Color.GREEN);
                break;
            case PREHRA:
                stavHryLabel.setText("Stav: Prehra!");
                stavHryLabel.setTextFill(Color.RED);
                break;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}